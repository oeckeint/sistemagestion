package datos.entity.reclamaciones;

import datos.entity.Cliente;
import dominio.componentesxml.DatosCabecera;
import dominio.componentesxml.reclamaciones.*;
import lombok.Data;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Data @Entity @Table(name = "reclamaciones")
public class Reclamacion {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reclamacion")
    private long idReclamacion;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Column(name = "codigo_empresa_emisora")
    private int codigoEmpresaEmisora;

    @Column(name = "codigo_empresa_destino")
    private int codigoEmpresaDestino;

    @Column(name = "codigo_de_paso")
    private long codigoDePaso;

    @Column(name = "codigo_del_proceso")
    private String codigoDelProceso;

    @Column(name = "codigo_de_solicitud")
    private long codigoDeSolicitud;

    @Column(name = "secuencial_de_solicitud")
    private int secuencialDeSolicitud;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_solicitud")
    private Date fechaSolicitud;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_incidente")
    private Date fechaIncidente;

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
    @Column(name = "secuencial_rechazo")
    private int secuencialRechazo;

    @Column(name = "codigo_motivo")
    private int codigoMotivo;

    @Column(name ="codigo_reclamacion_distribuidora")
    private int codigoReclamacionDistribuidora;

    public Reclamacion(){}

    /**
     * Paso 1
     * @param cliente
     * @param datosCabecera
     */
    public Reclamacion(Cliente cliente, DatosCabeceraReclamacion datosCabecera, SolicitudReclamacion solicitudReclamacion){
        this.cliente = cliente;

        //Cabecera
        this.codigoEmpresaEmisora = Integer.parseInt(datosCabecera.getCodigoREEEmpresaEmisora());
        this.codigoEmpresaDestino = Integer.parseInt(datosCabecera.getCodigoREEEmpresaDestino());
        this.codigoDePaso = Integer.parseInt(datosCabecera.getCodigoDePaso());
        this.codigoDelProceso = datosCabecera.getCodigoDelProceso();
        this.codigoDeSolicitud = Long.parseLong(datosCabecera.getCodigoDeSolicitud());
        this.secuencialDeSolicitud = Integer.parseInt(datosCabecera.getSecuencialDeSolicitud());
        try {
            this.fechaSolicitud = new SimpleDateFormat("yyyy-MM-dd").parse(datosCabecera.getFechaSolicitud());
        } catch (ParseException e) {
            System.out.println("Ocurrio un error al procesar una fecha");
            this.fechaSolicitud = null;
        }
        this.comentarios = solicitudReclamacion.getComentarios();
    }

    /**
     * Paso 2
     * @param cliente
     * @param datosCabecera
     * @param rechazoReclamacion
     */
    public Reclamacion(Cliente cliente, DatosCabeceraReclamacion datosCabecera, RechazoReclamacion rechazoReclamacion){
        this.cliente = cliente;

        //Cabecera
        this.codigoEmpresaEmisora = Integer.parseInt(datosCabecera.getCodigoREEEmpresaEmisora());
        this.codigoEmpresaDestino = Integer.parseInt(datosCabecera.getCodigoREEEmpresaDestino());
        this.codigoDePaso = Integer.parseInt(datosCabecera.getCodigoDePaso());
        this.codigoDelProceso = datosCabecera.getCodigoDelProceso();
        this.codigoDeSolicitud = Long.parseLong(datosCabecera.getCodigoDeSolicitud());
        this.secuencialDeSolicitud = Integer.parseInt(datosCabecera.getSecuencialDeSolicitud());
        try {
            this.fechaSolicitud = new SimpleDateFormat("yyyy-MM-dd").parse(datosCabecera.getFechaSolicitud());
        } catch (ParseException e) {
            System.out.println("Ocurrio un error al procesar una fecha");
            this.fechaSolicitud = null;
        }

        //Rechazo
        if(rechazoReclamacion.equals("")){
        this.secuencialRechazo = Integer.parseInt(rechazoReclamacion.getSecuencial());
        this.codigoMotivo = Integer.parseInt(rechazoReclamacion.getCodigoMotivo());
        this.comentarios = rechazoReclamacion.getComentariosReclamacion();
        } else{
            System.out.println("Cadenas vacias");
        }
    }

