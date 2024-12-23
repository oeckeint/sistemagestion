package datos.entity.medidas;

import datos.entity.Cliente;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Data
@Entity
@Table(name = "medida_h")
public class MedidaH implements Serializable {

    private static final long serialVersionUid = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medida_h")
    private long idMedidaH;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Column(name = "tipo_medida")
    private int tipoMedida;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "bandera_inv_ver")
    private float banderaInvVer;

    @Column(name = "actent")
    private float actent;

    @Column(name = "qactent")
    private float qactent;

    @Column(name = "actsal")
    private float actsal;

    @Column(name = "qactsal")
    private float qactsal;

    @Column(name = "r_q1")
    private float r_q1;

    @Column(name = "qr_q1")
    private float qr_q1;

    @Column(name = "r_q2")
    private float r_q2;

    @Column(name = "qr_q2")
    private float qr_q2;

    @Column(name = "r_q3")
    private float r_q3;

    @Column(name = "qr_q3")
    private float qr_q3;

    @Column(name = "r_q4")
    private float r_q4;

    @Column(name = "qr_q4")
    private float qr_q4;

    @Column(name = "medres1")
    private float medres1;

    @Column(name = "qmedres1")
    private float qmedres1;

    @Column(name = "medres2")
    private float medres2;

    @Column(name = "qmedres2")
    private float qmedres2;

    @Column(name = "metod_obt")
    private int metodObt;

    @Column(name = "temporal")
    private int temporal;

    @Column(name = "origen")
    private String origen;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    private Calendar createdOn;

    @Column(name ="created_by")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    private Calendar updatedOn;

    @Column(name = "updated_by")
    private String updatedBy;

    public MedidaH() {}

    @PrePersist
    public void prePersist() {
        createdOn = Calendar.getInstance();
    }

}
