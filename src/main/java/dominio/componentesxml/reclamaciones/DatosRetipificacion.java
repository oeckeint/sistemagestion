
package dominio.componentesxml.reclamaciones;

import datos.entity.reclamaciones.SubtipoReclamacion;
import datos.entity.reclamaciones.TipoReclamacion;
import lombok.Data;

import java.util.List;

@Data
public class DatosRetipificacion {

    private TipoReclamacion tipoReclamacion;
    private SubtipoReclamacion subtipoReclamacion;

    public DatosRetipificacion(TipoReclamacion tipoReclamacion, SubtipoReclamacion subtipoReclamacion) {
        this.tipoReclamacion = tipoReclamacion;
        this.subtipoReclamacion = subtipoReclamacion;
    }

}
