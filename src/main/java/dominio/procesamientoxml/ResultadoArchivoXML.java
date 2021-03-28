
package dominio.procesamientoxml;

import java.util.Date;

public class ResultadoArchivoXML {
    
    private int idClente;
    private String compania;
    private String tarifa;
    private String fechaFactura;
    private String fechaInicio;
    private String fechaFin;
    private String numeroDias;
    private String cups;
    private String cliente;
    private String importeTotalTerminoPotencia;
    private String importeTotalEnergiaActiva;
    private String importeTotalEnergiaReactiva;
    private String importeTotalExcesos;
    private String importeFacturacionAlquileres;
    
    /**************Elementos Datos periodo****************/
    /*DatosP# kW Fact*/
    private double potenciaAFacturar1;
    private double potenciaAFacturar2;
    private double potenciaAFacturar3;
    private double potenciaAFacturar4;
    private double potenciaAFacturar5;
    private double potenciaAFacturar6;
    /*Datos P# kW Cons*/
    private double potenciaMaxDemandada1;
    private double potenciaMaxDemandada2;
    private double potenciaMaxDemandada3;
    private double potenciaMaxDemandada4;
    private double potenciaMaxDemandada5;
    private double potenciaMaxDemandada6;
    /*PrecioPotencia*/
    private double precioPotencia1;
    private double precioPotencia2;
    private double precioPotencia3;
    private double precioPotencia4;
    private double precioPotencia5;
    private double precioPotencia6;
    /*PotenciaContratada*/
    private double potenciaContratada1;
    private double potenciaContratada2;
    private double potenciaContratada3;
    private double potenciaContratada4;
    private double potenciaContratada5;
    private double potenciaContratada6;
    /**************Fin Elementos Datos periodo****************/
    /*************************DatosMagnitud*************************/
    /*Datos A# kWh (Confirmar Orden)*/
    private double consumoCalculado1;
    private double consumoCalculado2;
    private double consumoCalculado3;
    private double consumoCalculado4;
    private double consumoCalculado5;
    private double consumoCalculado6;
    /*************************FinDatosMagnitud*************************/
    private String codigoFiscalFactura;
    private String tipoFactura;
    private String remesa;
    private String tipoLectura;

    public ResultadoArchivoXML(int idClente, String compania ,String tarifa, String fecha, String fechaInicio, String fechaFin, String numeroDias, String Cups, String Cliente, String ImporteTotalTerminoPotencia, String ImportetoralEnergiaActiva, String ImporteTotalEnergíaReactiva, String ImporteTotalExcesos, String ImporteFacturacionAlquileres,
                                double potenciaAFacturar1, double potenciaAFacturar2, double potenciaAFacturar3, double potenciaAFacturar4, double potenciaAFacturar5, double potenciaAFacturar6,
                                double potenciaMaxDemandada1, double potenciaMaxDemandada2, double potenciaMaxDemandada3, double potenciaMaxDemandada4, double potenciaMaxDemandada5, double potenciaMaxDemandada6,
                                double precioPotencia1, double precioPotencia2, double precioPotencia3, double precioPotencia4, double precioPotencia5, double precioPotencia6,
                                double potenciaContratada1, double potenciaContratada2, double potenciaContratada3, double potenciaContratada4, double potenciaContratada5, double potenciaContratada6,
                                double consumoalculado1, double consumoalculado2, double consumoalculado3, double consumoalculado4, double consumoalculado5, double consumoalculado6,
                                 String codigoFiscalFactura, String tipoFactura, String remesa) {
        this.idClente = idClente;
        this.compania = compania;
        this.tarifa = tarifa;
        this.fechaFactura = fecha;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.numeroDias = numeroDias;
        this.cups = Cups;
        this.cliente = Cliente;
        this.importeTotalTerminoPotencia = ImporteTotalTerminoPotencia;
        this.importeTotalEnergiaActiva = ImportetoralEnergiaActiva;
        this.importeTotalEnergiaReactiva = ImporteTotalEnergíaReactiva;
        this.importeTotalExcesos = ImporteTotalExcesos;
        this.importeFacturacionAlquileres = ImporteFacturacionAlquileres;
        
        this.potenciaAFacturar1 = potenciaAFacturar1;
        this.potenciaAFacturar2 = potenciaAFacturar2;
        this.potenciaAFacturar3 = potenciaAFacturar3;
        this.potenciaAFacturar4 = potenciaAFacturar4;
        this.potenciaAFacturar5 = potenciaAFacturar5;
        this.potenciaAFacturar6 = potenciaAFacturar6;
        
        this.potenciaMaxDemandada1 = potenciaMaxDemandada1;
        this.potenciaMaxDemandada2 = potenciaMaxDemandada2;
        this.potenciaMaxDemandada3 = potenciaMaxDemandada3;
        this.potenciaMaxDemandada4 = potenciaMaxDemandada4;
        this.potenciaMaxDemandada5 = potenciaMaxDemandada5;
        this.potenciaMaxDemandada6 = potenciaMaxDemandada6;
        
        this.precioPotencia1 = precioPotencia1;
        this.precioPotencia2 = precioPotencia2;
        this.precioPotencia3 = precioPotencia3;
        this.precioPotencia4 = precioPotencia4;
        this.precioPotencia5 = precioPotencia5;
        this.precioPotencia6 = precioPotencia6;
        
        this.potenciaContratada1 = potenciaContratada1;
        this.potenciaContratada2 = potenciaContratada2;
        this.potenciaContratada3 = potenciaContratada3;
        this.potenciaContratada4 = potenciaContratada4;
        this.potenciaContratada5 = potenciaContratada5;
        this.potenciaContratada6 = potenciaContratada6;
        
        this.consumoCalculado1 = consumoalculado1;
        this.consumoCalculado2 = consumoalculado2;
        this.consumoCalculado3 = consumoalculado3;
        this.consumoCalculado4 = consumoalculado4;
        this.consumoCalculado5 = consumoalculado5;
        this.consumoCalculado6 = consumoalculado6;
        
        this.codigoFiscalFactura = codigoFiscalFactura;
        this.tipoFactura = tipoFactura;
        this.remesa = remesa;
    }

