package dominio.componentesxml;

import dominio.Cliente;

public class DocumentoXml {

    //Cliente
    private Cliente cliente;

    //Cabecera
    private DatosCabecera datosCabecera;

    //Datos
    private DatosGeneralesFactura datosGeneralesFactura;
    private DatosFacturaAtr datosFacturaAtr;

    //Potencia
    private DatosExcesoPotencia datosExcesoPotencia;
    private DatosPotenciaContratada datosPotenciaContratada;
    private DatosPotenciaMaxDemandada datosPotenciaMaxDemandada;
    private DatosPotenciaAFacturar datosPotenciaAFacturar;
    private DatosPotenciaPrecio datosPotenciaPrecio;
    private DatosPotenciaImporteTotal datosPotenciaImporteTotal;

    //energia Activa
    private DatosEnergiaActiva datosEnergiaActiva;
    private DatosEnergiaActivaValores datosEnergiaActivaValores;
    private DatosEnergiaActivaPrecio datosEnergiaActivaPrecio;
    private DatosEnergiaActivaImporteTotal datosEnergiaActivaImporteTotal;

    //Cargos
    private Cargos cargos1;
    private Cargos cargos2;
    private CargoImporteTotal cargoImporteTotal1;
    private CargoImporteTotal cargoImporteTotal2;

    //OtrosDatos
    private DatosImpuestoElectrico datosImpuestoElectrico;
    private DatosAlquileres datosAlquileres;
    private DatosIva datosIva;

    //DatosAE
    private DatosAeConsumo datosAeConsumo;
    private DatosAeLecturaDesde datosAeLecturaDesde;
    private DatosAeLecturaHasta datosAeLecturaHasta;
    private DatosAeProcedenciaDesde datosAeProcedenciaDesde;
    private DatosAeProcedenciaHasta datosAeProcedenciaHasta;

    //DatosR
    private DatosRConsumo datosRConsumo;
    private DatosRLecturaDesde datosRLecturaDesde;
    private DatosRLecturaHasta datosRLecturaHasta;
    private ReactivaImporteTotal reactivaImporteTotal;

    //DatosPM
    private DatosPmConsumo datosPmConsumo;
    private DatosPmLecturaHasta datosPmLecturaHasta;

    //Fin de registro
    private DatosFinDeRegistro datosFinDeRegistro;

    //Comentarios
    private String comentarios;

    //Errores
    private String errores;

    public DocumentoXml() {
    }

