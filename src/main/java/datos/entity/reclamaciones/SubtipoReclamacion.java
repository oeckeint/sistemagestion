package datos.entity.reclamaciones;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

@Data @Entity @Table(name = "subtipo_reclamacion")
public class SubtipoReclamacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_subtipo_reclamacion")
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

    @OneToMany(mappedBy = "subtipoReclamacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reclamacion> reclamaciones;

    public SubtipoReclamacion() {
    }
    public SubtipoReclamacion(long id) {
        this.id = id;
    }

}
