package datos;

import dominio.Cliente;
import java.sql.*;
import java.util.*;

public class ClienteDao {

    private static final String SQL_SELECT = "SELECT * FROM cliente where is_deleted = 0 order by id_cliente desc";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM cliente where id_cliente = ? and is_deleted = 0";
    private static final String SQL_SELECT_BY_CUPS = "SELECT * FROM cliente where substring(cups, 1, 20) = substring(?, 1, 20) and is_deleted = 0";
    private static final String SQL_UPDATE = "UPDATE cliente SET cups=?, nombre_cliente=?, tarifa=? where id_cliente=?";
    private static final String SQL_DELETE = "UPDATE cliente set is_deleted=1 WHERE id_cliente=?";
    private static final String SQL_INSERT = "INSERT INTO cliente(cups, nombre_cliente, tarifa) values(?, ?, ?)";

    public List<Cliente> listar() {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Cliente> clientes = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int idCliente = rs.getInt("id_cliente");
                String cups = rs.getString("cups");
                String nombreCliente;
                if (rs.getString("nombre_cliente").length()>30) {
                    nombreCliente = rs.getString("nombre_cliente").substring(0, 30) + "...";
                } else{
                    nombreCliente = rs.getString("nombre_cliente");
                }
                String tarifa = rs.getString("tarifa");
                clientes.add(new Cliente(idCliente, cups, nombreCliente, tarifa));
            }

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return clientes;
    }

    public Cliente encontrar(Cliente cliente) {
        //Preparación de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            //Conecta a la base de datos
            conn = Conexion.getConnection();

            //Preparación del query
            stmt = conn.prepareStatement(SQL_SELECT_BY_ID);
            stmt.setInt(1, cliente.getIdCliente());

            //Ejecución del query
            rs = stmt.executeQuery();
            rs.absolute(1);//Posiciona el en primer registro

            //Mapeo de valores recibidos desde la base de datos
            String cups = rs.getString("cups");
            String nombreCliente = rs.getString("nombre_cliente");
            String tarifa = rs.getString("tarifa");

            //Creación del objeto cliente
            cliente.setCups(cups);
            cliente.setNombreCliente(nombreCliente);
            cliente.setTarifa(tarifa);

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return cliente;
    }
    
    public Cliente encontrarCups(Cliente cliente) {
        //Preparación de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            //Conecta a la base de datos
            conn = Conexion.getConnection();

            //Preparación del query
            stmt = conn.prepareStatement(SQL_SELECT_BY_CUPS);
            stmt.setString(1, cliente.getCups());

            //Ejecución del query
            rs = stmt.executeQuery();
            rs.absolute(1);//Posiciona el en primer registro

            //Mapeo de valores recibidos desde la base de datos
            int idCliente = rs.getInt("id_cliente");
            String cups = rs.getString("cups");
            String nombreCliente = rs.getString("nombre_cliente");
            String tarifa = rs.getString("tarifa");

            //Creación del objeto cliente
            cliente.setIdCliente(idCliente);
            cliente.setCups(cups);
            cliente.setNombreCliente(nombreCliente);
            cliente.setTarifa(tarifa);

        } catch (SQLException ex) {
            System.out.println("(Cliente DAO). No se encontró registro con el CUPS proporcionado ( " + cliente.getCups().substring(0,19) + " ). Solo se muestran los primeros 20 carácteres");
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return cliente;
    }

    public int insertar(Cliente cliente) {
        //Preparación de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            //Conecta a la base de datos
            conn = Conexion.getConnection();

            //Preparación del query
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setString(1, cliente.getCups());
            stmt.setString(2, cliente.getNombreCliente());
            stmt.setString(3, cliente.getTarifa());

            //Ejecución del query
            rows = stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            //Cierra conexión
            Conexion.close(stmt, conn);
        }
        return rows;
    }

    public int actualizar(Cliente cliente) {
        //Preparación de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            //Conecta a la base de datos
            conn = Conexion.getConnection();

            //Preparación del query
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, cliente.getCups());
            stmt.setString(2, cliente.getNombreCliente());
            stmt.setString(3, cliente.getTarifa());
            stmt.setInt(4, cliente.getIdCliente());

            //Ejecución del query
            rows = stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            //Cierra conexión
            Conexion.close(stmt, conn);
        }
        return rows;
    }
    
    public int eliminar(Cliente cliente){
        //Preparación de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            //Conecta a la base de datos
            conn = Conexion.getConnection();

            //Preparación del query
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, cliente.getIdCliente());

            //Ejecución del query
            rows = stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            //Cierra conexión
            Conexion.close(stmt, conn);
        }
        return rows;
    }
}
