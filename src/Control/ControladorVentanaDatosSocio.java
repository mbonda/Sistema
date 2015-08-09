package Control;

import ControladoresVistas.ControladorVentanaDatosSocio.VentanaDatosSocio;
import Modelo.Modelo;
import Sistema.Sistema;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * Clase encargada de manejar las interacciones entre el modelo (BaseDeDatos) y
 * la vista (VentanaSocios y VentanaAgregarSocio).
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

    public void desplegarVentanaAgregarSocio() {
        ventanaAgregarSocio.setVisible(true);
        ventanaAgregarSocio.setTitle("Agregar socio");
        rellenarComboBoxCobrador(ventanaAgregarSocio);
    }

    public void desplegarVentanaEditarSocio() {
        ventanaEditarSocio.setVisible(true);
        ventanaEditarSocio.setTitle("Editar datos de socio");
        rellenarComboBoxCobrador(ventanaEditarSocio);

        JTable tablaSocios = Sistema.getControladorVentanaSocios().getTablaSocios();
        int nroFilaSeleccionada = tablaSocios.getSelectedRow();

        // Obtener el valor de cada columna (índice N) del socio seleccionado:
        // Columna en la posición 1 es 'ID' y está oculta...
        Object nombre = tablaSocios.getValueAt(nroFilaSeleccionada, 1);
        Object RUT = tablaSocios.getValueAt(nroFilaSeleccionada, 2);
        Object nroSocio = tablaSocios.getValueAt(nroFilaSeleccionada, 3);
        Object direccion = tablaSocios.getValueAt(nroFilaSeleccionada, 4);
        Object telefono = tablaSocios.getValueAt(nroFilaSeleccionada, 5);
        Object moneda = tablaSocios.getValueAt(nroFilaSeleccionada, 6);
        // Columna en la posición 7 es 'activo' y está oculta...
        Object facturacion = tablaSocios.getValueAt(nroFilaSeleccionada, 8);
        Object cobrador = tablaSocios.getValueAt(nroFilaSeleccionada, 9);
        Object localidad = tablaSocios.getValueAt(nroFilaSeleccionada, 10);

        //Desplegar los valores obtenidos en los JTextField's correspondientes:
        ventanaEditarSocio.getCampoNombre().setText(nombre.toString());
        ventanaEditarSocio.getCampoRUT().setText(RUT.toString());
        ventanaEditarSocio.getCampoNroSocio().setText(nroSocio.toString());
        ventanaEditarSocio.getCampoDireccion().setText(direccion.toString());
        ventanaEditarSocio.getCampoTelefono().setText(telefono.toString());
        ventanaEditarSocio.getCboxMonedas().setSelectedItem(moneda.toString());
        ventanaEditarSocio.getCboxFacturacion().setSelectedItem(facturacion.toString());
        ventanaEditarSocio.getCboxCobrador().setSelectedItem(cobrador.toString());
        ventanaEditarSocio.getCampoLocalidad().setText(localidad.toString());
    }
    
    /**
     * Método auxiliar que rellena el JComboBox con todos los números de cobrador de la tabla COBRADOR.
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
     * Registra el listener del botón de cada ventana.
     */
    private void registrarListeners() {
        ventanaAgregarSocio.agregarListenerBotonGuardarNuevoSocio(new VentanaAgregarSocioBotonGuardarAL());
        ventanaEditarSocio.agregarListenerBotonGuardarDatosModificados(new VentanaEditarSocioBotonGuardarAL());
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
            String localidad = ventanaAgregarSocio.getCampoLocalidad().getText();
            String dptoProvincia = ventanaAgregarSocio.getCampoDptoProvincia().getText();
            String pais = (String) ventanaAgregarSocio.getCboxPaises().getSelectedItem();
            String nroCobrador = (String) ventanaAgregarSocio.getCboxCobrador().getSelectedItem();
            String facturacion = (String) ventanaAgregarSocio.getCboxFacturacion().getSelectedItem();
            String moneda = (String) ventanaAgregarSocio.getCboxMonedas().getSelectedItem();

            // Formateo de valores para hacer el INSERT en la base
            nroSocio = nroSocio.equals("") ? null : nroSocio;
            nombre = nombre.equals("") ? null : nombre;
            RUT = RUT.equals("") ? null : RUT;
            telefono = telefono.equals("") ? null : telefono;
            direccion = direccion.equals("") ? null : direccion;
            localidad = localidad.equals("") ? null : localidad;
            // ¿En qué tabla insertar Dpto/Provincia y País?
            nroCobrador = nroCobrador.equals("") ? null : nroCobrador;
            facturacion = facturacion.equals("") ? null : facturacion;
            moneda = moneda.equals("") ? null : moneda;
 
            if (nroSocio != null && nombre != null) {
                String sentenciaINSERT = "INSERT INTO socio (Nombre, RUT, NroSocio, Direccion, Telefono, Moneda, Activo, Facturacion, Cobrador, Localidad) VALUES ("
                        + "'" + nombre + "', '" + RUT + "', " + nroSocio + ", '" + direccion + "', '" + telefono + "', '" + moneda + "', TRUE, '" + facturacion + "', " + nroCobrador + ", '" + localidad + "');";
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
                ventanaAgregarSocio.getCampoLocalidad().setText("");
                ventanaAgregarSocio.getCampoDptoProvincia().setText("");
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
            String localidad = ventanaEditarSocio.getCampoLocalidad().getText();
            String dptoProvincia = ventanaEditarSocio.getCampoDptoProvincia().getText();
            String pais = (String) ventanaEditarSocio.getCboxPaises().getSelectedItem();
            String nroCobrador = (String) ventanaEditarSocio.getCboxCobrador().getSelectedItem();
            String facturacion = (String) ventanaEditarSocio.getCboxFacturacion().getSelectedItem();
            String moneda = (String) ventanaEditarSocio.getCboxMonedas().getSelectedItem();

            String sentenciaUPDATE
                    = "UPDATE socio SET "
                    + "nrosocio=" + nroSocio + ", nombre='" + nombre + "', RUT ='" + RUT +"', telefono=" + telefono + ", direccion='" + direccion + "', localidad='" + localidad + "', cobrador=" + nroCobrador + ", facturacion='" + facturacion + "', moneda='" + moneda + "' " 
                    + "WHERE id=" + ID + ";";
            try {
                modelo.ejecutarSentencia(sentenciaUPDATE);
                actualizarVentanaSocios();
                ventanaEditarSocio.dispose();   // Tras la actualización de los datos del socio, la ventana se cierra
            } catch (Exception e) {
            }
        }
    }
}
