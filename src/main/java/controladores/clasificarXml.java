package controladores;

import controladores.helper.Etiquetas;
import datos.entity.Cliente;
import datos.interfaces.ClienteService;
import excepciones.ArchivoNoCumpleParaSerClasificado;
import excepciones.ArchivoVacioException;
import excepciones.ClienteNoExisteException;
import excepciones.ErrorDesconocidoException;
import excepciones.MasDeUnClienteEncontrado;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @GetMapping("")
    public String inicio(Model model) {
        model.addAttribute("tituloPagina", Etiquetas.CLASIFICAR_FORMULARIO_TITULO_PAGINA);
        model.addAttribute("titulo", Etiquetas.CLASIFICAR_FORMULARIO_ENCABEZADO);
        model.addAttribute("mensajeRegistro", Etiquetas.CLASIFICAR_FORMULARIO_MENSAJE);
        model.addAttribute("etiquetaBoton", Etiquetas.CLASIFICAR_FORMULARIO_ETIQUETA_BOTON);
        model.addAttribute("controller", Etiquetas.CLASIFICAR_FORMULARIO_CONTROLLER);
        model.addAttribute("archivosErroneos", this.archivosErroneos);
        this.reiniciarVariables();
        return "xml/formulario";
    }

    @PostMapping("/procesar")
    public String evaluar(@RequestParam("archivosxml") MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
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
        Etiquetas.CLASIFICAR_FORMULARIO_MENSAJE = "Archivos clasificados (" + this.archivosCorrectos + " de " + archivosTotales + ")";
        return "redirect:/clasificar";
    }

    private void procesar(File archivo, String ruta) {
        try {
            elementosXml.clear();
            Document doc = this.prepareXml(archivo, nombreArchivo);
            this.initializarVariables(doc);
            this.cliente = this.clienteService.encontrarCups(elementosXml.get("cups"));
            if (this.cliente != null) {
                this.generarXML(this.nombreArchivo, ruta);
            } else {
                throw new ClienteNoExisteException(elementosXml.get("cups"));
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

    private void initializarVariables(Document doc) throws ArchivoNoCumpleParaSerClasificado{
        try {
            elementosXml.put("cups", doc.getElementsByTagName("CUPS").item(0).getTextContent());
            elementosXml.put("empEmi", doc.getElementsByTagName("CodigoREEEmpresaEmisora").item(0).getTextContent());
            elementosXml.put("empDes", doc.getElementsByTagName("CodigoREEEmpresaDestino").item(0).getTextContent());
            elementosXml.put("codFisFac", doc.getElementsByTagName("CodigoFiscalFactura").item(0).getTextContent());
            elementosXml.put("TipFac", doc.getElementsByTagName("TipoFactura").item(0).getTextContent());
            elementosXml.put("MotFac", doc.getElementsByTagName("MotivoFacturacion").item(0).getTextContent());
            elementosXml.put("codPro", doc.getElementsByTagName("CodigoDelProceso").item(0).getTextContent());
            elementosXml.put("codSol", doc.getElementsByTagName("CodigoDeSolicitud").item(0).getTextContent());
            elementosXml.put("codPas", doc.getElementsByTagName("CodigoDePaso").item(0).getTextContent());
        } catch (NullPointerException e) {
            elementosXml.clear();
            System.out.println("Parece no ser un archivo de peaje, se intentará revisar si es MensajeAceptacionModificacionDeATR");
            this.inicializarVariables2(doc);
       }
    }
    
    private void inicializarVariables2(Document doc) throws ArchivoNoCumpleParaSerClasificado{
        try {
            elementosXml.put("cups", doc.getElementsByTagName("CUPS").item(0).getTextContent());
            elementosXml.put("empEmi", doc.getElementsByTagName("CodigoREEEmpresaEmisora").item(0).getTextContent());
            elementosXml.put("empDes", doc.getElementsByTagName("CodigoREEEmpresaDestino").item(0).getTextContent());
            elementosXml.put("codPro", doc.getElementsByTagName("CodigoDelProceso").item(0).getTextContent());
            elementosXml.put("codSol", doc.getElementsByTagName("CodigoDeSolicitud").item(0).getTextContent());
            elementosXml.put("codPas", doc.getElementsByTagName("CodigoDePaso").item(0).getTextContent());
        } catch (NullPointerException e) {
            throw new ArchivoNoCumpleParaSerClasificado();
        }
    }

    private void generarXML(String nombreArchivoOriginal, String ruta) {
        String subCarpeta = "";
        if (elementosXml.get("empEmi").equals("0894")) {
            subCarpeta = "0894";
        } else {
            switch (elementosXml.get("codPro")) {
                case "F1":
                    subCarpeta = "F";
                    //elementos[5] = this.LeerCodigoFactura(ruta); //CodigoFiscalFactura

                    switch (elementosXml.get("TipFac")) {
                        case "A":
                            subCarpeta += "\\A";
                        case "N":
                            switch (elementosXml.get("MotFac")) {
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
                                    archivosErroneos.add("El motivo de factura (" + elementosXml.get("MotFac") + ") del archivo (" + nombreArchivoOriginal + ") no esta soportado");
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
                            archivosErroneos.add("El tipo de factura (" + elementosXml.get("TipFac") + ") del archivo (" + nombreArchivoOriginal + ") no esta soportado");
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

        String nombreArchivo = "C:\\Peajes\\Procesados\\" + subCarpeta + "\\"
                + formater.format(this.cliente.getIdCliente()) + "-" + this.cliente.getTarifa() + "_"
                + this.elementosXml.get("empEmi") + "_" + this.elementosXml.get("empDes") + "_" + this.elementosXml.get("codPro") + "_"
                + this.elementosXml.get("codPas") + "_" + this.elementosXml.get("cups") + "_" + this.elementosXml.get("codSol")
                + ".xml";
        File f = new File(nombreArchivo);

        FileWriter fichero = null;
        PrintWriter pw;
        try {
            f.createNewFile();
            fichero = new FileWriter(nombreArchivo);
            pw = new PrintWriter(fichero);

            //---------------------Escritura en el archivo-------------------------
            File archivo = null;
            FileReader fr = null;
            BufferedReader br = null;

            try {
                // Apertura del fichero y creacion de BufferedReader para poder
                // hacer una lectura comoda (disponer del metodo readLine()).
                archivo = new File(ruta);
                fr = new FileReader(archivo);
                br = new BufferedReader(fr);

                // Lectura del fichero
                String linea;
                while ((linea = br.readLine()) != null) {
                    pw.println(linea);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try {
                    if (null != fr) {
                        fr.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            //---------------------Fin de escritura en el archivo-------------------------
            this.archivosCorrectos++;
        } catch (IOException e) {

        } finally {
            try {
                // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (IOException e2) {

            }
        }
    }
    
    /**
     * Lectura y formateo del archivo xml recibido
     *
     * @param archivo archivo xml enviado desde el front
     * @param nombreArchivo nombre del archivo para indentificar el nombre con
     * el que se esta tratando
     * @return el archivo ya formateado y preparado para la lectura
     */
    public Document prepareXml(File archivo, String nombreArchivo) throws ArchivoVacioException{
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
        Etiquetas.CLASIFICAR_FORMULARIO_MENSAJE = "Clasificación de archivos xml en sus carpetas correspondientes.";
        this.archivosCorrectos = 0;
        this.archivosTotales = 0;
        this.elementosXml = new HashMap<String, String>();
        this.archivosErroneos = new ArrayList<>();
        this.nombreArchivo = null;
    }
}
