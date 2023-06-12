package datos.entity.medidas;

import datos.entity.Cliente;
import lombok.Data;
import java.util.*;
import javax.persistence.*;

@Data
@Entity
@Table(name = "medidaqh")
public class MedidaQH {
    public MedidaQH() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medidaQH")
    private long idMedidaQH;
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
    public MedidaQH(Cliente idCliente, int tipoMed, Date fecha, int banderaInvVer, int actent, int qactent, int actsal, int qactsal
    , int r_q1, int qr_q1, int r_q2, int qr_q2, int r_q3, int qr_q3, int r_q4, int qr_q4, int medres1, int qmedres1, int medres2, int qmedres2, int metodObt, int temporal){

        this.cliente = idCliente;
        this.tipoMed = tipoMed;
        this.fecha = fecha;
        this.banderaInvVer = banderaInvVer;
        this.actent = actent;
        this.qactent = qactent;
        this.actsal = actsal;
        this.qactsal = qactsal;
        this.r_q1 = r_q1;
        this.r_q2 = r_q2;
        this.r_q3 = r_q3;
        this.r_q4 = r_q4;
        this.qr_q1 = qr_q1;
        this.qr_q2 = qr_q2;
        this.qr_q3 = qr_q3;
        this.qr_q4 = qr_q4;
        this.medres1 = medres1;
        this.qmedres1 = qmedres1;
        this.medres2 = medres2;
        this.qmedres2 = qmedres2;
        this.metodObt = metodObt;
        this.temporal = temporal;
    }

    public MedidaQH(long idMedidaQH){

    }

}

