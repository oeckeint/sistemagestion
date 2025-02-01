package datos.entity.medidas;

import datos.entity.Cliente;
import datos.entity.GenericEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

    @ManyToOne//(cascade = CascadeType.PERSIST)
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

    @NonNull
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

    @Column(name = "temporal")
    private int temporal;

    @Override
    public MedidaQH createNewInstance() {
        MedidaQH newInstance = new MedidaQH();
        newInstance.cliente = this.cliente;
        newInstance.tipoMed = this.tipoMed;
        newInstance.fecha = this.fecha;
        newInstance.banderaInvVer = this.banderaInvVer;
        newInstance.actent = this.actent;
        newInstance.qactent = this.qactent;
        newInstance.actsal = this.actsal;
        newInstance.qactsal = this.qactsal;
        newInstance.r_q1 = this.r_q1;
        newInstance.qr_q1 = this.qr_q1;
        newInstance.r_q2 = this.r_q2;
        newInstance.qr_q2 = this.qr_q2;
        newInstance.r_q3 = this.r_q3;
        newInstance.qr_q3 = this.qr_q3;
        newInstance.r_q4 = this.r_q4;
        newInstance.qr_q4 = this.qr_q4;
        newInstance.medres1 = this.medres1;
        newInstance.qmedres1 = this.qmedres1;
        newInstance.medres2 = this.medres2;
        newInstance.qmedres2 = this.qmedres2;
        newInstance.metodObt = this.metodObt;
        newInstance.origen = this.origen;
        newInstance.createdOn = this.createdOn;
        newInstance.createdBy = this.createdBy;
        newInstance.updatedOn = this.updatedOn;
        newInstance.updatedBy = this.updatedBy;
        newInstance.temporal = this.temporal;
        return newInstance;
    }

    @Override
    public void update(MedidaQH source) {
        this.cliente = source.cliente;
        this.tipoMed = source.tipoMed;
        this.fecha = source.fecha;
        this.banderaInvVer = source.banderaInvVer;
        this.actent = source.actent;
        this.qactent = source.qactent;
        this.actsal = source.actsal;
        this.qactsal = source.qactsal;
        this.r_q1 = source.r_q1;
        this.qr_q1 = source.qr_q1;
        this.r_q2 = source.r_q2;
        this.qr_q2 = source.qr_q2;
        this.r_q3 = source.r_q3;
        this.qr_q3 = source.qr_q3;
        this.r_q4 = source.r_q4;
        this.qr_q4 = source.qr_q4;
        this.medres1 = source.medres1;
        this.qmedres1 = source.qmedres1;
        this.medres2 = source.medres2;
        this.qmedres2 = source.qmedres2;
        this.metodObt = source.metodObt;
        this.origen = source.origen;
        this.createdOn = source.createdOn;
        this.createdBy = source.createdBy;
        this.updatedOn = source.updatedOn;
        this.updatedBy = source.updatedBy;
        this.temporal = source.temporal;
    }

    @PrePersist
    public void prePersist() {
        createdOn = Calendar.getInstance();
    }

    @PreUpdate
    public void preUpdate() {
        updatedOn = Calendar.getInstance();
    }
}

