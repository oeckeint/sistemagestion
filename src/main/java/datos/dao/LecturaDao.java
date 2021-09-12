package datos.dao;

import dominio.Lectura;
import java.sql.*;

public class LecturaDao {

    private static final String SQL_SELECT_BY_ID = "SELECT id_lectura, tipo_lectura FROM lectura where id_lectura = ? and is_deleted = 0";

    public Lectura encontrar(Lectura lectura) {
        //Preparación de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            //Conecta a la base de datos
            //conn = Conexion.getConnection();

            //Preparación del query
            stmt = conn.prepareStatement(SQL_SELECT_BY_ID);
            stmt.setInt(1, lectura.getIdLectura());

            //Ejecución del query
            rs = stmt.executeQuery();
            rs.absolute(1);//Posiciona el en primer registro

            //Mapeo de valores recibidos desde la base de datos
            int idLectura = rs.getInt("id_lectura");
            String tipoLectura = rs.getString("tipo_lectura");

            //Creación del objeto cliente
            lectura.setIdLectura(idLectura);
            lectura.setTipoLectura(tipoLectura);
            
            

        } catch (SQLException ex) {
            System.out.println("No se encontró el registro con el id proporcionado" + lectura.getIdLectura());
            lectura.setTipoLectura("No se encontró el registro");
            ex.printStackTrace(System.out);
        } finally {
            //Conexion.close(rs, stmt, conn);
        }
        return lectura;
    }
}
