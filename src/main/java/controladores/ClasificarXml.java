package controladores;

import controladores.helper.Etiquetas;
import controladores.helper.NombresNodos;
import controladores.helper.Utilidades;
import datos.entity.Cliente;
import datos.interfaces.ClienteService;
import excepciones.ArchivoNoCumpleParaSerClasificado;
import excepciones.ArchivoVacioException;
import excepciones.ClienteNoExisteException;
import excepciones.ErrorDesconocidoException;
import excepciones.MasDeUnClienteEncontrado;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import utileria.xml;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
@Controller
@RequestMapping("/clasificar")
public class ClasificarXml {

    @Autowired
    ClienteService clienteService;

    private int archivosCorrectos;
    private int archivosTotales;
    private List<String> archivosErroneos;
    private HashMap<String, String> elementosXml;
    private Cliente cliente;
    private String nombreArchivo;
    private String mensajeRegistro = Etiquetas.CLASIFICAR_FORMULARIO_MENSAJE;
    private boolean isValidacionPago;
    private boolean isArchivarFactura;
    private Logger logger = Logger.getLogger(getClass().getName());
    
    @GetMapping("")
    public String inicio(Model model) {
        model.addAttribute("tituloPagina", Etiquetas.CLASIFICAR_FORMULARIO_TITULO_PAGINA);
        model.addAttribute("titulo", Etiquetas.CLASIFICAR_FORMULARIO_ENCABEZADO);
        model.addAttribute("mensajeRegistro", mensajeRegistro);
        model.addAttribute("etiquetaBoton", Etiquetas.CLASIFICAR_FORMULARIO_ETIQUETA_BOTON);
        model.addAttribute("controller", Etiquetas.CLASIFICAR_FORMULARIO_CONTROLLER);
        model.addAttribute("archivosErroneos", this.archivosErroneos);
        this.reiniciarVariables();
        return "xml/formulario";
    }

