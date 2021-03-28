package datos;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

public class Conexion {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/sge?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "admin";
    
    private static BasicDataSource dataSource;
    
    public static DataSource getDataSource(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (dataSource == null) {
           dataSource = new BasicDataSource();
           dataSource.setUrl(JDBC_URL);
           dataSource.setUsername(JDBC_USERNAME);
           dataSource.setPassword(JDBC_PASSWORD);
           dataSource.setInitialSize(5);   
        }
        return dataSource;
    }
    
    public static Connection getConnection() throws SQLException{
        return getDataSource().getConnection();
    }
    
    public static void close(ResultSet rs, PreparedStatement stmt, Connection conn){
        try {
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    public static void close(PreparedStatement stmt, Connection conn){
        try {
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
    }
}
