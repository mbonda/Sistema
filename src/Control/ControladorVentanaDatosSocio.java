package Control;

import Vista.VentanaDatosSocio.VentanaDatosSocio;
import Modelo.Modelo;
import Sistema.Sistema;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * Clase encargada de manejar las interacciones entre el modelo y la vista
 * VentanaDatosSocio.
 *
 * @author Mauri
 */
public class ControladorVentanaDatosSocio {

    private final Modelo modelo;
    private final VentanaDatosSocio ventanaAgregarSocio;
    private final VentanaDatosSocio ventanaEditarSocio;

    /**
     * Inicializa el modelo y las ventanas de la vista. Despliega la tabla de
     * socios. Registra los listeners para algunos componentes de las ventanas
     * de la vista.
     *
     * @param modelo
     * @throws java.sql.SQLException
     */
    public ControladorVentanaDatosSocio(Modelo modelo) throws SQLException {
        this.modelo = modelo;
        ventanaAgregarSocio = new VentanaDatosSocio();
        ventanaEditarSocio = new VentanaDatosSocio();
        registrarListeners();
    }

    /**
     * Comunica los cambios realizados en la base (INSERT y/o UPDATE de socios)
     * a la ventana de socios.
     *
     * @throws SQLException
     */
    private void actualizarVentanaSocios() throws SQLException {
        Sistema.getControladorVentanaSocios().actualizarTablaSocios("SELECT * FROM socio WHERE activo=TRUE ORDER BY id ASC;");
    }

    public void desplegarVentanaAgregarSocio() throws SQLException {
        ventanaAgregarSocio.setVisible(true);
        ventanaAgregarSocio.setTitle("Agregar socio");
        rellenarComboBoxCobrador(ventanaAgregarSocio);
        rellenarComboBoxLocalidades(ventanaAgregarSocio, "Uruguay");

    }

    public void desplegarVentanaEditarSocio() throws SQLException {
        ventanaEditarSocio.setVisible(true);
        ventanaEditarSocio.setTitle("Editar datos del socio seleccionado");
        rellenarComboBoxCobrador(ventanaEditarSocio);

        JTable tablaSocios = Sistema.getControladorVentanaSocios().getTablaSocios();
        int nroFilaSeleccionada = tablaSocios.getSelectedRow();

        // Obtener el valor de cada columna (índice N) del socio seleccionado:
        // Columna en la posición 0 es 'ID' y está oculta...
        Object nombre = tablaSocios.getValueAt(nroFilaSeleccionada, 1);
        Object RUT = tablaSocios.getValueAt(nroFilaSeleccionada, 2);
        Object nroSocio = tablaSocios.getValueAt(nroFilaSeleccionada, 3);
        Object direccion = tablaSocios.getValueAt(nroFilaSeleccionada, 4);
        Object telefono = tablaSocios.getValueAt(nroFilaSeleccionada, 5);
        // Columna en la posición 6 es 'activo' y está oculta...
        Object facturacion = tablaSocios.getValueAt(nroFilaSeleccionada, 7);
        Object cobrador = tablaSocios.getValueAt(nroFilaSeleccionada, 8);
        Object localidad = tablaSocios.getValueAt(nroFilaSeleccionada, 9);

        //Desplegar los valores obtenidos en los JTextField's y ComboBoxes correspondientes:
        ventanaEditarSocio.getCampoNombre().setText(nombre.toString());
        ventanaEditarSocio.getCampoRUT().setText(RUT.toString());
        ventanaEditarSocio.getCampoNroSocio().setText(nroSocio.toString());
        ventanaEditarSocio.getCampoDireccion().setText(direccion.toString());
        ventanaEditarSocio.getCampoTelefono().setText(telefono.toString());
        ventanaEditarSocio.getCboxFacturacion().setSelectedItem(facturacion.toString());
        ventanaEditarSocio.getCboxCobrador().setSelectedItem(cobrador.toString());
        // Para mostrar el país es necesario consultar la base:
        ResultSet resultadoConsulta = modelo.realizarConsulta("SELECT Pais FROM LOCALIDAD WHERE Nombre = '" + localidad + "';");
        resultadoConsulta.next();   // Apuntar el cursor del ResultSet a la primera (y única) fila del resultado de la consulta
        String pais = (String) resultadoConsulta.getObject(1);  // Obtiene el valor de la primer columna de la fila apuntada por el cursor del ResultSet
        // Seleccionar país y localidad correspondientes en los JComboBoxes:
        ventanaEditarSocio.getCboxPaises().setSelectedItem(pais);
        rellenarComboBoxLocalidades(ventanaEditarSocio, pais);
        ventanaEditarSocio.getCboxLocalidades().setSelectedItem(localidad.toString());
    }