    public DocumentoXml(
            Cliente cliente, DatosCabecera datosCabecera, DatosGeneralesFactura datosGeneralesFactura, DatosFacturaAtr datosFacturaAtr,
            DatosExcesoPotencia datosExcesoPotencia, DatosPotenciaContratada datosPotenciaContratada, DatosPotenciaMaxDemandada datosPotenciaMaxDemandada, DatosPotenciaAFacturar datosPotenciaAFacturar, DatosPotenciaPrecio datosPotenciaPrecio, DatosPotenciaImporteTotal datosPotenciaImporteTotal,
            DatosEnergiaActiva datosEnergiaActiva, DatosEnergiaActivaValores datosEnergiaActivaValores, DatosEnergiaActivaPrecio datosEnergiaActivaPrecio, DatosEnergiaActivaImporteTotal datosEnergiaActivaImporteTotal,
            Cargos cargos1, Cargos cargos2, CargoImporteTotal cargoImporteTotal1, CargoImporteTotal cargoImporteTotal2,
            DatosImpuestoElectrico datosImpuestoElectrico, DatosAlquileres datosAlquileres, DatosIva datosIva,
            DatosAeConsumo datosAeConsumo, DatosAeLecturaDesde datosAeLecturaDesde, DatosAeLecturaHasta datosAeLecturaHasta, DatosAeProcedenciaDesde datosAeProcedenciaDesde, DatosAeProcedenciaHasta datosAeProcedenciaHasta,
            DatosRConsumo datosRConsumo, DatosRLecturaDesde datosRLecturaDesde, DatosRLecturaHasta datosRLecturaHasta, ReactivaImporteTotal reactivaImporteTotal,
            DatosPmConsumo datosPmConsumo, DatosPmLecturaHasta datosPmLecturaHasta,
            DatosFinDeRegistro datosFinDeRegistro, String comentarios, String errores) {
        this.cliente = cliente;
        this.datosCabecera = datosCabecera;
        this.datosGeneralesFactura = datosGeneralesFactura;
        this.datosFacturaAtr = datosFacturaAtr;

        this.datosExcesoPotencia = datosExcesoPotencia;
        this.datosPotenciaContratada = datosPotenciaContratada;
        this.datosPotenciaMaxDemandada = datosPotenciaMaxDemandada;
        this.datosPotenciaAFacturar = datosPotenciaAFacturar;
        this.datosPotenciaPrecio = datosPotenciaPrecio;
        this.datosPotenciaImporteTotal = datosPotenciaImporteTotal;

        this.datosEnergiaActiva = datosEnergiaActiva;
        this.datosEnergiaActivaValores = datosEnergiaActivaValores;
        this.datosEnergiaActivaPrecio = datosEnergiaActivaPrecio;
        this.datosEnergiaActivaImporteTotal = datosEnergiaActivaImporteTotal;

        this.cargos1 = cargos1;
        this.cargos2 = cargos2;
        this.cargoImporteTotal1 = cargoImporteTotal1;
        this.cargoImporteTotal2 = cargoImporteTotal2;

        this.datosImpuestoElectrico = datosImpuestoElectrico;
        this.datosAlquileres = datosAlquileres;
        this.datosIva = datosIva;

        this.datosAeConsumo = datosAeConsumo;
        this.datosAeLecturaDesde = datosAeLecturaDesde;
        this.datosAeLecturaHasta = datosAeLecturaHasta;
        this.datosAeProcedenciaDesde = datosAeProcedenciaDesde;
        this.datosAeProcedenciaHasta = datosAeProcedenciaHasta;

        this.datosRConsumo = datosRConsumo;
        this.datosRLecturaDesde = datosRLecturaDesde;
        this.datosRLecturaHasta = datosRLecturaHasta;
        this.reactivaImporteTotal = reactivaImporteTotal;

        this.datosPmConsumo = datosPmConsumo;
        this.datosPmLecturaHasta = datosPmLecturaHasta;

        this.datosFinDeRegistro = datosFinDeRegistro;
        this.comentarios = comentarios;
        this.errores = errores;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public DatosCabecera getDatosCabecera() {
        return datosCabecera;
    }

    public DatosGeneralesFactura getDatosGeneralesFactura() {
        return datosGeneralesFactura;
    }

    public DatosFacturaAtr getDatosFacturaAtr() {
        return datosFacturaAtr;
    }

    public DatosExcesoPotencia getDatosExcesoPotencia() {
        return datosExcesoPotencia;
    }

    public DatosPotenciaContratada getDatosPotenciaContratada() {
        return datosPotenciaContratada;
    }

    public DatosPotenciaMaxDemandada getDatosPotenciaMaxDemandada() {
        return datosPotenciaMaxDemandada;
    }

    public DatosPotenciaAFacturar getDatosPotenciaAFacturar() {
        return datosPotenciaAFacturar;
    }

    public DatosPotenciaPrecio getDatosPotenciaPrecio() {
        return datosPotenciaPrecio;
    }

    public DatosPotenciaImporteTotal getDatosPotenciaImporteTotal() {
        return datosPotenciaImporteTotal;
    }

    public DatosEnergiaActiva getDatosEnergiaActiva() {
        return datosEnergiaActiva;
    }

    public DatosEnergiaActivaValores getDatosEnergiaActivaValores() {
        return datosEnergiaActivaValores;
    }

    public DatosEnergiaActivaPrecio getDatosEnergiaActivaPrecio() {
        return datosEnergiaActivaPrecio;
    }

    public DatosEnergiaActivaImporteTotal getDatosEnergiaActivaImporteTotal() {
        return datosEnergiaActivaImporteTotal;
    }

    public Cargos getCargos1() {
        return cargos1;
    }

    public void setCargos1(Cargos cargos1) {
        this.cargos1 = cargos1;
    }

    public Cargos getCargos2() {
        return cargos2;
    }

    public void setCargos2(Cargos cargos2) {
        this.cargos2 = cargos2;
    }

    public CargoImporteTotal getCargoImporteTotal1() {
        return cargoImporteTotal1;
    }

    public void setCargoImporteTotal1(CargoImporteTotal cargoImporteTotal1) {
        this.cargoImporteTotal1 = cargoImporteTotal1;
    }

    public CargoImporteTotal getCargoImporteTotal2() {
        return cargoImporteTotal2;
    }

    public void setCargoImporteTotal2(CargoImporteTotal cargoImporteTotal2) {
        this.cargoImporteTotal2 = cargoImporteTotal2;
    }

    public DatosImpuestoElectrico getDatosImpuestoElectrico() {
        return datosImpuestoElectrico;
    }

    public DatosAlquileres getDatosAlquileres() {
        return datosAlquileres;
    }

    public DatosIva getDatosIva() {
        return datosIva;
    }

    public DatosAeConsumo getDatosAeConsumo() {
        return datosAeConsumo;
    }

    public DatosAeLecturaDesde getDatosAeLecturaDesde() {
        return datosAeLecturaDesde;
    }

    public DatosAeLecturaHasta getDatosAeLecturaHasta() {
        return datosAeLecturaHasta;
    }

    public DatosAeProcedenciaDesde getDatosAeProcedenciaDesde() {
        return datosAeProcedenciaDesde;
    }

    public DatosAeProcedenciaHasta getDatosAeProcedenciaHasta() {
        return datosAeProcedenciaHasta;
    }

    public DatosRConsumo getDatosRConsumo() {
        return datosRConsumo;
    }

    public DatosRLecturaDesde getDatosRLecturaDesde() {
        return datosRLecturaDesde;
    }

    public DatosRLecturaHasta getDatosRLecturaHasta() {
        return datosRLecturaHasta;
    }

    public ReactivaImporteTotal getReactivaImporteTotal() {
        return reactivaImporteTotal;
    }

    public void setReactivaImporteTotal(ReactivaImporteTotal reactivaImporteTotal) {
        this.reactivaImporteTotal = reactivaImporteTotal;
    }

    public DatosPmConsumo getDatosPmConsumo() {
        return datosPmConsumo;
    }

    public DatosPmLecturaHasta getDatosPmLecturaHasta() {
        return datosPmLecturaHasta;
    }

    public DatosFinDeRegistro getDatosFinDeRegistro() {
        return datosFinDeRegistro;
    }

    public String getComentarios() {
        return comentarios;
    }

    public String getErrores() {
        return errores;
    }

    /*--------------------------------------------Campos Setter-------------------------------------*/
    public void setDatosPotenciaImporteTotal(DatosPotenciaImporteTotal datosPotenciaImporteTotal) {
        this.datosPotenciaImporteTotal = datosPotenciaImporteTotal;
    }

    public void setDatosEnergiaActivaImporteTotal(DatosEnergiaActivaImporteTotal datosEnergiaActivaImporteTotal) {
        this.datosEnergiaActivaImporteTotal = datosEnergiaActivaImporteTotal;
    }

    public void setDatosFinDeRegistro(DatosFinDeRegistro datosFinDeRegistro) {
        this.datosFinDeRegistro = datosFinDeRegistro;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "DocumentoXml{" + "datosGeneralesFactura=" + datosGeneralesFactura + ", datosFacturaAtr=" + datosFacturaAtr + ", datosExcesoPotencia=" + datosExcesoPotencia + ", datosPotenciaContratada=" + datosPotenciaContratada + ", datosPotenciaMaxDemandada=" + datosPotenciaMaxDemandada + ", datosPotenciaAFacturar=" + datosPotenciaAFacturar + ", datosPotenciaPrecio=" + datosPotenciaPrecio + ", datosEnergiaActiva=" + datosEnergiaActiva + ", datosEnergiaActivaValores=" + datosEnergiaActivaValores + ", datosEnergiaActivaPrecio=" + datosEnergiaActivaPrecio + ", datosEnergiaActivaImporteTotal=" + datosEnergiaActivaImporteTotal + ", datosImpuestoElectrico=" + datosImpuestoElectrico + ", datosAlquileres=" + datosAlquileres + ", datosIva=" + datosIva + ", datosAeConsumo=" + datosAeConsumo + ", datosAeLecturaDesde=" + datosAeLecturaDesde + ", datosAeLecturaHasta=" + datosAeLecturaHasta + ", datosAeProcedenciaDesde=" + datosAeProcedenciaDesde + ", datosAeProcedenciaHasta=" + datosAeProcedenciaHasta + ", datosRConsumo=" + datosRConsumo + ", datosRLecturaDesde=" + datosRLecturaDesde + ", datosRLecturaHasta=" + datosRLecturaHasta + ", datosPmConsumo=" + datosPmConsumo + ", datosPmLecturaHasta=" + datosPmLecturaHasta + ", datosFinDeRegistro=" + datosFinDeRegistro + '}';
    }
}
