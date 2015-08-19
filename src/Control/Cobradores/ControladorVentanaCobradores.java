package Control.Cobradores;


import Vista.Cobradores.VentanaCobradores.VentanaCobradores;
import Modelo.Modelo;
import Sistema.Sistema;
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
 * Clase encargada de manejar las interacciones entre el modelo y la vista VentanaCobradores.
 * @author Mauri
 */
public class ControladorVentanaCobradores {

    private final Modelo modelo;
    private final VentanaCobradores ventanaCobradores;

    /**
     * Inicializa el modelo y las ventanas de la vista. Despliega la tabla de
     * cobradores. Registra los listeners para algunos componentes de las ventanas
     * de la vista.
     *
     * @param modelo Modelo de datos y operaciones con el que trabaja este controlador
     * @throws java.sql.SQLException
     */
    public ControladorVentanaCobradores(Modelo modelo) throws SQLException {
        this.modelo = modelo;
        ventanaCobradores = new VentanaCobradores();
        ventanaCobradores.setVisible(true);
        actualizarTablaCobradores("SELECT * FROM COBRADOR WHERE activo=TRUE ORDER BY id ASC;");
        registrarListenersEnVentanas();
    }

    /**
     * Obtiene los datos de la consulta pasada como argumento y despliega los
     * nombres de las columnas y los datos en la tabla de cobradors.
     *
     * @param consulta
     * @throws SQLException
     */
    public void actualizarTablaCobradores(String consulta) throws SQLException {
        ResultSet resultadoConsulta = modelo.realizarConsulta(consulta);
        DefaultTableModel modeloTabla = Modelo.crearModeloTabla(resultadoConsulta);
        JTable tablaCobradores = getTablaCobradores();
        tablaCobradores.setModel(modeloTabla);
        // Ocultar columna 'ID' (índice de columna 0) y columna 'activo' (índice de columna 4):
        ocultarColumna(0, tablaCobradores);
        ocultarColumna(4, tablaCobradores);
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
        ventanaCobradores.agregarListenerBotonBuscar(new VentanaCobradoresBotonBuscarAL());
        ventanaCobradores.agregarListenerBotonAgregarCobrador(new VentanaCobradoresBotonAgregarCobradorAL());
        ventanaCobradores.agregarListenerBotonEditarCobrador(new VentanaCobradoresBotonEditarCobradorAL());
        ventanaCobradores.agregarListenerBotonEliminarCobrador(new VentanaCobradoresBotonEliminarCobrador());
    }
    
    public JTable getTablaCobradores()
    {
        return ventanaCobradores.getTablaCobradores();
    }
    
    public String getIDFilaSeleccionada()
    {
        return ventanaCobradores.getIDFila();
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Buscar' de la ventana Cobradores.
     */
    public class VentanaCobradoresBotonBuscarAL implements ActionListener {

        /**
         * Acción realizada cuando se presiona el botón 'Buscar'.
         * Recupera los cobradores cuyos valores de atributos coincidan
         * con los especificados en el o los campos de búsqueda.
         *
         * @param ae
         */
        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                String nroCobrador, nombreCobrador, consulta;

                nroCobrador = ventanaCobradores.getCampoNroCobrador().getText();
                nombreCobrador = ventanaCobradores.getCampoNombreCobrador().getText();
                consulta = "SELECT * FROM COBRADOR";

                if (!nroCobrador.equals("") && nombreCobrador.equals("")) {
                    consulta += " WHERE nrocobrador = " + nroCobrador + ";";
                } else if (nroCobrador.equals("") && !nombreCobrador.equals("")) {
                    consulta += " WHERE LOWER(nombre) LIKE LOWER('%" + nombreCobrador + "%');";
                } else if (!nroCobrador.equals("") && !nombreCobrador.equals("")) {
                    consulta += " WHERE nrocobrador = " + nroCobrador + " AND LOWER(nombre) LIKE LOWER('%" + nombreCobrador + "%');";
                }

                actualizarTablaCobradores(consulta);
            } catch (SQLException ex) {
            }
        }

    }

    public class VentanaCobradoresBotonAgregarCobradorAL implements ActionListener {

        @Override
        /**
         * Despliega la ventana de agregar cobrador al hacer clic en el botón
         * 'botonAgregarCobrador' de 'ventanaCobradores'.
         */
        public void actionPerformed(ActionEvent ae) {
            try {
                Sistema.getControladorVentanaDatosCobrador().desplegarVentanaAgregarCobrador();
            } catch (SQLException ex) {
                Logger.getLogger(ControladorVentanaCobradores.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Editar cobrador' de 'ventanaAgregarCobrador'.
     */
    public class VentanaCobradoresBotonEditarCobradorAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            JTable tablaCobradores = getTablaCobradores();
            int nroFilaSeleccionada = tablaCobradores.getSelectedRow();

            // Comprobar si hay una fila seleccionada en la tabla de cobradors:
            if (nroFilaSeleccionada == -1) // Si no hay una fila seleccionada, entonces:
            {
                JOptionPane.showMessageDialog(tablaCobradores, "Seleccione un cobrador para poder editarlo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {   try {
                // Hay una fila seleccionada:
                Sistema.getControladorVentanaDatosCobrador().desplegarVentanaEditarCobrador();
                } catch (SQLException ex) {
                    Logger.getLogger(ControladorVentanaCobradores.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Eliminar cobrador' de 'ventanaCobradores'.
     */
    public class VentanaCobradoresBotonEliminarCobrador implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            JTable tablaCobradores = getTablaCobradores();
            int nroFilaSeleccionada = tablaCobradores.getSelectedRow();

            // Comprobar si hay una fila seleccionada en la tabla de cobradors:
            if (nroFilaSeleccionada == -1) // Si no hay una fila seleccionada, entonces:
            {
                JOptionPane.showMessageDialog(tablaCobradores, "Seleccione el cobrador que quiere eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {
                // Recuperación del valor de 'ID' de la fila actualmente seleccionada:
                String ID = ventanaCobradores.getIDFila();

                try {
                    modelo.ejecutarSentencia("UPDATE cobrador SET activo=FALSE WHERE id = " + ID + ";");
                    actualizarTablaCobradores("SELECT * FROM cobrador WHERE activo=TRUE ORDER BY id ASC;");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(tablaCobradores, "No se pudo eliminar al cobrador seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