    /**
     * Método auxiliar que rellena el JComboBox con todos los números de
     * cobrador de la tabla COBRADOR.
     *
     * @param ventana
     */
    private void rellenarComboBoxCobrador(VentanaDatosSocio ventana) {
        try {
            ResultSet numerosCobrador = modelo.realizarConsulta("SELECT nrocobrador FROM cobrador ORDER BY id ASC");
            while (numerosCobrador.next()) {
                String nroCobrador = numerosCobrador.getString(1);
                if (nroCobrador != null) {
                    nroCobrador = nroCobrador.trim();
                }
                ventana.getCboxCobrador().addItem(nroCobrador);
            }
            numerosCobrador.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * Método auxiliar que rellena el JComboBox con todos los números de
     * cobrador de la tabla COBRADOR.
     *
     * @param ventana
     */
    private void rellenarComboBoxLocalidades(VentanaDatosSocio ventana, String pais) throws SQLException {
        // Vaciar JComboBox antes de agregar nuevos items:
        ventana.getCboxLocalidades().removeAllItems();
        
        String sql = "SELECT Nombre FROM LOCALIDAD WHERE Pais = '" + pais + "' ORDER BY Nombre ASC;";
        ResultSet localidades = modelo.realizarConsulta(sql);
        while (localidades.next()) {
            String localidad = localidades.getString(1); // 1 es la posición de la primer columna de la tabla: Nombre de la localidad
            if (localidad != null) {
                localidad = localidad.trim();
            }
            ventana.getCboxLocalidades().addItem(localidad);
        }
    }

    /**
     * Registra el listener del botón de cada ventana.
     */
    private void registrarListeners() {
        ventanaAgregarSocio.agregarListenerBotonGuardarNuevoSocio(new VentanaAgregarSocioBotonGuardarAL());
        ventanaEditarSocio.agregarListenerBotonGuardarDatosModificados(new VentanaEditarSocioBotonGuardarAL());
        ventanaAgregarSocio.agregarListenercboxLocalidades(new ItemChangeListener());
        ventanaEditarSocio.agregarListenercboxLocalidades(new ItemChangeListener());
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Guardar' de 'ventanaAgregarSocio'.
     */
    public class VentanaAgregarSocioBotonGuardarAL implements ActionListener {

        @Override
        /**
         * Acción realizada cuando se presiona el botón 'Guardar' en la ventana
         * 'ventanaAgregarSocio'. Recupera los valores ingresados en los campos
         * e inserta dichos valores en la base, representando una tupla
         * correspondiente al nuevo socio.
         *
         * @param ae
         */
        public void actionPerformed(ActionEvent ae) {

            // Recuperación del texto ingresado en los campos
            String nroSocio = ventanaAgregarSocio.getCampoNroSocio().getText();
            String nombre = ventanaAgregarSocio.getCampoNombre().getText();
            String RUT = ventanaAgregarSocio.getCampoRUT().getText();
            String telefono = ventanaAgregarSocio.getCampoTelefono().getText();
            String direccion = ventanaAgregarSocio.getCampoDireccion().getText();
            String localidad = (String) ventanaAgregarSocio.getCboxLocalidades().getSelectedItem();
            String nroCobrador = (String) ventanaAgregarSocio.getCboxCobrador().getSelectedItem();
            String facturacion = (String) ventanaAgregarSocio.getCboxFacturacion().getSelectedItem();

            // Formateo de valores para hacer el INSERT en la base
            nroSocio = nroSocio.equals("") ? null : nroSocio;
            nombre = nombre.equals("") ? null : nombre;
            RUT = RUT.equals("") ? null : RUT;
            telefono = telefono.equals("") ? null : telefono;
            direccion = direccion.equals("") ? null : direccion;
            localidad = localidad.equals("") ? null : localidad;
            nroCobrador = nroCobrador.equals("") ? null : nroCobrador;
            facturacion = facturacion.equals("") ? null : facturacion;

            if (nroSocio != null && nombre != null) {
                String sentenciaINSERT = "INSERT INTO socio (Nombre, RUT, NroSocio, Direccion, Telefono, Activo, Facturacion, Cobrador, Localidad) VALUES ("
                        + "'" + nombre + "', '" + RUT + "', " + nroSocio + ", '" + direccion + "', '" + telefono + "', TRUE, '" + facturacion + "', " + nroCobrador + ", '" + localidad + "');";
                try {
                    modelo.ejecutarSentencia(sentenciaINSERT);
                    actualizarVentanaSocios();
                } catch (SQLException ex) {
                }
                // Limpiar campos de la ventana luego de agregar el socio:
                ventanaAgregarSocio.getCampoNroSocio().setText("");
                ventanaAgregarSocio.getCampoNombre().setText("");
                ventanaAgregarSocio.getCampoRUT().setText("");
                ventanaAgregarSocio.getCampoTelefono().setText("");
                ventanaAgregarSocio.getCampoDireccion().setText("");
                ventanaAgregarSocio.getCboxLocalidades().setSelectedItem(null);
                rellenarComboBoxCobrador(ventanaEditarSocio);

                // Cerrar la ventana:
                ventanaAgregarSocio.dispose();
            } else {
                JOptionPane.showMessageDialog(ventanaAgregarSocio, "Debe ingresar al menos el número y nombre del socio.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Clase manejadora de eventos, cuya instancia se va registrar como un
     * Listener en el botón 'Guardar socio' de 'ventanaEditarSocio'.
     */
    public class VentanaEditarSocioBotonGuardarAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            // Recuperación del valor de 'ID' de la fila actualmente seleccionada:
            String ID = Sistema.getControladorVentanaSocios().getIDFilaSeleccionada();

            // Recuperación del texto ingresado en los campos:
            String nroSocio = ventanaEditarSocio.getCampoNroSocio().getText();
            String nombre = ventanaEditarSocio.getCampoNombre().getText();
            String RUT = ventanaEditarSocio.getCampoRUT().getText();
            String telefono = ventanaEditarSocio.getCampoTelefono().getText();
            String direccion = ventanaEditarSocio.getCampoDireccion().getText();
            String localidad = (String) ventanaEditarSocio.getCboxLocalidades().getSelectedItem();
            String nroCobrador = (String) ventanaEditarSocio.getCboxCobrador().getSelectedItem();
            String facturacion = (String) ventanaEditarSocio.getCboxFacturacion().getSelectedItem();

            String sentenciaUPDATE
                    = "UPDATE socio SET "
                    + "nrosocio=" + nroSocio + ", nombre='" + nombre + "', RUT ='" + RUT + "', telefono=" + telefono + ", direccion='" + direccion + "', localidad='" + localidad + "', cobrador=" + nroCobrador + ", facturacion='" + facturacion + "' "
                    + "WHERE id=" + ID + ";";
            try {
                modelo.ejecutarSentencia(sentenciaUPDATE);
                actualizarVentanaSocios();
                ventanaEditarSocio.dispose();   // Tras la actualización de los datos del socio, la ventana se cierra
            } catch (Exception e) {
            }
        }
    }

    /**
     * Clase manejadora de eventos de selección de items en un componente.
     */
    class ItemChangeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            String paisSeleccionado = (String) ventanaAgregarSocio.getCboxPaises().getSelectedItem();
            try {
                rellenarComboBoxLocalidades(ventanaAgregarSocio, paisSeleccionado);
            } catch (SQLException ex) {
                Logger.getLogger(ControladorVentanaDatosSocio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
