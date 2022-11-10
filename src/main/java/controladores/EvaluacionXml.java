package controladores;

import datos.interfaces.ClienteDao;
import dominio.Cliente;
import dominio.componentesxml.DatosGeneralesFactura;
import java.io.*;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

@PropertySource("classpath:cfg/path.properties")
@WebServlet("/EvaluacionXML")
@MultipartConfig
public class EvaluacionXml extends HttpServlet {

    @Autowired
    private Environment env;

    @Autowired
    ClienteDao clienteDao;
    
    String mensaje = "Seleccione los archivos que desea evaluar";
    String servlet = "EvaluacionXML";
    String etiquetaBoton = "Evaluar";
    int archivosCorrectos;
    int archivosTotales;
    List <String> archivosErroneos = new ArrayList<>();
    String icono = "<i class='far fa-check-square'></i>";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("tituloPagina", "Evaluación de archivos XML");
        request.setAttribute("titulo", icono + " Evaluación de archivos XML");
        request.setAttribute("mensajeRegistro", mensaje);
        request.setAttribute("servlet", this.servlet);
        request.setAttribute("etiquetaBoton", this.etiquetaBoton);
        request.setAttribute("archivosErroneos", this.archivosErroneos);
        
        request.getRequestDispatcher("/WEB-INF/paginas/cliente/xml/evaluacionxml.jsp").forward(request, response);
        
        mensaje = "Seleccione los archivos que desea evaluar";
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
            archivosTotales++;
            //Preparación de variables locales
            String nombreArchivo = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
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
            this.LeerXML(f.getAbsolutePath(), nombreArchivo);

