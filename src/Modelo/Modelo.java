package Modelo;

import java.sql.*;
import java.util.Vector;

public class Modelo {

    private Connection conexion = null;
    private Statement sentencia = null;
    private final String baseDeDatos;
    private final String usuario;
    private final String clave;

    /**
     * Constructor de la clase. Almacena el nombre de la base de datos y las
     * credenciales de conexión al DBMS, y establece la conexión con la base.
     *
     * @param baseDeDatos Nombre de la base de datos con la cual se desea
     * establecer la conexión.
     * @param usuario Nombre de usuario necesario para acceder a PostgreSQL.
     * @param clave Contraseña necesaria para acceder a PostgreSQL.
     * @throws java.sql.SQLException
     */
    public Modelo(String baseDeDatos, String usuario, String clave) throws SQLException {
        this.baseDeDatos = baseDeDatos;
        this.usuario = usuario;
        this.clave = clave;
        establecerConexion();
    }

    /**
     * Establece una conexión con la base de datos y las credenciales pasados
     * como argumentos. Si ya existe una conexión esablecida, el método retorna.
     *
     * @return true si se establece la conexión; false si no se establece.
     * @throws SQLException
     */
    private void establecerConexion() throws SQLException {
        if (conexion != null) {
            return; // Si ya existe una conexión establecida, retornar
        }
        String url = "jdbc:postgresql://localhost:5432/" + baseDeDatos; // Crea la URL de conexión a la base de datos
        try {
            Class.forName("org.postgresql.Driver"); // Carga el driver
            conexion = DriverManager.getConnection(url, usuario, clave); // Establece una conexión a la BD, si usuario y password son correctos
        } catch (ClassNotFoundException e) {
            System.out.println("Diver no encontrado :(");
        }
    }

    /**
     * Realiza la consulta especificada como argumento y almacena el resultado de la misma.
     *
     * @param consulta Consulta a realizar, cuyo resultado es almacenado.
     * @return
     */
    public ResultSet realizarConsulta(String consulta) {
        try {
            sentencia = conexion.createStatement();
            return sentencia.executeQuery(consulta);
        } catch (SQLException e) {
            return null;
        }
    }

    public void ejecutarSentencia(String SQL) {
        try {
            sentencia = conexion.createStatement();
            sentencia.executeUpdate(SQL);
            if (sentencia != null) {
                sentencia.close();
            }
        } catch (SQLException e) {
            String message = e.getMessage();
            System.out.println(message);
        }
    }
    
        /**
     * Crea y devuelve un modelo de tabla utilizable por un JTable, a partir del resultado de consulta pasado como argumento.
     * @param resultadoConsulta
     * @return Modelo de tabla con los datos del resultado de consulta pasado como argumento
     * @throws SQLException 
     */
    public static NonEditableTableModel crearModeloTabla(ResultSet resultadoConsulta) throws SQLException
    {
        ResultSetMetaData metaData = resultadoConsulta.getMetaData();

        // Cabecera de la tabla (nombre de las columnas):
        Vector<String> nombreColumnas = new Vector<>();
        int nroColumnas = metaData.getColumnCount();
        for (int indiceColumna = 1; indiceColumna <= nroColumnas; indiceColumna++) // Las columnas se indexan desde el 1 en adelante en el ResultSet.
        {
            nombreColumnas.add(metaData.getColumnName(indiceColumna));
        }

        // Cuerpo de la tabla (datos):
        Vector<Vector<Object>> datosTabla = new Vector<>();
        while (resultadoConsulta.next())    // Mientras haya filas...
        {
            Vector<Object> fila = new Vector<>();
            for (int indiceColumna = 1; indiceColumna <= nroColumnas; indiceColumna++) {
                fila.add(resultadoConsulta.getObject(indiceColumna));   // Obtiene el dato en la posición indiceColumna y lo agrega a la fila.
            }
            datosTabla.add(fila);   // Agrega la fila con todos sus datos.
        }        
        return new NonEditableTableModel(datosTabla, nombreColumnas);
    }
}
