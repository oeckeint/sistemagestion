package datos;

import dominio.Cliente;
import dominio.componentesxml.*;
import java.sql.*;
import java.util.*;

public class ContenidoXmlDao {

    //Busqueda en todas las facturas de contenido_XML
    private static final String SQL_SELECT_BY_COD_FISCAL = " where codigo_fiscal_factura = ? or codigo_fiscal_factura = ? AND is_deleted = 0";

    //Contenido XML por registro
    private static final String SQL_SELECT_BY_ID_CLIENTE = "SELECT * FROM contenido_xml where id_cliente = ? AND c_emp_emi != '0894' AND is_deleted = 0 order by idcontenido_xml desc";

    //Por Remesa
    private static final String SQL_SELECT_BY_REMESA = "SELECT * FROM contenido_xml where rf_id_rem = ? AND is_deleted = 0 order by idcontenido_xml desc";
    private static final String SQL_SELECT_BY_REMESA0894 = "SELECT * FROM contenido_xml_factura where rf_id_rem = ? AND is_deleted = 0 order by idcontenido_xml_factura desc";

    //Contenido XML por facturas
    private static final String SQL_SELECT_BY_ID_CLIENTE_0894 = "SELECT * FROM contenido_xml_factura where id_cliente = ? AND is_deleted = 0 order by idcontenido_xml_factura desc";

    //Selecciona todos los registros diferentes a 0894
    private static final String SQL_SELECT = "SELECT * FROM contenido_xml where is_deleted = 0 order by idcontenido_xml desc";

    //Selecciona todos los registros iguales a 0894
    private static final String SQL_SELECT_0894 = "SELECT * FROM contenido_xml_factura where is_deleted = 0 order by idcontenido_xml_factura desc";

    //Inserta datos en contenido_xml
    private static final String SQL_INSERT = "("
            + "c_emp_emi, c_emp_des, c_cod_pro, c_cod_pas, c_cod_sol, c_fec_sol, c_cups, " //Cabecera
            + "codigo_fiscal_factura, tipo_factura, motivo_facturacion, codigo_factura_rectificada_anulada, fecha_factura, "//DatosGenerales
            + "tarifa_atr_fact, modo_control_potencia, marca_medida_con_perdidas, vas_trafo, porcentaje_perdidas, numero_dias, "//DatosFacturaATR
            + "exceso_potencia1, exceso_potencia2, exceso_potencia3, exceso_potencia4, exceso_potencia5, exceso_potencia6, exceso_importe_total, " //Exceso Potencia
            + "potencia_contratada1, potencia_contratada2, potencia_contratada3, potencia_contratada4, potencia_contratada5, potencia_contratada6, " //PotenciaContrada
            + "potencia_max1, potencia_max2, potencia_max3, potencia_max4, potencia_max5, potencia_max6, " //Potencia Max
            + "potencia_fac1, potencia_fac2, potencia_fac3, " //Potencia a facturar
            + "potencia_pre1, potencia_pre2, potencia_pre3, potencia_pre4, potencia_pre5, potencia_pre6, " // Potencia Precio
            + "potencia_imp_tot, " // Potencia Importe Total
            + "ea_fecha_desde1, ea_fecha_hasta1, ea_fecha_desde2, ea_fecha_hasta2, " // DatosTerminoEnergiaActiva
            + "ea_val1, ea_val2, ea_val3, ea_val4, ea_val5, ea_val6, ea_val_sum, "//EnergiaActiva Valores
            + "ea_pre1, ea_pre2, ea_pre3, ea_pre4, ea_pre5, ea_pre6, " //Energia Activa Precio
            + "ea_imp_tot, " //EnergiaActiva Importe total
            + "car1_01, car2_01, car3_01, car4_01, car5_01, car6_01, " //Cargos01
            + "car_imp_tot_01, " //Cargo Importe Total 01
            + "car1_02, car2_02, car3_02, car4_02, car5_02, car6_02, " //Cargos02
            + "car_imp_tot_02, " //Cargo Importe Total 02
            + "ie_importe, " //ImpuestoElectrico Importe
            + "a_imp_fact, " // Alquileres importe Facturacion
            + "i_bas_imp," //Iva base imponible
            + "ae_cons1, ae_cons2, ae_cons3, ae_cons4, ae_cons5, ae_cons6, ae_cons_sum, " //AE Consumo
            + "ae_lec_des1, ae_lec_des2, ae_lec_des3, ae_lec_des4, ae_lec_des5, ae_lec_des6, " //AE Lectura Desde 
            + "ae_lec_has1, ae_lec_has2, ae_lec_has3, ae_lec_has4, ae_lec_has5, ae_lec_has6, " //AE Lectura Hasta*/
            + "ae_pro_des, " // AE Procedencia Desde
            + "ae_pro_has, " //AE ProcedenciaHasta
            + "r_con1, r_con2, r_con3, r_con4, r_con5, r_con6, r_con_sum, " //R Consumo
            + "r_lec_des1, r_lec_des2, r_lec_des3, r_lec_des4, r_lec_des5, r_lec_des6, " //R Lectura desde
            + "r_lec_has1, r_lec_has2, r_lec_has3, r_lec_has4, r_lec_has5, r_lec_has6, " //R LecturaHasta
            + "r_imp_tot, " //R Importe total
            + "pm_con1, pm_con2, pm_con3, pm_con4, pm_con5, pm_con6, pm_con_sum, " //PM Consumo
            + "pm_lec_has1, pm_lec_has2, pm_lec_has3, pm_lec_has4, pm_lec_has5, pm_lec_has6, " //PM Lectura Hasta
            + "rf_imp_tot, rf_sal_tot_fac, rf_tot_rec, rf_fec_val, rf_fec_lim_pag, rf_id_rem, " //RegistroFin
            + "comentarios, " //Comentarios
            + "id_error, " //Errores
            + "id_cliente"
            + ") values ("
            + "?, ?, ?, ?, ?, ?, ?, " //Cabecera 1-7
            + "?, ?, ?, ?, ?, " //DatosGenerales 8-12
            + "?, ?, ?, ?, ?, ?, " //DatosFacturaATR 12-18
            + "?, ?, ?, ?, ?, ?, ?, " //DatosExcesisPotencia 19-25
            + "?, ?, ?, ?, ?, ?, " //DatosPotenciaContrada 26 - 31
            + "?, ?, ?, ?, ?, ?, " //DatosPotenciaMaxima 32-37
            + "?, ?, ?, " // Potencia A facturar 38-40
            + "?, ?, ?, ?, ?, ?, " // Potencia Precio 41-46
            + "?, " // Potencia Importe Total
            + "?, ?, ?, ?, " //DatosTerminoEnergiaActiva
            + "?, ?, ?, ?, ?, ? ,?, " //Energia Activa Valores
            + "?, ?, ?, ?, ?, ?, " //EnergiaActiva Precio
            + "?, " // EnergiaActiva Importe total
            + "?, ?, ?, ?, ?, ?, " //Costos 01
            + "?, " //Costos importe Total 01
            + "?, ?, ?, ?, ?, ?, " //Costos 02
            + "?, " //Costos importe Total 02
            + "?, " //Impuestoelectrico Importe
            + "?, " //Alquileres Importe Facturacion
            + "?, " //Iva Base Imponible
            + "?, ?, ?, ?, ?, ?, ?, " //AE Consumo
            + "?, ?, ?, ?, ?, ?, " //Ae Lectura desde
            + "?, ?, ?, ?, ?, ?, " //AE lectura Hasta*/
            + "?, " // AE Procedencia Dede
            + "?, " // AE ProcedenciaHasta
            + "?, ?, ?, ?, ?, ?, ?, " //R Consumo
            + "?, ?, ?, ?, ?, ?, " // RLectura Desde
            + "?, ?, ?, ?, ?, ?, " //RLecturaHasta
            + "?, " //RImporteTotal
            + "?, ?, ?, ?, ?, ?, ?, " //PMConsumo
            + "?, ?, ?, ?, ?, ?, " //PM Lectura Hasta
            + "?, ?, ?, ?, ?, ?, " //Registro Fin
            + "?, " //Comentarios
            + "?, " //Errores
            + "?" // Cliente
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

