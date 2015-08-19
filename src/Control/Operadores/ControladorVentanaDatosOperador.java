package Control.Operadores;

import Modelo.Modelo;
import Sistema.Sistema;
import Vista.Operadores.VentanaDatosOperador.VentanaDatosOperador;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * Clase encargada de manejar las interacciones entre el modelo y la vista
 * VentanaDatosOperador.
 *
 * @author Mauri
 */
public class ControladorVentanaDatosOperador {

    private final Modelo modelo;
    private final VentanaDatosOperador ventanaAgregarOperador;
    private final VentanaDatosOperador ventanaEditarOperador;

    /**
     * Inicializa el modelo y las ventanas de la vista. Despliega la tabla de
     * operadores. Registra los listeners para los componentes de las ventana.
     *
     * @param modelo
     * @throws java.sql.SQLException
     */
    public ControladorVentanaDatosOperador(Modelo modelo) throws SQLException {
        this.modelo = modelo;
        ventanaAgregarOperador = new VentanaDatosOperador();
        ventanaEditarOperador = new VentanaDatosOperador();
        registrarListeners();
    }

    /**
     * Actualiza la tabla de operadores.
     *
     * @throws SQLException
     */
    private void actualizarVentanaOperadores() throws SQLException {
        Sistema.getControladorVentanaOperadores().actualizarTablaOperadores("SELECT * FROM operador WHERE activo=TRUE ORDER BY id ASC;");
    }

    /**
     * Hace visible a la ventana y le asigna el título de la misma.
     *
     * @throws SQLException
     */
    public void desplegarVentanaAgregarOperador() throws SQLException {
        ventanaAgregarOperador.setVisible(true);
        ventanaAgregarOperador.setTitle("Agregar operador");
    }

    /**
     * Hace visible a la ventana y le asigna el título de la misma. Recupera y
     * muestra los valores de los campos del operador seleccionado, para
     * permitir su edición.
     *
     * @throws SQLException
     */
    public void desplegarVentanaEditarOperador() throws SQLException {
        ventanaEditarOperador.setVisible(true);
        ventanaEditarOperador.setTitle("Editar datos del operador seleccionado");

        // Diálogo entre este controlador y el de la ventana de operadores, donde se solicita la tabla de operadores:
        JTable tablaOperadores = Sistema.getControladorVentanaOperadores().getTablaOperadores();
        int nroFilaSeleccionada = tablaOperadores.getSelectedRow();

        // Obtener el valor de cada columna (índice N) del operador seleccionado:
        // Columna en la posición 0 es 'ID' y está oculta...
        Object nroOperador = tablaOperadores.getValueAt(nroFilaSeleccionada, 1);
        Object nombre = tablaOperadores.getValueAt(nroFilaSeleccionada, 2);
        Object apellido = tablaOperadores.getValueAt(nroFilaSeleccionada, 3);
        // Columna en la posición 4 es 'activo' y está oculta...

        //Desplegar los valores obtenidos en los JTextField's que corresponden:
        ventanaEditarOperador.getCampoNroOperador().setText(nroOperador.toString());
        ventanaEditarOperador.getCampoApellido().setText(apellido.toString());
        ventanaEditarOperador.getCampoNombre().setText(nombre.toString());
    }

    /**
     * Registra los listeners en su respectivo componente de cada ventana.
     */
    private void registrarListeners() {
        ventanaAgregarOperador.agregarListenerBotonGuardarNuevoOperador(new VentanaAgregarOperadorBotonGuardarAL());
        ventanaEditarOperador.agregarListenerBotonGuardarDatosModificados(new VentanaEditarOperadorBotonGuardarAL());

    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Guardar' de 'ventanaAgregarOperador'.
     */
    public class VentanaAgregarOperadorBotonGuardarAL implements ActionListener {

        @Override
        /**
         * Acción realizada cuando se presiona el botón 'Guardar' en la ventana
         * 'ventanaAgregarOperador'. Recupera los valores ingresados en los
         * campos e inserta dichos valores en la base, representando con una
         * tupla al nuevo operador.
         *
         * @param ae
         */
        public void actionPerformed(ActionEvent ae) {

            // Recuperación del texto ingresado en los campos
            String nroOperador = ventanaAgregarOperador.getCampoNroOperador().getText();
            String nombre = ventanaAgregarOperador.getCampoNombre().getText();
            String apellido = ventanaAgregarOperador.getCampoApellido().getText();

            // Formateo de valores para hacer el INSERT en la base
            nroOperador = nroOperador.equals("") ? null : nroOperador;
            nombre = nombre.equals("") ? null : nombre;
            apellido = apellido.equals("") ? null : apellido;
            
            if (nroOperador != null && nombre != null) {
                String sentenciaINSERT = "INSERT INTO operador (NroOperador, Nombre, Apellido, Activo) VALUES ("
                        + nroOperador + ", '" + nombre + "', '" + apellido + "', TRUE);";
                try {
                    // Inserción del nuevo operador:
                    modelo.ejecutarSentencia(sentenciaINSERT);
                    actualizarVentanaOperadores();
                } catch (SQLException ex) {
                }
                // Limpiar campos de la ventana luego de agregar el operador, por si se agrega otro operador luego:
                ventanaAgregarOperador.getCampoNroOperador().setText("");
                ventanaAgregarOperador.getCampoNombre().setText("");
                ventanaAgregarOperador.getCampoApellido().setText("");
                // Cerrar la ventana:
                ventanaAgregarOperador.dispose();
            } else {
                JOptionPane.showMessageDialog(ventanaAgregarOperador, "Debe ingresar al menos el número y nombre del operador.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Guardar operador' de 'ventanaEditarOperador'.
     */
    public class VentanaEditarOperadorBotonGuardarAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            // Recuperación del valor de 'ID' de la fila (operador) actualmente seleccionada:
            String ID = Sistema.getControladorVentanaOperadores().getIDFilaSeleccionada();

            // Recuperación del texto ingresado en los campos:
            String nroOperador = ventanaEditarOperador.getCampoNroOperador().getText();
            String nombre = ventanaEditarOperador.getCampoNombre().getText();
            String apellido = ventanaEditarOperador.getCampoApellido().getText();
            
            String sentenciaUPDATE
                    = "UPDATE operador SET "
                    + "nrooperador=" + nroOperador + ", nombre='" + nombre + "', apellido ='" + apellido + "' "
                    + "WHERE id=" + ID + ";";
            try {
                // Actualización de los datos del operador:
                modelo.ejecutarSentencia(sentenciaUPDATE);
                actualizarVentanaOperadores();
                ventanaEditarOperador.dispose();
            } catch (Exception e) {
            }
        }
    }
}
