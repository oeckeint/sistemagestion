package datos.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.*;


@Data
@Entity
@Table(name = "medida_cch")

public class MedidaCCH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medida_cch")
    private long idMedidaCCH;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "bandera_inv_ver")
    private int banderaInvVer;

    @Column(name = "actent")
    private int actent;

    @Column(name = "metod")
    private int metod;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "created_on")
    private Calendar createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "updated_on")
    private Calendar updatedOn;

    @Column(name = "updated_by")
    private String updatedBy;

    public MedidaCCH() {
    }
    public MedidaCCH(Cliente cliente, Date fecha, int banderaInvVer, int actent, int metod, Calendar createdOn, String createdBy, Calendar updatedOn, String updatedBy){
        this.cliente = cliente;
        this.fecha = fecha;
        this.banderaInvVer = banderaInvVer;
        this.actent = actent;
        this.metod = metod;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.updatedOn = updatedOn;
        this.updatedBy = updatedBy;
    }

    public MedidaCCH(long idCliente){
    }

}