    @PostMapping("/procesar")
    public String evaluar(@RequestParam("archivosxml") MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
        	reiniciarVariablesFlujo();
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
            this.nombreArchivo = file.getOriginalFilename();
            this.procesar(f, f.getAbsolutePath());
            //Eliminación del archivo temporal
            f.deleteOnExit();
        }
        mensajeRegistro = "Archivos clasificados (" + this.archivosCorrectos + " de " + archivosTotales + ")";
        return "redirect:/clasificar";
    }

    private void procesar(File archivo, String ruta) {
        try {
            Document doc = this.prepareXml(archivo, nombreArchivo);
            this.initializarVariables(doc);
            if (isValidacionPago) {
            	this.generarValidacionPagoXML(ruta);
				return;
			}
            if (this.isArchivarFactura) {
            	this.generarArchivarFacturaXML(ruta);
				return;
			}
            this.cliente = this.clienteService.encontrarCups(elementosXml.get(NombresNodos.CUPS));
            if (this.cliente != null) {
                this.generarXML(this.nombreArchivo, ruta);
            } else {
                throw new ClienteNoExisteException(elementosXml.get(NombresNodos.CUPS));
            }
        } catch (MasDeUnClienteEncontrado | ArchivoNoCumpleParaSerClasificado | ArchivoVacioException | ClienteNoExisteException e) {
            this.archivosErroneos.add("El archivo <Strong>" + this.nombreArchivo + "</Strong> no se clasificó porque " + e.getMessage());
            utileria.ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
        } catch (Exception e) {
            this.archivosErroneos.add("El archivo <Strong>" + this.nombreArchivo + "</Strong> no se clasificó porque " + new ErrorDesconocidoException().getMessage() + " (<Strong>" + e.getMessage() + "</Strong>)");
            utileria.ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
            System.out.println(e.getMessage() + ", " + e.getClass());
            e.printStackTrace(System.out);
        }
    }

    private void initializarVariables(Document doc) throws ArchivoNoCumpleParaSerClasificado {
        try {
            elementosXml.put(NombresNodos.CUPS, xml.obtenerContenidoNodo(NombresNodos.CUPS, doc));
            elementosXml.put(NombresNodos.EMP_EMI, xml.obtenerContenidoNodo(NombresNodos.EMP_EMI, doc));
            elementosXml.put(NombresNodos.EMP_DES, xml.obtenerContenidoNodo(NombresNodos.EMP_DES, doc));
            elementosXml.put(NombresNodos.COD_FIS_FAC, xml.obtenerContenidoNodo(NombresNodos.COD_FIS_FAC, doc));
            elementosXml.put(NombresNodos.TIP_FAC, xml.obtenerContenidoNodo(NombresNodos.TIP_FAC, doc));
            elementosXml.put(NombresNodos.MOT_FAC, xml.obtenerContenidoNodo(NombresNodos.MOT_FAC, doc));
            elementosXml.put(NombresNodos.COD_PRO, xml.obtenerContenidoNodo(NombresNodos.COD_PRO, doc));
            elementosXml.put(NombresNodos.COD_SOL, xml.obtenerContenidoNodo(NombresNodos.COD_SOL, doc));
            elementosXml.put(NombresNodos.COD_PAS, xml.obtenerContenidoNodo(NombresNodos.COD_PAS, doc));
        } catch (NullPointerException e) {
            elementosXml.clear();
            logger.log(Level.INFO, ">>> Parece no ser un archivo de peaje, se intentará revisar si es MensajeAceptacionModificacionDeATR");
            this.inicializarVariables2(doc);
        }
    }

    private void inicializarVariables2(Document doc) throws ArchivoNoCumpleParaSerClasificado {
        try {
            elementosXml.put(NombresNodos.CUPS, xml.obtenerContenidoNodo(NombresNodos.CUPS, doc));
            elementosXml.put(NombresNodos.EMP_EMI, xml.obtenerContenidoNodo(NombresNodos.EMP_EMI, doc));
            elementosXml.put(NombresNodos.EMP_DES, xml.obtenerContenidoNodo(NombresNodos.EMP_DES, doc));
            elementosXml.put(NombresNodos.COD_PRO, xml.obtenerContenidoNodo(NombresNodos.COD_PRO, doc));
            elementosXml.put(NombresNodos.COD_SOL, xml.obtenerContenidoNodo(NombresNodos.COD_SOL, doc));
            elementosXml.put(NombresNodos.COD_PAS, xml.obtenerContenidoNodo(NombresNodos.COD_PAS, doc));
        } catch (NullPointerException e) {
        	elementosXml.clear();
        	logger.log(Level.INFO, ">>> Parece no ser un archivo de MensajeAceptacionModificacionDeATR, se intentará revisar si es validacionPago");
            this.reconocerValidarPago(doc);
        }
    }
    
    private void reconocerValidarPago(Document doc) throws ArchivoNoCumpleParaSerClasificado{
    	try {
			elementosXml.put(NombresNodos.REM_PAG, xml.obtenerContenidoNodo(NombresNodos.REM_PAG, doc));
            this.isValidacionPago = true;
        } catch (NullPointerException e) {
        	elementosXml.clear();
        	logger.log(Level.INFO, ">>> No es un archivo del tipo Validar Pago");
            this.isArchivarFactura(doc);
        }
    }
    
    public boolean isArchivarFactura(Document doc) throws ArchivoNoCumpleParaSerClasificado{
    	try {
    		elementosXml.put(NombresNodos.IS_DEL, xml.obtenerContenidoNodo(NombresNodos.IS_DEL, doc));
    		this.isArchivarFactura = true;
		} catch (Exception e) {
			elementosXml.clear();
        	logger.log(Level.INFO, ">>> No es un archivo del tipo ArchivarFactura");
        	throw new ArchivoNoCumpleParaSerClasificado();
		}
    	return true;
    }
    
    private void generarValidacionPagoXML(String rutaOriginal) {
    	String pathNuevoArchivo = "C:\\Peajes\\Procesados\\RemesaPago";
    	if (Utilidades.crearArchivo(this.nombreArchivo, rutaOriginal, pathNuevoArchivo)) {
			this.archivosCorrectos++;
		}
    }
    
    private void generarArchivarFacturaXML(String rutaOriginal) {
    	String pathNuevoArchivo = "C:\\Peajes\\Procesados\\ArchivarFactura";
    	if (Utilidades.crearArchivo(this.nombreArchivo, rutaOriginal, pathNuevoArchivo)) {
			this.archivosCorrectos++;
		}
    }

    private void generarXML(String nombreArchivoOriginal, String ruta) {
        String subCarpeta = "";
        if (elementosXml.get(NombresNodos.EMP_EMI).equals("0894")) {
            subCarpeta = "0894";
        } else {
            switch (elementosXml.get(NombresNodos.COD_PRO)) {
                case "F1":
                    subCarpeta = "F";
                    //elementos[5] = this.LeerCodigoFactura(ruta); //CodigoFiscalFactura

                    switch (elementosXml.get(NombresNodos.TIP_FAC)) {
                        case "A":
                            subCarpeta += "\\A";
                        case "N":
                            switch (elementosXml.get(NombresNodos.MOT_FAC)) {
                                case "01":
                                case "02":
                                case "03":
                                    break;
                                case "04":
                                case "08":
                                    subCarpeta += "\\M04";
                                    break;
                                default:
                                    subCarpeta += "\\MotivosNoSoportados";
                                    archivosErroneos.add("El motivo de factura (" + elementosXml.get(NombresNodos.MOT_FAC) + ") del archivo (" + nombreArchivoOriginal + ") no esta soportado");
                                    break;
                            }
                            break;
                        case "C":
                            subCarpeta += "\\C";
                            break;
                        case "R":
                            subCarpeta += "\\R";
                            break;
                        case "G":
                            subCarpeta += "\\G";
                            break;
                        default:
                            subCarpeta += "\\TiposNoSoportados";
                            archivosErroneos.add("El tipo de factura (" + elementosXml.get(NombresNodos.TIP_FAC) + ") del archivo (" + nombreArchivoOriginal + ") no esta soportado");
                            break;
                    }
                    break;
                case "Q1":
                case "W1":
                    subCarpeta = "Q";
                    break;
                case "D1":
                case "M1":
                case "C1":
                case "C2":
                case "A3": 	
                case "B1":
                    subCarpeta = "X";
                    break;
                case "R1":
                    subCarpeta = "R1";
                    break;
                default:
                    subCarpeta = "Otros";
                    break;
            }
        }

        NumberFormat formater = new DecimalFormat("#0000");
        String finString = this.elementosXml.get(NombresNodos.COD_SOL);
        if (this.elementosXml.get(NombresNodos.COD_PRO).equals("F1")) {
            finString = this.elementosXml.get(NombresNodos.COD_FIS_FAC);
        }
        
        String pathNuevoArchivo = "C:\\Peajes\\Procesados\\" + subCarpeta;
        String nombreNuevoArchivo = formater.format(this.cliente.getIdCliente()) + "-" + this.cliente.getTarifa() + "_"
                + this.elementosXml.get(NombresNodos.EMP_EMI) + "_" + this.elementosXml.get(NombresNodos.EMP_DES) + "_" + this.elementosXml.get(NombresNodos.COD_PRO) + "_"
                + this.elementosXml.get(NombresNodos.COD_PAS) + "_" + this.elementosXml.get(NombresNodos.CUPS) + "_" + finString
                + ".xml"; 
        
        if (Utilidades.crearArchivo(nombreNuevoArchivo, ruta, pathNuevoArchivo)) {
        	this.archivosCorrectos++;
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
     * Reinicio de las variables para la proxima recarga de la vista
     */
    private void reiniciarVariables() {
        mensajeRegistro = Etiquetas.CLASIFICAR_FORMULARIO_MENSAJE;
        this.archivosErroneos = new ArrayList<>();
        this.archivosCorrectos = 0;
        this.archivosTotales = 0;
    }
    
    private void reiniciarVariablesFlujo() {
    	this.isValidacionPago = false;
        this.isArchivarFactura = false;
        this.elementosXml = new HashMap<String, String>();
        this.nombreArchivo = null;
    }
    
}
