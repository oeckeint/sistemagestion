package datos;

import dominio.Distribuidora;
import java.sql.*;

public class DistribuidoraDao {

    private static final String SQL_SELECT = "SELECT id_distribuidora, nombre_distribuidora FROM distribuidora where is_deleted = 0";
    private static final String SQL_SELECT_BY_ID = "SELECT id_distribuidora, nombre_distribuidora FROM distribuidora where id_distribuidora = ? and is_deleted = 0";

    public Distribuidora encontrar(Distribuidora distribuidora) {
        //Preparación de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            //Conecta a la base de datos
            conn = Conexion.getConnection();

            //Preparación del query
            stmt = conn.prepareStatement(SQL_SELECT_BY_ID);
            stmt.setInt(1, distribuidora.getIdDistribuidora());

            //Ejecución del query
            rs = stmt.executeQuery();
            rs.absolute(1);//Posiciona el en primer registro

            //Mapeo de valores recibidos desde la base de datos
            int idDistribuidora = rs.getInt("id_distribuidora");
            String nombreDistribuidora = rs.getString("nombre_distribuidora");

            //Creación del objeto cliente
            distribuidora.setIdDistribuidora(idDistribuidora);
            distribuidora.setNombreDistribuidora(nombreDistribuidora);

        } catch (SQLException ex) {
            System.out.println("(DistribuidoraDao). No se encontró un registro con el id proporcionado");
            distribuidora.setNombreDistribuidora("No definido");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return distribuidora;
    }
}
