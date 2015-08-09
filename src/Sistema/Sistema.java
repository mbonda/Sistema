package Sistema;

import java.sql.SQLException;
import Modelo.Modelo;
import ControladoresVistas.ControladorVentanaSocios.ControladorVentanaSocios;
import ControladoresVistas.ControladorVentanaDatosSocio.ControladorVentanaDatosSocio;

/**
 * Clase encargada de la inicialización del sistema.
 * Crea un único modelo (conexión a la base de datos única) para todos los controladores instanciados y sus respectivas vistas.
 *
 * @author Mauri
 */
public class Sistema {
    
    private static Modelo modelo;
    private static ControladorVentanaSocios controladorVentanaSocios;
    private static ControladorVentanaDatosSocio controladorVentanaDatosSocio;
    
    
    public static void main(String[] args) throws SQLException {
        inciarSistema();
    }

    private static void inciarSistema() throws SQLException {
        modelo = new Modelo("BD", "postgres", "user");
        controladorVentanaSocios = new ControladorVentanaSocios(modelo);
        controladorVentanaDatosSocio = new ControladorVentanaDatosSocio(modelo);
    }

    public static ControladorVentanaSocios getControladorVentanaSocios() {
        return controladorVentanaSocios;
    }

    public static ControladorVentanaDatosSocio getControladorVentanaDatosSocio() {
        return controladorVentanaDatosSocio;
    }
}
