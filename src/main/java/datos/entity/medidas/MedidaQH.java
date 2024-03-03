package datos.entity.medidas;

import datos.entity.Cliente;
import datos.entity.GenericEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;
import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "medidaqh")
public class MedidaQH implements GenericEntity<MedidaQH> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medidaQH")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Column(name = "tipomed")
    private int tipoMed;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "bandera_inv_ver")
    private int banderaInvVer;

    @Column(name = "actent")
    private int actent;

    @Column(name = "qactent")
    private int qactent;

    @Column(name = "actsal")
    private int actsal;

    @Column(name = "qactsal")
    private int qactsal;

    @Column(name = "r_q1")
    private int r_q1;

    @Column(name = "qr_q1")
    private int qr_q1;

    @Column(name = "r_q2")
    private int r_q2;

    @Column(name = "qr_q2")
    private int qr_q2;

    @Column(name = "r_q3")
    private int r_q3;

    @Column(name = "qr_q3")
    private int qr_q3;

    @Column(name = "r_q4")
    private int r_q4;

    @Column(name = "qr_q4")
    private int qr_q4;

    @Column(name = "medres1")
    private int medres1;

    @Column(name = "qmedres1")
    private int qmedres1;

    @Column(name = "medres2")
    private int medres2;

    @Column(name = "qmedres2")
    private int qmedres2;

    @Column(name = "metod_obt")
    private int metodObt;
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

    @Column(name = "temporal")
    private int temporal;

    @Override
    public MedidaQH createNewInstance() {
        return null;
    }

    @Override
    public void update(MedidaQH source) {

    }
}

