package controladores;

import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

@Controller
@RequestMapping("/procesamiento")
public class ProcesamientoXml extends HttpServlet {

    @Autowired
    @Qualifier(value = "peajesServiceImp")
    private DocumentoXmlService contenidoXmlServicePeajes;

    @Autowired
    private ClienteService clienteService;

    String titulo = "Procesamiento XML";
    String mensaje = "Procesaremos la información contenida dentro de un XML. (Antes de esto, los archivos debieron haber sido evaluados).";
    String servlet = "ProcesamientoXML";
    String etiquetaBoton = "Procesar";
    int archivosCorrectos;
    int archivosTotales;
    List<String> archivosErroneos;
    String errores = "0";
    String icono = "<i class='fas fa-check-square'></i>";
    private int empresaEmisora;
    private boolean is894;

    /**
     * Muestra el fomulario que procesa los archivos
     *
     * @param model modelo usado por Spring
     * @return redireccionamiento al formulario
     */
    @GetMapping("/formulario")
    public String inicio(Model model) {
        model.addAttribute("tituloPagina", this.titulo);
        model.addAttribute("titulo", icono + " " + this.titulo);
        model.addAttribute("mensajeRegistro", this.mensaje);
        model.addAttribute("servlet", this.servlet);
        model.addAttribute("etiquetaBoton", this.etiquetaBoton);
        model.addAttribute("archivosErroneos", this.archivosErroneos);
        this.reiniciarVariables();
        return "xml/formulario";
    }

    /**
     * Metodo controlador en donde procesa cada uno de los archivos xml
     * recibidos
     *
     * @param files los archivos enviados desde el formulario
     * @return redireccionamiento al formulario
     * @throws IOException
     */
    @PostMapping("/procesar")
    public String procesamiento(@RequestParam("archivosxml") MultipartFile[] files) throws IOException {
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
            this.procesar(f, file.getOriginalFilename());
            //Eliminación del archivo temporal
            f.deleteOnExit();
        }
        this.mensaje = "Archivos Procesados (" + archivosCorrectos + " de " + archivosTotales + ")";
        return "redirect:/procesamiento/formulario";
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("tituloPagina", this.titulo);
        request.setAttribute("titulo", icono + " " + this.titulo);
        request.setAttribute("mensajeRegistro", this.mensaje);
        request.setAttribute("servlet", this.servlet);
        request.setAttribute("etiquetaBoton", this.etiquetaBoton);
        request.setAttribute("archivosErroneos", this.archivosErroneos);

        request.getRequestDispatcher("/WEB-INF/paginas/cliente/xml/procesamientoxml.jsp").forward(request, response);
        mensaje = "Procesaremos la información contenida dentro de un XML. (Antes de esto, los archivos debieron haber sido evaluados)";
        titulo = "Procesamiento de archivos XML";
        archivosCorrectos = 0;
        archivosTotales = 0;
        archivosErroneos.clear();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recuperación de todos los archivos enviados
        List<Part> fileParts = request.getParts().stream().filter(part -> "archivosxml".equals(part.getName())).collect(Collectors.toList());

        //Procesamiento de cada archivo
        for (Part filePart : fileParts) {
            String nombreArchivo = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

            archivosTotales++;
            //Preparación de variables locales
            File f;
            FileOutputStream ous;

            try (InputStream fileContent = filePart.getInputStream()) {
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

            //Revisión de cada XML
            this.procesar(f, nombreArchivo);
            //Eliminación del archivo temporal
            f.deleteOnExit();
        }

        mensaje = "Archivos Procesados (" + archivosCorrectos + " de " + archivosTotales + ")";
        response.sendRedirect(request.getContextPath() + "/ProcesamientoXML");
    }

    private void procesar(File archivo, String nombreArchivo) {

        System.out.println("(Ini)************************-----------------------------" + nombreArchivo);

        try {
            Document documento = this.prepareXml(archivo, nombreArchivo);
            //Revision de la compatiblidad
            if (documento.getElementsByTagName("MensajeFacturacion").getLength() == 0) {
                throw new XmlNoSoportado();
            }

            //Revisión del nodo
            if (documento.getElementsByTagName("OtrasFacturas").getLength() != 0) {
                System.out.println("Procesando en otras facturas");
                ProcesamientoOtrasFacturas of = new ProcesamientoOtrasFacturas();
                if (!of.registrar(documento, nombreArchivo).equals("")) {
                    this.archivosErroneos.add(of.registrar(documento, nombreArchivo));
                }
            } else {
                PeajesHelper pr = new PeajesHelper(documento, contenidoXmlServicePeajes, clienteService);
                pr.procesar(nombreArchivo);
            }
            System.out.println("Se proceso la factura");
            archivosCorrectos++;

        } catch (FacturaYaExisteException | ClienteNoExisteException | PeajeTipoFacturaNoSoportadaException | CodRectNoExisteException | XmlNoSoportado e) {
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
     */
    private Document prepareXml(File archivo, String nombreArchivo) {
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
        } catch (ArchivoVacioException e) {
            archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + e.getMessage());
            utileria.ArchivoTexto.escribirError(archivosErroneos.get(archivosErroneos.size() - 1));
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
        this.empresaEmisora = Integer.parseInt(doc.getElementsByTagName("CodigoREEEmpresaEmisora").item(0).getTextContent());
        if (this.empresaEmisora == 894) {
            is894 = true;
        }
    }

    private void reiniciarVariables() {
        this.mensaje = "Procesaremos la información contenida dentro de un XML. (Antes de esto, los archivos debieron haber sido evaluados).";
        this.archivosCorrectos = 0;
        this.archivosTotales = 0;
        this.archivosErroneos = new ArrayList<>();
    }

}
