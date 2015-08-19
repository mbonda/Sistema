package Control.Operadores;

import Vista.Cobradores.VentanaCobradores.VentanaCobradores;
import Modelo.Modelo;
import Sistema.Sistema;
import Vista.Operadores.VentanaOperadores.VentanaOperadores;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Clase encargada de manejar las interacciones entre el modelo y la vista VentanaOperadores.
 * @author Mauri
 */
public class ControladorVentanaOperadores {

    private final Modelo modelo;
    private final VentanaOperadores ventanaOperadores;

    /**
     * Inicializa el modelo y las ventanas de la vista. Despliega la tabla de
     * operadors. Registra los listeners para algunos componentes de las ventanas
     * de la vista.
     *
     * @param modelo Modelo de datos y operaciones con el que trabaja este controlador
     * @throws java.sql.SQLException
     */
    public ControladorVentanaOperadores(Modelo modelo) throws SQLException {
        this.modelo = modelo;
        ventanaOperadores = new VentanaOperadores();
        ventanaOperadores.setVisible(true);
        actualizarTablaOperadores("SELECT * FROM OPERADOR WHERE activo=TRUE ORDER BY id ASC;");
        registrarListenersEnVentanas();
    }

    /**
     * Obtiene los datos de la consulta pasada como argumento y despliega los
     * nombres de las columnas y los datos en la tabla de operadors.
     *
     * @param consulta
     * @throws SQLException
     */
    public void actualizarTablaOperadores(String consulta) throws SQLException {
        ResultSet resultadoConsulta = modelo.realizarConsulta(consulta);
        DefaultTableModel modeloTabla = Modelo.crearModeloTabla(resultadoConsulta);
        JTable tablaOperadores = getTablaOperadores();
        tablaOperadores.setModel(modeloTabla);
        // Ocultar columna 'ID' (índice de columna 0) y columna 'activo' (índice de columna 4):
        ocultarColumna(0, tablaOperadores);
        ocultarColumna(4, tablaOperadores);
    }
    
    /**
     * Método auxiliar que oculta la columna que está en la posición indicada de la tabla pasada como argumento.
     * @param indiceColumna Posición en la tabla, relativa a 0, de la columna que se va ocultar
     * @param tabla JTable en en el cual se va a ocultar la columna
     */
    private void ocultarColumna(int indiceColumna, JTable tabla)
    {
        TableColumn columna = tabla.getColumnModel().getColumn(indiceColumna);
        columna.setMinWidth(0);
        columna.setMaxWidth(0);
        columna.setWidth(0);
    }

    /**
     * Registra el listener del botón de cada ventana.
     */
    private void registrarListenersEnVentanas() {
        ventanaOperadores.agregarListenerBotonBuscar(new VentanaOperadoresBotonBuscarAL());
        ventanaOperadores.agregarListenerBotonAgregarOperador(new VentanaOperadoresBotonAgregarOperadorAL());
        ventanaOperadores.agregarListenerBotonEditarOperador(new VentanaOperadoresBotonEditarOperadorAL());
        ventanaOperadores.agregarListenerBotonEliminarOperador(new VentanaOperadoresBotonEliminarOperador());
    }
    
    public JTable getTablaOperadores()
    {
        return ventanaOperadores.getTablaOperadores();
    }
    
    public String getIDFilaSeleccionada()
    {
        return ventanaOperadores.getIDFila();
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Buscar' de la ventana Operadores.
     */
    public class VentanaOperadoresBotonBuscarAL implements ActionListener {

        /**
         * Acción realizada cuando se presiona el botón 'Buscar'.
         * Recupera los operadores cuyos valores de atributos coincidan
         * con los especificados en el o los campos de búsqueda.
         *
         * @param ae
         */
        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                String nroOperador, nombreOperador, consulta;

                nroOperador = ventanaOperadores.getCampoNroOperador().getText();
                nombreOperador = ventanaOperadores.getCampoNombreOperador().getText();
                consulta = "SELECT * FROM OPERADOR";

                if (!nroOperador.equals("") && nombreOperador.equals("")) {
                    consulta += " WHERE nrooperador = " + nroOperador + ";";
                } else if (nroOperador.equals("") && !nombreOperador.equals("")) {
                    consulta += " WHERE LOWER(nombre) LIKE LOWER('%" + nombreOperador + "%');";
                } else if (!nroOperador.equals("") && !nombreOperador.equals("")) {
                    consulta += " WHERE nrooperador = " + nroOperador + " AND LOWER(nombre) LIKE LOWER('%" + nombreOperador + "%');";
                }

                actualizarTablaOperadores(consulta);
            } catch (SQLException ex) {
            }
        }

    }

    public class VentanaOperadoresBotonAgregarOperadorAL implements ActionListener {

        @Override
        /**
         * Despliega la ventana de agregar operador al hacer clic en el botón
         * 'botonAgregarOperador' de 'ventanaOperadores'.
         */
        public void actionPerformed(ActionEvent ae) {
            try {
                Sistema.getControladorVentanaDatosOperador().desplegarVentanaAgregarOperador();
            } catch (SQLException ex) {
                Logger.getLogger(ControladorVentanaOperadores.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Editar operador' de 'ventanaAgregarOperador'.
     */
    public class VentanaOperadoresBotonEditarOperadorAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            JTable tablaOperadores = getTablaOperadores();
            int nroFilaSeleccionada = tablaOperadores.getSelectedRow();

            // Comprobar si hay una fila seleccionada en la tabla de operadors:
            if (nroFilaSeleccionada == -1) // Si no hay una fila seleccionada, entonces:
            {
                JOptionPane.showMessageDialog(tablaOperadores, "Seleccione un operador para poder editarlo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {   try {
                // Hay una fila seleccionada:
                Sistema.getControladorVentanaDatosOperador().desplegarVentanaEditarOperador();
                } catch (SQLException ex) {
                    Logger.getLogger(ControladorVentanaOperadores.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Eliminar operador' de 'ventanaOperadores'.
     */
    public class VentanaOperadoresBotonEliminarOperador implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            JTable tablaOperadores = getTablaOperadores();
            int nroFilaSeleccionada = tablaOperadores.getSelectedRow();

            // Comprobar si hay una fila seleccionada en la tabla de operadors:
            if (nroFilaSeleccionada == -1) // Si no hay una fila seleccionada, entonces:
            {
                JOptionPane.showMessageDialog(tablaOperadores, "Seleccione el operador que quiere eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {
                // Recuperación del valor de 'ID' de la fila actualmente seleccionada:
                String ID = ventanaOperadores.getIDFila();

                try {
                    modelo.ejecutarSentencia("UPDATE operador SET activo=FALSE WHERE id = " + ID + ";");
                    actualizarTablaOperadores("SELECT * FROM operador WHERE activo=TRUE ORDER BY id ASC;");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(tablaOperadores, "No se pudo eliminar al operador seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
