package datos.dao;

import dominio.Cliente;
import dominio.otrasfactuas.*;
import java.sql.*;
import java.util.*;

public class OtrasFacturasDao {

    //Busqueda en todas las facturas de contenido_XML
    private static final String SQL_SELECT_BY_COD_FISCAL = " where cod_fis_fac = ? AND is_deleted = 0";

    //Contenido XML por registro
    private static final String SQL_SELECT_BY_ID_CLIENTE = "SELECT * FROM contenido_xml_otras_facturas where id_cliente = ? AND is_deleted = 0 order by id_cont desc";

    //Por Remesa
    private static final String SQL_SELECT_BY_REMESA = "SELECT * FROM contenido_xml_otras_facturas where id_rem = ? AND is_deleted = 0 order by id_cont desc";

    //Selecciona todos los registros diferentes a 0894
    private static final String SQL_SELECT = "SELECT * FROM contenido_xml_otras_facturas where is_deleted = 0 order by id_cont desc";

    //Inserta datos en contenido_xml
    private static final String SQL_INSERT = "("
            + "cod_emp_emi, cod_emp_des, cod_pro, cod_pas, cod_sol, cups, id_cliente, " //cabecera
            + "cod_fis_fac, tip_fac, mot_fac, fec_fac, com, imp_tot_fac, "//datosGeneralesFactura
            + "con_rep, imp_tot_con_rep, "//conceptoRepercutible
            + "com_dev, id_err,"//extras
            + "id_rem" //registroFin
            + ") values ("
            + "?, ?, ?, ?, ?, ?, ?, " //cabecera
            + "?, ?, ?, ?, ?, ?, " //datosGeneralesFactura
            + "?, ?, " //conceptoRepercutible
            + "?, ?, " //extras
            + "?" // finRegistro
            + ")";

    private static final String SQL_UPDATE = "UPDATE contenido_xml set "
            + "c_emp_emi = ?, c_emp_des = ?, c_cod_pro = ?, c_cod_pas = ?, c_cod_sol = ?, c_fec_sol = ?, c_cups = ?, " //Cabecera
            + "codigo_fiscal_factura = ?, tipo_factura = ?, motivo_facturacion = ?, codigo_factura_rectificada_anulada = ?, fecha_factura = ?, "//DatosGenerales
            + "tarifa_atr_fact = ?, modo_control_potencia = ?, marca_medida_con_perdidas = ?, vas_trafo = ?, porcentaje_perdidas = ?, numero_dias = ?, "//DatosFacturaATR
            + "exceso_potencia1 = ?, exceso_potencia2 = ?, exceso_potencia3 = ?, exceso_potencia4 = ?, exceso_potencia5 = ?, exceso_potencia6 = ?, exceso_importe_total = ?, " //Exceso Potencia
            + "potencia_contratada1 = ?, potencia_contratada2 = ?, potencia_contratada3 = ?, potencia_contratada4 = ?, potencia_contratada5 = ?, potencia_contratada6 = ?, " //PotenciaContrada
            + "potencia_max1 = ?, potencia_max2 = ?, potencia_max3 = ?, potencia_max4 = ?, potencia_max5 = ?, potencia_max6 = ?, " //Potencia Max
            + "potencia_fac1 = ?, potencia_fac2 = ?, potencia_fac3 = ?, " //Potencia a facturar
            + "potencia_pre1 = ?, potencia_pre2 = ?, potencia_pre3 = ?, potencia_pre4 = ?, potencia_pre5 = ?, potencia_pre6 = ?, " // Potencia Precio
            + "potencia_imp_tot = ?, " // Potencia Importe Total
            + "ea_fecha_desde1 = ?, ea_fecha_hasta1 = ?, ea_fecha_desde2 = ?, ea_fecha_hasta2 = ?, " // DatosTerminoEnergiaActiva
            + "ea_val1 = ?, ea_val2 = ?, ea_val3 = ?, ea_val4 = ?, ea_val5 = ?, ea_val6 = ?, ea_val_sum = ?, "//EnergiaActiva Valores
            + "ea_pre1 = ?, ea_pre2 = ?, ea_pre3 = ?, ea_pre4 = ?, ea_pre5 = ?, ea_pre6 = ?, " //Energia Activa Precio
            + "ea_imp_tot = ?, " //EnergiaActiva Importe total
            + "ie_importe = ?, " //ImpuestoElectrico Importe
            + "a_imp_fact = ?, " // Alquileres importe Facturacion
            + "i_bas_imp = ?," //Iva base imponible
            + "ae_cons1 = ?, ae_cons2 = ?, ae_cons3 = ?, ae_cons4 = ?, ae_cons5 = ?, ae_cons6 = ?, ae_cons_sum = ?, " //AE Consumo
            + "ae_lec_des1 = ?, ae_lec_des2 = ?, ae_lec_des3 = ?, ae_lec_des4 = ?, ae_lec_des5 = ?, ae_lec_des6 = ?, " //AE Lectura Desde 
            + "ae_lec_has1 = ?, ae_lec_has2 = ?, ae_lec_has3 = ?, ae_lec_has4 = ?, ae_lec_has5 = ?, ae_lec_has6 = ?, " //AE Lectura Hasta*/
            + "ae_pro_des = ?, " // AE Procedencia Desde
            + "ae_pro_has = ?, " //AE ProcedenciaHasta
            + "r_con1 = ?, r_con2 = ?, r_con3 = ?, r_con4 = ?, r_con5 = ?, r_con6 = ?, r_con_sum = ?, " //R Consumo
            + "r_lec_des1 = ?, r_lec_des2 = ?, r_lec_des3 = ?, r_lec_des4 = ?, r_lec_des5 = ?, r_lec_des6 = ?, " //R Lectura desde
            + "r_lec_has1 = ?, r_lec_has2 = ?, r_lec_has3 = ?, r_lec_has4 = ?, r_lec_has5 = ?, r_lec_has6 = ?, " //R LecturaHasta
            + "pm_con1 = ?, pm_con2 = ?, pm_con3 = ?, pm_con4 = ?, pm_con5 = ?, pm_con6 = ?, pm_con_sum = ?, " //PM Consumo
            + "pm_lec_has1 = ?, pm_lec_has2 = ?, pm_lec_has3 = ?, pm_lec_has4 = ?, pm_lec_has5 = ?, pm_lec_has6 = ?, " //PM Lectura Hasta
            + "rf_imp_tot = ?, rf_sal_tot_fac = ?, rf_tot_rec = ?, rf_fec_val = ?, rf_fec_lim_pag = ?, rf_id_rem = ?, " //RegistroFin
            + "comentarios = ?, " //Comentarios
            + "id_error = ? " // Errores
            + "where codigo_fiscal_factura = ? or codigo_fiscal_factura = ?";

