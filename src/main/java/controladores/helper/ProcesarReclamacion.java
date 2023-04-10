package controladores.helper;

import datos.entity.Cliente;
import datos.entity.reclamaciones.Reclamacion;
import datos.entity.reclamaciones.SubtipoReclamacion;
import datos.entity.reclamaciones.TipoReclamacion;
import datos.interfaces.CrudDao;
import datos.interfaces.DocumentoXmlService;
import dominio.componentesxml.DatosCabecera;
import dominio.componentesxml.reclamaciones.RechazoReclamacion;
import excepciones.ClienteNoExisteException;
import excepciones.MasDeUnClienteEncontrado;
import excepciones.ReclamacionYaExisteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import utileria.xml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ProcesarReclamacion extends xmlHelper {

    private int codigoDePaso;

    private String codigoDeSolicitud;

    private String reclamacionTipo;

    private TipoReclamacion tipoReclamacion;

    private String reclamacionSubtipo;

    private SubtipoReclamacion subtipoReclamacion;

    private final CrudDao<Reclamacion> service;
    private CrudDao<TipoReclamacion> serviceTipoReclamacion;

    private CrudDao<SubtipoReclamacion> serviceSubtipoReclamacion;

    private final Logger logger = Logger.getLogger(getClass().getName());

    public ProcesarReclamacion(@Qualifier(value = "reclamacionServiceImp") CrudDao<Reclamacion> service,
                               @Qualifier(value = "tipoReclamacionServiceImp") CrudDao<TipoReclamacion> serviceTipoReclamacion,
                               @Qualifier(value = "subtipoReclamacionServiceImp")CrudDao<SubtipoReclamacion> serviceSubtipoReclamacion) {
        this.service = service;
        this.serviceTipoReclamacion = serviceTipoReclamacion;
        this.serviceSubtipoReclamacion = serviceSubtipoReclamacion;
    }

    public void procesar(Document doc, String nombreArchivo) throws MasDeUnClienteEncontrado, ClienteNoExisteException, ReclamacionYaExisteException {
        super.doc = doc;
        super.nombreArchivo = nombreArchivo;
        this.iniciarVariablesR();

        if (this.service.buscarFiltro(this.codigoDeSolicitud, "codigoSolicitud") == null){
            if(super.cliente !=null){
                registrarReclamacion();
            } else{
                logger.log(Level.INFO, "No existe un cliente con el cups {0} por lo que no se registra el archivo con codigo de solicitud " + this.codigoDeSolicitud, super.cups);
                throw new ClienteNoExisteException(super.cups);
            }
        } else {
            logger.log(Level.INFO, "Ya existe un registro con el codigo de solicitud {0} por lo que no se registra.", this.codigoDeSolicitud);
            throw new ReclamacionYaExisteException(this.codigoDeSolicitud);
        }
    }

    private void registrarReclamacion(){
        Reclamacion r = null;
        switch (this.codigoDePaso) {
            case 1:
                this.iniciarVariablesLocales();
                r = this.crearReclamacionPaso1();
                break;
            case 2:
                r = this.crearReclamacionPaso2();
                break;
            case 3:
                System.out.println("PAso 3");
                r = this.crearReclamacionPaso3();
                break;
            case 5:
                this.iniciarVariablesLocales();
                r = this.crearReclamacionPaso5();
                break;
            default:
                System.out.println("No soportado");
                break;
        }
        this.service.guardar(r);
    }

    private Reclamacion crearReclamacionPaso1(){
        Reclamacion reclamacion = new Reclamacion(super.cliente, super.cabeceraReclamacion(), super.solicitudReclamacion());
        return reclamacion;
    }

    private Reclamacion crearReclamacionPaso2(){
        Reclamacion reclamacion = new Reclamacion(super.cliente, super.cabeceraReclamacion(), super.reclamacionRechazo());
        return reclamacion;
    }

    private Reclamacion crearReclamacionPaso3(){
        Reclamacion reclamacion = new Reclamacion(
                super.cliente,
                super.cabeceraReclamacion(),
                super.datosInformacionReclamacion("DatosInformacion"),
                super.informacionAdicionalReclamacion(),
                super.datosRetipificacion(this.serviceTipoReclamacion, this.serviceSubtipoReclamacion)
        );
        return reclamacion;
    }

    private Reclamacion crearReclamacionPaso5(){
        Reclamacion reclamacion = new Reclamacion(super.cliente, super.cabeceraReclamacion(), super.datosInformacionReclamacion("DatosCierre"),super.informacionAdicionalReclamacion(), tipoReclamacion, subtipoReclamacion);
        return reclamacion;
    }

    private void iniciarVariablesR() throws MasDeUnClienteEncontrado {
        this.codigoDePaso = Integer.parseInt(super.doc.getElementsByTagName("CodigoDePaso").item(0).getTextContent());
        this.codigoDeSolicitud = xml.obtenerContenidoNodo("CodigoDeSolicitud", this.doc);
        super.cups = super.doc.getElementsByTagName("CUPS").item(0).getTextContent();
        super.cliente = super.clienteService.encontrarCups(super.cups);
    }

    private void iniciarVariablesLocales() {
        this.reclamacionTipo = xml.obtenerContenidoNodo("Tipo", doc);
        this.reclamacionSubtipo = xml.obtenerContenidoNodo("Subtipo", doc);

        this.tipoReclamacion = this.serviceTipoReclamacion.buscarId(Long.parseLong(this.reclamacionTipo));
        this.subtipoReclamacion = this.serviceSubtipoReclamacion.buscarId(Long.parseLong(this.reclamacionSubtipo));
    }

}
