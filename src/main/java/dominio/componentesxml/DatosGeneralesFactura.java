package dominio.componentesxml;

import java.util.List;

public class DatosGeneralesFactura {
    private String codigoFiscalFactura, tipoFactura, motivoFacturacion, codigoFacturaRectificadaAnulada, fechaFactura;

    public DatosGeneralesFactura() {
    }
    
    public DatosGeneralesFactura(List<String> datosGenerales){
        this.codigoFiscalFactura = datosGenerales.get(0);
        this.tipoFactura = datosGenerales.get(1);
        this.motivoFacturacion = datosGenerales.get(2);
        this.codigoFacturaRectificadaAnulada = datosGenerales.get(3);
        this.fechaFactura = datosGenerales.get(4);
    }

    public String getCodigoFiscalFactura() {
        return codigoFiscalFactura;
    }

    public String getTipoFactura() {
        return tipoFactura;
    }

    public String getMotivoFacturacion() {
        return motivoFacturacion;
    }

    public String getCodigoFacturaRectificadaAnulada() {
        return codigoFacturaRectificadaAnulada;
    }

    public String getFechaFactura() {
        return fechaFactura;
    }
    
    

    @Override
    public String toString() {
        return "DatosGeneralesFactura{" + "codigoFiscalFactura=" + codigoFiscalFactura + ", tipoFactura=" + tipoFactura + ", motivoFacturacion=" + motivoFacturacion + ", codigoFacturaRectificadaAnulada=" + codigoFacturaRectificadaAnulada + ", fechaFactura=" + fechaFactura + '}';
    }
}
