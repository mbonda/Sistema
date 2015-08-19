package Control.Cobradores;

import Vista.Cobradores.VentanaDatosCobrador.VentanaDatosCobrador;
import Modelo.Modelo;
import Sistema.Sistema;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * Clase encargada de manejar las interacciones entre el modelo y la vista
 * VentanaDatosCobrador.
 *
 * @author Mauri
 */
public class ControladorVentanaDatosCobrador {

    private final Modelo modelo;
    private final VentanaDatosCobrador ventanaAgregarCobrador;
    private final VentanaDatosCobrador ventanaEditarCobrador;

    /**
     * Inicializa el modelo y las ventanas de la vista. Despliega la tabla de
     * cobradores. Registra los listeners para los componentes de las ventana.
     *
     * @param modelo
     * @throws java.sql.SQLException
     */
    public ControladorVentanaDatosCobrador(Modelo modelo) throws SQLException {
        this.modelo = modelo;
        ventanaAgregarCobrador = new VentanaDatosCobrador();
        ventanaEditarCobrador = new VentanaDatosCobrador();
        registrarListeners();
    }

    /**
     * Actualiza la tabla de cobradores.
     *
     * @throws SQLException
     */
    private void actualizarVentanaCobradores() throws SQLException {
        Sistema.getControladorVentanaCobradores().actualizarTablaCobradores("SELECT * FROM cobrador WHERE activo=TRUE ORDER BY id ASC;");
    }

    /**
     * Hace visible a la ventana y le asigna el título de la misma.
     *
     * @throws SQLException
     */
    public void desplegarVentanaAgregarCobrador() throws SQLException {
        ventanaAgregarCobrador.setVisible(true);
        ventanaAgregarCobrador.setTitle("Agregar cobrador");
    }

    /**
     * Hace visible a la ventana y le asigna el título de la misma. Recupera y
     * muestra los valores de los campos del cobrador seleccionado, para
     * permitir su edición.
     *
     * @throws SQLException
     */
    public void desplegarVentanaEditarCobrador() throws SQLException {
        ventanaEditarCobrador.setVisible(true);
        ventanaEditarCobrador.setTitle("Editar datos del cobrador seleccionado");

        // Diálogo entre este controlador y el de la ventana de cobradores, donde se solicita la tabla de cobradores:
        JTable tablaCobradores = Sistema.getControladorVentanaCobradores().getTablaCobradores();
        int nroFilaSeleccionada = tablaCobradores.getSelectedRow();

        // Obtener el valor de cada columna (índice N) del cobrador seleccionado:
        // Columna en la posición 0 es 'ID' y está oculta...
        Object nroCobrador = tablaCobradores.getValueAt(nroFilaSeleccionada, 1);
        Object nombre = tablaCobradores.getValueAt(nroFilaSeleccionada, 2);
        Object apellido = tablaCobradores.getValueAt(nroFilaSeleccionada, 3);
        // Columna en la posición 4 es 'activo' y está oculta...

        //Desplegar los valores obtenidos en los JTextField's que corresponden:
        ventanaEditarCobrador.getCampoNroCobrador().setText(nroCobrador.toString());
        ventanaEditarCobrador.getCampoApellido().setText(apellido.toString());
        ventanaEditarCobrador.getCampoNombre().setText(nombre.toString());
    }

    /**
     * Registra los listeners en su respectivo componente de cada ventana.
     */
    private void registrarListeners() {
        ventanaAgregarCobrador.agregarListenerBotonGuardarNuevoCobrador(new VentanaAgregarCobradorBotonGuardarAL());
        ventanaEditarCobrador.agregarListenerBotonGuardarDatosModificados(new VentanaEditarCobradorBotonGuardarAL());

    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Guardar' de 'ventanaAgregarCobrador'.
     */
    public class VentanaAgregarCobradorBotonGuardarAL implements ActionListener {

        @Override
        /**
         * Acción realizada cuando se presiona el botón 'Guardar' en la ventana
         * 'ventanaAgregarCobrador'. Recupera los valores ingresados en los
         * campos e inserta dichos valores en la base, representando con una
         * tupla al nuevo cobrador.
         *
         * @param ae
         */
        public void actionPerformed(ActionEvent ae) {

            // Recuperación del texto ingresado en los campos
            String nroCobrador = ventanaAgregarCobrador.getCampoNroCobrador().getText();
            String nombre = ventanaAgregarCobrador.getCampoNombre().getText();
            String apellido = ventanaAgregarCobrador.getCampoApellido().getText();

            // Formateo de valores para hacer el INSERT en la base
            nroCobrador = nroCobrador.equals("") ? null : nroCobrador;
            nombre = nombre.equals("") ? null : nombre;
            apellido = apellido.equals("") ? null : apellido;
            
            if (nroCobrador != null && nombre != null) {
                String sentenciaINSERT = "INSERT INTO cobrador (NroCobrador, Nombre, Apellido, Activo) VALUES ("
                        + nroCobrador + ", '" + nombre + "', '" + apellido + "', TRUE);";
                try {
                    // Inserción del nuevo cobrador:
                    modelo.ejecutarSentencia(sentenciaINSERT);
                    actualizarVentanaCobradores();
                } catch (SQLException ex) {
                }
                // Limpiar campos de la ventana luego de agregar el cobrador, por si se agrega otro cobrador luego:
                ventanaAgregarCobrador.getCampoNroCobrador().setText("");
                ventanaAgregarCobrador.getCampoNombre().setText("");
                ventanaAgregarCobrador.getCampoApellido().setText("");
                // Cerrar la ventana:
                ventanaAgregarCobrador.dispose();
            } else {
                JOptionPane.showMessageDialog(ventanaAgregarCobrador, "Debe ingresar al menos el número y nombre del cobrador.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Guardar cobrador' de 'ventanaEditarCobrador'.
     */
    public class VentanaEditarCobradorBotonGuardarAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            // Recuperación del valor de 'ID' de la fila (cobrador) actualmente seleccionada:
            String ID = Sistema.getControladorVentanaCobradores().getIDFilaSeleccionada();

            // Recuperación del texto ingresado en los campos:
            String nroCobrador = ventanaEditarCobrador.getCampoNroCobrador().getText();
            String nombre = ventanaEditarCobrador.getCampoNombre().getText();
            String apellido = ventanaEditarCobrador.getCampoApellido().getText();
            
            String sentenciaUPDATE
                    = "UPDATE cobrador SET "
                    + "nrocobrador=" + nroCobrador + ", nombre='" + nombre + "', apellido ='" + apellido + "' "
                    + "WHERE id=" + ID + ";";
            try {
                // Actualización de los datos del cobrador:
                modelo.ejecutarSentencia(sentenciaUPDATE);
                actualizarVentanaCobradores();
                ventanaEditarCobrador.dispose();
            } catch (Exception e) {
            }
        }
    }
}