    public Reclamacion(Cliente cliente, DatosCabeceraReclamacion datosCabecera, DatosInformacionReclamacion datosInformacionReclamacion, InformacionAdicionalReclamacion informacionAdicionalReclamacion, DatosRetipificacion datosRetipificacion){
        this.cliente = cliente;
        this.tipoReclamacion = datosRetipificacion.getTipoReclamacion();
        this.subtipoReclamacion = datosRetipificacion.getSubtipoReclamacion();

        //Cabecera
        this.codigoEmpresaEmisora = Integer.parseInt(datosCabecera.getCodigoREEEmpresaEmisora());
        this.codigoEmpresaDestino = Integer.parseInt(datosCabecera.getCodigoREEEmpresaDestino());
        this.codigoDePaso = Integer.parseInt(datosCabecera.getCodigoDePaso());
        this.codigoDelProceso = datosCabecera.getCodigoDelProceso();
        this.codigoDeSolicitud = Long.parseLong(datosCabecera.getCodigoDeSolicitud());
        this.secuencialDeSolicitud = Integer.parseInt(datosCabecera.getSecuencialDeSolicitud());
        try {
            this.fechaSolicitud = new SimpleDateFormat("yyyy-MM-dd").parse(datosCabecera.getFechaSolicitud());
        } catch (ParseException e) {
            System.out.println("Ocurrio un error al procesar una fecha");
            this.fechaSolicitud = null;
        }

        //Datos Informacion
        this.codigoReclamacionDistribuidora = Integer.parseInt(datosInformacionReclamacion.getCodigoReclamacioneDistribuidora());

        //Informacion Adicional
        this.comentarios = informacionAdicionalReclamacion.getComentarios();
    };


    /**
     * Paso 5
     * @param cliente
     * @param datosCabecera
     * @param datosInformacionReclamacion
     * @param informacionAdicionalReclamacion
     * @param tipoReclamacion
     * @param subtipoReclamacion
     */
    public Reclamacion(Cliente cliente, DatosCabeceraReclamacion datosCabecera, DatosInformacionReclamacion datosInformacionReclamacion, InformacionAdicionalReclamacion informacionAdicionalReclamacion, TipoReclamacion tipoReclamacion, SubtipoReclamacion subtipoReclamacion){
        this.cliente = cliente;
        this.tipoReclamacion = tipoReclamacion;
        this.subtipoReclamacion = subtipoReclamacion;

        //Cabecera
        this.codigoEmpresaEmisora = Integer.parseInt(datosCabecera.getCodigoREEEmpresaEmisora());
        this.codigoEmpresaDestino = Integer.parseInt(datosCabecera.getCodigoREEEmpresaDestino());
        this.codigoDePaso = Integer.parseInt(datosCabecera.getCodigoDePaso());
        this.codigoDelProceso = datosCabecera.getCodigoDelProceso();
        this.codigoDeSolicitud = Long.parseLong(datosCabecera.getCodigoDeSolicitud());
        this.secuencialDeSolicitud = Integer.parseInt(datosCabecera.getSecuencialDeSolicitud());
        try {
            this.fechaSolicitud = new SimpleDateFormat("yyyy-MM-dd").parse(datosCabecera.getFechaSolicitud());
        } catch (ParseException e) {
            System.out.println("Ocurrio un error al procesar una fecha");
            this.fechaSolicitud = null;
        }

        //Datos Informacion
        this.codigoReclamacionDistribuidora = Integer.parseInt(datosInformacionReclamacion.getCodigoReclamacioneDistribuidora());

        //Informacion Adicional
        this.comentarios = informacionAdicionalReclamacion.getComentarios();
    };
}
