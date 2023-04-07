
package dominio.componentesxml.reclamaciones;

import java.util.List;

public class DatosCabeceraReclamacion {
    private String codigoREEEmpresaEmisora, codigoREEEmpresaDestino, codigoDelProceso, codigoDePaso, codigoDeSolicitud, fechaSolicitud, cups, secuencialDeSolicitud;

    public DatosCabeceraReclamacion() {
    }

    public DatosCabeceraReclamacion(List<String> datos) {
        this.codigoREEEmpresaEmisora = datos.get(0);
        this.codigoREEEmpresaDestino = datos.get(1);
        this.codigoDelProceso = datos.get(2);
        this.codigoDePaso = datos.get(3);
        this.codigoDeSolicitud = datos.get(4);
        this.fechaSolicitud = datos.get(5);
        this.cups = datos.get(6);
        this.secuencialDeSolicitud = datos.get(7);
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

    public String getSecuencialDeSolicitud(){
        return this.secuencialDeSolicitud;
    }
    @Override
    public String toString() {
        return "DatosCabecera{" + "codigoREEEmpresaEmisora=" + codigoREEEmpresaEmisora + ", codigoREEEmpresaDestino=" + codigoREEEmpresaDestino + ", codigoDelProceso=" + codigoDelProceso + ", codigoDePaso=" + codigoDePaso + ", codigoDeSolicitud=" + codigoDeSolicitud + ", fechaSolicitud=" + fechaSolicitud + ", cups=" + cups + '}';
    }
    
}
