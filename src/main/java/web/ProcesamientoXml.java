package web;

import datos.*;
import dominio.Cliente;
import dominio.componentesxml.*;
import excepciones.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import utileria.texto.Cadenas;

@WebServlet("/ProcesamientoXML")
@MultipartConfig
public class ProcesamientoXml extends HttpServlet {

    String titulo = "Procesamiento de archivos XML";
    String mensaje = "Procesaremos la información contenida dentro de un XML. (Antes de esto, los archivos debieron haber sido evaluados)";
    String servlet = "ProcesamientoXML";
    String etiquetaBoton = "Procesar";
    int archivosCorrectos;
    int archivosTotales;
    List<String> archivosErroneos = new ArrayList<>();
    List<String> comentarios = new ArrayList<>();
    String errores = "0";
    String icono = "<i class='fas fa-check-square'></i>";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("(ProcesamientoXML). Dentro del servlet de procesamiento XML");

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
            if (archivo.length() != 0) {
                //Preparación del procesamiento del XML
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbFactory.newDocumentBuilder();
                Document documento = builder.parse(archivo);
                documento.getDocumentElement().normalize();

                //Revisión del nodo
                if (documento.getElementsByTagName("OtrasFacturas").getLength() != 0) {
                    System.out.println("Procesando en otras facturas");
                    ProcesamientoOtrasFacturas of = new ProcesamientoOtrasFacturas();
                    if (!of.registrar(documento, nombreArchivo).equals("")) {
                        archivosErroneos.add(of.registrar(documento, nombreArchivo));
                    } else {
                        archivosCorrectos++;
                    }
                } else {
                    ProcesarPeaje pr = new ProcesarPeaje(documento);
                    pr.procesar(nombreArchivo);
                    archivosCorrectos++;
                    //this.LeerCabeceraXML(archivo.getAbsolutePath(), nombreArchivo);
                }
            } else {
                throw new ArchivoVacioException();
            }

        } catch (ParserConfigurationException | SAXException | IOException ex) {
        } catch (FacturaYaExisteException | ClienteNoExisteException | PeajeTipoFacturaNoSoportadaException | ArchivoVacioException ex) {
            archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + ex.getMessage());
        }

        System.out.println("(Fin)************************-----------------------------" + nombreArchivo);
    }
}
