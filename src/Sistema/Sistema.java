package Sistema;

import java.sql.SQLException;
import Modelo.Modelo;
import Control.Socios.*;
import Control.Cobradores.*;
import Control.Operadores.*;

/**
 * Clase encargada de la inicialización del sistema.
 * Crea un único modelo (conexión a la base de datos única) para todos los controladores instanciados y sus respectivas vistas.
 * Lso getters de los controladores permiten que éstos hablen entre ellos y se soliciten tareas unos a otros.
 * 
 * @author Mauri
 */
public class Sistema {
    
    // Declaración del modelo y todos los controladores. Cada vista será inicializada por su respectivo controlador:
    private static Modelo modelo;
    private static ControladorVentanaSocios controladorVentanaSocios;
    private static ControladorVentanaDatosSocio controladorVentanaDatosSocio;
    private static ControladorVentanaOperadores controladorVentanaOperadores;
    private static ControladorVentanaDatosOperador controladorVentanaDatosOperador;
    private static ControladorVentanaCobradores controladorVentanaCobradores;
    private static ControladorVentanaDatosCobrador controladorVentanaDatosCobrador;
    
    /**
     * Inicializa los componentes MVC del sistema. 
     * @param args
     * @throws SQLException 
     */
    public static void main(String[] args) throws SQLException {
         // Instancia del modelo:
        modelo = new Modelo("Insect", "postgres", "user");
        // Instancia de los controladores:
        controladorVentanaSocios = new ControladorVentanaSocios(modelo);
        controladorVentanaDatosSocio = new ControladorVentanaDatosSocio(modelo);
        controladorVentanaOperadores = new ControladorVentanaOperadores(modelo);
        controladorVentanaDatosOperador = new ControladorVentanaDatosOperador(modelo);
        controladorVentanaCobradores = new ControladorVentanaCobradores(modelo);
        controladorVentanaDatosCobrador = new ControladorVentanaDatosCobrador(modelo);
    }
    
    // Getters de todos los controladores del sistema:
    public static ControladorVentanaSocios getControladorVentanaSocios() {
        return controladorVentanaSocios;
    }

    public static ControladorVentanaDatosSocio getControladorVentanaDatosSocio() {
        return controladorVentanaDatosSocio;
    }

    public static ControladorVentanaOperadores getControladorVentanaOperadores() {
        return controladorVentanaOperadores;
    }

    public static ControladorVentanaDatosOperador getControladorVentanaDatosOperador() {
        return controladorVentanaDatosOperador;
    }

    public static ControladorVentanaCobradores getControladorVentanaCobradores() {
        return controladorVentanaCobradores;
    }

    public static ControladorVentanaDatosCobrador getControladorVentanaDatosCobrador() {
        return controladorVentanaDatosCobrador;
    }
}
