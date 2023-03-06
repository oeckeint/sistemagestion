package datos.entity.reclamaciones;

import datos.entity.Cliente;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Data @Entity @Table(name = "reclamaciones")
public class Reclamacion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reclamacion")
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Column(name = "codigo_empresa_emisora")
    private int codigoEmpresaEmisora;

    @Column(name = "codigo_empresa_destino")
    private int codigoEmpresaDestino;

    @Column(name = "codigo_de_paso")
    private int codigoDePaso;

    @Column(name = "codigo_de_solicitud")
    private int codigoDeSolicitud;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_solicitud")
    private Calendar fechaSolicitud;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_incidente")
    private Calendar fechaIncidente;

    @Column(name = "numero_factura_atr")
    private String numeroFacturaATR;

    @Column(name = "comentarios")
    private String comentarios;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_tipo_reclamacion")
    private TipoReclamacion tipoReclamacion;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_subtipo_reclamacion")
    private SubtipoReclamacion subtipoReclamacion;

    @Column(name = "is_deleted")
    private int isDeleted;

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

    public Reclamacion(){}


}
