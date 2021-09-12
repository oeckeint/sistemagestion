package datos.dao;

import dominio.Cliente;
import java.sql.*;
import java.util.*;

public class DatosFacturaDao {

    private static final String SQL_SELECT_BY_CODIGO_FISCAL = " where codigo_fiscal_factura = ? or codigo_fiscal_factura = ? and is_deleted = 0";
    
    public boolean encontrarCodFiscal(String codigoFiscal) {
        //Preparación de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean resultado = false;
        try {
            //Conecta a la base de datos
            //conn = Conexion.getConnection();

            //Preparación del query
            stmt = conn.prepareStatement("SELECT codigo_fiscal_factura FROM contenido_xml" + SQL_SELECT_BY_CODIGO_FISCAL);
            stmt.setString(1, codigoFiscal);
            stmt.setString(2, codigoFiscal + "-A");

            //Ejecución del query
            rs = stmt.executeQuery();
            rs.absolute(1);//Posiciona el en primer registro
            if (rs.getRow() != 0) {
                System.out.println("(DatosFacturaDao) Ya existe la factura en la tabla");
                resultado = true;
            }
        } catch (SQLException ex) {
            System.out.println("(DatosFactura DAO). No se encontró cups");
        } finally {
            //Conexion.close(rs, stmt, conn);
        }
        return resultado;
    }
    
    public boolean encontrarCodFiscal(String codigoFiscal, String tabla) {
        //Preparación de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean resultado = false;
        try {
            //Conecta a la base de datos
            //conn = Conexion.getConnection();

            //Preparación del query
            stmt = conn.prepareStatement("SELECT codigo_fiscal_factura FROM " + tabla + SQL_SELECT_BY_CODIGO_FISCAL);
            stmt.setString(1, codigoFiscal);
            stmt.setString(2, codigoFiscal + "-A");

            //Ejecución del query
            rs = stmt.executeQuery();
            rs.absolute(1);//Posiciona el en primer registro
            if (rs.getRow() != 0) {
                resultado = true;
            }
        } catch (SQLException ex) {
            System.out.println("(DatosFactura DAO). No se encontró cups");
        } finally {
            //Conexion.close(rs, stmt, conn);
        }
        return resultado;
    }
    
    public boolean encontrarCodFiscal(String codigoFiscal, int emisora) {
        //Preparación de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean resultado = false;
        String tabla;
        
        switch(emisora){
            case 894:
                tabla = "contenido_xml_factura";
                break;
            default:
                tabla = "contenido_xml";
                break;
        }
        
        try {
            
            //Conecta a la base de datos
            //conn = Conexion.getConnection();

            //Preparación del query
            
            stmt = conn.prepareStatement("SELECT codigo_fiscal_factura FROM " + tabla + SQL_SELECT_BY_CODIGO_FISCAL);
            stmt.setString(1, codigoFiscal);
            stmt.setString(2, codigoFiscal + "-A");

            //Ejecución del query
            rs = stmt.executeQuery();
            rs.absolute(1);//Posiciona el en primer registro
            if (rs.getRow() != 0) {
                resultado = true;
                System.out.println("\n\n(DatosFacturaDao) Se encontro un registro con el codF (" + codigoFiscal + ") y la empEmi(" + emisora + ") en la tabla -" + tabla + "-\n\n");
            }
        } catch (SQLException ex) {
            System.out.println("(DatosFactura DAO). No se encontró cups");
        } finally {
            //Conexion.close(rs, stmt, conn);
        }
        return resultado;
    }
}
