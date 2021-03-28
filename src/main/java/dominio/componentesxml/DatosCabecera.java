
package dominio.componentesxml;

import java.util.List;

public class DatosCabecera {
    private String codigoREEEmpresaEmisora, codigoREEEmpresaDestino, codigoDelProceso, codigoDePaso, codigoDeSolicitud, fechaSolicitud, cups;

    public DatosCabecera() {
    }

    public DatosCabecera(List<String> datos) {
        this.codigoREEEmpresaEmisora = datos.get(0);
        this.codigoREEEmpresaDestino = datos.get(1);
        this.codigoDelProceso = datos.get(2);
        this.codigoDePaso = datos.get(3);
        this.codigoDeSolicitud = datos.get(4);
        this.fechaSolicitud = datos.get(5);
        this.cups = datos.get(6);
    }

    public String getCodigoREEEmpresaEmisora() {
        return codigoREEEmpresaEmisora;
    }

    public String getCodigoREEEmpresaDestino() {
        return codigoREEEmpresaDestino;
    }

    public String getCodigoDelProceso() {
        return codigoDelProceso;
    }

    public String getCodigoDePaso() {
        return codigoDePaso;
    }

    public String getCodigoDeSolicitud() {
        return codigoDeSolicitud;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public String getCups() {
        return cups;
    }

    @Override
    public String toString() {
        return "DatosCabecera{" + "codigoREEEmpresaEmisora=" + codigoREEEmpresaEmisora + ", codigoREEEmpresaDestino=" + codigoREEEmpresaDestino + ", codigoDelProceso=" + codigoDelProceso + ", codigoDePaso=" + codigoDePaso + ", codigoDeSolicitud=" + codigoDeSolicitud + ", fechaSolicitud=" + fechaSolicitud + ", cups=" + cups + '}';
    }
    
}
