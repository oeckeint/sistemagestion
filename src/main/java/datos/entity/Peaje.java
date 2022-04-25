package datos.entity;

import dominio.componentesxml.*;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "contenido_xml")
public class Peaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcontenido_xml")
    private long idPeaje;

    @Column(name = "is_deleted")
    private int isDeleted;

    @Column(name = "id_cliente")
    private long idCliente;

    @Column(name = "c_emp_emi")
    private String codEmpEmi;

    @Column(name = "c_emp_des")
    private String codEmpDes;

    @Column(name = "c_cod_pro")
    private String codPro;

    @Column(name = "c_cod_pas")
    private String codPas;

    @Column(name = "c_cod_sol")
    private String codSol;

    @Column(name = "c_fec_sol")
    private String fecSol;

    @Column(name = "c_cups")
    private String cups;

    @Column(name = "codigo_fiscal_factura")
    private String codFisFac;

    @Column(name = "imp_tot_fac")
    private double impTotFac;
    
    @Column(name = "tipo_factura")
    private String tipFac;

    @Column(name = "motivo_facturacion")
    private String motFac;

    @Column(name = "codigo_factura_rectificada_anulada")
    private String codFacRecAnu;

    @Column(name = "fecha_factura")
    private String fecFac;

    @Column(name = "tarifa_atr_fact")
    private String tarAtrFac;

    @Column(name = "modo_control_potencia")
    private String modConPot;

    @Column(name = "marca_medida_con_perdidas")
    private String marMedPer;

    @Column(name = "vas_trafo")
    private String vasTra;

    @Column(name = "porcentaje_perdidas")
    private String porPer;

    @Column(name = "numero_dias")
    private int numDias;

    @Column(name = "exceso_potencia1")
    private double excPot1;

    @Column(name = "exceso_potencia2")
    private double excPot2;

    @Column(name = "exceso_potencia3")
    private double excPot3;

    @Column(name = "exceso_potencia4")
    private double excPot4;

    @Column(name = "exceso_potencia5")
    private double excPot5;

    @Column(name = "exceso_potencia6")
    private double excPot6;

    @Column(name = "exceso_importe_total")
    private double excImpTot;

    @Column(name = "potencia_contratada1")
    private double potCon1;

    @Column(name = "potencia_contratada2")
    private double potCon2;

    @Column(name = "potencia_contratada3")
    private double potCon3;

    @Column(name = "potencia_contratada4")
    private double potCon4;

    @Column(name = "potencia_contratada5")
    private double potCon5;

    @Column(name = "potencia_contratada6")
    private double potCon6;

    @Column(name = "potencia_max1")
    private double potMax1;

    @Column(name = "potencia_max2")
    private double potMax2;

    @Column(name = "potencia_max3")
    private double potMax3;

    @Column(name = "potencia_max4")
    private double potMax4;

    @Column(name = "potencia_max5")
    private double potMax5;

    @Column(name = "potencia_max6")
    private double potMax6;

    @Column(name = "potencia_fac1")
    private double potFac1;

    @Column(name = "potencia_fac2")
    private double potFac2;

    @Column(name = "potencia_fac3")
    private double potFac3;

    @Column(name = "potencia_pre1")
    private double potPre1;

    @Column(name = "potencia_pre2")
    private double potPre2;

    @Column(name = "potencia_pre3")
    private double potPre3;

    @Column(name = "potencia_pre4")
    private double potPre4;

    @Column(name = "potencia_pre5")
    private double potPre5;

    @Column(name = "potencia_pre6")
    private double potPre6;

    @Column(name = "potencia_imp_tot")
    private double potImpTot;

    @Column(name = "ea_fecha_desde1")
    private String eaFecDes1;

    @Column(name = "ea_fecha_hasta1")
    private String eaFecHas1;

    @Column(name = "ea_fecha_desde2")
    private String eaFecDes2;

    @Column(name = "ea_fecha_hasta2")
    private String eaFecHas2;

    @Column(name = "ea_val1")
    private double eaVal1;

    @Column(name = "ea_val2")
    private double eaVal2;

    @Column(name = "ea_val3")
    private double eaVal3;

    @Column(name = "ea_val4")
    private double eaVal4;

    @Column(name = "ea_val5")
    private double eaVal5;

    @Column(name = "ea_val6")
    private double eaVal6;

    @Column(name = "ea_val_sum")
    private double eaValSum;

    @Column(name = "ea_pre1")
    private double eaPre1;

    @Column(name = "ea_pre2")
    private double eaPre2;

    @Column(name = "ea_pre3")
    private double eaPre3;

    @Column(name = "ea_pre4")
    private double eaPre4;

    @Column(name = "ea_pre5")
    private double eaPre5;

    @Column(name = "ea_pre6")
    private double eaPre6;

    @Column(name = "ea_imp_tot")
    private double eaImpTot;

    @Column(name = "car1_01")
    private double car1_01;

    @Column(name = "car2_01")
    private double car2_01;

    @Column(name = "car3_01")
    private double car3_01;

    @Column(name = "car4_01")
    private double car4_01;

    @Column(name = "car5_01")
    private double car5_01;

    @Column(name = "car6_01")
    private double car6_01;

    @Column(name = "car_imp_tot_01")
    private double carImpTot_01;

    @Column(name = "car1_02")
    private double car1_02;

    @Column(name = "car2_02")
    private double car2_02;

    @Column(name = "car3_02")
    private double car3_02;

    @Column(name = "car4_02")
    private double car4_02;

    @Column(name = "car5_02")
    private double car5_02;

    @Column(name = "car6_02")
    private double car6_02;

    @Column(name = "car_imp_tot_02")
    private double carImpTot_02;

    @Column(name = "ie_importe")
    private double ieImp;

    @Column(name = "a_imp_fact")
    private double aImpFac;

    @Column(name = "i_bas_imp")
    private double iBasImp;

    @Column(name = "ae_cons1")
    private double aeCon1;

    @Column(name = "ae_cons2")
    private double aeCon2;

    @Column(name = "ae_cons3")
    private double aeCon3;

    @Column(name = "ae_cons4")
    private double aeCon4;

    @Column(name = "ae_cons5")
    private double aeCon5;

    @Column(name = "ae_cons6")
    private double aeCon6;

    @Column(name = "ae_cons_sum")
    private double aeConSum;

    @Column(name = "ae_lec_des1")
    private double aeLecDes1;

    @Column(name = "ae_lec_des2")
    private double aeLecDes2;

    @Column(name = "ae_lec_des3")
    private double aeLecDes3;

    @Column(name = "ae_lec_des4")
    private double aeLecDes4;

    @Column(name = "ae_lec_des5")
    private double aeLecDes5;

    @Column(name = "ae_lec_des6")
    private double aeLecDes6;

    @Column(name = "ae_lec_has1")
    private double aeLecHas1;

    @Column(name = "ae_lec_has2")
    private double aeLecHas2;

    @Column(name = "ae_lec_has3")
    private double aeLecHas3;

    @Column(name = "ae_lec_has4")
    private double aeLecHas4;

    @Column(name = "ae_lec_has5")
    private double aeLecHas5;

    @Column(name = "ae_lec_has6")
    private double aeLecHas6;

    @Column(name = "ae_pro_des")
    private int aeProDes;

    @Column(name = "ae_pro_has")
    private int aeProHas;

    @Column(name = "r_con1")
    private double rCon1;

    @Column(name = "r_con2")
    private double rCon2;

    @Column(name = "r_con3")
    private double rCon3;

    @Column(name = "r_con4")
    private double rCon4;

    @Column(name = "r_con5")
    private double rCon5;

    @Column(name = "r_con6")
    private double rCon6;

    @Column(name = "r_con_sum")
    private double rConSum;

    @Column(name = "r_lec_des1")
    private double rLecDes1;

    @Column(name = "r_lec_des2")
    private double rLecDes2;

    @Column(name = "r_lec_des3")
    private double rLecDes3;

    @Column(name = "r_lec_des4")
    private double rLecDes4;

    @Column(name = "r_lec_des5")
    private double rLecDes5;

    @Column(name = "r_lec_des6")
    private double rLecDes6;

    @Column(name = "r_lec_has1")
    private double rLecHas1;

    @Column(name = "r_lec_has2")
    private double rLecHas2;

    @Column(name = "r_lec_has3")
    private double rLecHas3;

    @Column(name = "r_lec_has4")
    private double rLecHas4;

    @Column(name = "r_lec_has5")
    private double rLecHas5;

    @Column(name = "r_lec_has6")
    private double rLecHas6;

    @Column(name = "r_imp_tot")
    private double rImpTot;

    @Column(name = "pm_con1")
    private double pmCon1;

    @Column(name = "pm_con2")
    private double pmCon2;

    @Column(name = "pm_con3")
    private double pmCon3;

    @Column(name = "pm_con4")
    private double pmCon4;

    @Column(name = "pm_con5")
    private double pmCon5;

    @Column(name = "pm_con6")
    private double pmCon6;

    @Column(name = "pm_con_sum")
    private double pmConSum;

    @Column(name = "pm_lec_has1")
    private double pmLecHas1;

    @Column(name = "pm_lec_has2")
    private double pmLecHas2;

    @Column(name = "pm_lec_has3")
    private double pmLecHas3;

    @Column(name = "pm_lec_has4")
    private double pmLecHas4;

    @Column(name = "pm_lec_has5")
    private double pmLecHas5;

    @Column(name = "pm_lec_has6")
    private double pmLecHas6;

    @Column(name = "rf_imp_tot")
    private double rfImpTot;

    @Column(name = "rf_sal_tot_fac")
    private double rfSalTotFac;

    @Column(name = "rf_tot_rec")
    private int rfTotRec;

    @Column(name = "rf_fec_val")
    private String rfFecVal;

    @Column(name = "rf_fec_lim_pag")
    private String rfFecLimPag;

    @Column(name = "rf_id_rem")
    private String rfIdRem;

    @Column(name = "comentarios")
    private String comentarios;

    @Column(name = "id_error")
    private String idError;
    
    @Column(name = "remesa_pago")
    private String remesaPago;
    
    @Column(name = "estado_pago")
    private int estadoPago;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "created_on")
    private Date createdOn;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "updated_on")
    private Date updatedOn;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_energia_excedentaria")
    private EnergiaExcedentaria energiaExcedentaria;

    public Peaje() {
    }

    public Peaje(
            Cliente cliente, DatosCabecera datosCabecera, DatosGeneralesFactura datosGeneralesFactura, DatosFacturaAtr datosFacturaAtr,
            DatosExcesoPotencia datosExcesoPotencia, DatosPotenciaContratada datosPotenciaContratada, DatosPotenciaMaxDemandada datosPotenciaMaxDemandada, DatosPotenciaAFacturar datosPotenciaAFacturar, DatosPotenciaPrecio datosPotenciaPrecio, DatosPotenciaImporteTotal datosPotenciaImporteTotal,
            DatosEnergiaActiva datosEnergiaActiva, DatosEnergiaActivaValores datosEnergiaActivaValores, DatosEnergiaActivaPrecio datosEnergiaActivaPrecio, DatosEnergiaActivaImporteTotal datosEnergiaActivaImporteTotal,
            Cargos cargos1, Cargos cargos2, CargoImporteTotal cargoImporteTotal1, CargoImporteTotal cargoImporteTotal2,
            DatosImpuestoElectrico datosImpuestoElectrico, DatosAlquileres datosAlquileres, DatosIva datosIva,
            DatosAeConsumo datosAeConsumo, DatosAeLecturaDesde datosAeLecturaDesde, DatosAeLecturaHasta datosAeLecturaHasta, DatosAeProcedenciaDesde datosAeProcedenciaDesde, DatosAeProcedenciaHasta datosAeProcedenciaHasta,
            DatosRConsumo datosRConsumo, DatosRLecturaDesde datosRLecturaDesde, DatosRLecturaHasta datosRLecturaHasta, ReactivaImporteTotal reactivaImporteTotal,
            DatosPmConsumo datosPmConsumo, DatosPmLecturaHasta datosPmLecturaHasta,
            DatosFinDeRegistro datosFinDeRegistro, String comentarios, String errores) {

        this.idCliente = cliente.getIdCliente();
        //Cabecera
        this.codEmpEmi = datosCabecera.getCodigoREEEmpresaEmisora();
        this.codEmpDes = datosCabecera.getCodigoREEEmpresaDestino();
        this.codPro = datosCabecera.getCodigoDelProceso();
        this.codPas = datosCabecera.getCodigoDePaso();
        this.codSol = datosCabecera.getCodigoDeSolicitud();
        this.fecSol = datosCabecera.getFechaSolicitud();
        this.cups = datosCabecera.getCups();

        //DatosGeneralesFactura
        this.codFisFac = datosGeneralesFactura.getCodigoFiscalFactura();
        this.impTotFac = datosGeneralesFactura.getImpTotFac();
        this.tipFac = datosGeneralesFactura.getTipoFactura();
        this.motFac = datosGeneralesFactura.getMotivoFacturacion();
        this.codFacRecAnu = datosGeneralesFactura.getCodigoFacturaRectificadaAnulada();
        this.fecFac = datosGeneralesFactura.getFechaFactura();

        //FacturaATR
        this.tarAtrFac = datosFacturaAtr.getTarifaAtrFact();
        this.modConPot = datosFacturaAtr.getModoControlPotencia();
        this.marMedPer = datosFacturaAtr.getMarcaMedidaConPerdidas();
        this.vasTra = datosFacturaAtr.getVAsTrafo();
        this.porPer = datosFacturaAtr.getPorcentajePerdidas();
        this.numDias = datosFacturaAtr.getNumeroDias();

        //ExcesoPotencia
        this.excPot1 = datosExcesoPotencia.getP1();
        this.excPot2 = datosExcesoPotencia.getP2();
        this.excPot3 = datosExcesoPotencia.getP3();
        this.excPot4 = datosExcesoPotencia.getP4();
        this.excPot5 = datosExcesoPotencia.getP5();
        this.excPot6 = datosExcesoPotencia.getP6();
        this.excImpTot = datosExcesoPotencia.getT();

        //PotenciaContratada
        this.potCon1 = datosPotenciaContratada.getP1();
        this.potCon2 = datosPotenciaContratada.getP2();
        this.potCon3 = datosPotenciaContratada.getP3();
        this.potCon4 = datosPotenciaContratada.getP4();
        this.potCon5 = datosPotenciaContratada.getP5();
        this.potCon6 = datosPotenciaContratada.getP6();

        //PotenciaMaxDemandada
        this.potMax1 = datosPotenciaMaxDemandada.getP1();
        this.potMax2 = datosPotenciaMaxDemandada.getP2();
        this.potMax3 = datosPotenciaMaxDemandada.getP3();
        this.potMax4 = datosPotenciaMaxDemandada.getP4();
        this.potMax5 = datosPotenciaMaxDemandada.getP5();
        this.potMax6 = datosPotenciaMaxDemandada.getP6();

        //PotenciaAFacturar
        this.potFac1 = datosPotenciaAFacturar.getP1();
        this.potFac2 = datosPotenciaAFacturar.getP2();
        this.potFac3 = datosPotenciaAFacturar.getP3();

        //PotenciaPrecio
        this.potPre1 = datosPotenciaPrecio.getP1();
        this.potPre2 = datosPotenciaPrecio.getP2();
        this.potPre3 = datosPotenciaPrecio.getP3();
        this.potPre4 = datosPotenciaPrecio.getP4();
        this.potPre5 = datosPotenciaPrecio.getP5();
        this.potPre6 = datosPotenciaPrecio.getP6();
        this.potImpTot = datosPotenciaImporteTotal.getImporteTotal();

        //EnergíaActiva
        this.eaFecDes1 = datosEnergiaActiva.getFechaDesde1();
        this.eaFecHas1 = datosEnergiaActiva.getFechaHasta1();
        this.eaFecDes2 = datosEnergiaActiva.getFechaDesde2();
        this.eaFecHas2 = datosEnergiaActiva.getFechaHasta2();

        this.eaVal1 = datosEnergiaActivaValores.getV1();
        this.eaVal2 = datosEnergiaActivaValores.getV2();
        this.eaVal3 = datosEnergiaActivaValores.getV3();
        this.eaVal4 = datosEnergiaActivaValores.getV4();
        this.eaVal5 = datosEnergiaActivaValores.getV5();
        this.eaVal6 = datosEnergiaActivaValores.getV6();
        this.eaValSum = datosEnergiaActivaValores.getTotal();

        this.eaPre1 = datosEnergiaActivaPrecio.getP1();
        this.eaPre2 = datosEnergiaActivaPrecio.getP2();
        this.eaPre3 = datosEnergiaActivaPrecio.getP3();
        this.eaPre4 = datosEnergiaActivaPrecio.getP4();
        this.eaPre5 = datosEnergiaActivaPrecio.getP5();
        this.eaPre6 = datosEnergiaActivaPrecio.getP6();
        this.eaImpTot = datosEnergiaActivaImporteTotal.getImporteTotal();

        //Cargos
        this.car1_01 = cargos1.getC1();
        this.car2_01 = cargos1.getC2();
        this.car3_01 = cargos1.getC3();
        this.car4_01 = cargos1.getC4();
        this.car5_01 = cargos1.getC5();
        this.car6_01 = cargos1.getC6();
        this.carImpTot_01 = cargoImporteTotal1.getImpTot();

        this.car1_02 = cargos2.getC1();
        this.car2_02 = cargos2.getC2();
        this.car3_02 = cargos2.getC3();
        this.car4_02 = cargos2.getC4();
        this.car5_02 = cargos2.getC5();
        this.car6_02 = cargos2.getC6();
        this.carImpTot_02 = cargoImporteTotal2.getImpTot();

        this.ieImp = datosImpuestoElectrico.getImporte();

        this.aImpFac = datosAlquileres.getImporte();

        this.iBasImp = datosIva.getBaseImponible();

        this.aeCon1 = datosAeConsumo.getConsumoCalculado1();
        this.aeCon2 = datosAeConsumo.getConsumoCalculado2();
        this.aeCon3 = datosAeConsumo.getConsumoCalculado3();
        this.aeCon4 = datosAeConsumo.getConsumoCalculado4();
        this.aeCon5 = datosAeConsumo.getConsumoCalculado5();
        this.aeCon6 = datosAeConsumo.getConsumoCalculado6();
        this.aeConSum = datosAeConsumo.getSuma();

        this.aeLecDes1 = datosAeLecturaDesde.getLectura1();
        this.aeLecDes2 = datosAeLecturaDesde.getLectura2();
        this.aeLecDes3 = datosAeLecturaDesde.getLectura3();
        this.aeLecDes4 = datosAeLecturaDesde.getLectura4();
        this.aeLecDes5 = datosAeLecturaDesde.getLectura5();
        this.aeLecDes6 = datosAeLecturaDesde.getLectura6();

        this.aeLecHas1 = datosAeLecturaHasta.getLectura1();
        this.aeLecHas2 = datosAeLecturaHasta.getLectura2();
        this.aeLecHas3 = datosAeLecturaHasta.getLectura3();
        this.aeLecHas4 = datosAeLecturaHasta.getLectura4();
        this.aeLecHas5 = datosAeLecturaHasta.getLectura5();
        this.aeLecHas6 = datosAeLecturaHasta.getLectura6();

        this.aeProDes = datosAeProcedenciaDesde.getProcedencia();
        this.aeProHas = datosAeProcedenciaHasta.getProcedencia();

        this.rCon1 = datosRConsumo.getConsumo1();
        this.rCon2 = datosRConsumo.getConsumo2();
        this.rCon3 = datosRConsumo.getConsumo3();
        this.rCon4 = datosRConsumo.getConsumo4();
        this.rCon5 = datosRConsumo.getConsumo5();
        this.rCon6 = datosRConsumo.getConsumo6();
        this.rConSum = datosRConsumo.getSuma();

        this.rLecDes1 = datosRLecturaDesde.getLectura1();
        this.rLecDes2 = datosRLecturaDesde.getLectura2();
        this.rLecDes3 = datosRLecturaDesde.getLectura3();
        this.rLecDes4 = datosRLecturaDesde.getLectura4();
        this.rLecDes5 = datosRLecturaDesde.getLectura5();
        this.rLecDes6 = datosRLecturaDesde.getLectura6();

        this.rLecHas1 = datosRLecturaHasta.getLectura1();
        this.rLecHas2 = datosRLecturaHasta.getLectura2();
        this.rLecHas3 = datosRLecturaHasta.getLectura3();
        this.rLecHas4 = datosRLecturaHasta.getLectura4();
        this.rLecHas5 = datosRLecturaHasta.getLectura5();
        this.rLecHas6 = datosRLecturaHasta.getLectura6();
        this.rImpTot = reactivaImporteTotal.getImporteTotal();

        this.pmCon1 = datosPmConsumo.getConsumo1();
        this.pmCon2 = datosPmConsumo.getConsumo2();
        this.pmCon3 = datosPmConsumo.getConsumo3();
        this.pmCon4 = datosPmConsumo.getConsumo4();
        this.pmCon5 = datosPmConsumo.getConsumo5();
        this.pmCon6 = datosPmConsumo.getConsumo6();
        this.pmConSum = datosPmConsumo.getSuma();

        this.pmLecHas1 = datosPmLecturaHasta.getLectura1();
        this.pmLecHas2 = datosPmLecturaHasta.getLectura2();
        this.pmLecHas3 = datosPmLecturaHasta.getLectura3();
        this.pmLecHas4 = datosPmLecturaHasta.getLectura4();
        this.pmLecHas5 = datosPmLecturaHasta.getLectura5();
        this.pmLecHas6 = datosPmLecturaHasta.getLectura6();

        this.rfImpTot = datosFinDeRegistro.getImporteTotal();
        this.rfSalTotFac = datosFinDeRegistro.getSaldoTotalFacturacion();
        this.rfTotRec = datosFinDeRegistro.getTotalRecibos();
        this.rfFecVal = datosFinDeRegistro.getFechaValor();
        this.rfFecLimPag = datosFinDeRegistro.getFechaLimitePago();
        this.rfIdRem = datosFinDeRegistro.getIdRemesa();

        this.comentarios = comentarios;
        this.idError = errores;
    }

    public long getIdPeaje() {
        return idPeaje;
    }

    public void setIdPeaje(long idPeaje) {
        this.idPeaje = idPeaje;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public String getCodEmpEmi() {
        return codEmpEmi;
    }

    public void setCodEmpEmi(String codEmpEmi) {
        this.codEmpEmi = codEmpEmi;
    }

    public String getCodEmpDes() {
        return codEmpDes;
    }

    public void setCodEmpDes(String codEmpDes) {
        this.codEmpDes = codEmpDes;
    }

    public String getCodPro() {
        return codPro;
    }

    public void setCodPro(String codPro) {
        this.codPro = codPro;
    }

    public String getCodPas() {
        return codPas;
    }

    public void setCodPas(String codPas) {
        this.codPas = codPas;
    }

    public String getCodSol() {
        return codSol;
    }

    public void setCodSol(String codSol) {
        this.codSol = codSol;
    }

    public String getFecSol() {
        return fecSol;
    }

    public void setFecSol(String fecSol) {
        this.fecSol = fecSol;
    }

    public String getCups() {
        return cups;
    }

    public void setCups(String cups) {
        this.cups = cups;
    }

    public String getCodFisFac() {
        return codFisFac;
    }

    public void setCodFisFac(String codFisFac) {
        this.codFisFac = codFisFac;
    }

    public double getImpTotFac() {
        return impTotFac;
    }

    public void setImpTotFac(double impTotFac) {
        this.impTotFac = impTotFac;
    }

    public String getTipFac() {
        return tipFac;
    }

    public void setTipFac(String tipFac) {
        this.tipFac = tipFac;
    }

    public String getMotFac() {
        return motFac;
    }

    public void setMotFac(String motFac) {
        this.motFac = motFac;
    }

    public String getCodFacRecAnu() {
        return codFacRecAnu;
    }

    public void setCodFacRecAnu(String codFacRecAnu) {
        this.codFacRecAnu = codFacRecAnu;
    }

    public String getFecFac() {
        return fecFac;
    }

    public void setFecFac(String fecFac) {
        this.fecFac = fecFac;
    }

    public String getTarAtrFac() {
        return tarAtrFac;
    }

    public void setTarAtrFac(String tarAtrFac) {
        this.tarAtrFac = tarAtrFac;
    }

    public String getModConPot() {
        return modConPot;
    }

    public void setModConPot(String modConPot) {
        this.modConPot = modConPot;
    }

    public String getMarMedPer() {
        return marMedPer;
    }

    public void setMarMedPer(String marMedPer) {
        this.marMedPer = marMedPer;
    }

    public String getVasTra() {
        return vasTra;
    }

    public void setVasTra(String vasTra) {
        this.vasTra = vasTra;
    }

    public String getPorPer() {
        return porPer;
    }

    public void setPorPer(String porPer) {
        this.porPer = porPer;
    }

    public int getNumDias() {
        return numDias;
    }

    public void setNumDias(int numDias) {
        this.numDias = numDias;
    }

    public double getExcPot1() {
        return excPot1;
    }

    public void setExcPot1(double excPot1) {
        this.excPot1 = excPot1;
    }

    public double getExcPot2() {
        return excPot2;
    }

    public void setExcPot2(double excPot2) {
        this.excPot2 = excPot2;
    }

    public double getExcPot3() {
        return excPot3;
    }

    public void setExcPot3(double excPot3) {
        this.excPot3 = excPot3;
    }

    public double getExcPot4() {
        return excPot4;
    }

    public void setExcPot4(double excPot4) {
        this.excPot4 = excPot4;
    }

    public double getExcPot5() {
        return excPot5;
    }

    public void setExcPot5(double excPot5) {
        this.excPot5 = excPot5;
    }

    public double getExcPot6() {
        return excPot6;
    }

    public void setExcPot6(double excPot6) {
        this.excPot6 = excPot6;
    }

    public double getExcImpTot() {
        return excImpTot;
    }

    public void setExcImpTot(double excImpTot) {
        this.excImpTot = excImpTot;
    }

    public double getPotCon1() {
        return potCon1;
    }

    public void setPotCon1(double potCon1) {
        this.potCon1 = potCon1;
    }

    public double getPotCon2() {
        return potCon2;
    }

    public void setPotCon2(double potCon2) {
        this.potCon2 = potCon2;
    }

    public double getPotCon3() {
        return potCon3;
    }

    public void setPotCon3(double potCon3) {
        this.potCon3 = potCon3;
    }

    public double getPotCon4() {
        return potCon4;
    }

    public void setPotCon4(double potCon4) {
        this.potCon4 = potCon4;
    }

    public double getPotCon5() {
        return potCon5;
    }

    public void setPotCon5(double potCon5) {
        this.potCon5 = potCon5;
    }

    public double getPotCon6() {
        return potCon6;
    }

    public void setPotCon6(double potCon6) {
        this.potCon6 = potCon6;
    }

    public double getPotMax1() {
        return potMax1;
    }

    public void setPotMax1(double potMax1) {
        this.potMax1 = potMax1;
    }

    public double getPotMax2() {
        return potMax2;
    }

    public void setPotMax2(double potMax2) {
        this.potMax2 = potMax2;
    }

    public double getPotMax3() {
        return potMax3;
    }

    public void setPotMax3(double potMax3) {
        this.potMax3 = potMax3;
    }

    public double getPotMax4() {
        return potMax4;
    }

    public void setPotMax4(double potMax4) {
        this.potMax4 = potMax4;
    }

    public double getPotMax5() {
        return potMax5;
    }

    public void setPotMax5(double potMax5) {
        this.potMax5 = potMax5;
    }

    public double getPotMax6() {
        return potMax6;
    }

    public void setPotMax6(double potMax6) {
        this.potMax6 = potMax6;
    }

    public double getPotFac1() {
        return potFac1;
    }

    public void setPotFac1(double potFac1) {
        this.potFac1 = potFac1;
    }

    public double getPotFac2() {
        return potFac2;
    }

    public void setPotFac2(double potFac2) {
        this.potFac2 = potFac2;
    }

    public double getPotFac3() {
        return potFac3;
    }

    public void setPotFac3(double potFac3) {
        this.potFac3 = potFac3;
    }

    public double getPotPre1() {
        return potPre1;
    }

    public void setPotPre1(double potPre1) {
        this.potPre1 = potPre1;
    }

    public double getPotPre2() {
        return potPre2;
    }

    public void setPotPre2(double potPre2) {
        this.potPre2 = potPre2;
    }

    public double getPotPre3() {
        return potPre3;
    }

    public void setPotPre3(double potPre3) {
        this.potPre3 = potPre3;
    }

    public double getPotPre4() {
        return potPre4;
    }

    public void setPotPre4(double potPre4) {
        this.potPre4 = potPre4;
    }

    public double getPotPre5() {
        return potPre5;
    }

    public void setPotPre5(double potPre5) {
        this.potPre5 = potPre5;
    }

    public double getPotPre6() {
        return potPre6;
    }

    public void setPotPre6(double potPre6) {
        this.potPre6 = potPre6;
    }

    public double getPotImpTot() {
        return potImpTot;
    }

    public void setPotImpTot(double potImpTot) {
        this.potImpTot = potImpTot;
    }

    public String getEaFecDes1() {
        return eaFecDes1;
    }

    public void setEaFecDes1(String eaFecDes1) {
        this.eaFecDes1 = eaFecDes1;
    }

    public String getEaFecHas1() {
        return eaFecHas1;
    }

    public void setEaFecHas1(String eaFecHas1) {
        this.eaFecHas1 = eaFecHas1;
    }

    public String getEaFecDes2() {
        return eaFecDes2;
    }

    public void setEaFecDes2(String eaFecDes2) {
        this.eaFecDes2 = eaFecDes2;
    }

    public String getEaFecHas2() {
        return eaFecHas2;
    }

    public void setEaFecHas2(String eaFecHas2) {
        this.eaFecHas2 = eaFecHas2;
    }

    public double getEaVal1() {
        return eaVal1;
    }

    public void setEaVal1(double eaVal1) {
        this.eaVal1 = eaVal1;
    }

    public double getEaVal2() {
        return eaVal2;
    }

    public void setEaVal2(double eaVal2) {
        this.eaVal2 = eaVal2;
    }

    public double getEaVal3() {
        return eaVal3;
    }

    public void setEaVal3(double eaVal3) {
        this.eaVal3 = eaVal3;
    }

    public double getEaVal4() {
        return eaVal4;
    }

    public void setEaVal4(double eaVal4) {
        this.eaVal4 = eaVal4;
    }

    public double getEaVal5() {
        return eaVal5;
    }

    public void setEaVal5(double eaVal5) {
        this.eaVal5 = eaVal5;
    }

    public double getEaVal6() {
        return eaVal6;
    }

    public void setEaVal6(double eaVal6) {
        this.eaVal6 = eaVal6;
    }

    public double getEaValSum() {
        return eaValSum;
    }

    public void setEaValSum(double eaValSum) {
        this.eaValSum = eaValSum;
    }

    public double getEaPre1() {
        return eaPre1;
    }

    public void setEaPre1(double eaPre1) {
        this.eaPre1 = eaPre1;
    }

    public double getEaPre2() {
        return eaPre2;
    }

    public void setEaPre2(double eaPre2) {
        this.eaPre2 = eaPre2;
    }

    public double getEaPre3() {
        return eaPre3;
    }

    public void setEaPre3(double eaPre3) {
        this.eaPre3 = eaPre3;
    }

    public double getEaPre4() {
        return eaPre4;
    }

    public void setEaPre4(double eaPre4) {
        this.eaPre4 = eaPre4;
    }

    public double getEaPre5() {
        return eaPre5;
    }

    public void setEaPre5(double eaPre5) {
        this.eaPre5 = eaPre5;
    }

    public double getEaPre6() {
        return eaPre6;
    }

    public void setEaPre6(double eaPre6) {
        this.eaPre6 = eaPre6;
    }

    public double getEaImpTot() {
        return eaImpTot;
    }

    public void setEaImpTot(double eaImpTot) {
        this.eaImpTot = eaImpTot;
    }

    public double getCar1_01() {
        return car1_01;
    }

    public void setCar1_01(double car1_01) {
        this.car1_01 = car1_01;
    }

    public double getCar2_01() {
        return car2_01;
    }

    public void setCar2_01(double car2_01) {
        this.car2_01 = car2_01;
    }

    public double getCar3_01() {
        return car3_01;
    }

    public void setCar3_01(double car3_01) {
        this.car3_01 = car3_01;
    }

    public double getCar4_01() {
        return car4_01;
    }

    public void setCar4_01(double car4_01) {
        this.car4_01 = car4_01;
    }

    public double getCar5_01() {
        return car5_01;
    }

    public void setCar5_01(double car5_01) {
        this.car5_01 = car5_01;
    }

    public double getCar6_01() {
        return car6_01;
    }

    public void setCar6_01(double car6_01) {
        this.car6_01 = car6_01;
    }

    public double getCarImpTot_01() {
        return carImpTot_01;
    }

    public void setCarImpTot_01(double carImpTot_01) {
        this.carImpTot_01 = carImpTot_01;
    }

    public double getCar1_02() {
        return car1_02;
    }

    public void setCar1_02(double car1_02) {
        this.car1_02 = car1_02;
    }

    public double getCar2_02() {
        return car2_02;
    }

    public void setCar2_02(double car2_02) {
        this.car2_02 = car2_02;
    }

    public double getCar3_02() {
        return car3_02;
    }

    public void setCar3_02(double car3_02) {
        this.car3_02 = car3_02;
    }

    public double getCar4_02() {
        return car4_02;
    }

    public void setCar4_02(double car4_02) {
        this.car4_02 = car4_02;
    }

    public double getCar5_02() {
        return car5_02;
    }

    public void setCar5_02(double car5_02) {
        this.car5_02 = car5_02;
    }

    public double getCar6_02() {
        return car6_02;
    }

    public void setCar6_02(double car6_02) {
        this.car6_02 = car6_02;
    }

    public double getCarImpTot_02() {
        return carImpTot_02;
    }

    public void setCarImpTot_02(double carImpTot_02) {
        this.carImpTot_02 = carImpTot_02;
    }

    public double getIeImp() {
        return ieImp;
    }

    public void setIeImp(double ieImp) {
        this.ieImp = ieImp;
    }

    public double getaImpFac() {
        return aImpFac;
    }

    public void setaImpFac(double aImpFac) {
        this.aImpFac = aImpFac;
    }

    public double getiBasImp() {
        return iBasImp;
    }

    public void setiBasImp(double iBasImp) {
        this.iBasImp = iBasImp;
    }

    public double getAeCon1() {
        return aeCon1;
    }

    public void setAeCon1(double aeCon1) {
        this.aeCon1 = aeCon1;
    }

    public double getAeCon2() {
        return aeCon2;
    }

    public void setAeCon2(double aeCon2) {
        this.aeCon2 = aeCon2;
    }

    public double getAeCon3() {
        return aeCon3;
    }

    public void setAeCon3(double aeCon3) {
        this.aeCon3 = aeCon3;
    }

    public double getAeCon4() {
        return aeCon4;
    }

    public void setAeCon4(double aeCon4) {
        this.aeCon4 = aeCon4;
    }

    public double getAeCon5() {
        return aeCon5;
    }

    public void setAeCon5(double aeCon5) {
        this.aeCon5 = aeCon5;
    }

    public double getAeCon6() {
        return aeCon6;
    }

    public void setAeCon6(double aeCon6) {
        this.aeCon6 = aeCon6;
    }

    public double getAeConSum() {
        return aeConSum;
    }

    public void setAeConSum(double aeConSum) {
        this.aeConSum = aeConSum;
    }

    public double getAeLecDes1() {
        return aeLecDes1;
    }

    public void setAeLecDes1(double aeLecDes1) {
        this.aeLecDes1 = aeLecDes1;
    }

    public double getAeLecDes2() {
        return aeLecDes2;
    }

    public void setAeLecDes2(double aeLecDes2) {
        this.aeLecDes2 = aeLecDes2;
    }

    public double getAeLecDes3() {
        return aeLecDes3;
    }

    public void setAeLecDes3(double aeLecDes3) {
        this.aeLecDes3 = aeLecDes3;
    }

    public double getAeLecDes4() {
        return aeLecDes4;
    }

    public void setAeLecDes4(double aeLecDes4) {
        this.aeLecDes4 = aeLecDes4;
    }

    public double getAeLecDes5() {
        return aeLecDes5;
    }

    public void setAeLecDes5(double aeLecDes5) {
        this.aeLecDes5 = aeLecDes5;
    }

    public double getAeLecDes6() {
        return aeLecDes6;
    }

    public void setAeLecDes6(double aeLecDes6) {
        this.aeLecDes6 = aeLecDes6;
    }

    public double getAeLecHas1() {
        return aeLecHas1;
    }

    public void setAeLecHas1(double aeLecHas1) {
        this.aeLecHas1 = aeLecHas1;
    }

    public double getAeLecHas2() {
        return aeLecHas2;
    }

    public void setAeLecHas2(double aeLecHas2) {
        this.aeLecHas2 = aeLecHas2;
    }

    public double getAeLecHas3() {
        return aeLecHas3;
    }

    public void setAeLecHas3(double aeLecHas3) {
        this.aeLecHas3 = aeLecHas3;
    }

    public double getAeLecHas4() {
        return aeLecHas4;
    }

    public void setAeLecHas4(double aeLecHas4) {
        this.aeLecHas4 = aeLecHas4;
    }

    public double getAeLecHas5() {
        return aeLecHas5;
    }

    public void setAeLecHas5(double aeLecHas5) {
        this.aeLecHas5 = aeLecHas5;
    }

    public double getAeLecHas6() {
        return aeLecHas6;
    }

    public void setAeLecHas6(double aeLecHas6) {
        this.aeLecHas6 = aeLecHas6;
    }

    public int getAeProDes() {
        return aeProDes;
    }

    public void setAeProDes(int aeProDes) {
        this.aeProDes = aeProDes;
    }

    public int getAeProHas() {
        return aeProHas;
    }

    public void setAeProHas(int aeProHas) {
        this.aeProHas = aeProHas;
    }

    public double getrCon1() {
        return rCon1;
    }

    public void setrCon1(double rCon1) {
        this.rCon1 = rCon1;
    }

    public double getrCon2() {
        return rCon2;
    }

    public void setrCon2(double rCon2) {
        this.rCon2 = rCon2;
    }

    public double getrCon3() {
        return rCon3;
    }

    public void setrCon3(double rCon3) {
        this.rCon3 = rCon3;
    }

    public double getrCon4() {
        return rCon4;
    }

    public void setrCon4(double rCon4) {
        this.rCon4 = rCon4;
    }

    public double getrCon5() {
        return rCon5;
    }

    public void setrCon5(double rCon5) {
        this.rCon5 = rCon5;
    }

    public double getrCon6() {
        return rCon6;
    }

    public void setrCon6(double rCon6) {
        this.rCon6 = rCon6;
    }

    public double getrConSum() {
        return rConSum;
    }

    public void setrConSum(double rConSum) {
        this.rConSum = rConSum;
    }

    public double getrLecDes1() {
        return rLecDes1;
    }

    public void setrLecDes1(double rLecDes1) {
        this.rLecDes1 = rLecDes1;
    }

    public double getrLecDes2() {
        return rLecDes2;
    }

    public void setrLecDes2(double rLecDes2) {
        this.rLecDes2 = rLecDes2;
    }

    public double getrLecDes3() {
        return rLecDes3;
    }

    public void setrLecDes3(double rLecDes3) {
        this.rLecDes3 = rLecDes3;
    }

    public double getrLecDes4() {
        return rLecDes4;
    }

    public void setrLecDes4(double rLecDes4) {
        this.rLecDes4 = rLecDes4;
    }

    public double getrLecDes5() {
        return rLecDes5;
    }

    public void setrLecDes5(double rLecDes5) {
        this.rLecDes5 = rLecDes5;
    }

    public double getrLecDes6() {
        return rLecDes6;
    }

    public void setrLecDes6(double rLecDes6) {
        this.rLecDes6 = rLecDes6;
    }

    public double getrLecHas1() {
        return rLecHas1;
    }

    public void setrLecHas1(double rLecHas1) {
        this.rLecHas1 = rLecHas1;
    }

    public double getrLecHas2() {
        return rLecHas2;
    }

    public void setrLecHas2(double rLecHas2) {
        this.rLecHas2 = rLecHas2;
    }

    public double getrLecHas3() {
        return rLecHas3;
    }

    public void setrLecHas3(double rLecHas3) {
        this.rLecHas3 = rLecHas3;
    }

    public double getrLecHas4() {
        return rLecHas4;
    }

    public void setrLecHas4(double rLecHas4) {
        this.rLecHas4 = rLecHas4;
    }

    public double getrLecHas5() {
        return rLecHas5;
    }

    public void setrLecHas5(double rLecHas5) {
        this.rLecHas5 = rLecHas5;
    }

    public double getrLecHas6() {
        return rLecHas6;
    }

    public void setrLecHas6(double rLecHas6) {
        this.rLecHas6 = rLecHas6;
    }

    public double getrImpTot() {
        return rImpTot;
    }

    public void setrImpTot(double rImpTot) {
        this.rImpTot = rImpTot;
    }

    public double getPmCon1() {
        return pmCon1;
    }

    public void setPmCon1(double pmCon1) {
        this.pmCon1 = pmCon1;
    }

    public double getPmCon2() {
        return pmCon2;
    }

    public void setPmCon2(double pmCon2) {
        this.pmCon2 = pmCon2;
    }

    public double getPmCon3() {
        return pmCon3;
    }

    public void setPmCon3(double pmCon3) {
        this.pmCon3 = pmCon3;
    }

    public double getPmCon4() {
        return pmCon4;
    }

    public void setPmCon4(double pmCon4) {
        this.pmCon4 = pmCon4;
    }

    public double getPmCon5() {
        return pmCon5;
    }

    public void setPmCon5(double pmCon5) {
        this.pmCon5 = pmCon5;
    }

    public double getPmCon6() {
        return pmCon6;
    }

    public void setPmCon6(double pmCon6) {
        this.pmCon6 = pmCon6;
    }

    public double getPmConSum() {
        return pmConSum;
    }

    public void setPmConSum(double pmConSum) {
        this.pmConSum = pmConSum;
    }

    public double getPmLecHas1() {
        return pmLecHas1;
    }

    public void setPmLecHas1(double pmLecHas1) {
        this.pmLecHas1 = pmLecHas1;
    }

    public double getPmLecHas2() {
        return pmLecHas2;
    }

    public void setPmLecHas2(double pmLecHas2) {
        this.pmLecHas2 = pmLecHas2;
    }

    public double getPmLecHas3() {
        return pmLecHas3;
    }

    public void setPmLecHas3(double pmLecHas3) {
        this.pmLecHas3 = pmLecHas3;
    }

    public double getPmLecHas4() {
        return pmLecHas4;
    }

    public void setPmLecHas4(double pmLecHas4) {
        this.pmLecHas4 = pmLecHas4;
    }

    public double getPmLecHas5() {
        return pmLecHas5;
    }

    public void setPmLecHas5(double pmLecHas5) {
        this.pmLecHas5 = pmLecHas5;
    }

    public double getPmLecHas6() {
        return pmLecHas6;
    }

    public void setPmLecHas6(double pmLecHas6) {
        this.pmLecHas6 = pmLecHas6;
    }

    public double getRfImpTot() {
        return rfImpTot;
    }

    public void setRfImpTot(double rfImpTot) {
        this.rfImpTot = rfImpTot;
    }

    public double getRfSalTotFac() {
        return rfSalTotFac;
    }

    public void setRfSalTotFac(double rfSalTotFac) {
        this.rfSalTotFac = rfSalTotFac;
    }

    public int getRfTotRec() {
        return rfTotRec;
    }

    public void setRfTotRec(int rfTotRec) {
        this.rfTotRec = rfTotRec;
    }

    public String getRfFecVal() {
        return rfFecVal;
    }

    public void setRfFecVal(String rfFecVal) {
        this.rfFecVal = rfFecVal;
    }

    public String getRfFecLimPag() {
        return rfFecLimPag;
    }

    public void setRfFecLimPag(String rfFecLimPag) {
        this.rfFecLimPag = rfFecLimPag;
    }

    public String getRfIdRem() {
        return rfIdRem;
    }

    public void setRfIdRem(String rfIdRem) {
        this.rfIdRem = rfIdRem;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getIdError() {
        return idError;
    }

    public void setIdError(String idError) {
        this.idError = idError;
    }

    public String getRemesaPago() {
        return remesaPago;
    }

    public void setRemesaPago(String remesaPago) {
        this.remesaPago = remesaPago;
    }

    public int getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(int estadoPago) {
        this.estadoPago = estadoPago;
    }

    public EnergiaExcedentaria getEnergiaExcedentaria() {
        return energiaExcedentaria;
    }

    public void setEnergiaExcedentaria(EnergiaExcedentaria energiaExcedentaria) {
        this.energiaExcedentaria = energiaExcedentaria;
    }

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
    
}