    /*
    
    public List<DocumentoOtraFactura> listar() {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<DocumentoOtraFactura> facturas = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT);
            rs = stmt.executeQuery();

            while (rs.next()) {

                //DatosCabecera
                ArrayList<String> datosC = new ArrayList<>(6);
                datosC.add(rs.getString("cod_emp_emi"));
                datosC.add(rs.getString("cod_emp_des"));
                datosC.add(rs.getString("cod_pro"));
                datosC.add(rs.getString("cod_pas"));
                datosC.add(rs.getString("cod_sol"));
                datosC.add(rs.getString("cups"));

                //Datos Generales Factura
                ArrayList<String> datosD = new ArrayList<>(6);
                datosD.add(rs.getString("cod_fis_fac"));
                datosD.add(rs.getString("tip_fac"));
                datosD.add(rs.getString("mot_fac"));
                datosD.add(rs.getString("fec_fac"));
                datosD.add(rs.getString("com"));
                datosD.add(rs.getString("imp_tot_fac"));

                //ConceptoRepercutible
                ArrayList<String> datosCR = new ArrayList<>();
                datosCR.add(rs.getString("con_rep"));
                datosCR.add(rs.getString("imp_tot_con_rep"));

                //registroFin
                ArrayList<String> datosR = new ArrayList<>(1);
                datosR.add(rs.getString("id_rem"));

                facturas.add(
                        new DocumentoOtraFactura(
                                new ClienteDao().encontrarCups(new Cliente(rs.getString("cups"))),
                                new Cabecera(datosC),
                                new dominio.otrasfactuas.DatosGeneralesFactura(datosD),
                                new ConceptoRepercutible(datosCR),
                                new RegistroFin(datosR),
                                rs.getString("com_dev"),
                                rs.getString("id_err"))
                );

            }

        } catch (SQLException ex) {
            System.out.println("(ContenidoXmlDao). No se encontró ningún registro");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return facturas;
    }

    public Boolean existeCodFiscal(String cod) {

        boolean encontrado = false;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String tabla = "contenido_xml_otras_facturas";

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " + tabla + SQL_SELECT_BY_COD_FISCAL);
            stmt.setString(1, cod);
            rs = stmt.executeQuery();

            if (rs.next()) {
                encontrado = true;
            }

        } catch (SQLException ex) {
            System.out.println("(ContenidoXmlDao). No se encontró ningún registro");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return encontrado;
    }

    public DocumentoOtraFactura buscarByCodFiscal(String cod) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DocumentoOtraFactura documento = null;

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM contenido_xml_otras_facturas " + SQL_SELECT_BY_COD_FISCAL);
            stmt.setString(1, cod);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Registro vacío");
            } else {
                rs.absolute(1);

                //Cliente
                Cliente cliente = new ClienteDao().encontrarCups(new Cliente(rs.getString("cups")));

                ArrayList<String> datosS = new ArrayList<>();

                datosS.add(rs.getString("cod_emp_emi"));
                datosS.add(rs.getString("cod_emp_des"));
                datosS.add(rs.getString("cod_pro"));
                datosS.add(rs.getString("cod_pas"));
                datosS.add(rs.getString("cod_sol"));
                datosS.add(rs.getString("cups"));
                Cabecera cabecera = new Cabecera(datosS);
                datosS.clear();

                datosS.add(rs.getString("cod_fis_fac"));
                datosS.add(rs.getString("tip_fac"));
                datosS.add(rs.getString("mot_fac"));
                datosS.add(rs.getString("fec_fac"));
                datosS.add(rs.getString("com"));
                datosS.add(rs.getString("imp_tot_fac"));
                DatosGeneralesFactura datosGeneralesFactura = new DatosGeneralesFactura(datosS);
                datosS.clear();

                datosS.add(rs.getString("con_rep"));
                datosS.add(rs.getString("imp_tot_con_rep"));
                ConceptoRepercutible conceptoRepercutible = new ConceptoRepercutible(datosS);
                datosS.clear();

                datosS.add(rs.getString("id_rem"));
                RegistroFin registroFin = new RegistroFin(datosS);
                datosS.clear();

                documento = new DocumentoOtraFactura(cliente, cabecera, datosGeneralesFactura, conceptoRepercutible,
                        registroFin, rs.getString("com_dev"), rs.getString("id_err"));

            }
        } catch (SQLException ex) {
            System.out.println("(ContenidoXmlDao). No se encontró ningún registro");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return documento;
    }

    public List<DocumentoOtraFactura> buscarByRemesa(String remesa) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<DocumentoOtraFactura> documentos = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_BY_REMESA);
            stmt.setString(1, remesa);
            rs = stmt.executeQuery();

            while (rs.next()) {
                //Cliente
                Cliente cliente = new ClienteDao().encontrarCups(new Cliente(rs.getString("cups")));

                ArrayList<String> datosS = new ArrayList<>();

                datosS.add(rs.getString("cod_emp_emi"));
                datosS.add(rs.getString("cod_emp_des"));
                datosS.add(rs.getString("cod_pro"));
                datosS.add(rs.getString("cod_pas"));
                datosS.add(rs.getString("cod_sol"));
                datosS.add(rs.getString("cups"));
                Cabecera cabecera = new Cabecera(datosS);
                datosS.clear();

                datosS.add(rs.getString("cod_fis_fac"));
                datosS.add(rs.getString("tip_fac"));
                datosS.add(rs.getString("mot_fac"));
                datosS.add(rs.getString("fec_fac"));
                datosS.add(rs.getString("com"));
                datosS.add(rs.getString("imp_tot_fac"));
                DatosGeneralesFactura datosGeneralesFactura = new DatosGeneralesFactura(datosS);
                datosS.clear();

                datosS.add(rs.getString("con_rep"));
                datosS.add(rs.getString("imp_tot_con_rep"));
                ConceptoRepercutible conceptoRepercutible = new ConceptoRepercutible(datosS);
                datosS.clear();

                datosS.add(rs.getString("id_rem"));
                RegistroFin registroFin = new RegistroFin(datosS);
                datosS.clear();

                documentos.add(
                        new DocumentoOtraFactura(cliente, cabecera, datosGeneralesFactura, conceptoRepercutible,
                        registroFin, rs.getString("com_dev"), rs.getString("id_err"))
                                );
                
            }

        } catch (SQLException ex) {
            System.out.println("(OtrasFacturasDao). No se encontró ningún registro");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return documentos;
    }
    
    public List<DocumentoOtraFactura> buscarByIdCliente(String id_cliente) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<DocumentoOtraFactura> documentos = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_BY_ID_CLIENTE);
            stmt.setString(1, id_cliente);
            rs = stmt.executeQuery();

            while (rs.next()) {
                //DatosCabecera
                ArrayList<String> datosC = new ArrayList<>(6);
                datosC.add(rs.getString("cod_emp_emi"));
                datosC.add(rs.getString("cod_emp_des"));
                datosC.add(rs.getString("cod_pro"));
                datosC.add(rs.getString("cod_pas"));
                datosC.add(rs.getString("cod_sol"));
                datosC.add(rs.getString("cups"));

                //Datos Generales Factura
                ArrayList<String> datosD = new ArrayList<>(6);
                datosD.add(rs.getString("cod_fis_fac"));
                datosD.add(rs.getString("tip_fac"));
                datosD.add(rs.getString("mot_fac"));
                datosD.add(rs.getString("fec_fac"));
                datosD.add(rs.getString("com"));
                datosD.add(rs.getString("imp_tot_fac"));

                //ConceptoRepercutible
                ArrayList<String> datosCR = new ArrayList<>();
                datosCR.add(rs.getString("con_rep"));
                datosCR.add(rs.getString("imp_tot_con_rep"));

                //registroFin
                ArrayList<String> datosR = new ArrayList<>(1);
                datosR.add(rs.getString("id_rem"));

                documentos.add(
                        new DocumentoOtraFactura(
                                new ClienteDao().encontrarCups(new Cliente(rs.getString("cups"))),
                                new Cabecera(datosC),
                                new dominio.otrasfactuas.DatosGeneralesFactura(datosD),
                                new ConceptoRepercutible(datosCR),
                                new RegistroFin(datosR),
                                rs.getString("com_dev"),
                                rs.getString("id_err"))
                );
            }

        } catch (SQLException ex) {
            System.out.println("(ContenidoXmlDao). No se encontró ningún registro");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return documentos;
    }
    
    public int insertar(DocumentoOtraFactura factura) {
        //Preparación de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;

        //Vaiables de uso
        int rows = 0;
        String nombreTabla = "contenido_xml_otras_facturas";

        //Revisión de la existencia de la factura en las tablas
        if (this.existeCodFiscal(factura.getDatosGeneralesFactura().getCodFisFac())) {
            System.out.println("Ya existe la factura (" + factura.getDatosGeneralesFactura().getCodFisFac() + ")  en la tabla " + nombreTabla);
        } else {
            try {
                //Conecta a la base de datos
                conn = Conexion.getConnection();
                //Preparación del query
                stmt = conn.prepareStatement("INSERT INTO " + nombreTabla + SQL_INSERT);

                int i = 1;

                //DatosCabecera
                stmt.setString(i++, factura.getCabecera().getCodEmpEmi());
                stmt.setString(i++, factura.getCabecera().getCodEmpDes());
                stmt.setString(i++, factura.getCabecera().getCodPro());
                stmt.setString(i++, factura.getCabecera().getCodPas());
                stmt.setString(i++, factura.getCabecera().getCodSol());
                stmt.setString(i++, factura.getCabecera().getCups());
                stmt.setString(i++, String.valueOf(new ClienteDao().encontrarCups(new Cliente(factura.getCabecera().getCups())).getIdCliente()));
                
                

                //datosGeneralesFactura
                stmt.setString(i++, factura.getDatosGeneralesFactura().getCodFisFac());
                stmt.setString(i++, factura.getDatosGeneralesFactura().getTipFac());
                stmt.setString(i++, factura.getDatosGeneralesFactura().getMotFac());
                stmt.setString(i++, factura.getDatosGeneralesFactura().getFecFac());
                stmt.setString(i++, factura.getDatosGeneralesFactura().getCom());
                stmt.setDouble(i++, factura.getDatosGeneralesFactura().getImpTotFac());

                //conceptoRepercutible
                stmt.setString(i++, factura.getConceptoRepercutible().getConRep());
                stmt.setDouble(i++, factura.getConceptoRepercutible().getImpTot());

                //Comentarios
                stmt.setString(i++, String.valueOf(factura.getComentarios()));

                //Errores
                stmt.setString(i++, String.valueOf(factura.getErrores()));

                //finRegistro
                stmt.setString(i++, factura.getRegistroFin().getIdRemesa());

                //Ejecución del query
                rows = stmt.executeUpdate();

            } catch (SQLException ex) {
                System.out.println("(ContenidoXMLDao). Ha ocurrido un error al insertar un registro");
                ex.printStackTrace(System.out);
            } finally {
                //Cierra conexión
                Conexion.close(stmt, conn);
            }
        }
        return rows;
    }
*/
}