    public int getIdClente() {
        return idClente;
    }

    public void setIdClente(int idClente) {
        this.idClente = idClente;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getTarifa() {
        return tarifa;
    }

    public void setTarifa(String tarifa) {
        this.tarifa = tarifa;
    }

    public String getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(String fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getNumeroDias() {
        return numeroDias;
    }

    public void setNumeroDias(String numeroDias) {
        this.numeroDias = numeroDias;
    }

    public String getCups() {
        return cups;
    }

    public void setCups(String cups) {
        this.cups = cups;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getImporteTotalTerminoPotencia() {
        return importeTotalTerminoPotencia;
    }

    public void setImporteTotalTerminoPotencia(String importeTotalTerminoPotencia) {
        this.importeTotalTerminoPotencia = importeTotalTerminoPotencia;
    }

    public String getImporteTotalEnergiaActiva() {
        return importeTotalEnergiaActiva;
    }

    public void setImporteTotalEnergiaActiva(String importeTotalEnergiaActiva) {
        this.importeTotalEnergiaActiva = importeTotalEnergiaActiva;
    }

    public String getImporteTotalEnergiaReactiva() {
        return importeTotalEnergiaReactiva;
    }

    public void setImporteTotalEnergiaReactiva(String importeTotalEnergiaReactiva) {
        this.importeTotalEnergiaReactiva = importeTotalEnergiaReactiva;
    }

    public String getImporteTotalExcesos() {
        return importeTotalExcesos;
    }

    public void setImporteTotalExcesos(String importeTotalExcesos) {
        this.importeTotalExcesos = importeTotalExcesos;
    }

    public String getImporteFacturacionAlquileres() {
        return importeFacturacionAlquileres;
    }

    public void setImporteFacturacionAlquileres(String importeFacturacionAlquileres) {
        this.importeFacturacionAlquileres = importeFacturacionAlquileres;
    }
    /////
    /*Potencia a Facturar*/

    public double getPotenciaAFacturar1() {
        return potenciaAFacturar1;
    }

    public void setPotenciaAFacturar1(double potenciaAFacturar1) {
        this.potenciaAFacturar1 = potenciaAFacturar1;
    }

    public double getPotenciaAFacturar2() {
        return potenciaAFacturar2;
    }

    public void setPotenciaAFacturar2(double potenciaAFacturar2) {
        this.potenciaAFacturar2 = potenciaAFacturar2;
    }

    public double getPotenciaAFacturar3() {
        return potenciaAFacturar3;
    }

    public void setPotenciaAFacturar3(double potenciaAFacturar3) {
        this.potenciaAFacturar3 = potenciaAFacturar3;
    }

    public double getPotenciaAFacturar4() {
        return potenciaAFacturar4;
    }

    public void setPotenciaAFacturar4(double potenciaAFacturar4) {
        this.potenciaAFacturar4 = potenciaAFacturar4;
    }

    public double getPotenciaAFacturar5() {
        return potenciaAFacturar5;
    }

    public void setPotenciaAFacturar5(double potenciaAFacturar5) {
        this.potenciaAFacturar5 = potenciaAFacturar5;
    }

    public double getPotenciaAFacturar6() {
        return potenciaAFacturar6;
    }

    public void setPotenciaAFacturar6(double potenciaAFacturar6) {
        this.potenciaAFacturar6 = potenciaAFacturar6;
    }

    public double getPotenciaMaxDemandada1() {
        return potenciaMaxDemandada1;
    }

    public void setPotenciaMaxDemandada1(double potenciaMaxDemandada1) {
        this.potenciaMaxDemandada1 = potenciaMaxDemandada1;
    }

    public double getPotenciaMaxDemandada2() {
        return potenciaMaxDemandada2;
    }

    public void setPotenciaMaxDemandada2(double potenciaMaxDemandada2) {
        this.potenciaMaxDemandada2 = potenciaMaxDemandada2;
    }

    public double getPotenciaMaxDemandada3() {
        return potenciaMaxDemandada3;
    }

    public void setPotenciaMaxDemandada3(double potenciaMaxDemandada3) {
        this.potenciaMaxDemandada3 = potenciaMaxDemandada3;
    }

    public double getPotenciaMaxDemandada4() {
        return potenciaMaxDemandada4;
    }

    public void setPotenciaMaxDemandada4(double potenciaMaxDemandada4) {
        this.potenciaMaxDemandada4 = potenciaMaxDemandada4;
    }

    public double getPotenciaMaxDemandada5() {
        return potenciaMaxDemandada5;
    }

    public void setPotenciaMaxDemandada5(double potenciaMaxDemandada5) {
        this.potenciaMaxDemandada5 = potenciaMaxDemandada5;
    }

    public double getPotenciaMaxDemandada6() {
        return potenciaMaxDemandada6;
    }

    public void setPotenciaMaxDemandada6(double potenciaMaxDemandada6) {
        this.potenciaMaxDemandada6 = potenciaMaxDemandada6;
    }

    public double getPrecioPotencia1() {
        return precioPotencia1;
    }

    public void setPrecioPotencia1(double precioPotencia1) {
        this.precioPotencia1 = precioPotencia1;
    }

    public double getPrecioPotencia2() {
        return precioPotencia2;
    }

    public void setPrecioPotencia2(double precioPotencia2) {
        this.precioPotencia2 = precioPotencia2;
    }

    public double getPrecioPotencia3() {
        return precioPotencia3;
    }

    public void setPrecioPotencia3(double precioPotencia3) {
        this.precioPotencia3 = precioPotencia3;
    }

    public double getPrecioPotencia4() {
        return precioPotencia4;
    }

    public void setPrecioPotencia4(double precioPotencia4) {
        this.precioPotencia4 = precioPotencia4;
    }

    public double getPrecioPotencia5() {
        return precioPotencia5;
    }

    public void setPrecioPotencia5(double precioPotencia5) {
        this.precioPotencia5 = precioPotencia5;
    }

    public double getPrecioPotencia6() {
        return precioPotencia6;
    }

    public void setPrecioPotencia6(double precioPotencia6) {
        this.precioPotencia6 = precioPotencia6;
    }

    public double getPotenciaContratada1() {
        return potenciaContratada1;
    }

    public void setPotenciaContratada1(double potenciaContratada1) {
        this.potenciaContratada1 = potenciaContratada1;
    }

    public double getPotenciaContratada2() {
        return potenciaContratada2;
    }

    public void setPotenciaContratada2(double potenciaContratada2) {
        this.potenciaContratada2 = potenciaContratada2;
    }

    public double getPotenciaContratada3() {
        return potenciaContratada3;
    }

    public void setPotenciaContratada3(double potenciaContratada3) {
        this.potenciaContratada3 = potenciaContratada3;
    }

    public double getPotenciaContratada4() {
        return potenciaContratada4;
    }

    public void setPotenciaContratada4(double potenciaContratada4) {
        this.potenciaContratada4 = potenciaContratada4;
    }

    public double getPotenciaContratada5() {
        return potenciaContratada5;
    }

    public void setPotenciaContratada5(double potenciaContratada5) {
        this.potenciaContratada5 = potenciaContratada5;
    }

    public double getPotenciaContratada6() {
        return potenciaContratada6;
    }

    public void setPotenciaContratada6(double potenciaContratada6) {
        this.potenciaContratada6 = potenciaContratada6;
    }

    public double getConsumoCalculado1() {
        return consumoCalculado1;
    }

    public void setConsumoCalculado1(double consumoCalculado1) {
        this.consumoCalculado1 = consumoCalculado1;
    }

    public double getConsumoCalculado2() {
        return consumoCalculado2;
    }

    public void setConsumoCalculado2(double consumoCalculado2) {
        this.consumoCalculado2 = consumoCalculado2;
    }

    public double getConsumoCalculado3() {
        return consumoCalculado3;
    }

    public void setConsumoCalculado3(double consumoCalculado3) {
        this.consumoCalculado3 = consumoCalculado3;
    }

    public double getConsumoCalculado4() {
        return consumoCalculado4;
    }

    public void setConsumoCalculado4(double consumoCalculado4) {
        this.consumoCalculado4 = consumoCalculado4;
    }

    public double getConsumoCalculado5() {
        return consumoCalculado5;
    }

    public void setConsumoCalculado5(double consumoCalculado5) {
        this.consumoCalculado5 = consumoCalculado5;
    }

    public double getConsumoCalculado6() {
        return consumoCalculado6;
    }

    public void setConsumoCalculado6(double consumoCalculado6) {
        this.consumoCalculado6 = consumoCalculado6;
    }
    
    
    
    ///

    public String getCodigoFiscalFactura() {
        return codigoFiscalFactura;
    }

    public void setCodigoFiscalFactura(String codigoFiscalFactura) {
        this.codigoFiscalFactura = codigoFiscalFactura;
    }

    public String getTipoFactura() {
        return tipoFactura;
    }

    public void setTipoFactura(String tipoFactura) {
        this.tipoFactura = tipoFactura;
    }

    public String getRemesa() {
        return remesa;
    }

    public void setRemesa(String remesa) {
        this.remesa = remesa;
    }
    
    
}
