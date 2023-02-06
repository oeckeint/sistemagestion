package datos.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "medida")
public class Medida {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medida")
    private long idMedida;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "bandera_inv_ver")
    private int banderaInvVer;

    @Column(name = "ae1")
    private int ae1;

    @Column(name = "as1")
    private int as1;

    @Column(name = "r_q1")
    private int rq1;

    @Column(name = "r_q2")
    private int rq2;

    @Column(name = "r_q3")
    private int rq3;

    @Column(name = "r_q4")
    private int rq4;

    @Column(name = "metod_obt")
    private int metodObj;

    @Column(name = "indic_firmez")
    private int indicFirmez;

    @Column(name = "codigo_factura")
    private String codigoFactura;

    @Column(name = "medida_col")
    private String medidaCol;

    public Medida() {
    }

    public Medida(Cliente cliente, Date fecha, int banderaInvVer, int ae1, int as1, int rq1, int rq2, int rq3, int rq4, int metodObj, int indicFirmez, String codigoFactura, String medidaCol) {
        this.cliente = cliente;
        this.fecha = fecha;
        this.banderaInvVer = banderaInvVer;
        this.ae1 = ae1;
        this.as1 = as1;
        this.rq1 = rq1;
        this.rq2 = rq2;
        this.rq3 = rq3;
        this.rq4 = rq4;
        this.metodObj = metodObj;
        this.indicFirmez = indicFirmez;
        this.codigoFactura = codigoFactura;
        this.medidaCol = medidaCol;
    }

    public Medida(long idCliente){

    }

}
