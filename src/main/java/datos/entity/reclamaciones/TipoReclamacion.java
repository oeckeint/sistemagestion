package datos.entity.reclamaciones;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

@Data @Entity @Table(name = "tipo_reclamacion")
public class TipoReclamacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_reclamacion")
    private long id;

    @Column(name = "codigo_reclamacion")
    private long codigoReclamacion;

    @Column(name = "descripcion")
    private String descripcion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    private Calendar createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    private Calendar updatedOn;

    @Column(name = "updated_by")
    private String updatedBy;

    @OneToMany(mappedBy = "tipoReclamacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reclamacion> reclamaciones;

    public TipoReclamacion() {
    }

    public TipoReclamacion(long id) {
        this.id = id;
    }

    public TipoReclamacion(int codigoReclamacion, String descripcion) {
        this.codigoReclamacion = codigoReclamacion;
        this.descripcion = descripcion;
    }

}
