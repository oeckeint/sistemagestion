
package dominio.componentesxml.reclamaciones;

import java.util.List;

public class InformacionAdicionalReclamacion {
    private String comentarios;

    public InformacionAdicionalReclamacion() {
    }

    public InformacionAdicionalReclamacion(List<String> datos) {
        this.comentarios = datos.get(0);
    }

    public String getComentarios(){
        return this.comentarios;
    }

    @Override
    public String toString() {
        return "DatosCabecera{" + "comentarios= " + this.comentarios + '}';
    }
    
}