    public List<DocumentoXml> listar(int emisora) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<DocumentoXml> documentos = new ArrayList<>();
        try {
            conn = Conexion.getConnection();

            if (emisora == 894) {
                stmt = conn.prepareStatement(SQL_SELECT_0894);
            } else {
                stmt = conn.prepareStatement(SQL_SELECT);
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                //Cliente
                Cliente cliente = new ClienteDao().encontrarCups(new Cliente(rs.getString("c_cups")));

                //DatosCabecera
                List<String> datosC = new ArrayList<>();
                datosC.add(rs.getString("c_emp_emi"));
                datosC.add(rs.getString("c_emp_des"));
                datosC.add(rs.getString("c_cod_pro"));
                datosC.add(rs.getString("c_cod_pas"));
                datosC.add(rs.getString("c_cod_sol"));
                datosC.add(rs.getString("c_fec_sol"));
                datosC.add(rs.getString("c_cups"));
                DatosCabecera datosCabecera = new DatosCabecera(datosC);

                //DatosGenerales
                List<String> datosGF = new ArrayList<>();
                datosGF.add(rs.getString("codigo_fiscal_factura"));
                datosGF.add(rs.getString("tipo_factura"));
                datosGF.add(rs.getString("motivo_facturacion"));
                datosGF.add(rs.getString("codigo_factura_rectificada_anulada"));
                datosGF.add(rs.getString("fecha_factura"));
                DatosGeneralesFactura datosGeneralesFactura = new DatosGeneralesFactura(datosGF);

                //DatosFacturaATR
                List<String> datosATR = new ArrayList<>();
                datosATR.add(rs.getString("tarifa_atr_fact"));
                datosATR.add(rs.getString("modo_control_potencia"));
                datosATR.add(rs.getString("marca_medida_con_perdidas"));
                datosATR.add(rs.getString("vas_trafo"));
                datosATR.add(rs.getString("porcentaje_perdidas"));
                datosATR.add(rs.getString("numero_dias"));
                DatosFacturaAtr datosFacturaAtr = new DatosFacturaAtr(datosATR);

                //ExcesosPotencia
                List<Double> datosEP = new ArrayList<>();
                datosEP.add(rs.getDouble("exceso_potencia1"));
                datosEP.add(rs.getDouble("exceso_potencia2"));
                datosEP.add(rs.getDouble("exceso_potencia3"));
                datosEP.add(rs.getDouble("exceso_potencia4"));
                datosEP.add(rs.getDouble("exceso_potencia5"));
                datosEP.add(rs.getDouble("exceso_potencia6"));
                datosEP.add(rs.getDouble("exceso_importe_total"));
                DatosExcesoPotencia datosExcesoPotencia = new DatosExcesoPotencia(datosEP);

                //PotenciaContratada
                List<Double> datosPC = new ArrayList<>();
                datosPC.add(rs.getDouble("potencia_contratada1"));
                datosPC.add(rs.getDouble("potencia_contratada2"));
                datosPC.add(rs.getDouble("potencia_contratada3"));
                datosPC.add(rs.getDouble("potencia_contratada4"));
                datosPC.add(rs.getDouble("potencia_contratada5"));
                datosPC.add(rs.getDouble("potencia_contratada6"));
                DatosPotenciaContratada datosPotenciaContratada = new DatosPotenciaContratada(datosPC);

                //Potencia Maxima demandada
                List<Double> datosPMD = new ArrayList<>();
                datosPMD.add(rs.getDouble("potencia_max1"));
                datosPMD.add(rs.getDouble("potencia_max2"));
                datosPMD.add(rs.getDouble("potencia_max3"));
                datosPMD.add(rs.getDouble("potencia_max4"));
                datosPMD.add(rs.getDouble("potencia_max5"));
                datosPMD.add(rs.getDouble("potencia_max6"));
                DatosPotenciaMaxDemandada datosPotenciaMaxDemandada = new DatosPotenciaMaxDemandada(datosPMD);

                //Potencia a facturar
                List<Double> datosPF = new ArrayList<>();
                datosPF.add(rs.getDouble("potencia_fac1"));
                datosPF.add(rs.getDouble("potencia_fac2"));
                datosPF.add(rs.getDouble("potencia_fac3"));
                DatosPotenciaAFacturar datosPotenciaAFacturar = new DatosPotenciaAFacturar(datosPF);

                //Precio potencia
                List<Double> datosPP = new ArrayList<>();
                datosPP.add(rs.getDouble("potencia_pre1"));
                datosPP.add(rs.getDouble("potencia_pre2"));
                datosPP.add(rs.getDouble("potencia_pre3"));
                datosPP.add(rs.getDouble("potencia_pre4"));
                datosPP.add(rs.getDouble("potencia_pre5"));
                datosPP.add(rs.getDouble("potencia_pre6"));
                DatosPotenciaPrecio datosPotenciaPrecio = new DatosPotenciaPrecio(datosPP);

                //Potencia Importe Total
                List<Double> datosPIT = new ArrayList<>();
                datosPIT.add(rs.getDouble("potencia_imp_tot"));
                DatosPotenciaImporteTotal datosPotenciaImporteTotal = new DatosPotenciaImporteTotal(datosPIT);

                //DatosEnergiaActiva
                List<String> datosEA = new ArrayList<>();
                datosEA.add(rs.getString("ea_fecha_desde1"));
                datosEA.add(rs.getString("ea_fecha_hasta1"));
                datosEA.add(rs.getString("ea_fecha_desde2"));
                datosEA.add(rs.getString("ea_fecha_hasta2"));
                DatosEnergiaActiva datosEnergiaActiva = new DatosEnergiaActiva(datosEA);

                //EnergiaActivaValor
                List<Double> datosEAV = new ArrayList<>();
                datosEAV.add(rs.getDouble("ea_val1"));
                datosEAV.add(rs.getDouble("ea_val2"));
                datosEAV.add(rs.getDouble("ea_val3"));
                datosEAV.add(rs.getDouble("ea_val4"));
                datosEAV.add(rs.getDouble("ea_val5"));
                datosEAV.add(rs.getDouble("ea_val6"));
                datosEAV.add(rs.getDouble("ea_val_sum"));
                DatosEnergiaActivaValores datosEnergiaActivaValores = new DatosEnergiaActivaValores(datosEAV);

                //EnergiaActivaPrecio
                List<Double> datosEAP = new ArrayList<>();
                datosEAP.add(rs.getDouble("ea_pre1"));
                datosEAP.add(rs.getDouble("ea_pre2"));
                datosEAP.add(rs.getDouble("ea_pre3"));
                datosEAP.add(rs.getDouble("ea_pre4"));
                datosEAP.add(rs.getDouble("ea_pre5"));
                datosEAP.add(rs.getDouble("ea_pre6"));
                DatosEnergiaActivaPrecio datosEnergiaActivaPrecio = new DatosEnergiaActivaPrecio(datosEAP);

                //EnergiaActivaImporteTotal
                List<Double> datosIT = new ArrayList<>();
                datosIT.add(rs.getDouble("ea_imp_tot"));
                DatosEnergiaActivaImporteTotal datosEnergiaActivaImporteTotal = new DatosEnergiaActivaImporteTotal(datosIT);

                //Costos 01
                List<Double> datosC1 = new ArrayList<>(6);
                datosC1.add(rs.getDouble("car1_01"));
                datosC1.add(rs.getDouble("car2_01"));
                datosC1.add(rs.getDouble("car3_01"));
                datosC1.add(rs.getDouble("car4_01"));
                datosC1.add(rs.getDouble("car5_01"));
                datosC1.add(rs.getDouble("car6_01"));
                Cargos cargos1 = new Cargos(datosC1);

                //Costos Importe Total 01
                datosC1 = new ArrayList<>(1);
                datosC1.add(rs.getDouble("car_imp_tot_01"));
                CargoImporteTotal cargoImporteTotal1 = new CargoImporteTotal(datosC1);

                //Costos 02
                datosC1 = new ArrayList<>(6);
                datosC1.add(rs.getDouble("car1_02"));
                datosC1.add(rs.getDouble("car2_02"));
                datosC1.add(rs.getDouble("car3_02"));
                datosC1.add(rs.getDouble("car4_02"));
                datosC1.add(rs.getDouble("car5_02"));
                datosC1.add(rs.getDouble("car6_02"));
                Cargos cargos2 = new Cargos(datosC1);

                //Costos Importe Total 02
                datosC1 = new ArrayList<>(1);
                datosC1.add(rs.getDouble("car_imp_tot_02"));
                CargoImporteTotal cargoImporteTotal2 = new CargoImporteTotal(datosC1);

                //ImpuestoElectrico
                List<Double> datosIE = new ArrayList<>();
                datosIE.add(rs.getDouble("ie_importe"));
                DatosImpuestoElectrico datosImpuestoElectrico = new DatosImpuestoElectrico(datosIE);

                //Alquileres
                List<Double> datosA = new ArrayList<>();
                datosA.add(rs.getDouble("a_imp_fact"));
                DatosAlquileres datosAlquileres = new DatosAlquileres(datosA);

                //Iva
                List<Double> datosI = new ArrayList<>();
                datosI.add(rs.getDouble("i_bas_imp"));
                DatosIva datosIva = new DatosIva(datosI);

                //AE Consumo
                List<Double> datosAEC = new ArrayList<>();
                datosAEC.add(rs.getDouble("ae_cons1"));
                datosAEC.add(rs.getDouble("ae_cons2"));
                datosAEC.add(rs.getDouble("ae_cons3"));
                datosAEC.add(rs.getDouble("ae_cons4"));
                datosAEC.add(rs.getDouble("ae_cons5"));
                datosAEC.add(rs.getDouble("ae_cons6"));
                datosAEC.add(rs.getDouble("ae_cons_sum"));
                DatosAeConsumo datosAeConsumo = new DatosAeConsumo(datosAEC);

                //AE LecturaDesde
                List<Double> datosAELD = new ArrayList<>();
                datosAELD.add(rs.getDouble("ae_lec_des1"));
                datosAELD.add(rs.getDouble("ae_lec_des2"));
                datosAELD.add(rs.getDouble("ae_lec_des3"));
                datosAELD.add(rs.getDouble("ae_lec_des4"));
                datosAELD.add(rs.getDouble("ae_lec_des5"));
                datosAELD.add(rs.getDouble("ae_lec_des6"));
                DatosAeLecturaDesde datosAeLecturaDesde = new DatosAeLecturaDesde(datosAELD);

                //AE LecturaHasta
                List<Double> datosAELH = new ArrayList<>();
                datosAELH.add(rs.getDouble("ae_lec_has1"));
                datosAELH.add(rs.getDouble("ae_lec_has2"));
                datosAELH.add(rs.getDouble("ae_lec_has3"));
                datosAELH.add(rs.getDouble("ae_lec_has4"));
                datosAELH.add(rs.getDouble("ae_lec_has5"));
                datosAELH.add(rs.getDouble("ae_lec_has6"));
                DatosAeLecturaHasta datosAeLecturaHasta = new DatosAeLecturaHasta(datosAELH);

                //AE ProcedenciaDesde
                List<Integer> datosAEPD = new ArrayList<>();
                datosAEPD.add(rs.getInt("ae_pro_des"));
                DatosAeProcedenciaDesde datosAeProcedenciaDesde = new DatosAeProcedenciaDesde(datosAEPD);

                //AE ProcedenciaHasta
                List<Integer> datosAEPH = new ArrayList<>();
                datosAEPH.add(rs.getInt("ae_pro_has"));
                DatosAeProcedenciaHasta datosAeProcedenciaHasta = new DatosAeProcedenciaHasta(datosAEPH);

                //R Consumo
                List<Double> datosRC = new ArrayList<>();
                datosRC.add(rs.getDouble("r_con1"));
                datosRC.add(rs.getDouble("r_con2"));
                datosRC.add(rs.getDouble("r_con3"));
                datosRC.add(rs.getDouble("r_con4"));
                datosRC.add(rs.getDouble("r_con5"));
                datosRC.add(rs.getDouble("r_con6"));
                datosRC.add(rs.getDouble("r_con_sum"));
                DatosRConsumo datosRConsumo = new DatosRConsumo(datosRC);

                //R LecturaDesde
                List<Double> datosRLD = new ArrayList<>();
                datosRLD.add(rs.getDouble("r_lec_des1"));
                datosRLD.add(rs.getDouble("r_lec_des2"));
                datosRLD.add(rs.getDouble("r_lec_des3"));
                datosRLD.add(rs.getDouble("r_lec_des4"));
                datosRLD.add(rs.getDouble("r_lec_des5"));
                datosRLD.add(rs.getDouble("r_lec_des6"));
                DatosRLecturaDesde datosRLecturaDesde = new DatosRLecturaDesde(datosRLD);

                //R LecturaHasta
                List<Double> datosRLH = new ArrayList<>();
                datosRLH.add(rs.getDouble("r_lec_has1"));
                datosRLH.add(rs.getDouble("r_lec_has2"));
                datosRLH.add(rs.getDouble("r_lec_has3"));
                datosRLH.add(rs.getDouble("r_lec_has4"));
                datosRLH.add(rs.getDouble("r_lec_has5"));
                datosRLH.add(rs.getDouble("r_lec_has6"));
                DatosRLecturaHasta datosRLecturaHasta = new DatosRLecturaHasta(datosRLH);

                //R Importe Total
                ArrayList<String> datosRIT = new ArrayList<>(1);
                datosRIT.add(rs.getString("r_imp_tot"));
                ReactivaImporteTotal reactivaImporteTotal = new ReactivaImporteTotal(datosRIT);

                //PM consumo
                List<Double> datosPMC = new ArrayList<>();
                datosPMC.add(rs.getDouble("pm_con1"));
                datosPMC.add(rs.getDouble("pm_con2"));
                datosPMC.add(rs.getDouble("pm_con3"));
                datosPMC.add(rs.getDouble("pm_con4"));
                datosPMC.add(rs.getDouble("pm_con5"));
                datosPMC.add(rs.getDouble("pm_con6"));
                datosPMC.add(rs.getDouble("pm_con_sum"));
                DatosPmConsumo datosPmConsumo = new DatosPmConsumo(datosPMC);

                //PM LecturaHasta
                List<Double> datosPMLH = new ArrayList<>();
                datosPMLH.add(rs.getDouble("pm_lec_has1"));
                datosPMLH.add(rs.getDouble("pm_lec_has2"));
                datosPMLH.add(rs.getDouble("pm_lec_has3"));
                datosPMLH.add(rs.getDouble("pm_lec_has4"));
                datosPMLH.add(rs.getDouble("pm_lec_has5"));
                datosPMLH.add(rs.getDouble("pm_lec_has6"));
                DatosPmLecturaHasta datosPmLecturaHasta = new DatosPmLecturaHasta(datosPMLH);

                //RegistroFin
                List<String> datosRF = new ArrayList<>();
                datosRF.add(rs.getString("rf_imp_tot"));
                datosRF.add(rs.getString("rf_sal_tot_fac"));
                datosRF.add(rs.getString("rf_tot_rec"));
                datosRF.add(rs.getString("rf_fec_val"));
                datosRF.add(rs.getString("rf_fec_lim_pag"));
                datosRF.add(rs.getString("rf_id_rem"));
                DatosFinDeRegistro datosFinDeRegistro = new DatosFinDeRegistro(datosRF);

                //Comentarios
                String comentarios = rs.getString("comentarios");

                //Errores
                String errores = rs.getString("id_error");

                DocumentoXml documentoXml = new DocumentoXml(cliente, datosCabecera, datosGeneralesFactura, datosFacturaAtr,
                        datosExcesoPotencia, datosPotenciaContratada, datosPotenciaMaxDemandada, datosPotenciaAFacturar, datosPotenciaPrecio, datosPotenciaImporteTotal,
                        datosEnergiaActiva, datosEnergiaActivaValores, datosEnergiaActivaPrecio, datosEnergiaActivaImporteTotal,
                        cargos1, cargos2, cargoImporteTotal1, cargoImporteTotal2,
                        datosImpuestoElectrico, datosAlquileres, datosIva,
                        datosAeConsumo, datosAeLecturaDesde, datosAeLecturaHasta, datosAeProcedenciaDesde, datosAeProcedenciaHasta,
                        datosRConsumo, datosRLecturaDesde, datosRLecturaHasta, reactivaImporteTotal,
                        datosPmConsumo, datosPmLecturaHasta,
                        datosFinDeRegistro,
                        comentarios, errores);

                documentos.add(documentoXml);
            }

        } catch (SQLException ex) {
            System.out.println("(ContenidoXmlDao). No se encontr?? ning??n registro");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return documentos;
    }

