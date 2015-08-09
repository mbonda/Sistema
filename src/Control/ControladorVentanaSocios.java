package Control;

import ControladoresVistas.ControladorVentanaSocios.VentanaSocios;
import Modelo.Modelo;
import Sistema.Sistema;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Clase encargada de manejar las interacciones entre el modelo y la vista VentanaSocios.
 * @author Mauri
 */
public class ControladorVentanaSocios {

    private final Modelo modelo;
    private final VentanaSocios ventanaSocios;

    /**
     * Inicializa el modelo y las ventanas de la vista. Despliega la tabla de
     * socios. Registra los listeners para algunos componentes de las ventanas
     * de la vista.
     *
     * @param modelo
     * @throws java.sql.SQLException
     */
    public ControladorVentanaSocios(Modelo modelo) throws SQLException {
        this.modelo = modelo;
        ventanaSocios = new VentanaSocios();
        actualizarTablaSocios("SELECT * FROM socio WHERE activo=TRUE ORDER BY id ASC;");
        registrarListenersEnVentanas();
    }

    /**
     * Obtiene los datos de la consulta pasada como argumento y despliega los
     * nombres de las columnas y los datos en la tabla de socios.
     *
     * @param consulta
     * @throws SQLException
     */
    public void actualizarTablaSocios(String consulta) throws SQLException {
        ResultSet resultadoConsulta = modelo.realizarConsulta(consulta);
        DefaultTableModel modeloTabla = Modelo.crearModeloTabla(resultadoConsulta);
        JTable tablaSocios = getTablaSocios();
        tablaSocios.setModel(modeloTabla);
        // Ocultar columna 'ID' (índice de columna 0) y columna 'activo' (índice de columna 7):
        ocultarColumna(0, tablaSocios);
        ocultarColumna(7, tablaSocios);
    }
    
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
        ventanaSocios.agregarListenerBotonBuscar(new VentanaSociosBotonBuscarAL());
        ventanaSocios.agregarListenerBotonAgregarSocio(new VentanaSociosBotonAgregarSocioAL());
        ventanaSocios.agregarListenerBotonEditarSocio(new VentanaSociosBotonEditarSocioAL());
        ventanaSocios.agregarListenerBotonEliminarSocio(new VentanaSociosBotonEliminarSocio());
    }
    
    public JTable getTablaSocios()
    {
        return ventanaSocios.getTablaSocios();
    }
    
    public String getIDFilaSeleccionada()
    {
        return ventanaSocios.getIDFila();
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Buscar' de la ventana Socios.
     */
    public class VentanaSociosBotonBuscarAL implements ActionListener {

        /**
         * Acción realizada cuando se presiona el botón 'Buscar' en la ventana
         * de socios. Recupera los socios cuyos valores de atributos coincidan
         * con los especificados en el o los campos de búsqueda.
         *
         * @param ae
         */
        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                String nroSocio, nombreSocio, consulta;

                nroSocio = ventanaSocios.getCampoNroSocio().getText();
                nombreSocio = ventanaSocios.getCampoNombreSocio().getText();
                consulta = "SELECT * FROM socio";

                if (!nroSocio.equals("") && nombreSocio.equals("")) {
                    consulta += " WHERE numero = " + nroSocio + ";";
                } else if (nroSocio.equals("") && !nombreSocio.equals("")) {
                    consulta += " WHERE LOWER(nombre) LIKE LOWER('%" + nombreSocio + "%');";
                } else if (!nroSocio.equals("") && !nombreSocio.equals("")) {
                    consulta += " WHERE numero = " + nroSocio + " AND LOWER(nombre) LIKE LOWER('%" + nombreSocio + "%');";
                }

                actualizarTablaSocios(consulta);
            } catch (SQLException ex) {
            }
        }

    }

    public class VentanaSociosBotonAgregarSocioAL implements ActionListener {

        @Override
        /**
         * Despliega la ventana de agregar socio al hacer clic en el botón
         * 'botonAgregarSocio' de 'ventanaSocios'.
         */
        public void actionPerformed(ActionEvent ae) {
            Sistema.getControladorVentanaDatosSocio().desplegarVentanaAgregarSocio();
        }
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Editar socio' de 'ventanaAgregarSocio'.
     */
    public class VentanaSociosBotonEditarSocioAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            JTable tablaSocios = getTablaSocios();
            int nroFilaSeleccionada = tablaSocios.getSelectedRow();

            // Comprobar si hay una fila seleccionada en la tabla de socios:
            if (nroFilaSeleccionada == -1) // Si no hay una fila seleccionada, entonces:
            {
                JOptionPane.showMessageDialog(tablaSocios, "Debe seleccionar un socio para poder editarlo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {   // Hay una fila seleccionada:
                Sistema.getControladorVentanaDatosSocio().desplegarVentanaEditarSocio();
            }
        }
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Eliminar socio' de 'ventanaSocios'.
     */
    public class VentanaSociosBotonEliminarSocio implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            JTable tablaSocios = getTablaSocios();
            int nroFilaSeleccionada = tablaSocios.getSelectedRow();

            // Comprobar si hay una fila seleccionada en la tabla de socios:
            if (nroFilaSeleccionada == -1) // Si no hay una fila seleccionada, entonces:
            {
                JOptionPane.showMessageDialog(tablaSocios, "Debe seleccionar el socio que va a eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {
                // Recuperación del valor de 'ID' de la fila actualmente seleccionada:
                String ID = ventanaSocios.getIDFila();

                try {
                    modelo.ejecutarSentencia("UPDATE socio SET activo=FALSE WHERE id = " + ID + ";");
                    actualizarTablaSocios("SELECT * FROM socio WHERE activo=TRUE ORDER BY id ASC;");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(tablaSocios, "No se pudo eliminar al socio seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
