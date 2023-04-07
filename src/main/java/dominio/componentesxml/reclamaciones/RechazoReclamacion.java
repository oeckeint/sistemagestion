
package dominio.componentesxml.reclamaciones;

import java.util.List;

public class RechazoReclamacion {
    private String secuencial, codigoMotivo, comentariosReclamacion;

    public RechazoReclamacion() {
    }

    public RechazoReclamacion(List<String> datos) {
        this.secuencial = datos.get(0);
        this.codigoMotivo = datos.get(1);
        this.comentariosReclamacion = datos.get(2);
    }

    public String getSecuencial(){
        return this.secuencial;
    }

    public String getCodigoMotivo(){
        return this.codigoMotivo;
    }

    public String getComentariosReclamacion() {
        return this.comentariosReclamacion;
    }

    @Override
    public String toString() {
        return "RechazoReclamacion{" + "secuencial=" + this.secuencial + ", codigoMotivo= " + this.codigoMotivo + ", comentarios= " + this.comentariosReclamacion + "}";
    }
}