    public DocumentoXml buscarCodFiscal(String cod, int emisora) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DocumentoXml documento = null;

        String tabla;

        switch (emisora) {
            case 894:
                tabla = "contenido_xml_factura";
                break;
            default:
                tabla = "contenido_xml";
                break;
        }

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " + tabla + SQL_SELECT_BY_COD_FISCAL);
            stmt.setString(1, cod);
            stmt.setString(2, cod.substring(0, cod.length() - 2) + "-A");
            rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Registro vacio");
            } else {
                rs.absolute(1);

                //while (rs.next()) {
                //Cliente
                Cliente cliente = new ClienteDao().encontrarCups(new Cliente(rs.getString("c_cups")));

                //DatosCabecera
                List<String> datosC = new ArrayList<>();
                datosC.add(rs.getString("c_emp_emi"));
                datosC.add(rs.getString("c_emp_des"));
                datosC.add(rs.getString("c_cod_pro"));
                datosC.add(rs.getString("c_cod_pas"));
                datosC.add(rs.getString("c_cod_sol"));
                datosC.add(rs.getString("c_fec_sol"));
                datosC.add(rs.getString("c_cups"));
                DatosCabecera datosCabecera = new DatosCabecera(datosC);

                //DatosGenerales
                List<String> datosGF = new ArrayList<>();
                datosGF.add(rs.getString("codigo_fiscal_factura"));
                datosGF.add(rs.getString("tipo_factura"));
                datosGF.add(rs.getString("motivo_facturacion"));
                datosGF.add(rs.getString("codigo_factura_rectificada_anulada"));
                datosGF.add(rs.getString("fecha_factura"));
                DatosGeneralesFactura datosGeneralesFactura = new DatosGeneralesFactura(datosGF);

                //DatosFacturaATR
                List<String> datosATR = new ArrayList<>();
                datosATR.add(rs.getString("tarifa_atr_fact"));
                datosATR.add(rs.getString("modo_control_potencia"));
                datosATR.add(rs.getString("marca_medida_con_perdidas"));
                datosATR.add(rs.getString("vas_trafo"));
                datosATR.add(rs.getString("porcentaje_perdidas"));
                datosATR.add(rs.getString("numero_dias"));
                DatosFacturaAtr datosFacturaAtr = new DatosFacturaAtr(datosATR);

                //ExcesosPotencia
                List<Double> datosEP = new ArrayList<>();
                datosEP.add(rs.getDouble("exceso_potencia1"));
                datosEP.add(rs.getDouble("exceso_potencia2"));
                datosEP.add(rs.getDouble("exceso_potencia3"));
                datosEP.add(rs.getDouble("exceso_potencia4"));
                datosEP.add(rs.getDouble("exceso_potencia5"));
                datosEP.add(rs.getDouble("exceso_potencia6"));
                datosEP.add(rs.getDouble("exceso_importe_total"));
                DatosExcesoPotencia datosExcesoPotencia = new DatosExcesoPotencia(datosEP);

                //PotenciaContratada
                List<Double> datosPC = new ArrayList<>();
                datosPC.add(rs.getDouble("potencia_contratada1"));
                datosPC.add(rs.getDouble("potencia_contratada2"));
                datosPC.add(rs.getDouble("potencia_contratada3"));
                datosPC.add(rs.getDouble("potencia_contratada4"));
                datosPC.add(rs.getDouble("potencia_contratada5"));
                datosPC.add(rs.getDouble("potencia_contratada6"));
                DatosPotenciaContratada datosPotenciaContratada = new DatosPotenciaContratada(datosPC);

                //Potencia Maxima demandada
                List<Double> datosPMD = new ArrayList<>();
                datosPMD.add(rs.getDouble("potencia_max1"));
                datosPMD.add(rs.getDouble("potencia_max2"));
                datosPMD.add(rs.getDouble("potencia_max3"));
                datosPMD.add(rs.getDouble("potencia_max4"));
                datosPMD.add(rs.getDouble("potencia_max5"));
                datosPMD.add(rs.getDouble("potencia_max6"));
                DatosPotenciaMaxDemandada datosPotenciaMaxDemandada = new DatosPotenciaMaxDemandada(datosPMD);

                //Potencia a facturar
                List<Double> datosPF = new ArrayList<>();
                datosPF.add(rs.getDouble("potencia_fac1"));
                datosPF.add(rs.getDouble("potencia_fac2"));
                datosPF.add(rs.getDouble("potencia_fac3"));
                DatosPotenciaAFacturar datosPotenciaAFacturar = new DatosPotenciaAFacturar(datosPF);

                //Precio potencia
                List<Double> datosPP = new ArrayList<>();
                datosPP.add(rs.getDouble("potencia_pre1"));
                datosPP.add(rs.getDouble("potencia_pre2"));
                datosPP.add(rs.getDouble("potencia_pre3"));
                datosPP.add(rs.getDouble("potencia_pre4"));
                datosPP.add(rs.getDouble("potencia_pre5"));
                datosPP.add(rs.getDouble("potencia_pre6"));
                DatosPotenciaPrecio datosPotenciaPrecio = new DatosPotenciaPrecio(datosPP);

                //Potencia Importe Total
                List<Double> datosPIT = new ArrayList<>();
                datosPIT.add(rs.getDouble("potencia_imp_tot"));
                DatosPotenciaImporteTotal datosPotenciaImporteTotal = new DatosPotenciaImporteTotal(datosPIT);

                //DatosEnergiaActiva
                List<String> datosEA = new ArrayList<>();
                datosEA.add(rs.getString("ea_fecha_desde1"));
                datosEA.add(rs.getString("ea_fecha_hasta1"));
                datosEA.add(rs.getString("ea_fecha_desde2"));
                datosEA.add(rs.getString("ea_fecha_hasta2"));
                DatosEnergiaActiva datosEnergiaActiva = new DatosEnergiaActiva(datosEA);

                //EnergiaActivaValor
                List<Double> datosEAV = new ArrayList<>();
                datosEAV.add(rs.getDouble("ea_val1"));
                datosEAV.add(rs.getDouble("ea_val2"));
                datosEAV.add(rs.getDouble("ea_val3"));
                datosEAV.add(rs.getDouble("ea_val4"));
                datosEAV.add(rs.getDouble("ea_val5"));
                datosEAV.add(rs.getDouble("ea_val6"));
                datosEAV.add(rs.getDouble("ea_val_sum"));
                DatosEnergiaActivaValores datosEnergiaActivaValores = new DatosEnergiaActivaValores(datosEAV);

                //EnergiaActivaPrecio
                List<Double> datosEAP = new ArrayList<>();
                datosEAP.add(rs.getDouble("ea_pre1"));
                datosEAP.add(rs.getDouble("ea_pre2"));
                datosEAP.add(rs.getDouble("ea_pre3"));
                datosEAP.add(rs.getDouble("ea_pre4"));
                datosEAP.add(rs.getDouble("ea_pre5"));
                datosEAP.add(rs.getDouble("ea_pre6"));
                DatosEnergiaActivaPrecio datosEnergiaActivaPrecio = new DatosEnergiaActivaPrecio(datosEAP);

                //EnergiaActivaImporteTotal
                List<Double> datosIT = new ArrayList<>();
                datosIT.add(rs.getDouble("ea_imp_tot"));
                DatosEnergiaActivaImporteTotal datosEnergiaActivaImporteTotal = new DatosEnergiaActivaImporteTotal(datosIT);

                //Costos 01
                List<Double> datosC1 = new ArrayList<>(6);
                datosC1.add(rs.getDouble("car1_01"));
                datosC1.add(rs.getDouble("car2_01"));
                datosC1.add(rs.getDouble("car3_01"));
                datosC1.add(rs.getDouble("car4_01"));
                datosC1.add(rs.getDouble("car5_01"));
                datosC1.add(rs.getDouble("car6_01"));
                Cargos cargos1 = new Cargos(datosC1);

                //Costos Importe Total 01
                datosC1 = new ArrayList<>(1);
                datosC1.add(rs.getDouble("car_imp_tot_01"));
                CargoImporteTotal cargoImporteTotal1 = new CargoImporteTotal(datosC1);

                //Costos 02
                datosC1 = new ArrayList<>(6);
                datosC1.add(rs.getDouble("car1_02"));
                datosC1.add(rs.getDouble("car2_02"));
                datosC1.add(rs.getDouble("car3_02"));
                datosC1.add(rs.getDouble("car4_02"));
                datosC1.add(rs.getDouble("car5_02"));
                datosC1.add(rs.getDouble("car6_02"));
                Cargos cargos2 = new Cargos(datosC1);

                //Costos Importe Total 02
                datosC1 = new ArrayList<>(1);
                datosC1.add(rs.getDouble("car_imp_tot_02"));
                CargoImporteTotal cargoImporteTotal2 = new CargoImporteTotal(datosC1);

                //ImpuestoElectrico
                List<Double> datosIE = new ArrayList<>();
                datosIE.add(rs.getDouble("ie_importe"));
                DatosImpuestoElectrico datosImpuestoElectrico = new DatosImpuestoElectrico(datosIE);

                //Alquileres
                List<Double> datosA = new ArrayList<>();
                datosA.add(rs.getDouble("a_imp_fact"));
                DatosAlquileres datosAlquileres = new DatosAlquileres(datosA);

                //Iva
                List<Double> datosI = new ArrayList<>();
                datosI.add(rs.getDouble("i_bas_imp"));
                DatosIva datosIva = new DatosIva(datosI);

                //AE Consumo
                List<Double> datosAEC = new ArrayList<>();
                datosAEC.add(rs.getDouble("ae_cons1"));
                datosAEC.add(rs.getDouble("ae_cons2"));
                datosAEC.add(rs.getDouble("ae_cons3"));
                datosAEC.add(rs.getDouble("ae_cons4"));
                datosAEC.add(rs.getDouble("ae_cons5"));
                datosAEC.add(rs.getDouble("ae_cons6"));
                datosAEC.add(rs.getDouble("ae_cons_sum"));
                DatosAeConsumo datosAeConsumo = new DatosAeConsumo(datosAEC);

                //AE LecturaDesde
                List<Double> datosAELD = new ArrayList<>();
                datosAELD.add(rs.getDouble("ae_lec_des1"));
                datosAELD.add(rs.getDouble("ae_lec_des2"));
                datosAELD.add(rs.getDouble("ae_lec_des3"));
                datosAELD.add(rs.getDouble("ae_lec_des4"));
                datosAELD.add(rs.getDouble("ae_lec_des5"));
                datosAELD.add(rs.getDouble("ae_lec_des6"));
                DatosAeLecturaDesde datosAeLecturaDesde = new DatosAeLecturaDesde(datosAELD);

                //AE LecturaHasta
                List<Double> datosAELH = new ArrayList<>();
                datosAELH.add(rs.getDouble("ae_lec_has1"));
                datosAELH.add(rs.getDouble("ae_lec_has2"));
                datosAELH.add(rs.getDouble("ae_lec_has3"));
                datosAELH.add(rs.getDouble("ae_lec_has4"));
                datosAELH.add(rs.getDouble("ae_lec_has5"));
                datosAELH.add(rs.getDouble("ae_lec_has6"));
                DatosAeLecturaHasta datosAeLecturaHasta = new DatosAeLecturaHasta(datosAELH);

                //AE ProcedenciaDesde
                List<Integer> datosAEPD = new ArrayList<>();
                datosAEPD.add(rs.getInt("ae_pro_des"));
                DatosAeProcedenciaDesde datosAeProcedenciaDesde = new DatosAeProcedenciaDesde(datosAEPD);

                //AE ProcedenciaHasta
                List<Integer> datosAEPH = new ArrayList<>();
                datosAEPH.add(rs.getInt("ae_pro_has"));
                DatosAeProcedenciaHasta datosAeProcedenciaHasta = new DatosAeProcedenciaHasta(datosAEPH);

                //R Consumo
                List<Double> datosRC = new ArrayList<>();
                datosRC.add(rs.getDouble("r_con1"));
                datosRC.add(rs.getDouble("r_con2"));
                datosRC.add(rs.getDouble("r_con3"));
                datosRC.add(rs.getDouble("r_con4"));
                datosRC.add(rs.getDouble("r_con5"));
                datosRC.add(rs.getDouble("r_con6"));
                datosRC.add(rs.getDouble("r_con_sum"));
                DatosRConsumo datosRConsumo = new DatosRConsumo(datosRC);

                //R LecturaDesde
                List<Double> datosRLD = new ArrayList<>();
                datosRLD.add(rs.getDouble("r_lec_des1"));
                datosRLD.add(rs.getDouble("r_lec_des2"));
                datosRLD.add(rs.getDouble("r_lec_des3"));
                datosRLD.add(rs.getDouble("r_lec_des4"));
                datosRLD.add(rs.getDouble("r_lec_des5"));
                datosRLD.add(rs.getDouble("r_lec_des6"));
                DatosRLecturaDesde datosRLecturaDesde = new DatosRLecturaDesde(datosRLD);

                //R LecturaHasta
                List<Double> datosRLH = new ArrayList<>();
                datosRLH.add(rs.getDouble("r_lec_has1"));
                datosRLH.add(rs.getDouble("r_lec_has2"));
                datosRLH.add(rs.getDouble("r_lec_has3"));
                datosRLH.add(rs.getDouble("r_lec_has4"));
                datosRLH.add(rs.getDouble("r_lec_has5"));
                datosRLH.add(rs.getDouble("r_lec_has6"));
                DatosRLecturaHasta datosRLecturaHasta = new DatosRLecturaHasta(datosRLH);

                //R Importe Total
                ArrayList<String> datosRIT = new ArrayList<>(1);
                datosRIT.add(rs.getString("r_imp_tot"));
                ReactivaImporteTotal reactivaImporteTotal = new ReactivaImporteTotal(datosRIT);

                //PM consumo
                List<Double> datosPMC = new ArrayList<>();
                datosPMC.add(rs.getDouble("pm_con1"));
                datosPMC.add(rs.getDouble("pm_con2"));
                datosPMC.add(rs.getDouble("pm_con3"));
                datosPMC.add(rs.getDouble("pm_con4"));
                datosPMC.add(rs.getDouble("pm_con5"));
                datosPMC.add(rs.getDouble("pm_con6"));
                datosPMC.add(rs.getDouble("pm_con_sum"));
                DatosPmConsumo datosPmConsumo = new DatosPmConsumo(datosPMC);

                //PM LecturaHasta
                List<Double> datosPMLH = new ArrayList<>();
                datosPMLH.add(rs.getDouble("pm_lec_has1"));
                datosPMLH.add(rs.getDouble("pm_lec_has2"));
                datosPMLH.add(rs.getDouble("pm_lec_has3"));
                datosPMLH.add(rs.getDouble("pm_lec_has4"));
                datosPMLH.add(rs.getDouble("pm_lec_has5"));
                datosPMLH.add(rs.getDouble("pm_lec_has6"));
                DatosPmLecturaHasta datosPmLecturaHasta = new DatosPmLecturaHasta(datosPMLH);

                //RegistroFin
                List<String> datosRF = new ArrayList<>();
                datosRF.add(rs.getString("rf_imp_tot"));
                datosRF.add(rs.getString("rf_sal_tot_fac"));
                datosRF.add(rs.getString("rf_tot_rec"));
                datosRF.add(rs.getString("rf_fec_val"));
                datosRF.add(rs.getString("rf_fec_lim_pag"));
                datosRF.add(rs.getString("rf_id_rem"));
                DatosFinDeRegistro datosFinDeRegistro = new DatosFinDeRegistro(datosRF);

                //Comentarios
                String comentarios = rs.getString("comentarios");

                //Errores
                String errores = rs.getString("id_error");

                documento = new DocumentoXml(cliente, datosCabecera, datosGeneralesFactura, datosFacturaAtr,
                        datosExcesoPotencia, datosPotenciaContratada, datosPotenciaMaxDemandada, datosPotenciaAFacturar, datosPotenciaPrecio, datosPotenciaImporteTotal,
                        datosEnergiaActiva, datosEnergiaActivaValores, datosEnergiaActivaPrecio, datosEnergiaActivaImporteTotal,
                        cargos1, cargos2, cargoImporteTotal1, cargoImporteTotal2,
                        datosImpuestoElectrico, datosAlquileres, datosIva,
                        datosAeConsumo, datosAeLecturaDesde, datosAeLecturaHasta, datosAeProcedenciaDesde, datosAeProcedenciaHasta,
                        datosRConsumo, datosRLecturaDesde, datosRLecturaHasta, reactivaImporteTotal,
                        datosPmConsumo, datosPmLecturaHasta,
                        datosFinDeRegistro,
                        comentarios, errores);
                //}
            }
        } catch (SQLException ex) {
            System.out.println("(ContenidoXmlDao). No se encontr?? ning??n registro");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return documento;
    }

    public List<DocumentoXml> buscarIdCliente(String id_cliente, int emisora) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<DocumentoXml> documentos = new ArrayList<>();
        try {
            conn = Conexion.getConnection();

            if (emisora == 894) {
                stmt = conn.prepareStatement(SQL_SELECT_BY_ID_CLIENTE_0894);
            } else {
                stmt = conn.prepareStatement(SQL_SELECT_BY_ID_CLIENTE);
            }

            stmt.setString(1, id_cliente);
            rs = stmt.executeQuery();

            while (rs.next()) {
                //Cliente
                Cliente cliente = new ClienteDao().encontrarCups(new Cliente(rs.getString("c_cups")));

                //DatosCabecera
                List<String> datosC = new ArrayList<>();
                datosC.add(rs.getString("c_emp_emi"));
                datosC.add(rs.getString("c_emp_des"));
                datosC.add(rs.getString("c_cod_pro"));
                datosC.add(rs.getString("c_cod_pas"));
                datosC.add(rs.getString("c_cod_sol"));
                datosC.add(rs.getString("c_fec_sol"));
                datosC.add(rs.getString("c_cups"));
                DatosCabecera datosCabecera = new DatosCabecera(datosC);

                //DatosGenerales
                List<String> datosGF = new ArrayList<>();
                datosGF.add(rs.getString("codigo_fiscal_factura"));
                datosGF.add(rs.getString("tipo_factura"));
                datosGF.add(rs.getString("motivo_facturacion"));
                datosGF.add(rs.getString("codigo_factura_rectificada_anulada"));
                datosGF.add(rs.getString("fecha_factura"));
                DatosGeneralesFactura datosGeneralesFactura = new DatosGeneralesFactura(datosGF);

                //DatosFacturaATR
                List<String> datosATR = new ArrayList<>();
                datosATR.add(rs.getString("tarifa_atr_fact"));
                datosATR.add(rs.getString("modo_control_potencia"));
                datosATR.add(rs.getString("marca_medida_con_perdidas"));
                datosATR.add(rs.getString("vas_trafo"));
                datosATR.add(rs.getString("porcentaje_perdidas"));
                datosATR.add(rs.getString("numero_dias"));
                DatosFacturaAtr datosFacturaAtr = new DatosFacturaAtr(datosATR);

                //ExcesosPotencia
                List<Double> datosEP = new ArrayList<>();
                datosEP.add(rs.getDouble("exceso_potencia1"));
                datosEP.add(rs.getDouble("exceso_potencia2"));
                datosEP.add(rs.getDouble("exceso_potencia3"));
                datosEP.add(rs.getDouble("exceso_potencia4"));
                datosEP.add(rs.getDouble("exceso_potencia5"));
                datosEP.add(rs.getDouble("exceso_potencia6"));
                datosEP.add(rs.getDouble("exceso_importe_total"));
                DatosExcesoPotencia datosExcesoPotencia = new DatosExcesoPotencia(datosEP);

                //PotenciaContratada
                List<Double> datosPC = new ArrayList<>();
                datosPC.add(rs.getDouble("potencia_contratada1"));
                datosPC.add(rs.getDouble("potencia_contratada2"));
                datosPC.add(rs.getDouble("potencia_contratada3"));
                datosPC.add(rs.getDouble("potencia_contratada4"));
                datosPC.add(rs.getDouble("potencia_contratada5"));
                datosPC.add(rs.getDouble("potencia_contratada6"));
                DatosPotenciaContratada datosPotenciaContratada = new DatosPotenciaContratada(datosPC);

                //Potencia Maxima demandada
                List<Double> datosPMD = new ArrayList<>();
                datosPMD.add(rs.getDouble("potencia_max1"));
                datosPMD.add(rs.getDouble("potencia_max2"));
                datosPMD.add(rs.getDouble("potencia_max3"));
                datosPMD.add(rs.getDouble("potencia_max4"));
                datosPMD.add(rs.getDouble("potencia_max5"));
                datosPMD.add(rs.getDouble("potencia_max6"));
                DatosPotenciaMaxDemandada datosPotenciaMaxDemandada = new DatosPotenciaMaxDemandada(datosPMD);

                //Potencia a facturar
                List<Double> datosPF = new ArrayList<>();
                datosPF.add(rs.getDouble("potencia_fac1"));
                datosPF.add(rs.getDouble("potencia_fac2"));
                datosPF.add(rs.getDouble("potencia_fac3"));
                DatosPotenciaAFacturar datosPotenciaAFacturar = new DatosPotenciaAFacturar(datosPF);

                //Precio potencia
                List<Double> datosPP = new ArrayList<>();
                datosPP.add(rs.getDouble("potencia_pre1"));
                datosPP.add(rs.getDouble("potencia_pre2"));
                datosPP.add(rs.getDouble("potencia_pre3"));
                datosPP.add(rs.getDouble("potencia_pre4"));
                datosPP.add(rs.getDouble("potencia_pre5"));
                datosPP.add(rs.getDouble("potencia_pre6"));
                DatosPotenciaPrecio datosPotenciaPrecio = new DatosPotenciaPrecio(datosPP);

                //Potencia Importe Total
                List<Double> datosPIT = new ArrayList<>();
                datosPIT.add(rs.getDouble("potencia_imp_tot"));
                DatosPotenciaImporteTotal datosPotenciaImporteTotal = new DatosPotenciaImporteTotal(datosPIT);

                //DatosEnergiaActiva
                List<String> datosEA = new ArrayList<>();
                datosEA.add(rs.getString("ea_fecha_desde1"));
                datosEA.add(rs.getString("ea_fecha_hasta1"));
                datosEA.add(rs.getString("ea_fecha_desde2"));
                datosEA.add(rs.getString("ea_fecha_hasta2"));
                DatosEnergiaActiva datosEnergiaActiva = new DatosEnergiaActiva(datosEA);

                //EnergiaActivaValor
                List<Double> datosEAV = new ArrayList<>();
                datosEAV.add(rs.getDouble("ea_val1"));
                datosEAV.add(rs.getDouble("ea_val2"));
                datosEAV.add(rs.getDouble("ea_val3"));
                datosEAV.add(rs.getDouble("ea_val4"));
                datosEAV.add(rs.getDouble("ea_val5"));
                datosEAV.add(rs.getDouble("ea_val6"));
                datosEAV.add(rs.getDouble("ea_val_sum"));
                DatosEnergiaActivaValores datosEnergiaActivaValores = new DatosEnergiaActivaValores(datosEAV);

                //EnergiaActivaPrecio
                List<Double> datosEAP = new ArrayList<>();
                datosEAP.add(rs.getDouble("ea_pre1"));
                datosEAP.add(rs.getDouble("ea_pre2"));
                datosEAP.add(rs.getDouble("ea_pre3"));
                datosEAP.add(rs.getDouble("ea_pre4"));
                datosEAP.add(rs.getDouble("ea_pre5"));
                datosEAP.add(rs.getDouble("ea_pre6"));
                DatosEnergiaActivaPrecio datosEnergiaActivaPrecio = new DatosEnergiaActivaPrecio(datosEAP);

                //EnergiaActivaImporteTotal
                List<Double> datosIT = new ArrayList<>();
                datosIT.add(rs.getDouble("ea_imp_tot"));
                DatosEnergiaActivaImporteTotal datosEnergiaActivaImporteTotal = new DatosEnergiaActivaImporteTotal(datosIT);

                //Costos 01
                List<Double> datosC1 = new ArrayList<>(6);
                datosC1.add(rs.getDouble("car1_01"));
                datosC1.add(rs.getDouble("car2_01"));
                datosC1.add(rs.getDouble("car3_01"));
                datosC1.add(rs.getDouble("car4_01"));
                datosC1.add(rs.getDouble("car5_01"));
                datosC1.add(rs.getDouble("car6_01"));
                Cargos cargos1 = new Cargos(datosC1);

                //Costos Importe Total 01
                datosC1 = new ArrayList<>(1);
                datosC1.add(rs.getDouble("car_imp_tot_01"));
                CargoImporteTotal cargoImporteTotal1 = new CargoImporteTotal(datosC1);

                //Costos 02
                datosC1 = new ArrayList<>(6);
                datosC1.add(rs.getDouble("car1_02"));
                datosC1.add(rs.getDouble("car2_02"));
                datosC1.add(rs.getDouble("car3_02"));
                datosC1.add(rs.getDouble("car4_02"));
                datosC1.add(rs.getDouble("car5_02"));
                datosC1.add(rs.getDouble("car6_02"));
                Cargos cargos2 = new Cargos(datosC1);

                //Costos Importe Total 02
                datosC1 = new ArrayList<>(1);
                datosC1.add(rs.getDouble("car_imp_tot_02"));
                CargoImporteTotal cargoImporteTotal2 = new CargoImporteTotal(datosC1);

                //ImpuestoElectrico
                List<Double> datosIE = new ArrayList<>();
                datosIE.add(rs.getDouble("ie_importe"));
                DatosImpuestoElectrico datosImpuestoElectrico = new DatosImpuestoElectrico(datosIE);

                //Alquileres
                List<Double> datosA = new ArrayList<>();
                datosA.add(rs.getDouble("a_imp_fact"));
                DatosAlquileres datosAlquileres = new DatosAlquileres(datosA);

                //Iva
                List<Double> datosI = new ArrayList<>();
                datosI.add(rs.getDouble("i_bas_imp"));
                DatosIva datosIva = new DatosIva(datosI);

                //AE Consumo
                List<Double> datosAEC = new ArrayList<>();
                datosAEC.add(rs.getDouble("ae_cons1"));
                datosAEC.add(rs.getDouble("ae_cons2"));
                datosAEC.add(rs.getDouble("ae_cons3"));
                datosAEC.add(rs.getDouble("ae_cons4"));
                datosAEC.add(rs.getDouble("ae_cons5"));
                datosAEC.add(rs.getDouble("ae_cons6"));
                datosAEC.add(rs.getDouble("ae_cons_sum"));
                DatosAeConsumo datosAeConsumo = new DatosAeConsumo(datosAEC);

                //AE LecturaDesde
                List<Double> datosAELD = new ArrayList<>();
                datosAELD.add(rs.getDouble("ae_lec_des1"));
                datosAELD.add(rs.getDouble("ae_lec_des2"));
                datosAELD.add(rs.getDouble("ae_lec_des3"));
                datosAELD.add(rs.getDouble("ae_lec_des4"));
                datosAELD.add(rs.getDouble("ae_lec_des5"));
                datosAELD.add(rs.getDouble("ae_lec_des6"));
                DatosAeLecturaDesde datosAeLecturaDesde = new DatosAeLecturaDesde(datosAELD);

                //AE LecturaHasta
                List<Double> datosAELH = new ArrayList<>();
                datosAELH.add(rs.getDouble("ae_lec_has1"));
                datosAELH.add(rs.getDouble("ae_lec_has2"));
                datosAELH.add(rs.getDouble("ae_lec_has3"));
                datosAELH.add(rs.getDouble("ae_lec_has4"));
                datosAELH.add(rs.getDouble("ae_lec_has5"));
                datosAELH.add(rs.getDouble("ae_lec_has6"));
                DatosAeLecturaHasta datosAeLecturaHasta = new DatosAeLecturaHasta(datosAELH);

                //AE ProcedenciaDesde
                List<Integer> datosAEPD = new ArrayList<>();
                datosAEPD.add(rs.getInt("ae_pro_des"));
                DatosAeProcedenciaDesde datosAeProcedenciaDesde = new DatosAeProcedenciaDesde(datosAEPD);

                //AE ProcedenciaHasta
                List<Integer> datosAEPH = new ArrayList<>();
                datosAEPH.add(rs.getInt("ae_pro_has"));
                DatosAeProcedenciaHasta datosAeProcedenciaHasta = new DatosAeProcedenciaHasta(datosAEPH);

                //R Consumo
                List<Double> datosRC = new ArrayList<>();
                datosRC.add(rs.getDouble("r_con1"));
                datosRC.add(rs.getDouble("r_con2"));
                datosRC.add(rs.getDouble("r_con3"));
                datosRC.add(rs.getDouble("r_con4"));
                datosRC.add(rs.getDouble("r_con5"));
                datosRC.add(rs.getDouble("r_con6"));
                datosRC.add(rs.getDouble("r_con_sum"));
                DatosRConsumo datosRConsumo = new DatosRConsumo(datosRC);

                //R LecturaDesde
                List<Double> datosRLD = new ArrayList<>();
                datosRLD.add(rs.getDouble("r_lec_des1"));
                datosRLD.add(rs.getDouble("r_lec_des2"));
                datosRLD.add(rs.getDouble("r_lec_des3"));
                datosRLD.add(rs.getDouble("r_lec_des4"));
                datosRLD.add(rs.getDouble("r_lec_des5"));
                datosRLD.add(rs.getDouble("r_lec_des6"));
                DatosRLecturaDesde datosRLecturaDesde = new DatosRLecturaDesde(datosRLD);

                //R LecturaHasta
                List<Double> datosRLH = new ArrayList<>();
                datosRLH.add(rs.getDouble("r_lec_has1"));
                datosRLH.add(rs.getDouble("r_lec_has2"));
                datosRLH.add(rs.getDouble("r_lec_has3"));
                datosRLH.add(rs.getDouble("r_lec_has4"));
                datosRLH.add(rs.getDouble("r_lec_has5"));
                datosRLH.add(rs.getDouble("r_lec_has6"));
                DatosRLecturaHasta datosRLecturaHasta = new DatosRLecturaHasta(datosRLH);

                //R Importe Total
                ArrayList<String> datosRIT = new ArrayList<>(1);
                datosRIT.add(rs.getString("r_imp_tot"));
                ReactivaImporteTotal reactivaImporteTotal = new ReactivaImporteTotal(datosRIT);

                //PM consumo
                List<Double> datosPMC = new ArrayList<>();
                datosPMC.add(rs.getDouble("pm_con1"));
                datosPMC.add(rs.getDouble("pm_con2"));
                datosPMC.add(rs.getDouble("pm_con3"));
                datosPMC.add(rs.getDouble("pm_con4"));
                datosPMC.add(rs.getDouble("pm_con5"));
                datosPMC.add(rs.getDouble("pm_con6"));
                datosPMC.add(rs.getDouble("pm_con_sum"));
                DatosPmConsumo datosPmConsumo = new DatosPmConsumo(datosPMC);

                //PM LecturaHasta
                List<Double> datosPMLH = new ArrayList<>();
                datosPMLH.add(rs.getDouble("pm_lec_has1"));
                datosPMLH.add(rs.getDouble("pm_lec_has2"));
                datosPMLH.add(rs.getDouble("pm_lec_has3"));
                datosPMLH.add(rs.getDouble("pm_lec_has4"));
                datosPMLH.add(rs.getDouble("pm_lec_has5"));
                datosPMLH.add(rs.getDouble("pm_lec_has6"));
                DatosPmLecturaHasta datosPmLecturaHasta = new DatosPmLecturaHasta(datosPMLH);

                //RegistroFin
                List<String> datosRF = new ArrayList<>();
                datosRF.add(rs.getString("rf_imp_tot"));
                datosRF.add(rs.getString("rf_sal_tot_fac"));
                datosRF.add(rs.getString("rf_tot_rec"));
                datosRF.add(rs.getString("rf_fec_val"));
                datosRF.add(rs.getString("rf_fec_lim_pag"));
                datosRF.add(rs.getString("rf_id_rem"));
                DatosFinDeRegistro datosFinDeRegistro = new DatosFinDeRegistro(datosRF);

                //Comentarios
                String comentarios = rs.getString("comentarios");

                //Errores
                String errores = rs.getString("id_error");

                DocumentoXml documentoXml = new DocumentoXml(cliente, datosCabecera, datosGeneralesFactura, datosFacturaAtr,
                        datosExcesoPotencia, datosPotenciaContratada, datosPotenciaMaxDemandada, datosPotenciaAFacturar, datosPotenciaPrecio, datosPotenciaImporteTotal,
                        datosEnergiaActiva, datosEnergiaActivaValores, datosEnergiaActivaPrecio, datosEnergiaActivaImporteTotal,
                        cargos1, cargos2, cargoImporteTotal1, cargoImporteTotal2,
                        datosImpuestoElectrico, datosAlquileres, datosIva,
                        datosAeConsumo, datosAeLecturaDesde, datosAeLecturaHasta, datosAeProcedenciaDesde, datosAeProcedenciaHasta,
                        datosRConsumo, datosRLecturaDesde, datosRLecturaHasta, reactivaImporteTotal,
                        datosPmConsumo, datosPmLecturaHasta,
                        datosFinDeRegistro,
                        comentarios, errores);

                documentos.add(documentoXml);
            }

        } catch (SQLException ex) {
            System.out.println("(ContenidoXmlDao). No se encontr?? ning??n registro");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return documentos;
    }

    public List<DocumentoXml> buscarRemesa(String remesa, int emisora) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<DocumentoXml> documentos = new ArrayList<>();
        try {
            conn = Conexion.getConnection();

            if (emisora == 894) {
                stmt = conn.prepareStatement(SQL_SELECT_BY_REMESA0894);
            } else {
                stmt = conn.prepareStatement(SQL_SELECT_BY_REMESA);
            }
            stmt.setString(1, remesa);
            rs = stmt.executeQuery();

            while (rs.next()) {
                //Cliente
                Cliente cliente = new ClienteDao().encontrarCups(new Cliente(rs.getString("c_cups")));

                //DatosCabecera
                List<String> datosC = new ArrayList<>();
                datosC.add(rs.getString("c_emp_emi"));
                datosC.add(rs.getString("c_emp_des"));
                datosC.add(rs.getString("c_cod_pro"));
                datosC.add(rs.getString("c_cod_pas"));
                datosC.add(rs.getString("c_cod_sol"));
                datosC.add(rs.getString("c_fec_sol"));
                datosC.add(rs.getString("c_cups"));
                DatosCabecera datosCabecera = new DatosCabecera(datosC);

                //DatosGenerales
                List<String> datosGF = new ArrayList<>();
                datosGF.add(rs.getString("codigo_fiscal_factura"));
                datosGF.add(rs.getString("tipo_factura"));
                datosGF.add(rs.getString("motivo_facturacion"));
                datosGF.add(rs.getString("codigo_factura_rectificada_anulada"));
                datosGF.add(rs.getString("fecha_factura"));
                DatosGeneralesFactura datosGeneralesFactura = new DatosGeneralesFactura(datosGF);

                //DatosFacturaATR
                List<String> datosATR = new ArrayList<>();
                datosATR.add(rs.getString("tarifa_atr_fact"));
                datosATR.add(rs.getString("modo_control_potencia"));
                datosATR.add(rs.getString("marca_medida_con_perdidas"));
                datosATR.add(rs.getString("vas_trafo"));
                datosATR.add(rs.getString("porcentaje_perdidas"));
                datosATR.add(rs.getString("numero_dias"));
                DatosFacturaAtr datosFacturaAtr = new DatosFacturaAtr(datosATR);

                //ExcesosPotencia
                List<Double> datosEP = new ArrayList<>();
                datosEP.add(rs.getDouble("exceso_potencia1"));
                datosEP.add(rs.getDouble("exceso_potencia2"));
                datosEP.add(rs.getDouble("exceso_potencia3"));
                datosEP.add(rs.getDouble("exceso_potencia4"));
                datosEP.add(rs.getDouble("exceso_potencia5"));
                datosEP.add(rs.getDouble("exceso_potencia6"));
                datosEP.add(rs.getDouble("exceso_importe_total"));
                DatosExcesoPotencia datosExcesoPotencia = new DatosExcesoPotencia(datosEP);

                //PotenciaContratada
                List<Double> datosPC = new ArrayList<>();
                datosPC.add(rs.getDouble("potencia_contratada1"));
                datosPC.add(rs.getDouble("potencia_contratada2"));
                datosPC.add(rs.getDouble("potencia_contratada3"));
                datosPC.add(rs.getDouble("potencia_contratada4"));
                datosPC.add(rs.getDouble("potencia_contratada5"));
                datosPC.add(rs.getDouble("potencia_contratada6"));
                DatosPotenciaContratada datosPotenciaContratada = new DatosPotenciaContratada(datosPC);

                //Potencia Maxima demandada
                List<Double> datosPMD = new ArrayList<>();
                datosPMD.add(rs.getDouble("potencia_max1"));
                datosPMD.add(rs.getDouble("potencia_max2"));
                datosPMD.add(rs.getDouble("potencia_max3"));
                datosPMD.add(rs.getDouble("potencia_max4"));
                datosPMD.add(rs.getDouble("potencia_max5"));
                datosPMD.add(rs.getDouble("potencia_max6"));
                DatosPotenciaMaxDemandada datosPotenciaMaxDemandada = new DatosPotenciaMaxDemandada(datosPMD);

                //Potencia a facturar
                List<Double> datosPF = new ArrayList<>();
                datosPF.add(rs.getDouble("potencia_fac1"));
                datosPF.add(rs.getDouble("potencia_fac2"));
                datosPF.add(rs.getDouble("potencia_fac3"));
                DatosPotenciaAFacturar datosPotenciaAFacturar = new DatosPotenciaAFacturar(datosPF);

                //Precio potencia
                List<Double> datosPP = new ArrayList<>();
                datosPP.add(rs.getDouble("potencia_pre1"));
                datosPP.add(rs.getDouble("potencia_pre2"));
                datosPP.add(rs.getDouble("potencia_pre3"));
                datosPP.add(rs.getDouble("potencia_pre4"));
                datosPP.add(rs.getDouble("potencia_pre5"));
                datosPP.add(rs.getDouble("potencia_pre6"));
                DatosPotenciaPrecio datosPotenciaPrecio = new DatosPotenciaPrecio(datosPP);

                //Potencia Importe Total
                List<Double> datosPIT = new ArrayList<>();
                datosPIT.add(rs.getDouble("potencia_imp_tot"));
                DatosPotenciaImporteTotal datosPotenciaImporteTotal = new DatosPotenciaImporteTotal(datosPIT);

                //DatosEnergiaActiva
                List<String> datosEA = new ArrayList<>();
                datosEA.add(rs.getString("ea_fecha_desde1"));
                datosEA.add(rs.getString("ea_fecha_hasta1"));
                datosEA.add(rs.getString("ea_fecha_desde2"));
                datosEA.add(rs.getString("ea_fecha_hasta2"));
                DatosEnergiaActiva datosEnergiaActiva = new DatosEnergiaActiva(datosEA);

                //EnergiaActivaValor
                List<Double> datosEAV = new ArrayList<>();
                datosEAV.add(rs.getDouble("ea_val1"));
                datosEAV.add(rs.getDouble("ea_val2"));
                datosEAV.add(rs.getDouble("ea_val3"));
                datosEAV.add(rs.getDouble("ea_val4"));
                datosEAV.add(rs.getDouble("ea_val5"));
                datosEAV.add(rs.getDouble("ea_val6"));
                datosEAV.add(rs.getDouble("ea_val_sum"));
                DatosEnergiaActivaValores datosEnergiaActivaValores = new DatosEnergiaActivaValores(datosEAV);

                //EnergiaActivaPrecio
                List<Double> datosEAP = new ArrayList<>();
                datosEAP.add(rs.getDouble("ea_pre1"));
                datosEAP.add(rs.getDouble("ea_pre2"));
                datosEAP.add(rs.getDouble("ea_pre3"));
                datosEAP.add(rs.getDouble("ea_pre4"));
                datosEAP.add(rs.getDouble("ea_pre5"));
                datosEAP.add(rs.getDouble("ea_pre6"));
                DatosEnergiaActivaPrecio datosEnergiaActivaPrecio = new DatosEnergiaActivaPrecio(datosEAP);

                //EnergiaActivaImporteTotal
                List<Double> datosIT = new ArrayList<>();
                datosIT.add(rs.getDouble("ea_imp_tot"));
                DatosEnergiaActivaImporteTotal datosEnergiaActivaImporteTotal = new DatosEnergiaActivaImporteTotal(datosIT);

                //Costos 01
                List<Double> datosC1 = new ArrayList<>(6);
                datosC1.add(rs.getDouble("car1_01"));
                datosC1.add(rs.getDouble("car2_01"));
                datosC1.add(rs.getDouble("car3_01"));
                datosC1.add(rs.getDouble("car4_01"));
                datosC1.add(rs.getDouble("car5_01"));
                datosC1.add(rs.getDouble("car6_01"));
                Cargos cargos1 = new Cargos(datosC1);

                //Costos Importe Total 01
                datosC1 = new ArrayList<>(1);
                datosC1.add(rs.getDouble("car_imp_tot_01"));
                CargoImporteTotal cargoImporteTotal1 = new CargoImporteTotal(datosC1);

                //Costos 02
                datosC1 = new ArrayList<>(6);
                datosC1.add(rs.getDouble("car1_02"));
                datosC1.add(rs.getDouble("car2_02"));
                datosC1.add(rs.getDouble("car3_02"));
                datosC1.add(rs.getDouble("car4_02"));
                datosC1.add(rs.getDouble("car5_02"));
                datosC1.add(rs.getDouble("car6_02"));
                Cargos cargos2 = new Cargos(datosC1);

                //Costos Importe Total 02
                datosC1 = new ArrayList<>(1);
                datosC1.add(rs.getDouble("car_imp_tot_02"));
                CargoImporteTotal cargoImporteTotal2 = new CargoImporteTotal(datosC1);

                //ImpuestoElectrico
                List<Double> datosIE = new ArrayList<>();
                datosIE.add(rs.getDouble("ie_importe"));
                DatosImpuestoElectrico datosImpuestoElectrico = new DatosImpuestoElectrico(datosIE);

                //Alquileres
                List<Double> datosA = new ArrayList<>();
                datosA.add(rs.getDouble("a_imp_fact"));
                DatosAlquileres datosAlquileres = new DatosAlquileres(datosA);

                //Iva
                List<Double> datosI = new ArrayList<>();
                datosI.add(rs.getDouble("i_bas_imp"));
                DatosIva datosIva = new DatosIva(datosI);

                //AE Consumo
                List<Double> datosAEC = new ArrayList<>();
                datosAEC.add(rs.getDouble("ae_cons1"));
                datosAEC.add(rs.getDouble("ae_cons2"));
                datosAEC.add(rs.getDouble("ae_cons3"));
                datosAEC.add(rs.getDouble("ae_cons4"));
                datosAEC.add(rs.getDouble("ae_cons5"));
                datosAEC.add(rs.getDouble("ae_cons6"));
                datosAEC.add(rs.getDouble("ae_cons_sum"));
                DatosAeConsumo datosAeConsumo = new DatosAeConsumo(datosAEC);

                //AE LecturaDesde
                List<Double> datosAELD = new ArrayList<>();
                datosAELD.add(rs.getDouble("ae_lec_des1"));
                datosAELD.add(rs.getDouble("ae_lec_des2"));
                datosAELD.add(rs.getDouble("ae_lec_des3"));
                datosAELD.add(rs.getDouble("ae_lec_des4"));
                datosAELD.add(rs.getDouble("ae_lec_des5"));
                datosAELD.add(rs.getDouble("ae_lec_des6"));
                DatosAeLecturaDesde datosAeLecturaDesde = new DatosAeLecturaDesde(datosAELD);

                //AE LecturaHasta
                List<Double> datosAELH = new ArrayList<>();
                datosAELH.add(rs.getDouble("ae_lec_has1"));
                datosAELH.add(rs.getDouble("ae_lec_has2"));
                datosAELH.add(rs.getDouble("ae_lec_has3"));
                datosAELH.add(rs.getDouble("ae_lec_has4"));
                datosAELH.add(rs.getDouble("ae_lec_has5"));
                datosAELH.add(rs.getDouble("ae_lec_has6"));
                DatosAeLecturaHasta datosAeLecturaHasta = new DatosAeLecturaHasta(datosAELH);

                //AE ProcedenciaDesde
                List<Integer> datosAEPD = new ArrayList<>();
                datosAEPD.add(rs.getInt("ae_pro_des"));
                DatosAeProcedenciaDesde datosAeProcedenciaDesde = new DatosAeProcedenciaDesde(datosAEPD);

                //AE ProcedenciaHasta
                List<Integer> datosAEPH = new ArrayList<>();
                datosAEPH.add(rs.getInt("ae_pro_has"));
                DatosAeProcedenciaHasta datosAeProcedenciaHasta = new DatosAeProcedenciaHasta(datosAEPH);

                //R Consumo
                List<Double> datosRC = new ArrayList<>();
                datosRC.add(rs.getDouble("r_con1"));
                datosRC.add(rs.getDouble("r_con2"));
                datosRC.add(rs.getDouble("r_con3"));
                datosRC.add(rs.getDouble("r_con4"));
                datosRC.add(rs.getDouble("r_con5"));
                datosRC.add(rs.getDouble("r_con6"));
                datosRC.add(rs.getDouble("r_con_sum"));
                DatosRConsumo datosRConsumo = new DatosRConsumo(datosRC);

                //R LecturaDesde
                List<Double> datosRLD = new ArrayList<>();
                datosRLD.add(rs.getDouble("r_lec_des1"));
                datosRLD.add(rs.getDouble("r_lec_des2"));
                datosRLD.add(rs.getDouble("r_lec_des3"));
                datosRLD.add(rs.getDouble("r_lec_des4"));
                datosRLD.add(rs.getDouble("r_lec_des5"));
                datosRLD.add(rs.getDouble("r_lec_des6"));
                DatosRLecturaDesde datosRLecturaDesde = new DatosRLecturaDesde(datosRLD);

                //R LecturaHasta
                List<Double> datosRLH = new ArrayList<>();
                datosRLH.add(rs.getDouble("r_lec_has1"));
                datosRLH.add(rs.getDouble("r_lec_has2"));
                datosRLH.add(rs.getDouble("r_lec_has3"));
                datosRLH.add(rs.getDouble("r_lec_has4"));
                datosRLH.add(rs.getDouble("r_lec_has5"));
                datosRLH.add(rs.getDouble("r_lec_has6"));
                DatosRLecturaHasta datosRLecturaHasta = new DatosRLecturaHasta(datosRLH);

                //R Importe Total
                ArrayList<String> datosRIT = new ArrayList<>(1);
                datosRIT.add(rs.getString("r_imp_tot"));
                ReactivaImporteTotal reactivaImporteTotal = new ReactivaImporteTotal(datosRIT);

                //PM consumo
                List<Double> datosPMC = new ArrayList<>();
                datosPMC.add(rs.getDouble("pm_con1"));
                datosPMC.add(rs.getDouble("pm_con2"));
                datosPMC.add(rs.getDouble("pm_con3"));
                datosPMC.add(rs.getDouble("pm_con4"));
                datosPMC.add(rs.getDouble("pm_con5"));
                datosPMC.add(rs.getDouble("pm_con6"));
                datosPMC.add(rs.getDouble("pm_con_sum"));
                DatosPmConsumo datosPmConsumo = new DatosPmConsumo(datosPMC);

                //PM LecturaHasta
                List<Double> datosPMLH = new ArrayList<>();
                datosPMLH.add(rs.getDouble("pm_lec_has1"));
                datosPMLH.add(rs.getDouble("pm_lec_has2"));
                datosPMLH.add(rs.getDouble("pm_lec_has3"));
                datosPMLH.add(rs.getDouble("pm_lec_has4"));
                datosPMLH.add(rs.getDouble("pm_lec_has5"));
                datosPMLH.add(rs.getDouble("pm_lec_has6"));
                DatosPmLecturaHasta datosPmLecturaHasta = new DatosPmLecturaHasta(datosPMLH);

                //RegistroFin
                List<String> datosRF = new ArrayList<>();
                datosRF.add(rs.getString("rf_imp_tot"));
                datosRF.add(rs.getString("rf_sal_tot_fac"));
                datosRF.add(rs.getString("rf_tot_rec"));
                datosRF.add(rs.getString("rf_fec_val"));
                datosRF.add(rs.getString("rf_fec_lim_pag"));
                datosRF.add(rs.getString("rf_id_rem"));
                DatosFinDeRegistro datosFinDeRegistro = new DatosFinDeRegistro(datosRF);

                //Comentarios
                String comentarios = rs.getString("comentarios");

                //Errores
                String errores = rs.getString("id_error");

                DocumentoXml documentoXml = new DocumentoXml(cliente, datosCabecera, datosGeneralesFactura, datosFacturaAtr,
                        datosExcesoPotencia, datosPotenciaContratada, datosPotenciaMaxDemandada, datosPotenciaAFacturar, datosPotenciaPrecio, datosPotenciaImporteTotal,
                        datosEnergiaActiva, datosEnergiaActivaValores, datosEnergiaActivaPrecio, datosEnergiaActivaImporteTotal,
                        cargos1, cargos2, cargoImporteTotal1, cargoImporteTotal2,
                        datosImpuestoElectrico, datosAlquileres, datosIva,
                        datosAeConsumo, datosAeLecturaDesde, datosAeLecturaHasta, datosAeProcedenciaDesde, datosAeProcedenciaHasta,
                        datosRConsumo, datosRLecturaDesde, datosRLecturaHasta, reactivaImporteTotal,
                        datosPmConsumo, datosPmLecturaHasta,
                        datosFinDeRegistro,
                        comentarios, errores);

                documentos.add(documentoXml);
            }

        } catch (SQLException ex) {
            System.out.println("(ContenidoXmlDao). No se encontr?? ning??n registro");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs, stmt, conn);
        }
        return documentos;
    }

    public int actualizarR(DocumentoXml documentoXml, String remesaNueva, String nombreArchivoNuevo) {
        //Preparaci??n de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;

        //Variables de proceso
        int rows = 0;
        String tablaXml;
        int i = 1;

        //Revisi??n de Empresa emisora
        switch (Integer.parseInt(documentoXml.getDatosCabecera().getCodigoREEEmpresaEmisora())) {
            case 894:
                tablaXml = "contenido_xml_factura";
                break;
            default:
                tablaXml = "contenido_xml";
                break;
        }

        try {
            //Conecta a la base de datos
            conn = Conexion.getConnection();

            //Preparaci??n del query
            stmt = conn.prepareStatement("INSERT INTO " + tablaXml + SQL_INSERT);

            //DatosCabecera
            stmt.setString(i++, documentoXml.getDatosCabecera().getCodigoREEEmpresaEmisora());
            stmt.setString(i++, documentoXml.getDatosCabecera().getCodigoREEEmpresaDestino());
            stmt.setString(i++, documentoXml.getDatosCabecera().getCodigoDelProceso());
            stmt.setString(i++, documentoXml.getDatosCabecera().getCodigoDePaso());
            stmt.setString(i++, documentoXml.getDatosCabecera().getCodigoDeSolicitud());
            stmt.setString(i++, documentoXml.getDatosCabecera().getFechaSolicitud());
            stmt.setString(i++, documentoXml.getDatosCabecera().getCups());

            //DatosGenerales
            stmt.setString(i++, documentoXml.getDatosGeneralesFactura().getCodigoFiscalFactura() + "-A");
            stmt.setString(i++, documentoXml.getDatosGeneralesFactura().getTipoFactura());
            stmt.setString(i++, documentoXml.getDatosGeneralesFactura().getMotivoFacturacion());
            stmt.setString(i++, documentoXml.getDatosGeneralesFactura().getCodigoFacturaRectificadaAnulada());
            stmt.setString(i++, documentoXml.getDatosGeneralesFactura().getFechaFactura());

            //DatosFacturaATR
            stmt.setString(i++, documentoXml.getDatosFacturaAtr().getTarifaAtrFact());
            stmt.setString(i++, documentoXml.getDatosFacturaAtr().getModoControlPotencia());
            stmt.setString(i++, documentoXml.getDatosFacturaAtr().getMarcaMedidaConPerdidas());
            stmt.setString(i++, documentoXml.getDatosFacturaAtr().getVAsTrafo());
            stmt.setString(i++, documentoXml.getDatosFacturaAtr().getPorcentajePerdidas());
            stmt.setInt(i++, documentoXml.getDatosFacturaAtr().getNumeroDias() * -1);

            //ExcesoPotencia
            stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP6() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getT() * -1));

            //DatosPotenciaContrada
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP6() * -1));

            //PotenciaMax
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP6() * -1));

            //PotenciaAFacturar
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaAFacturar().getP1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaAFacturar().getP2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaAFacturar().getP3() * -1));

            //Potencia Precio
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP1()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP2()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP3()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP4()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP5()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP6()));

            //Potencia Importe Total
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaImporteTotal().getImporteTotal() * -1));

            //DatosTerminoEnergiaActiva
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActiva().getFechaHasta1()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActiva().getFechaDesde1()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActiva().getFechaHasta2()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActiva().getFechaDesde2()));

            //EnergiaActiva Valores
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV6() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getTotal() * -1));

            //EnergiaActiva Precio
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP1()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP2()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP3()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP4()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP5()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP6()));

            //EnergiaActiva Importe Total
            stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaImporteTotal().getImporteTotal() * -1));

            //Impuesto Electrico
            stmt.setString(i++, String.valueOf(documentoXml.getDatosImpuestoElectrico().getImporte() * -1));

            //Alquileres
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAlquileres().getImporte() * -1));

            //Iva
            stmt.setString(i++, String.valueOf(documentoXml.getDatosIva().getBaseImponible()));

            //AE Consumo
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado6() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getSuma() * -1));

            //AE Lectura Desde
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura6() * -1));

            //AE Lectura Hasta
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura6() * -1));

            //AE Procedencia Desde
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeProcedenciaDesde().getProcedencia()));

            //AE Procedencia Hasta
            stmt.setString(i++, String.valueOf(documentoXml.getDatosAeProcedenciaHasta().getProcedencia()));

            //R Consumo
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo6() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getSuma() * -1));

            //R Lectura desde
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura6() * -1));

            //R LecturaHasta
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura6() * -1));

            //R ImporteTotal
            stmt.setString(i++, String.valueOf(documentoXml.getReactivaImporteTotal().getImporteTotal()));

            //PM Consumo
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo6() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getSuma() * -1));

            //PM Lectura Hasta
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura1() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura2() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura3() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura4() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura5() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura6() * -1));

            //Fin de registro
            stmt.setString(i++, String.valueOf(documentoXml.getDatosFinDeRegistro().getImporteTotal() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosFinDeRegistro().getSaldoTotalFacturacion() * -1));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosFinDeRegistro().getTotalRecibos()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosFinDeRegistro().getFechaValor()));
            stmt.setString(i++, String.valueOf(documentoXml.getDatosFinDeRegistro().getFechaLimitePago()));
            stmt.setString(i++, String.valueOf(remesaNueva));

            //Comentarios
            stmt.setString(i++, String.valueOf(documentoXml.getComentarios() + "El archivo ha sido rectificado por la factura <Strong>" + nombreArchivoNuevo + "</Strong> <br/>"
                    + "Se cambio la remesa <Strong>" + documentoXml.getDatosFinDeRegistro().getIdRemesa() + "</Strong> por <Strong>" + remesaNueva + "</Strong>."));

            //Errores
            stmt.setString(i++, String.valueOf(documentoXml.getErrores()));

            //Cliente
            stmt.setString(i++, String.valueOf(documentoXml.getCliente().getIdCliente()));

            //Condiciones
            //stmt.setString(130, documentoXml.getDatosGeneralesFactura().getCodigoFiscalFactura());
            //stmt.setString(131, documentoXml.getDatosGeneralesFactura().getCodigoFiscalFactura() + "-A");
            //Ejecuci??n del query
            rows = stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("(ContenidoXMLDao). Ha ocurrido un error al insertar un registro");
            ex.printStackTrace(System.out);
        } finally {
            //Cierra conexi??n
            Conexion.close(stmt, conn);
        }
        return rows;
    }

    public int insertar(DocumentoXml documentoXml) {
        //Preparaci??n de las variables necesarias para el programa
        Connection conn = null;
        PreparedStatement stmt = null;

        //Vaiables de uso
        int rows = 0;
        String tablaXml;

        //Revisi??n de Empresa emisora
        switch (Integer.parseInt(documentoXml.getDatosCabecera().getCodigoREEEmpresaEmisora())) {
            case 894:
                tablaXml = "contenido_xml_factura";
                break;
            default:
                tablaXml = "contenido_xml";
                break;
        }

        //Revisi??n de la existencia de la factura en las tablas
        if (new DatosFacturaDao().encontrarCodFiscal(documentoXml.getDatosGeneralesFactura().getCodigoFiscalFactura(), tablaXml)) {
            System.out.println("Ya existe la factura en la tabla " + tablaXml);
        } else {
            try {
                //Conecta a la base de datos
                conn = Conexion.getConnection();
                //Preparaci??n del query
                stmt = conn.prepareStatement("INSERT INTO " + tablaXml + SQL_INSERT);

                int i = 1;

                //DatosCabecera
                stmt.setString(i++, documentoXml.getDatosCabecera().getCodigoREEEmpresaEmisora());
                stmt.setString(i++, documentoXml.getDatosCabecera().getCodigoREEEmpresaDestino());
                stmt.setString(i++, documentoXml.getDatosCabecera().getCodigoDelProceso());
                stmt.setString(i++, documentoXml.getDatosCabecera().getCodigoDePaso());
                stmt.setString(i++, documentoXml.getDatosCabecera().getCodigoDeSolicitud());
                stmt.setString(i++, documentoXml.getDatosCabecera().getFechaSolicitud());
                stmt.setString(i++, documentoXml.getDatosCabecera().getCups());

                //DatosGenerales
                stmt.setString(i++, documentoXml.getDatosGeneralesFactura().getCodigoFiscalFactura());
                stmt.setString(i++, documentoXml.getDatosGeneralesFactura().getTipoFactura());
                stmt.setString(i++, documentoXml.getDatosGeneralesFactura().getMotivoFacturacion());
                stmt.setString(i++, documentoXml.getDatosGeneralesFactura().getCodigoFacturaRectificadaAnulada());
                stmt.setString(i++, documentoXml.getDatosGeneralesFactura().getFechaFactura());

                //DatosFacturaATR
                stmt.setString(i++, documentoXml.getDatosFacturaAtr().getTarifaAtrFact());
                stmt.setString(i++, documentoXml.getDatosFacturaAtr().getModoControlPotencia());
                stmt.setString(i++, documentoXml.getDatosFacturaAtr().getMarcaMedidaConPerdidas());
                stmt.setString(i++, documentoXml.getDatosFacturaAtr().getVAsTrafo());
                stmt.setString(i++, documentoXml.getDatosFacturaAtr().getPorcentajePerdidas());
                stmt.setInt(i++, documentoXml.getDatosFacturaAtr().getNumeroDias());

                //ExcesoPotencia
                stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getP6()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosExcesoPotencia().getT()));

                //DatosPotenciaContrada
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaContratada().getP6()));

                //PotenciaMax
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaMaxDemandada().getP6()));

                //PotenciaAFacturar
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaAFacturar().getP1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaAFacturar().getP2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaAFacturar().getP3()));

                //Potencia Precio
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaPrecio().getP6()));

                //Potencia Importe Total
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPotenciaImporteTotal().getImporteTotal()));

                //DatosTerminoEnergiaActiva
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActiva().getFechaDesde1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActiva().getFechaHasta1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActiva().getFechaDesde2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActiva().getFechaHasta2()));

                //EnergiaActiva Valores
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getV6()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaValores().getTotal()));

                //EnergiaActiva Precio
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaPrecio().getP6()));

                //EnergiaActiva Importe Total
                stmt.setString(i++, String.valueOf(documentoXml.getDatosEnergiaActivaImporteTotal().getImporteTotal()));

                //Costos 01
                stmt.setString(i++, String.valueOf(documentoXml.getCargos1().getC1()));
                stmt.setString(i++, String.valueOf(documentoXml.getCargos1().getC2()));
                stmt.setString(i++, String.valueOf(documentoXml.getCargos1().getC3()));
                stmt.setString(i++, String.valueOf(documentoXml.getCargos1().getC4()));
                stmt.setString(i++, String.valueOf(documentoXml.getCargos1().getC5()));
                stmt.setString(i++, String.valueOf(documentoXml.getCargos1().getC6()));

                //Costos importe total 01
                stmt.setString(i++, String.valueOf(documentoXml.getCargoImporteTotal1().getImpTot()));

                //Costos 01
                stmt.setString(i++, String.valueOf(documentoXml.getCargos2().getC1()));
                stmt.setString(i++, String.valueOf(documentoXml.getCargos2().getC2()));
                stmt.setString(i++, String.valueOf(documentoXml.getCargos2().getC3()));
                stmt.setString(i++, String.valueOf(documentoXml.getCargos2().getC4()));
                stmt.setString(i++, String.valueOf(documentoXml.getCargos2().getC5()));
                stmt.setString(i++, String.valueOf(documentoXml.getCargos2().getC6()));

                //Costos importe total 02
                stmt.setString(i++, String.valueOf(documentoXml.getCargoImporteTotal2().getImpTot()));

                //Impuesto Electrico
                stmt.setString(i++, String.valueOf(documentoXml.getDatosImpuestoElectrico().getImporte()));

                //Alquileres
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAlquileres().getImporte()));

                //Iva
                stmt.setString(i++, String.valueOf(documentoXml.getDatosIva().getBaseImponible()));

                //AE Consumo
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getConsumoCalculado6()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeConsumo().getSuma()));

                //AE Lectura Desde
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaDesde().getLectura6()));

                //AE Lectura Hasta
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeLecturaHasta().getLectura6()));

                //AE Procedencia Desde
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeProcedenciaDesde().getProcedencia()));

                //AE Procedencia Hasta
                stmt.setString(i++, String.valueOf(documentoXml.getDatosAeProcedenciaHasta().getProcedencia()));

                //R Consumo
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getConsumo6()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRConsumo().getSuma()));

                //R Lectura desde
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaDesde().getLectura6()));

                //R LecturaHasta
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosRLecturaHasta().getLectura6()));

                //R ImporteTotal
                stmt.setString(i++, String.valueOf(documentoXml.getReactivaImporteTotal().getImporteTotal()));

                //PM Consumo
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getConsumo6()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmConsumo().getSuma()));

                //PM Lectura Hasta
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura1()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura2()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura3()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura4()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura5()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosPmLecturaHasta().getLectura6()));

                //Fin de registro
                stmt.setString(i++, String.valueOf(documentoXml.getDatosFinDeRegistro().getImporteTotal()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosFinDeRegistro().getSaldoTotalFacturacion()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosFinDeRegistro().getTotalRecibos()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosFinDeRegistro().getFechaValor()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosFinDeRegistro().getFechaLimitePago()));
                stmt.setString(i++, String.valueOf(documentoXml.getDatosFinDeRegistro().getIdRemesa()));

                //Comentarios
                stmt.setString(i++, String.valueOf(documentoXml.getComentarios()));

                //Errores
                stmt.setString(i++, String.valueOf(documentoXml.getErrores()));

                //Cliente
                stmt.setString(i++, String.valueOf(documentoXml.getCliente().getIdCliente()));

                //Ejecuci??n del query
                rows = stmt.executeUpdate();

            } catch (SQLException ex) {
                System.out.println("(ContenidoXMLDao). Ha ocurrido un error al insertar un registro");
                ex.printStackTrace(System.out);
            } finally {
                //Cierra conexi??n
                Conexion.close(stmt, conn);
            }
        }
        return rows;
    }
}
