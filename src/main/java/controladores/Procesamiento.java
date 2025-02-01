package controladores;

import controladores.comercializador.Comercializador;
import controladores.comercializador.HelperComercializador;
import controladores.helper.*;
import controladores.helper.medidas.*;
import controladores.helper.ProcesarReclamacion;
import datos.interfaces.ClienteService;
import datos.interfaces.CrudDao;
import datos.interfaces.DocumentoXmlService;
import excepciones.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import excepciones.comercializador.*;
import excepciones.medidas.NombreArchivoContieneEspacios;
import excepciones.medidas.NombreArchivoElementosTamanoDiferente;
import excepciones.medidas.NombreArchivoSinExtension;
import excepciones.medidas.NombreArchivoTamanoDiferente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import utileria.ArchivoTexto;
import utileria.StringHelper;

@Controller
@RequestMapping("/procesar")
public class Procesamiento {

    private static final Logger log = LoggerFactory.getLogger(Procesamiento.class);
    @Autowired
    @Qualifier(value = "peajesServiceImp")
    private DocumentoXmlService contenidoXmlServicePeajes;

    @Autowired
    @Qualifier(value = "facturasServiceImp")
    DocumentoXmlService contenidoXmlServiceFacturas;

    @Autowired
    @Qualifier(value = "otrasFacturasServiceImp")
    private DocumentoXmlService contenidoXmlServiceOtrasFacturas;

    @Autowired
    protected ClienteService clienteService;
    
    @Autowired
    @Qualifier(value = "tarifasServiceImp")
    private CrudDao tarifasService;

    @Autowired
    private ProcesarFactura procesarFactura;

    @Autowired
    private ProcesarPeaje procesarPeaje;

    @Autowired
    private ProcesarOtrasFacturas procesarOtrasFacturas;

    @Autowired
    private ProcesarMedida procesarMedida;

    @Autowired
    private ProcesarMedidaCCH procesarMedidaCCH;

    @Autowired
    private MedidasHandler medidasHandler;

    @Autowired
    private MedidasHelper medidasHelper;

    @Autowired
    private ProcesarReclamacion procesarReclamacion;

    @Autowired
    private HelperComercializador helperComercializador;

    @Autowired
    private Comercializador comercializador;

    int archivosCorrectos;
    int archivosTotales;
    List<String> archivosErroneos;
    private boolean isFactura;
    private boolean isRemesaPago;
    private boolean isArchivarFactura;
    private boolean isOtrasFacturas;
    private boolean isMACCConCambios;
    private boolean isMACCSinCambios;
    private boolean isMACCSaliente;
    private boolean isComercializador;
    private boolean isConsultaFacturacion;
    private boolean isPeaje;
    private boolean isReclamacion;
    private HashMap<String, Boolean> datosAdicionales;

    /**
     * Muestra el fomulario que procesa los archivos
     *
     * @param model modelo usado por Spring
     * @return redireccionamiento al formulario
     */
    @GetMapping("")
    public String inicio(Model model) {
        model.addAttribute("tituloPagina", Etiquetas.PROCESAMIENTO_FORMULARIO_TITULO_PAGINA);
        model.addAttribute("titulo", Etiquetas.PROCESAMIENTO_FORMULARIO_ENCABEZADO);
        model.addAttribute("mensajeRegistro", Etiquetas.PROCESAMIENTO_FORMULARIO_MENSAJE);
        model.addAttribute("etiquetaBoton", Etiquetas.PROCESAMIENTO_FORMULARIO_ETIQUETA_BOTON);
        model.addAttribute("archivosErroneos", this.archivosErroneos);
        model.addAttribute("controller", Etiquetas.PROCESAMIENTO_FORMULARIO_CONTROLLER);
        this.reiniciarVariables();
        return "xml/formulario";
    }

