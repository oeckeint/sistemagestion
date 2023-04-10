package dominio.componentesxml.reclamaciones;

import lombok.Data;

import java.util.List;

@Data
public class SolicitudReclamacion {

    private String comentarios;

    public SolicitudReclamacion(List<String> datos){
        this.comentarios = datos.get(0);
    }

}
