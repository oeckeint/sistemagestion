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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import utileria.xml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Component
public class ProcesarReclamacion extends xmlHelper {

    private int codigoDePaso;

    private String reclamacionTipo;

    private TipoReclamacion tipoReclamacion;

    private String reclamacionSubtipo;

    private SubtipoReclamacion subtipoReclamacion;

    private final CrudDao<Reclamacion> service;
    private CrudDao<TipoReclamacion> serviceTipoReclamacion;

    private CrudDao<SubtipoReclamacion> serviceSubtipoReclamacion;

    public ProcesarReclamacion(@Qualifier(value = "reclamacionServiceImp") CrudDao<Reclamacion> service,
                               @Qualifier(value = "tipoReclamacionServiceImp") CrudDao<TipoReclamacion> serviceTipoReclamacion,
                               @Qualifier(value = "subtipoReclamacionServiceImp")CrudDao<SubtipoReclamacion> serviceSubtipoReclamacion) {
        this.service = service;
        this.serviceTipoReclamacion = serviceTipoReclamacion;
        this.serviceSubtipoReclamacion = serviceSubtipoReclamacion;
    }

    public void procesar(Document doc, String nombreArchivo) throws MasDeUnClienteEncontrado {
        super.doc = doc;
        super.nombreArchivo = nombreArchivo;
        this.iniciarVariablesR();

        if(super.cliente !=null){
            registrarReclamacion();
        } else{
            System.out.println("No se registro nada :(");
        }
    }

    private void registrarReclamacion(){
        Reclamacion r = null;
        System.out.println(this.codigoDePaso);
        switch (this.codigoDePaso) {
            case 1:
                this.iniciarVariablesLocales();
                r = this.crearReclamacionPaso1();
                break;
            case 2:
                r = this.crearReclamacionPaso2();
                break;
            case 3:
                this.iniciarVariablesLocales();
                r = this.crearReclamacionPaso3();
                break;
            default:
                System.out.println("No soportado");
                break;
        }
        this.service.guardar(r);
    }

    private Reclamacion crearReclamacionPaso1(){
        Reclamacion reclamacion = new Reclamacion(super.cliente, super.cabeceraReclamacion());
        return reclamacion;
    }

    private Reclamacion crearReclamacionPaso2(){
        Reclamacion reclamacion = new Reclamacion(super.cliente, super.cabeceraReclamacion(), super.reclamacionRechazo());
        return reclamacion;
    }

    private Reclamacion crearReclamacionPaso3(){
        Reclamacion reclamacion = new Reclamacion(super.cliente, super.cabeceraReclamacion(), super.datosInformacionReclamacion(),super.informacionAdicionalReclamacion(), tipoReclamacion, subtipoReclamacion);
        return reclamacion;
    }

    private void iniciarVariablesR() throws MasDeUnClienteEncontrado {

        this.codigoDePaso = Integer.parseInt(super.doc.getElementsByTagName("CodigoDePaso").item(0).getTextContent());
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