            //Eliminación del archivo temporal
            f.deleteOnExit();
        }
        /* Un solo registro
        Part archivo = request.getPart("archivosxml");
        String nombre = Paths.get(archivo.getSubmittedFileName()).getFileName().toString();
        
        File f = new File("C:\\a\\" + nombre);
        
        InputStream contenido = archivo.getInputStream();
        FileOutputStream ous = new FileOutputStream(f);
        
        int dato = contenido.read();
        while (dato != -1) {            
            ous.write(dato);
            dato = contenido.read();
        }
        contenido.close();
        ous.close();
        
        this.LeerXML(f.getAbsolutePath());
         */
        //request.setAttribute("mensajeRegistro", "a");
        //this.doGet(request, response);
        mensaje = "Archivos evaluados (" + archivosCorrectos + " de " + archivosTotales + "). Favor de verificarlos en las carpetas";
        response.sendRedirect(request.getContextPath() + "/EvaluacionXML");
    }

    private void LeerXML(String ruta, String nombreArchivo) {
        System.out.println("\n ********************************Inicio de procesamiento del Archivo ( " + nombreArchivo + " )********************************");
        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Indica el nombre del Nodo XML
            System.out.println("\nContenido del documento " + documentoXml.getDocumentElement().getNodeName() + "\n");

            //Recupera todos los elementos con el nombre de Cabecera
            NodeList Cabecera = documentoXml.getElementsByTagName("Cabecera");

            //Se recorre cada nodo interno de cabeceras
            for (int i = 0; i < Cabecera.getLength(); i++) {

                //Se asigna el nodo interno a recorrer
                Node factura = Cabecera.item(i);
                System.out.println("Nombre elemento actual: \t" + factura.getNodeName());

                //Recuperación de cada nodo interno dentro de Cabecera
                if (factura.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) factura;
                    String elementos[] = new String[6];
                    elementos[0] = element.getElementsByTagName("CodigoREEEmpresaEmisora").item(0).getTextContent();
                    elementos[1] = element.getElementsByTagName("CodigoREEEmpresaDestino").item(0).getTextContent();
                    elementos[2] = element.getElementsByTagName("CodigoDelProceso").item(0).getTextContent();
                    elementos[3] = element.getElementsByTagName("CodigoDePaso").item(0).getTextContent();
                    elementos[4] = element.getElementsByTagName("CodigoDelProceso").item(0).getTextContent();
                    elementos[5] = element.getElementsByTagName("CodigoDeSolicitud").item(0).getTextContent();

                    System.out.println("CodigoREEEmpresaEmisora: \t" + element.getElementsByTagName("CodigoREEEmpresaEmisora").item(0).getTextContent());
                    System.out.println("CodigoREEEmpresaDestino: \t" + element.getElementsByTagName("CodigoREEEmpresaDestino").item(0).getTextContent());
                    System.out.println("CodigoDelProceso: \t\t" + element.getElementsByTagName("CodigoDelProceso").item(0).getTextContent());
                    System.out.println("CodigoDeSolicitud: \t\t" + element.getElementsByTagName("CodigoDeSolicitud").item(0).getTextContent());
                    System.out.println("SecuencialDeSolicitud: \t" + element.getElementsByTagName("SecuencialDeSolicitud").item(0).getTextContent());
                    System.out.println("FechaSolicitud: \t\t" + element.getElementsByTagName("FechaSolicitud").item(0).getTextContent());
                    System.out.println("CUPS: \t\t\t" + element.getElementsByTagName("CUPS").item(0).getTextContent());
                    //Creacion de un modelo Cliente
/*                    Cliente cliente = clienteDao.encontrarCups(new Cliente(element.getElementsByTagName("CUPS").item(0).getTextContent()));
                    
                    //Evaluación del modelo y generación del xml
                    if (cliente.getIdCliente() != 0) {
                        
                        DatosGeneralesFactura   datosGeneralesFactura = new DatosGeneralesFactura(this.LeerDatosGeneralesXML(ruta));
                        
                        /*
                        if (new DatosFacturaDao().encontrarCodFiscal(datosGeneralesFactura.getCodigoFiscalFactura())) {
                            archivosErroneos.add("Ya se ha insertado la factura (" + datosGeneralesFactura.getCodigoFiscalFactura() +") del archivo " + nombreArchivo);
                            break;
                        }
                        
                        this.generarXML(cliente, ruta, elementos, element.getElementsByTagName("CUPS").item(0).getTextContent(), nombreArchivo, datosGeneralesFactura);

                    } else {
                        archivosErroneos.add("No se encontró CUPS (" + cliente.getCups().substring(0,19) + ") en " + nombreArchivo);
                        this.GuardarNoProcesados(ruta, nombreArchivo);
                    }
*/                    
                }
                System.out.println("\n ********************************Fin de procesamiento Archivo ( " + nombreArchivo + " )********************************");

            }

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
            if (e.getMessage().contains("Byte no válido")) {
                archivosErroneos.add("No se procesó el archivo (<Strong>" + nombreArchivo + "</Strong>) porque <Strong> su codificación es diferente a UTF-8</Strong>.");
            } else {
                if (nombreArchivo.length() != 0) {
                    archivosErroneos.add("El archivo (<Strong>" + nombreArchivo + "</Strong>) esta vacío.");
                } else {
                    archivosErroneos.add("No ha seleccionado ningún archivo.");
                }
            }
        }
    }

    private List<String> LeerDatosGeneralesXML(String ruta) {
        //Preparacion de variable para obtener datos

        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual             DatosGeneralesFactura");
        System.out.println("------------------------------------------------------------------------------");

        List<String> elementos = new ArrayList<>();
        elementos.add(0, "No se encontró");
        elementos.add(1, "No se encontró");
        elementos.add(2, "No se encontró");
        elementos.add(3, "No se encontró");
        elementos.add(4, "No se encontró");

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            NodeList flowList = documentoXml.getElementsByTagName("DatosGeneralesFactura");
            
            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("CodigoFiscalFactura".equals(childNode.getNodeName())) {
                        elementos.set(0, childList.item(j).getTextContent().trim());
                    } else if ("TipoFactura".equals(childNode.getNodeName())) {
                        elementos.set(1, childList.item(j).getTextContent().trim());
                    } else if ("MotivoFacturacion".equals(childNode.getNodeName())) {
                        elementos.set(2, childList.item(j).getTextContent().trim());
                    } else if ("CodigoFacturaRectificadaAnulada".equals(childNode.getNodeName())) {
                        elementos.set(3, childList.item(j).getTextContent().trim());
                    } else if ("FechaFactura".equals(childNode.getNodeName())) {
                        elementos.set(4, childList.item(j).getTextContent().trim());
                    } 
                }
            }

            //Lectura de valores
            System.out.println("<0>CodigoFiscalFactura         " + elementos.get(0));
            System.out.println("<1>TipoFactura                 " + elementos.get(1));
            System.out.println("<2>MotivoFacturacion           " + elementos.get(2));
            System.out.println("<3>CodigoFacturaRectificadaAnulada  " + elementos.get(3));
            System.out.println("<4>FechaFactura                " + elementos.get(4));
            
            System.out.println("------------------------------------------------------------------------------");
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }
    
    private void generarXML(Cliente c, String ruta, String[] elementos, String cups, String nombreArchivoOriginal, DatosGeneralesFactura datosGeneralesFactura) throws IOException {
        /*
        Cliente cliente = new ClienteDao().encontrarCups(c);

        if (cliente.getIdCliente() != 0) {
            
            String subCarpeta = "";
            
            if (elementos[0].equals("0894")) {
                subCarpeta = "0894";
            } else {
                switch (elementos[4]) {
                    case "F1":
                        subCarpeta = "F";
                        elementos[5] = this.LeerCodigoFactura(ruta);

                        String tipoFactura = datosGeneralesFactura.getTipoFactura();
                        switch(tipoFactura){
                            case "N":

                                String motivo = datosGeneralesFactura.getMotivoFacturacion();
                                switch(motivo){
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
                                        archivosErroneos.add("El motivo de factura (" + motivo + ") del archivo (" + nombreArchivoOriginal + ") no esta soportado");
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
                                archivosErroneos.add("El tipo de factura (" + tipoFactura + ") del archivo (" + nombreArchivoOriginal + ") no esta soportado");
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
                    case "R1":
                        subCarpeta = "X";
                        break;
                    default:
                        subCarpeta = "Otros";
                        break;
                }
        
            }
            
            NumberFormat formater = new DecimalFormat("#0000");

            String nombreArchivo = "C:\\Peajes\\Procesados\\" + subCarpeta + "\\" + formater.format(cliente.getIdCliente()) + "-" + cliente.getTarifa() + "_" + elementos[0] + "_" + elementos[1] + "_" + elementos[2] + "_ " + elementos[3] + "_" + cups + "_" + elementos[5] + ".xml";
            File f = new File(nombreArchivo);
            f.createNewFile();

            FileWriter fichero = null;
            PrintWriter pw;
            try {
                fichero = new FileWriter(nombreArchivo);
                pw = new PrintWriter(fichero);

                /*---------------------Escritura en el archivo-------------------------
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
                /*---------------------Fin de escritura en el archivo-------------------------
                archivosCorrectos++;
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
        } else {
            System.out.println("(EvaluacionXML). No se creo el archivo XML porque no se encontró un registro que coincida con el cups del XML proporcionado (" + cliente.getCups() + ")");
        }
        */
    }

    private String LeerCodigoFactura(String ruta) {
        //Preparacion de variable para obtener datos
        String codigoFactura = "";
        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre de Cabecera
            NodeList flowList = documentoXml.getElementsByTagName("DatosGeneralesFactura");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("CodigoFiscalFactura".equals(childNode.getNodeName())) {
                        codigoFactura = childList.item(j).getTextContent().trim();
                    }
                }
            }

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return codigoFactura;
    }

    private void GuardarNoProcesados(String ruta, String nombreArchivo) throws IOException {

            String subCarpeta = "NoProcesados";

            String nuevoArchivo =
                    System.getProperty("user.dir") + env.getProperty("peajes.procesados") + subCarpeta + "/" + nombreArchivo;
            File f = new File(nuevoArchivo);
            f.createNewFile();

            FileWriter fichero = null;
            PrintWriter pw;
            try {
                fichero = new FileWriter(nuevoArchivo);
                pw = new PrintWriter(fichero);

                /*---------------------Escritura en el archivo-------------------------*/
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
                /*---------------------Fin de escritura en el archivo-------------------------*/
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
}
