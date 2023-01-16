package controladores;

import controladores.helper.ArchivarFactura;
import controladores.helper.CambiodeComercializador;
import controladores.helper.Etiquetas;
import controladores.helper.NombresNodos;
import controladores.helper.ConsultaFacturacion;
import controladores.helper.ProcesarFactura;
import controladores.helper.ProcesarOtrasFacturas;
import controladores.helper.ProcesarPeaje;
import controladores.helper.ProcesarRemesaPagoFactura;
import controladores.helper.ProcesarRemesaPagoOtrasFacturas;
import controladores.helper.ProcesarRemesaPagoPeaje;
import controladores.helper.Utilidades;
import datos.interfaces.ClienteService;
import datos.interfaces.CrudDao;
import datos.interfaces.DocumentoXmlService;
import excepciones.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

import com.mchange.v2.cfg.DelayedLogItem.Level;

import utileria.StringHelper;
import utileria.xml;

@Controller
@RequestMapping("/procesar")
public class ProcesamientoXml {

    @Autowired
    @Qualifier(value = "peajesServiceImp")
    private DocumentoXmlService contenidoXmlServicePeajes;

    @Autowired
    @Qualifier(value = "facturasServiceImp")
    private DocumentoXmlService contenidoXmlServiceFacturas;

    @Autowired
    @Qualifier(value = "otrasFacturasServiceImp")
    private DocumentoXmlService contenidoXmlServiceOtrasFacturas;

    @Autowired
    protected ClienteService clienteService;
    
    @Autowired
    @Qualifier(value = "tarifasServiceImp")
    private CrudDao tarifasService;

    int archivosCorrectos;
    int archivosTotales;
    List<String> archivosErroneos;
    private boolean isFactura;
    private boolean isRemesaPago;
    private boolean isArchivarFactura;
    private boolean isOtrasFacturas;
    private boolean isMACCConCambios;
    private boolean isMACCSinCambios;
    private boolean isConsultaFacturacion;
    private boolean isPeaje;
    private HashMap<String, String> elementosCF;
	private ResultSet rs;

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

            //Escritura en archivos temporales por cada archivo recibido
            try (InputStream fileContent = file.getInputStream()) {
                //Creación del los archivos temporales para su procesamiento
                f = File.createTempFile("xmlTemp", null);
                //File f = new File("C:\\a\\" + fileName);
                ous = new FileOutputStream(f);
                int dato = fileContent.read();
                while (dato != -1) {
                    ous.write(dato);
                    dato = fileContent.read();
                }
            }
            //Fin del flujo de escritura
            ous.close();
            this.procesar(f, file.getOriginalFilename());
            //Eliminación del archivo temporal
            f.deleteOnExit();
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
    private void procesar(File archivo, String nombreArchivo) {
        System.out.println("(Ini)************************-----------------------------" + nombreArchivo);
        try {
            Document documento = this.prepareXml(archivo, nombreArchivo);
            this.initializarVariables(documento);

            if (isMACCSinCambios) {
                new CambiodeComercializador(documento, clienteService, tarifasService);
            } else if (isMACCConCambios) {
                System.out.println("MensajeActivacionCambiodeComercializadorConCambios ");
            } else if (isOtrasFacturas) {
                new ProcesarOtrasFacturas(documento, contenidoXmlServiceOtrasFacturas, clienteService, nombreArchivo);
            } else if (isRemesaPago) {
                this.procesarRemesaPago(documento);
            } else if (isArchivarFactura) {
            	new ArchivarFactura(this.definirTablaBusqueda(documento), documento);      	
            }   else if (isFactura) {
                new ProcesarFactura(documento, contenidoXmlServiceFacturas, clienteService, nombreArchivo);    
            } else if (isPeaje){
                new ProcesarPeaje(documento, contenidoXmlServicePeajes, clienteService, nombreArchivo);
            } else if(isConsultaFacturacion) {
            	this.procesarConsultaFactura(documento, nombreArchivo);
            }
            else {
                throw new XmlNoSoportado();
            }
            archivosCorrectos++;

        } catch (FacturaYaExisteException | ClienteNoExisteException | PeajeTipoFacturaNoSoportadaException | CodRectNoExisteException | XmlNoSoportado
                | MasDeUnClienteEncontrado | ArchivoVacioException | PeajeCodRectNoExisteException | TablaBusquedaNoExisteException | TablaBusquedaNoEspecificadaException
                | NoExisteElNodoException | ArchivoNoCumpleParaSerClasificado | MasDatosdeLosEsperadosException | TarifaNoExisteException | PeajeMasDeUnRegistroException e) {
            this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + e.getMessage());
            utileria.ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
        } catch (Exception e) {
            this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + new ErrorDesconocidoException().getMessage() + " (<Strong>" + e.getMessage() + "</Strong>)");
            utileria.ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
            e.printStackTrace(System.out);
        }

        System.out.println("(Fin)************************-----------------------------" + nombreArchivo);
    }
    
    public void initializarConsultaFacturacion(Document doc) throws ArchivoNoCumpleParaSerClasificado, SQLException {
    	rs = null;
    			
    	try {
    		if(rs.next()) {
    			elementosCF = new HashMap<String, String>();
    			do {
    				elementosCF.put(NombresNodos.COD_FIS_FAC, xml.obtenerContenidoNodo(NombresNodos.COD_FIS_FAC, doc));
    				elementosCF.put(NombresNodos.FIL, xml.obtenerContenidoNodo(NombresNodos.FIL , doc));
    			} while(rs.next());
    		}
		} catch (NullPointerException e){
			elementosCF.clear();
		}
    }
    
    
    /**
     * Lectura y formateo del archivo xml recibido
     *
     * @param archivo archivo xml enviado desde el front
     * @param nombreArchivo nombre del archivo para indentificar el nombre con
     * el que se esta tratando
     * @return el archivo ya formateado y preparado para la lectura
     * @throws excepciones.ArchivoVacioException
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
            utileria.ArchivoTexto.escribirError(archivosErroneos.get(archivosErroneos.size() - 1));
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
        if (Utilidades.existeNodo(doc, "MensajeActivacionCambiodeComercializadorConCambios")) {
            this.isMACCConCambios = true;
            return;
        } else if (Utilidades.existeNodo(doc, "MensajeActivacionCambiodeComercializadorSinCambios")) {
            this.isMACCSinCambios = true;
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
        this.isOtrasFacturas = false;
        this.isMACCConCambios = false;
        this.isMACCSinCambios = false;
        this.isFactura = false;
        this.isRemesaPago = false;
        this.isArchivarFactura = false;
        this.isPeaje = false;
        this.isConsultaFacturacion = false;
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
