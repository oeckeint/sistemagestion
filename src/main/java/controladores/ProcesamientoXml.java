package controladores;

import controladores.helper.Etiquetas;
import controladores.helper.ProcesarFactura;
import controladores.helper.ProcesarOtrasFacturas;
import controladores.helper.ProcesarPeaje;
import controladores.helper.ProcesarRemesaPago;
import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.ArchivoVacioException;
import excepciones.ClienteNoExisteException;
import excepciones.CodRectNoExisteException;
import excepciones.ErrorDesconocidoException;
import excepciones.FacturaYaExisteException;
import excepciones.MasDeUnClienteEncontrado;
import excepciones.PeajeCodRectNoExisteException;
import excepciones.PeajeTipoFacturaNoSoportadaException;
import excepciones.XmlNoSoportado;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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

    int archivosCorrectos;
    int archivosTotales;
    List<String> archivosErroneos;
    private int empresaEmisora;
    private boolean is894;
    private boolean isRemesaPago;

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
    @PostMapping("/procesar")
    public String procesamiento(@RequestParam("archivosxml") MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            this.empresaEmisora = 0;
            this.is894 = false;
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
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    private void procesar(File archivo, String nombreArchivo) {

        System.out.println("(Ini)************************-----------------------------" + nombreArchivo);

        try {
            Document documento = this.prepareXml(archivo, nombreArchivo);
            this.initializarVariables(documento);
            //Revision de la compatiblidad
            if (!isRemesaPago) {
                if (documento.getElementsByTagName("MensajeFacturacion").getLength() == 0) {
                    throw new XmlNoSoportado();
                }
            }

            //Revisa el nodo "Otras facturas" y en caso de que el valor sea mayor a 1 ejecutara la clase de Otras facturas
            if (documento.getElementsByTagName("OtrasFacturas").getLength() != 0) {
                new ProcesarOtrasFacturas(documento, contenidoXmlServiceOtrasFacturas, clienteService, nombreArchivo);
            } else if (isRemesaPago) {
                new ProcesarRemesaPago(documento, contenidoXmlServicePeajes);
            } else if (is894) {
                new ProcesarFactura(documento, contenidoXmlServiceFacturas, clienteService, nombreArchivo);
            } else {
                new ProcesarPeaje(documento, contenidoXmlServicePeajes, clienteService, nombreArchivo);
            }
            archivosCorrectos++;

        } catch (FacturaYaExisteException | ClienteNoExisteException | PeajeTipoFacturaNoSoportadaException | CodRectNoExisteException | XmlNoSoportado
                | MasDeUnClienteEncontrado | ArchivoVacioException | PeajeCodRectNoExisteException e) {
            this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + e.getMessage());
            utileria.ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
        } catch (Exception e) {
            this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + new ErrorDesconocidoException().getMessage() + " (<Strong>" + e.getMessage() + "</Strong>)");
            utileria.ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
            e.printStackTrace(System.out);
        }

        System.out.println("(Fin)************************-----------------------------" + nombreArchivo);
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
     * Busca e inicializa los valores desde el documentoXML en varibales de
     * instancia para definir el flujo del documento
     *
     * @param doc es el archivo xml a tratar
     */
    private void initializarVariables(Document doc) {
        try {
            this.empresaEmisora = Integer.parseInt(doc.getElementsByTagName("CodigoREEEmpresaEmisora").item(0).getTextContent());
            if (this.empresaEmisora == 894) {
                is894 = true;
            }
        } catch (Exception e) {
            this.isRemesaPago = doc.getElementsByTagName("RemesaPago").item(0).getTextContent().length() != 0;
        }
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

}