    /**
     * Metodo controlador en recibe y procesa los archivos xml
     *
     * @param files los archivos enviados desde el formulario
     * @return redireccionamiento al formulario
     * @throws IOException
     */
    @PostMapping(path = "/procesar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String procesamiento(@RequestParam("archivosxml") MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
        	this.reiniciarVariablesBooleanas();
            archivosTotales++;
            File f;
            FileOutputStream ous;
            String nombreArchivo = null;

            //Escritura en archivos temporales por cada archivo recibido
            try (InputStream fileContent = file.getInputStream()) {
                //Creación del los archivos temporales para su procesamiento
                f = File.createTempFile("processTemp", null);
                //File f = new File("C:\\a\\" + fileName);
                ous = new FileOutputStream(f);
                int dato = fileContent.read();
                while (dato != -1) {
                    ous.write(dato);
                    dato = fileContent.read();
                }

                //Fin del flujo de escritura
                ous.close();

                nombreArchivo = file.getOriginalFilename();
                switch (Utilidades.definirExtensionArchivo(nombreArchivo)){
                    case XML:
                        this.procesarXML(f, nombreArchivo);
                        break;
                    case MEDIDAS:
                        this.procesarMedidas(f, nombreArchivo);
                        break;
                    default:
                        throw new ExtensionArchivoNoReconocida();
                }

                //Eliminación del archivo temporal
                f.deleteOnExit();

            } catch (ExtensionArchivoNoReconocida e) {
                this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + e.getMessage());
                ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
            } catch (Exception e) {
                this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + new ErrorDesconocidoException().getMessage() + " (<Strong>" + e.getMessage() + "</Strong>)");
                ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
                e.printStackTrace(System.out);
            }
        }
        Etiquetas.PROCESAMIENTO_FORMULARIO_MENSAJE = "Archivos Procesados (" + archivosCorrectos + " de " + archivosTotales + ")";
        return "redirect:/procesar";
    }

    /**
     * Procesamiento de cada archivo individualmente y definición del flujo a
     * seguir para insertar en la base de datos
     *
     * @param archivo Archivo xml recibido
     * @param nombreArchivo Nombre del archivo xml recibido
     */
    private void procesarXML(File archivo, String nombreArchivo) {
        System.out.println("(Ini)************************-----------------------------" + nombreArchivo);
        try {
            Document documento = this.prepareXml(archivo, nombreArchivo);
            this.initializarVariables(documento);

            if (this.isComercializador){
                this.procesarComercializador(this.helperComercializador.definirTipoMedida(nombreArchivo, this.datosAdicionales));
            } else if (isOtrasFacturas) {
                this.procesarOtrasFacturas.procesar(documento, nombreArchivo);
            } else if (isRemesaPago) {
                this.procesarRemesaPago(documento);
            } else if (isArchivarFactura) {
            	new ArchivarFactura(this.definirTablaBusqueda(documento), documento);      	
            }   else if (isFactura) {
                this.procesarFactura.procesarFactura(documento, nombreArchivo);
            } else if (isPeaje){
                this.procesarPeaje.procesar(documento, nombreArchivo);
            } else if(isConsultaFacturacion) {
            	this.procesarConsultaFactura(documento, nombreArchivo);
            } else if(isReclamacion){
                this.procesarReclamacion.procesar(documento, nombreArchivo);
            }
            else {
                throw new XmlNoSoportado();
            }
            archivosCorrectos++;

        } catch (FacturaYaExisteException | PeajeYaExisteException | OtraFacturaYaExisteException | ClienteNoExisteException | PeajeTipoFacturaNoSoportadaException | CodRectNoExisteException | XmlNoSoportado
                | MasDeUnClienteEncontrado | ArchivoVacioException | PeajeCodRectNoExisteException | TablaBusquedaNoExisteException | TablaBusquedaNoEspecificadaException
                | NoExisteElNodoException
                //| ArchivoNoCumpleParaSerClasificado | MasDatosdeLosEsperadosException
                | TarifaNoExisteException | PeajeMasDeUnRegistroException
                | FacturaNoEspecificaCodRecticadaException | FacturaNoExisteException | FacturaCodRectNoExisteException | ReclamacionYaExisteException
                //Excepciones comercializador
                | NombreArchivoTamanoDiferenteComercializador | NombreArchivoContieneEspaciosComercializador | NombreArchivoElementosTamanoDiferenteComercializador | ComercializadorNoReconocido | NoCoincidenLosElementosInicialesComercializadorException
                | CodigoProcesoNoReconocidoExeption | CodigoPasoNoReconocidoExeption | SubtipoCodigoPasoNoReconocidoException | CodigoProcesoNoPuedeSerProcesadoException
                e) {
            this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + e.getMessage());
            ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
        } catch (Exception e) {
            this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + new ErrorDesconocidoException().getMessage() + " (<Strong>" + e.getMessage() + "</Strong>)");
            ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
            e.printStackTrace(System.out);
        }

        System.out.println("(Fin)************************-----------------------------" + nombreArchivo);
    }

    /**
     * Procesamiento de las medidas individuales
     *
     * @param archivo archivo de la medida
     * @param nombreArchivo nombre del archivo de la medida
     */
    private void procesarMedidas(File archivo, String nombreArchivo){
        Queue<String> errores = null;
        try {
            switch (medidasHelper.definirTipoMedida(nombreArchivo)){
                case F5:
                    this.procesarMedida.guardar(archivo, nombreArchivo);
                    break;
                case P5:
                    this.procesarMedidaCCH.guardar(archivo, nombreArchivo);
                    break;
                case P1:
                    errores = this.medidasHandler.procesarMedida(archivo, nombreArchivo, MedidasHelper.TIPO_MEDIDA.P1);
                    break;
                case P2:
                    errores = this.medidasHandler.procesarMedida(archivo, nombreArchivo, MedidasHelper.TIPO_MEDIDA.P2);
                    break;
                case DESCONOCIDO:
                    throw new MedidaTipoNoReconocido();
            }
            archivosCorrectos++;
            if (errores != null && !errores.isEmpty())
                this.archivosErroneos.addAll(errores);
        } catch (MedidaTipoNoReconocido | NombreArchivoTamanoDiferente | NombreArchivoContieneEspacios |
                 NombreArchivoSinExtension | NombreArchivoElementosTamanoDiferente e){
            this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + e.getMessage());
            ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
        }
    }

    
    /**
     * Lectura y formateo del archivo xml recibido
     *
     * @param archivo archivo xml enviado desde el front
     * @param nombreArchivo nombre del archivo para indentificar el nombre con
     * el que se esta tratando
     * @return el archivo ya formateado y preparado para la lectura
     * @throws ArchivoVacioException
     */
    public Document prepareXml(File archivo, String nombreArchivo) throws ArchivoVacioException {
        Document doc = null;
        try {
            if (archivo.length() != 0) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbFactory.newDocumentBuilder();
                doc = builder.parse(archivo);
                doc.getDocumentElement().normalize();
            } else {
                throw new ArchivoVacioException();
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + new ErrorDesconocidoException().getMessage() + " (<Strong>" + e.getMessage() + "</Strong>)");
            ArchivoTexto.escribirError(archivosErroneos.get(archivosErroneos.size() - 1));
            e.printStackTrace(System.out);
        }
        return doc;
    }

    /**
     * Busca e inicializa los valores desde el documentoXML en variables
     * booleanas para definir el flujo del documento
     *
     * @param doc es el archivo xml a tratar
     */
    private void initializarVariables(Document doc) {
        /* Comercializadores (Posibles cabeceros)
            TIPO C1
                01              MensajeCambiodeComercializadorSinCambios
                                ns:MensajeCambiodeComercializadorSinCambios
                02 Aceptacion   MensajeAceptacionCambiodeComercializadorSinCambios
                02 Rechazo      MensajeRechazo
                05              MensajeActivacionCambiodeComercializadorSinCambios
                06              MensajeActivacionComercializadorSaliente
                11              MensajeAceptacionCambiodeComercializadorSaliente

        */

        if (Utilidades.existeNodo(doc, "ns:MensajeCambiodeComercializadorSinCambios")) {
            throw new RuntimeException("Se detecto que el nodo comienza con ns:");
        }
        if (Utilidades.existeNodo(doc, "MensajeCambiodeComercializadorSinCambios") ||          //Puede existir CONCAMBIOS???
            //Utilidades.existeNodo(doc, "ns:MensajeCambiodeComercializadorSinCambios") ||        //Porque en los archivos C1_01 vienen con ns: en el cabecero???
            Utilidades.existeNodo(doc, "MensajeAceptacionCambiodeComercializadorSinCambios") || //Puede existir CONCAMBIOS???
            Utilidades.existeNodo(doc, "MensajeRechazo") ||
            Utilidades.existeNodo(doc, "MensajeActivacionCambiodeComercializadorSinCambios") || //Puede existir CONCAMBIOS???
            Utilidades.existeNodo(doc, "MensajeActivacionComercializadorSaliente") ||
            Utilidades.existeNodo(doc, "MensajeAceptacionCambiodeComercializadorSaliente")){
            this.datosAdicionales = new HashMap<>();
            if (Utilidades.existeNodo(doc, "MensajeAceptacionCambiodeComercializadorSinCambios")) this.datosAdicionales.put("aceptacion", true); //Tambien existen en los C2???
            if (Utilidades.existeNodo(doc, "MensajeRechazo") && this.datosAdicionales.get("aceptacion") == null) this.datosAdicionales.put("rechazo", true);
            this.isComercializador = true;

            return;
        }
         /* TIPO C2
                01              MensajeCambiodeComercializadorConCambios
                02 Aceptacion   MensajeAceptacionCambiodeComercializadorConCambios
                02 Rechazo                                                                          ###falta por definir
                05              MensajeActivacionCambiodeComercializadorConCambios
                06              MensajeActivacionComercializadorSaliente                            Se repite en C1
                11              MensajeAceptacionCambiodeComercializadorSaliente                    Se repite en C1
         */
        if (Utilidades.existeNodo(doc, "MensajeCambiodeComercializadorConCambios") ||
            Utilidades.existeNodo(doc, "MensajeAceptacionCambiodeComercializadorConCambios") ||
            Utilidades.existeNodo(doc, "MensajeActivacionCambiodeComercializadorConCambios")){

            this.datosAdicionales = this.datosAdicionales == null ? new HashMap<>() : this.datosAdicionales;

            if (Utilidades.existeNodo(doc, "MensajeAceptacionCambiodeComercializadorConCambios")) this.datosAdicionales.put("aceptacion", true);
            //Aun falta definir el arhivo que pertenece a los rechazos de C2 ???

            this.isComercializador = true;
            return;
        }




        if(Utilidades.existeNodo(doc, "MensajeReclamacionPeticion") || Utilidades.existeNodo(doc, "MensajeAceptacionReclamacion") ||
                Utilidades.existeNodo(doc, "MensajeCierreReclamacion") || Utilidades.existeNodo(doc, "MensajePeticionInformacionAdicionalReclamacion")){
            this.isReclamacion = true;
            return;
        }

        if (Utilidades.existeNodo(doc, "CodigoREEEmpresaEmisora")) {
            this.isFactura = StringHelper.toInteger(doc.getElementsByTagName("CodigoREEEmpresaEmisora").item(0).getTextContent()) == 894;
            if (this.isFactura) {
                return;
            }
        }

        if (Utilidades.existeNodo(doc, "RemesaPago")) {
            this.isRemesaPago = true;
            return;
        } else if (Utilidades.existeNodo(doc, "isDeleted")) {
			this.isArchivarFactura = true;
			return;
		} else if (Utilidades.existeNodo(doc, "OtrasFacturas")) {
            this.isOtrasFacturas = true;
            return;
        } else if (Utilidades.existeNodo(doc, "MensajeFacturacion")) {
            this.isPeaje = true;
            return;
        } else if(Utilidades.existeNodo(doc, "ConsultaFacturacion")) {
        	this.isConsultaFacturacion = true;
        	return;
        }
    }
    
    private void reiniciarVariablesBooleanas(){
        this.isMACCSaliente = false;
        this.isOtrasFacturas = false;
        this.isMACCConCambios = false;
        this.isMACCSinCambios = false;
        this.isComercializador = false;
        this.isFactura = false;
        this.isRemesaPago = false;
        this.isArchivarFactura = false;
        this.isPeaje = false;
        this.isConsultaFacturacion = false;
        this.isReclamacion = false;
        this.datosAdicionales = null;
    }

    /**
     * Reinicio de las variables para la proxima recarga de la vista
     */
    private void reiniciarVariables() {
        Etiquetas.PROCESAMIENTO_FORMULARIO_MENSAJE = "Procesaremos la información contenida dentro de un XML. (Antes de esto, los archivos debieron haber sido evaluados).";
        this.archivosCorrectos = 0;
        this.archivosTotales = 0;
        this.archivosErroneos = new ArrayList<>();
    }
    
    private void procesarRemesaPago(Document doc) throws TablaBusquedaNoExisteException, TablaBusquedaNoEspecificadaException, NoExisteElNodoException, PeajeMasDeUnRegistroException, RegistroVacioException {
        switch (this.definirTablaBusqueda(doc)) {
            case peajes:
                new ProcesarRemesaPagoPeaje(doc, contenidoXmlServicePeajes);
                break;
            case facturas:
                new ProcesarRemesaPagoFactura(doc, contenidoXmlServiceFacturas);
                break;
            case otrasFacturas:
                new ProcesarRemesaPagoOtrasFacturas(doc, contenidoXmlServiceOtrasFacturas);
                break;
        }
    }
    
    private void procesarConsultaFactura(Document doc, String nombreArchivo) throws TablaBusquedaNoExisteException, TablaBusquedaNoEspecificadaException, NoExisteElNodoException, PeajeMasDeUnRegistroException {
        ConsultaFacturacion cf = null;
        TablaBusqueda ref = this.definirTablaBusqueda(doc);
    	switch(ref) {
	    	case peajes:
	    		cf = new ConsultaFacturacion(doc, nombreArchivo, contenidoXmlServicePeajes);
	    		break;
	    	case facturas:
                cf = new ConsultaFacturacion(doc, nombreArchivo, contenidoXmlServiceFacturas);
	    		break;
	    	case otrasFacturas:
                cf = new ConsultaFacturacion(doc, nombreArchivo, contenidoXmlServiceOtrasFacturas);
	    		break;
    	}
        cf.actualizarFiltro(ref);
    }


    private void procesarComercializador(HelperComercializador.TIPO_COMERCIALIZADOR tc) throws CodigoProcesoNoPuedeSerProcesadoException {
        switch (tc){
            /* Tipos C1 */
            case C1_01:
                this.comercializador.procesarC1_01();
                break;
            case C1_02_ACEPTACION:
                this.comercializador.procesarC1_02_Aceptacion();
                break;
            case C1_02_RECHAZO:
                this.comercializador.procesarC1_02_Rechazo();
                break;
            case C1_05:
                this.comercializador.procesarC1_05();
                break;
            case C1_06:
                this.comercializador.procesarC1_06();
                break;
            case C1_11:
                this.comercializador.procesarC1_11();
                break;

            /* Tipos C2 */

            case C2_01:
                this.comercializador.procesarC2_01();
                break;
            case C2_02_ACEPTACION:
                this.comercializador.procesarC2_02_Aceptacion();
                break;
            case C2_02_RECHAZO:
                this.comercializador.procesarC2_02_Rechazo();
                break;
            case C2_05:
                this.comercializador.procesarC2_05();
                break;
            case C2_06:
                this.comercializador.procesarC2_06();
                break;
            case C2_11:
                this.comercializador.procesarC2_11();
                break;

            default:
                throw new CodigoProcesoNoPuedeSerProcesadoException(tc.name());
        }
    }

    
    private TablaBusqueda definirTablaBusqueda(Document doc) throws TablaBusquedaNoExisteException, TablaBusquedaNoEspecificadaException, NoExisteElNodoException {
    	if (Utilidades.existeNodo(doc, "TablaBusqueda")) {
            String tablaBusqueda = doc.getElementsByTagName("TablaBusqueda").item(0).getTextContent();
            if (StringHelper.isValid(tablaBusqueda)) {
                switch (tablaBusqueda) {
                    case "contenido_xml":
                        return TablaBusqueda.peajes;
                    case "contenido_xml_factura":
                    	return TablaBusqueda.facturas;
                    case "contenido_xml_otras_facturas":
                    	return TablaBusqueda.otrasFacturas;

                    default:
                        throw new TablaBusquedaNoExisteException(tablaBusqueda);
                }
            } else {
                throw new TablaBusquedaNoEspecificadaException();
            }
        } else {
            throw new NoExisteElNodoException("TablaBusqueda");
        }
    }

    public enum TablaBusqueda{
    	peajes, facturas, otrasFacturas;
    }
    
}
