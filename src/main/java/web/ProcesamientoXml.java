package web;

import datos.*;
import dominio.Cliente;
import dominio.Distribuidora;
import dominio.Lectura;
import dominio.componentesxml.*;
import dominio.procesamientoxml.PotenciaAFacturar;
import dominio.procesamientoxml.Remesa;
import dominio.procesamientoxml.ResultadoArchivoXML;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
        if (archivo.length() != 0) {
            try {
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
                    this.LeerCabeceraXML(archivo.getAbsolutePath(), nombreArchivo);
                }

                ArrayList<String> cabecera = this.cabecera(documento);

                Cliente cliente = new ClienteDao().encontrarCups(new Cliente(cabecera.get(6)));
                if (cliente != null) {
                    //comentarios.add("(Archivo Original) " + nombreArchivo);
                } else {
                    System.out.println("No se encontro un cliente");
                }

            } catch (ParserConfigurationException | SAXException | IOException ex) {
                Logger.getLogger(ProcesamientoXml.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Archivo vacío");
        }

        System.out.println("(Fin)************************-----------------------------" + nombreArchivo);
    }

    private ArrayList cabecera(Document doc) {
        ArrayList<String> elementos = new ArrayList<>(7);
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");

        NodeList flowList = doc.getElementsByTagName("Cabecera");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "CodigoREEEmpresaEmisora":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            break;
                        case "CodigoREEEmpresaDestino":
                            elementos.set(1, childList.item(j).getTextContent().trim());
                            break;
                        case "CodigoDelProceso":
                            elementos.set(2, childList.item(j).getTextContent().trim());
                            break;
                        case "CodigoDePaso":
                            elementos.set(3, childList.item(j).getTextContent().trim());
                            break;
                        case "CodigoDeSolicitud":
                            elementos.set(4, String.valueOf(Integer.parseInt(childList.item(j).getTextContent().trim())));
                            break;
                        case "FechaSolicitud":
                            elementos.set(5, childList.item(j).getTextContent().trim().substring(0, 10));
                            break;
                        case "CUPS":
                            elementos.set(6, childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        System.out.print("\n-------------------------\n" + "Cabecera " + elementos + "\n-------------------------");
        return elementos;
    }

    private ArrayList datosGenerales(Document doc) {
        ArrayList<String> elementos = new ArrayList<>(5);
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");

        NodeList flowList = doc.getElementsByTagName("DatosGeneralesFactura");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "CodigoFiscalFactura":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            break;
                        case "TipoFactura":
                            elementos.set(1, childList.item(j).getTextContent().trim());
                            break;
                        case "MotivoFacturacion":
                            elementos.set(2, childList.item(j).getTextContent().trim());
                            break;
                        case "CodigoFacturaRectificadaAnulada":
                            elementos.set(3, childList.item(j).getTextContent().trim());
                            break;
                        case "FechaFactura":
                            elementos.set(4, childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        System.out.print("\n-------------------------\n" + "datosGenerales " + elementos + "\n-------------------------");
        return elementos;
    }

    private void LeerCabeceraXML(String ruta, String nombreArchivo) {
        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            System.out.println("\n ********************************Inicio de procesamiento del Archivo ( " + nombreArchivo + " )********************************");
            //Indica el nombre del Nodo XML
            System.out.println("Contenido del documento " + documentoXml.getDocumentElement().getNodeName() + "\n");

            //Recupera todos los elementos con el nombre de Cabecera
            NodeList Cabecera = documentoXml.getElementsByTagName("Cabecera");

            //Se recorre cada nodo interno de cabecera
            for (int i = 0; i < Cabecera.getLength(); i++) {

                //Se asigna el nodo interno a recorrer
                Node factura = Cabecera.item(i);
                System.out.println("------------------------------------------------------------------------------");
                System.out.println("Nombre Nodo actual          " + factura.getNodeName());
                System.out.println("------------------------------------------------------------------------------");
                //Recuperación de cada nodo interno dentro de Cabecera
                if (factura.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) factura;

                    //Preparacion de variable para obtener datos
                    List<String> elementos = new ArrayList<>();

                    //Lectura y asignacion de valores
                    elementos.add(element.getElementsByTagName("CodigoREEEmpresaEmisora").item(0).getTextContent());
                    elementos.add(element.getElementsByTagName("CodigoREEEmpresaDestino").item(0).getTextContent());
                    elementos.add(element.getElementsByTagName("CodigoDelProceso").item(0).getTextContent());
                    elementos.add(element.getElementsByTagName("CodigoDePaso").item(0).getTextContent());
                    if (element.getElementsByTagName("CodigoDeSolicitud").item(0).getTextContent().trim().substring(0, 1).equals("0")) {
                        elementos.add(String.valueOf(Integer.parseInt(element.getElementsByTagName("CodigoDeSolicitud").item(0).getTextContent().trim())));
                    } else {
                        elementos.add(element.getElementsByTagName("CodigoDeSolicitud").item(0).getTextContent().trim());
                    }
                    elementos.add(element.getElementsByTagName("FechaSolicitud").item(0).getTextContent().substring(0, 10));
                    elementos.add(element.getElementsByTagName("CUPS").item(0).getTextContent());

                    DatosCabecera datosCabecera = new DatosCabecera(elementos);

                    //Revision de los calores proporcionados
                    System.out.println("CodigoREEEmpresaEmisora     " + elementos.get(0));
                    System.out.println("CodigoREEEmpresaDestino     " + elementos.get(1));
                    System.out.println("CodigoDelProceso            " + elementos.get(2));
                    System.out.println("CodigoDePaso                " + elementos.get(3));
                    System.out.println("CodigoDeSolicitud           " + elementos.get(4));
                    System.out.println("FechaSolicitud              " + elementos.get(5));
                    System.out.println("CUPS                        " + elementos.get(6));
                    System.out.println("------------------------------------------------------------------------------");

                    //Creación del modelo para clientes
                    Cliente cliente = new ClienteDao().encontrarCups(new Cliente(element.getElementsByTagName("CUPS").item(0).getTextContent()));

                    //Evaluación si el cliente existe
                    if (cliente.getIdCliente() != 0) {

                        comentarios.add("Nombre del archivo original: <Strong>" + nombreArchivo + "</Strong><br/>");

                        DatosGeneralesFactura datosGeneralesFactura = new DatosGeneralesFactura(this.LeerDatosGeneralesXML(ruta));

                        if (new DatosFacturaDao().encontrarCodFiscal(datosGeneralesFactura.getCodigoFiscalFactura(), Integer.parseInt(elementos.get(0)))) {
                            archivosErroneos.add("Ya se ha insertado la factura (<Strong>" + datosGeneralesFactura.getCodigoFiscalFactura() + "</Strong>) del archivo <Strong>" + nombreArchivo + "</Strong>");
                            System.out.println("\n ********************************Fin de procesamiento Archivo ( " + nombreArchivo + " )********************************");
                            break;
                        }

                        switch (datosGeneralesFactura.getTipoFactura()) {
                            case "A":
                                this.procesarFacturaAbono(cliente, datosCabecera, datosGeneralesFactura, ruta, nombreArchivo);
                                break;
                            case "R":
                                this.procesarFacturaR(cliente, datosCabecera, datosGeneralesFactura, ruta, nombreArchivo);
                                break;
                            case "N":
                            case "G":
                                this.procesarFacturaN(cliente, datosCabecera, datosGeneralesFactura, ruta);
                                break;
                            default:
                                archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> tiene un <Strong>tipo de factura no soportado (" + datosGeneralesFactura.getTipoFactura() + ")</Strong>");
                        }

                        //Reinicio de variables
                        this.comentarios.clear();
                        this.errores = "0";

                    } else {
                        archivosErroneos.add("No se encontró CUPS (" + cliente.getCups().substring(0, 19) + ") en " + nombreArchivo);
                        System.out.println("(ProcesamientoXML). No se encontró un cliente con el CUPS proporcionado (" + elementos.get(6) + ")");
                    }

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

    /*--------------------------------------Procesamiento de facturas---------------------------------------*/
    private void procesarFacturaN(Cliente cliente, DatosCabecera datosCabecera, DatosGeneralesFactura datosGeneralesFactura, String ruta) {

        DatosFacturaAtr datosFacturaAtr = new DatosFacturaAtr(this.LeerDatosFacturaATRXML(ruta));
        DatosExcesoPotencia datosExcesoPotencia = new DatosExcesoPotencia(this.LeerExcesoPotenciaXML(ruta));

        //Potencia
        DatosPotenciaContratada datosPotenciaContratada = new DatosPotenciaContratada(this.LeerPeriodoPotenciaContratadaXML(ruta));
        DatosPotenciaMaxDemandada datosPotenciaMaxDemandada = new DatosPotenciaMaxDemandada(this.LeerPeriodoPotenciaMaxDemandadaXML(ruta));
        DatosPotenciaAFacturar datosPotenciaAFacturar = new DatosPotenciaAFacturar(this.LeerPeriodoPotenciaAFacturarXML(ruta));
        DatosPotenciaPrecio datosPotenciaPrecio = new DatosPotenciaPrecio(this.LeerPeriodoPrecioProtenciaXML(ruta));
        DatosPotenciaImporteTotal datosPotenciaImporteTotal = new DatosPotenciaImporteTotal(this.LeerTerminoPotenciaImporteTotalXML(ruta));

        //EnergiaActiva
        DatosEnergiaActiva datosEnergiaActiva = new DatosEnergiaActiva(this.LeerEnergiaActivaDatosXML(ruta));
        DatosEnergiaActivaValores datosEnergiaActivaValores = new DatosEnergiaActivaValores(this.LeerEnergiaActivaValorXML(ruta));
        DatosEnergiaActivaPrecio datosEnergiaActivaPrecio = new DatosEnergiaActivaPrecio(this.LeerEnergiaActivaPrecioXML(ruta));
        DatosEnergiaActivaImporteTotal datosEnergiaActivaImporteTotal = new DatosEnergiaActivaImporteTotal(this.LeerEnergiaActivaImporteTotalXML(ruta));

        DatosImpuestoElectrico datosImpuestoElectrico = new DatosImpuestoElectrico(this.LeerImpuestoElectricoXML(ruta));
        DatosAlquileres datosAlquileres = new DatosAlquileres(this.LeerAlquileresXML(ruta));
        DatosIva datosIva = new DatosIva(this.LeerIvaXML(ruta));

        //DatosAE
        DatosAeConsumo datosAeConsumo = new DatosAeConsumo(this.LeerIntegradorAEConsumoXML(ruta));
        DatosAeLecturaDesde datosAeLecturaDesde = new DatosAeLecturaDesde(this.LeerIntegradorAELecturaDesdeXML(ruta));
        DatosAeLecturaHasta datosAeLecturaHasta = new DatosAeLecturaHasta(this.LeerIntegradorAELecturaHastaXML(ruta));
        DatosAeProcedenciaDesde datosAeProcedenciaDesde = new DatosAeProcedenciaDesde(this.LeerIntegradorAEProcedenciaDesdeXML(ruta));
        DatosAeProcedenciaHasta datosAeProcedenciaHasta = new DatosAeProcedenciaHasta(this.LeerIntegradorAEProcedenciaHastaXML(ruta));

        //Datos R
        DatosRConsumo datosRConsumo = new DatosRConsumo(this.LeerIntegradorRConsumoXML(ruta));
        DatosRLecturaDesde datosRLecturaDesde = new DatosRLecturaDesde(this.LeerIntegradorRLecturaDesdeXML(ruta));
        DatosRLecturaHasta datosRLecturaHasta = new DatosRLecturaHasta(this.LeerIntegradorRLecturaHastaXML(ruta));
        ReactivaImporteTotal reactivaImporteTotal = new ReactivaImporteTotal(this.ReactivaImporteTotal(ruta));

        //Datos PM
        DatosPmConsumo datosPmConsumo = new DatosPmConsumo(this.LeerIntegradorPMConsumoXML(ruta));
        DatosPmLecturaHasta datosPmLecturaHasta = new DatosPmLecturaHasta(this.LeerIntegradorPMLecturaHastaXML(ruta));

        //Fin de registro
        DatosFinDeRegistro datosFinDeRegistro = new DatosFinDeRegistro(this.LeerFinDeRegistroXML(ruta));

        //Comentarios
        System.out.println(comentarios.get(0));

        //Inserción de registro
        this.insertarDatos(cliente, datosCabecera,
                datosGeneralesFactura, datosFacturaAtr, datosExcesoPotencia,
                datosPotenciaContratada, datosPotenciaMaxDemandada, datosPotenciaAFacturar, datosPotenciaPrecio, datosPotenciaImporteTotal,
                datosEnergiaActiva, datosEnergiaActivaValores, datosEnergiaActivaPrecio, datosEnergiaActivaImporteTotal,
                datosImpuestoElectrico, datosAlquileres, datosIva,
                datosAeConsumo, datosAeLecturaDesde, datosAeLecturaHasta, datosAeProcedenciaDesde, datosAeProcedenciaHasta,
                datosRConsumo, datosRLecturaDesde, datosRLecturaHasta, reactivaImporteTotal,
                datosPmConsumo, datosPmLecturaHasta,
                datosFinDeRegistro, comentarios.get(0), errores);

        //Contador archivos
        this.archivosCorrectos++;
    }

    private void procesarFacturaR(Cliente cliente, DatosCabecera datosCabecera, DatosGeneralesFactura datosGeneralesFactura, String ruta, String nombreArchivo) {
        DocumentoXml documentoXmls = new ContenidoXmlDao().buscarCodFiscal(datosGeneralesFactura.getCodigoFacturaRectificadaAnulada(), Integer.parseInt(datosCabecera.getCodigoREEEmpresaEmisora()));
        if (documentoXmls != null) {
            System.out.println("Se encontro registro");
            new ContenidoXmlDao().actualizarR(documentoXmls, this.LeerFinDeRegistroXML(ruta).get(5), nombreArchivo);
            this.procesarFacturaN(cliente, datosCabecera, datosGeneralesFactura, ruta);
        } else {
            this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque no se encontró el codRectificado (<Strong>" + datosGeneralesFactura.getCodigoFacturaRectificadaAnulada() + ")</Strong>");
        }
    }

    private void procesarFacturaAbono(Cliente cliente, DatosCabecera datosCabecera, DatosGeneralesFactura datosGeneralesFactura, String ruta, String nombreArchivo) {
        DocumentoXml documentoXmls = new ContenidoXmlDao().buscarCodFiscal(datosGeneralesFactura.getCodigoFacturaRectificadaAnulada(), Integer.parseInt(datosCabecera.getCodigoREEEmpresaEmisora()));
        if (documentoXmls != null) {
            System.out.println("Se encontro registro");
            this.procesarFacturaN(cliente, datosCabecera, datosGeneralesFactura, ruta);
        } else {
            this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque no se encontró el codRectificado (<Strong>" + datosGeneralesFactura.getCodigoFacturaRectificadaAnulada() + ")</Strong>");
        }
    }

    /*-----------------------------------Procesamiento de Datos-------------------------------*/
    private int insertarDatos(Cliente cliente, DatosCabecera datosCabecera,
            DatosGeneralesFactura datosGeneralesFactura, DatosFacturaAtr datosFacturaAtr, 
            DatosExcesoPotencia datosExcesoPotencia, DatosPotenciaContratada datosPotenciaContratada, DatosPotenciaMaxDemandada datosPotenciaMaxDemandada, DatosPotenciaAFacturar datosPotenciaAFacturar, DatosPotenciaPrecio datosPotenciaPrecio, DatosPotenciaImporteTotal datosPotenciaImporteTotal, 
            DatosEnergiaActiva datosEnergiaActiva, DatosEnergiaActivaValores datosEnergiaActivaValores, DatosEnergiaActivaPrecio datosEnergiaActivaPrecio, DatosEnergiaActivaImporteTotal datosEnergiaActivaImporteTotal, 
            DatosImpuestoElectrico datosImpuestoElectrico, DatosAlquileres datosAlquileres, DatosIva datosIva, 
            DatosAeConsumo datosAeConsumo, DatosAeLecturaDesde datosAeLecturaDesde, DatosAeLecturaHasta datosAeLecturaHasta, DatosAeProcedenciaDesde datosAeProcedenciaDesde, DatosAeProcedenciaHasta datosAeProcedenciaHasta, 
            DatosRConsumo datosRConsumo, DatosRLecturaDesde datosRLecturaDesde, DatosRLecturaHasta datosRLecturaHasta, ReactivaImporteTotal reactivaImporteTotal,
            DatosPmConsumo datosPmConsumo, DatosPmLecturaHasta datosPmLecturaHasta, 
            DatosFinDeRegistro datosFinDeRegistro, 
            String comentarios, String errores) {
        
        DocumentoXml documentoXml = new DocumentoXml(cliente, datosCabecera, datosGeneralesFactura, datosFacturaAtr, 
                datosExcesoPotencia, datosPotenciaContratada, datosPotenciaMaxDemandada, datosPotenciaAFacturar, datosPotenciaPrecio, datosPotenciaImporteTotal, 
                datosEnergiaActiva, datosEnergiaActivaValores, datosEnergiaActivaPrecio, datosEnergiaActivaImporteTotal, 
                datosImpuestoElectrico, datosAlquileres, datosIva, 
                datosAeConsumo, datosAeLecturaDesde, datosAeLecturaHasta, datosAeProcedenciaDesde, datosAeProcedenciaHasta, 
                datosRConsumo, datosRLecturaDesde, datosRLecturaHasta, reactivaImporteTotal,
                datosPmConsumo, datosPmLecturaHasta, 
                datosFinDeRegistro, 
                comentarios, errores);

        new ContenidoXmlDao().insertar(documentoXml);

        return 0;
    }

    /*------------------------------------------Obtencion de datos de la factura----------------------------------------------*/
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

    private List<String> LeerDatosFacturaATRXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual              DatosFacturaATR");
        System.out.println("------------------------------------------------------------------------------");
        //Preparacion de variable para obtener datos
        List<String> elementos = new ArrayList<>();
        elementos.add(0, "0");
        elementos.add(1, "0");
        elementos.add(2, "0");
        elementos.add(3, "0");
        elementos.add(4, "0");
        elementos.add(5, "0");
        elementos.add(6, "0");

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowListFacturaATR = documentoXml.getElementsByTagName("DatosFacturaATR");

            for (int i = 0; i < flowListFacturaATR.getLength(); i++) {
                NodeList childList = flowListFacturaATR.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("TarifaATRFact".equals(childNode.getNodeName())) {
                        elementos.set(0, childList.item(j).getTextContent().trim());
                    } else if ("ModoControlPotencia".equals(childNode.getNodeName())) {
                        elementos.set(1, childList.item(j).getTextContent().trim());
                    } else if ("MarcaMedidaConPerdidas".equals(childNode.getNodeName())) {
                        elementos.set(2, childList.item(j).getTextContent().trim());
                    } else if ("VAsTrafo".equals(childNode.getNodeName())) {
                        elementos.set(3, childList.item(j).getTextContent().trim());
                    } else if ("PorcentajePerdidas".equals(childNode.getNodeName())) {
                        elementos.set(4, childList.item(j).getTextContent().trim());
                    }
                }
            }

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("Periodo");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("NumeroDias".equals(childNode.getNodeName())) {
                        elementos.set(5, childList.item(j).getTextContent().trim());
                    }
                }
            }

            int indice = 0;
            System.out.println("<0> TarifaATRFact            " + elementos.get(0));
            System.out.println("<1> ModoControlPotencia      " + elementos.get(1));
            System.out.println("<2> MarcaMedidaConPerdidas   " + elementos.get(2));
            System.out.println("<3> VAsTrafo                 " + elementos.get(3));
            System.out.println("<4> PorcentajePerdidas       " + elementos.get(4));
            System.out.println("<5> NumeroDias               " + elementos.get(5));

            System.out.println("------------------------------------------------------------------------------");
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerPeriodoPotenciaContratadaXML(String ruta) {

        //Se asigna el nodo interno a recorrer
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          " + "PotenciaContratada");
        System.out.println("Obtiene hasta 12 posibles valores y los ordena en 6 correspondientes, sumando 1+6, 2+7 ... 6+12");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementosPeriodo = Arrays.asList(new Double[6]);
        elementosPeriodo.set(0, 0.0);
        elementosPeriodo.set(1, 0.0);
        elementosPeriodo.set(2, 0.0);
        elementosPeriodo.set(3, 0.0);
        elementosPeriodo.set(4, 0.0);
        elementosPeriodo.set(5, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            List<Double> elementosInternos = Arrays.asList(new Double[12]);
            elementosInternos.set(0, 0.0);
            elementosInternos.set(1, 0.0);
            elementosInternos.set(2, 0.0);
            elementosInternos.set(3, 0.0);
            elementosInternos.set(4, 0.0);
            elementosInternos.set(5, 0.0);
            elementosInternos.set(6, 0.0);
            elementosInternos.set(7, 0.0);
            elementosInternos.set(8, 0.0);
            elementosInternos.set(9, 0.0);
            elementosInternos.set(10, 0.0);
            elementosInternos.set(11, 0.0);

            //Recupera todos los elementos con el nombre brindado
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("Periodo");

            int indicePeriodo = 0;
            boolean continuar = true;
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if (indicePeriodo > 11) {
                        continuar = false;
                        this.agregarError("5");
                        break;
                    } else {
                        if ("PotenciaContratada".equals(childNode.getNodeName())) {
                            elementosInternos.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()) / 1000);
                        }
                    }
                }
                if (!continuar) {
                    break;
                }
            }
            indicePeriodo = 0;

            for (int i = 0; i < 6; i++) {
                elementosPeriodo.set(i, (elementosInternos.get(i) + elementosInternos.get(i + 6)));
            }

            for (int i = 0; i < elementosPeriodo.size(); i++) {
                System.out.println("<" + i + "> Potencia Contratada Total " + (i + 1) + "     " + elementosPeriodo.get(i));
            }

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementosPeriodo;
    }

    private List<Double> LeerPeriodoPotenciaMaxDemandadaXML(String ruta) {

        //Se asigna el nodo interno a recorrer
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          " + "Potencia Maxima demandada");
        System.out.println("Obtiene hasta 12 posibles valores y los ordena en 6 correspondientes, sumando 1+6, 2+7 ... 6+12");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementosPeriodo = Arrays.asList(new Double[6]);
        elementosPeriodo.set(0, 0.0);
        elementosPeriodo.set(1, 0.0);
        elementosPeriodo.set(2, 0.0);
        elementosPeriodo.set(3, 0.0);
        elementosPeriodo.set(4, 0.0);
        elementosPeriodo.set(5, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            List<Double> elementosInternos = Arrays.asList(new Double[12]);
            elementosInternos.set(0, 0.0);
            elementosInternos.set(1, 0.0);
            elementosInternos.set(2, 0.0);
            elementosInternos.set(3, 0.0);
            elementosInternos.set(4, 0.0);
            elementosInternos.set(5, 0.0);
            elementosInternos.set(6, 0.0);
            elementosInternos.set(7, 0.0);
            elementosInternos.set(8, 0.0);
            elementosInternos.set(9, 0.0);
            elementosInternos.set(10, 0.0);
            elementosInternos.set(11, 0.0);

            //Recupera todos los elementos con el nombre brindado
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("Periodo");

            int indicePeriodo = 0;
            boolean continuar = true;
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if (indicePeriodo > 11) {
                        continuar = false;
                        this.agregarError("6");
                        break;
                    } else {
                        if ("PotenciaMaxDemandada".equals(childNode.getNodeName())) {
                            elementosInternos.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()) / 1000);
                        }
                    }
                }
                if (!continuar) {
                    break;
                }
            }
            indicePeriodo = 0;

            for (int i = 0; i < 6; i++) {
                elementosPeriodo.set(i, (elementosInternos.get(i) + elementosInternos.get(i + 6)));
            }

            for (int i = 0; i < elementosPeriodo.size(); i++) {
                System.out.println("<" + i + "> PotenciaMaxDemandada Total " + (i + 1) + "     " + elementosPeriodo.get(i));
            }

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementosPeriodo;
    }

    private List<Double> LeerPeriodoPotenciaAFacturarXML(String ruta) {

        //Se asigna el nodo interno a recorrer
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          " + "Potencia a facturar");
        System.out.println("Obtiene 6 posibles datos, organizandoles en 3 el valor máximo entre dos medidas (1:4, 2:5, 3:6)");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementosPeriodo = Arrays.asList(new Double[3]);
        elementosPeriodo.set(0, 0.0);
        elementosPeriodo.set(1, 0.0);
        elementosPeriodo.set(2, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            List<Double> elementosInternos = Arrays.asList(new Double[6]);
            elementosInternos.set(0, 0.0);
            elementosInternos.set(1, 0.0);
            elementosInternos.set(2, 0.0);
            elementosInternos.set(3, 0.0);
            elementosInternos.set(4, 0.0);
            elementosInternos.set(5, 0.0);

            //Recupera todos los elementos con el nombre brindado
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("Periodo");

            int indicePeriodo = 0;
            boolean seguir = true;
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if ("PotenciaAFacturar".equals(childNode.getNodeName())) {
                        if (indicePeriodo > 5) {
                            //this.agregarComentario("Se encontraron <Strong>más de 6 potencias a Facturar</Strong>. Revisar el archivo");
                            this.agregarError("1");
                            seguir = false;
                        } else {
                            elementosInternos.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()) / 1000);
                        }
                    }
                    if (!seguir) {
                        break;
                    }
                }
                if (!seguir) {
                    break;
                }
            }

            indicePeriodo = 0;

            for (int i = 0; i < 3; i++) {
                if (elementosInternos.get(i) > elementosInternos.get((i + 3))) {
                    elementosPeriodo.set(i, elementosInternos.get(i));
                } else {
                    elementosPeriodo.set(i, elementosInternos.get(i + 3));
                }
            }

            for (int i = 0; i < elementosPeriodo.size(); i++) {
                System.out.println("<" + i + "> PotenciaAFacturar P " + (i + 1) + "     " + elementosPeriodo.get(i));
            }

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementosPeriodo;
    }

    private List<Double> LeerPeriodoPrecioProtenciaXML(String ruta) {

        //Se asigna el nodo interno a recorrer
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          " + "Precio Potencia");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementosPeriodo = Arrays.asList(new Double[6]);
        elementosPeriodo.set(0, 0.0);
        elementosPeriodo.set(1, 0.0);
        elementosPeriodo.set(2, 0.0);
        elementosPeriodo.set(3, 0.0);
        elementosPeriodo.set(4, 0.0);
        elementosPeriodo.set(5, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            List<Double> elementosInternos = Arrays.asList(new Double[12]);
            elementosInternos.set(0, 0.0);
            elementosInternos.set(1, 0.0);
            elementosInternos.set(2, 0.0);
            elementosInternos.set(3, 0.0);
            elementosInternos.set(4, 0.0);
            elementosInternos.set(5, 0.0);
            elementosInternos.set(6, 0.0);
            elementosInternos.set(7, 0.0);
            elementosInternos.set(8, 0.0);
            elementosInternos.set(9, 0.0);
            elementosInternos.set(10, 0.0);
            elementosInternos.set(11, 0.0);

            //Recupera todos los elementos con el nombre brindado
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("Periodo");

            int indicePeriodo = 0;
            boolean continuar = true;
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if (indicePeriodo > 11) {
                        continuar = false;
                        this.agregarError("7");
                        break;
                    } else {
                        if ("PrecioPotencia".equals(childNode.getNodeName())) {
                            elementosInternos.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                        }
                    }
                }
                if (!continuar) {
                    break;
                }
            }
            indicePeriodo = 0;

            for (int i = 0; i < 6; i++) {
                elementosPeriodo.set(i, (elementosInternos.get(i) + elementosInternos.get(i + 6)));
            }

            double mayorValor = Collections.max(elementosInternos);

            System.out.println("Elemento de mayor valor = " + String.format("%.9f", mayorValor));

            for (int i = 0; i < elementosPeriodo.size(); i++) {
                System.out.println("<" + i + "> PrecioPotencia Total " + (i + 1) + "     " + elementosPeriodo.get(i));
            }

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementosPeriodo;
    }

    private List<Double> LeerExcesoPotenciaXML(String ruta) {

        //Se asigna el nodo interno a recorrer
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          " + "Exceso Potencia");
        System.out.println("Se obtienen todos los 6 excesos de potencia y 1 importe total de excesos");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementosPeriodo = Arrays.asList(new Double[7]);
        elementosPeriodo.set(0, 0.0);
        elementosPeriodo.set(1, 0.0);
        elementosPeriodo.set(2, 0.0);
        elementosPeriodo.set(3, 0.0);
        elementosPeriodo.set(4, 0.0);
        elementosPeriodo.set(5, 0.0);
        elementosPeriodo.set(6, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("Periodo");

            int indicePeriodo = 0;
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if ("ValorExcesoPotencia".equals(childNode.getNodeName())) {
                        elementosPeriodo.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    } else if ("ImporteTotalExcesos".equals(childNode.getNodeName())) {
                        elementosPeriodo.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("ExcesoPotencia");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childListLecturaHasta = flowList.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if ("ImporteTotalExcesos".equals(childNode.getNodeName())) {
                        elementosPeriodo.set(6, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }

            indicePeriodo = 0;

            for (int i = 0; i < elementosPeriodo.size() - 1; i++) {
                System.out.println("<" + i + ">ValorExcesoPotencia" + (i + 1) + "     " + elementosPeriodo.get(i));
            }
            System.out.println("<6>ImporteTotalExcesos      " + elementosPeriodo.get(6));

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementosPeriodo;
    }

    //Energia Activa
    private List<String> LeerEnergiaActivaDatosXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual              Datos de Termino Energía Activa");
        System.out.println("------------------------------------------------------------------------------");

        List<String> elementos = new ArrayList<>();
        elementos.add(0, "No encontrada");
        elementos.add(1, "No encontrada");
        elementos.add(2, "No encontrada");
        elementos.add(3, "No encontrada");

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            int indice = 0;
            boolean continuar = true;
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("TerminoEnergiaActiva");
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childList = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if (indice > 3) {
                        this.agregarError("2");
                        continuar = false;
                        break;
                    } else {
                        if ("FechaDesde".equals(childNode.getNodeName())) {
                            elementos.set(indice++, childList.item(j).getTextContent().trim());
                        } else if ("FechaHasta".equals(childNode.getNodeName())) {
                            elementos.set(indice++, childList.item(j).getTextContent().trim());
                        }
                    }
                }
                if (!continuar) {
                    break;
                }
            }
            indice = 0;

            for (int i = 0; i < elementos.size(); i++) {
                System.out.println("<" + i + "> Fecha desde " + (i + 1) + "      " + elementos.get(i++));
                System.out.println("<" + i + "> Fecha Hasta " + (i) + "      " + elementos.get(i));
            }

            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerEnergiaActivaValorXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual              Valores de Energía Activa");
        System.out.println("Obtiene 12 posibles valores, los organiza en 6 (1+7, 2+8 ... 6+12) y los suma en un total");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = new ArrayList<>();
        elementos.add(0, 0.0);
        elementos.add(1, 0.0);
        elementos.add(2, 0.0);
        elementos.add(3, 0.0);
        elementos.add(4, 0.0);
        elementos.add(5, 0.0);
        elementos.add(6, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            List<Double> elementosInternos = Arrays.asList(new Double[12]);
            elementosInternos.set(0, 0.0);
            elementosInternos.set(1, 0.0);
            elementosInternos.set(2, 0.0);
            elementosInternos.set(3, 0.0);
            elementosInternos.set(4, 0.0);
            elementosInternos.set(5, 0.0);
            elementosInternos.set(6, 0.0);
            elementosInternos.set(7, 0.0);
            elementosInternos.set(8, 0.0);
            elementosInternos.set(9, 0.0);
            elementosInternos.set(10, 0.0);
            elementosInternos.set(11, 0.0);

            //Recupera todos los elementos con el nombre brindado
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("Periodo");

            int indicePeriodo = 0;
            boolean continuar = true;
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if (indicePeriodo > 11) {
                        this.agregarError("3");
                        continuar = false;
                        break;
                    } else {
                        if ("ValorEnergiaActiva".equals(childNode.getNodeName())) {
                            elementosInternos.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                        }
                    }
                }
                if (!continuar) {
                    break;
                }
            }
            indicePeriodo = 0;

            for (int i = 0; i < 6; i++) {
                elementos.set(i, (elementosInternos.get(i) + elementosInternos.get(i + 6)));
            }

            for (int i = 0; i < 6; i++) {
                System.out.println("<" + i + "> ValorEnergiaActiva Total " + (i + 1) + "     " + elementos.get(i));
            }

            for (int i = 0; i < 6; i++) {
                elementos.set(6, elementos.get(6) + elementos.get(i));
            }

            System.out.println("<6> Suma                           " + elementos.get(6));

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerEnergiaActivaPrecioXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual              Precio de Energía Activa");
        System.out.println("Obtiene 12 posibles valores y los suma en 6 correspondiendo a pocisiones 1+7 2+8 ... 6+12");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = new ArrayList<>();
        elementos.add(0, 0.0);
        elementos.add(1, 0.0);
        elementos.add(2, 0.0);
        elementos.add(3, 0.0);
        elementos.add(4, 0.0);
        elementos.add(5, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            List<Double> elementosInternos = Arrays.asList(new Double[12]);
            elementosInternos.set(0, 0.0);
            elementosInternos.set(1, 0.0);
            elementosInternos.set(2, 0.0);
            elementosInternos.set(3, 0.0);
            elementosInternos.set(4, 0.0);
            elementosInternos.set(5, 0.0);
            elementosInternos.set(6, 0.0);
            elementosInternos.set(7, 0.0);
            elementosInternos.set(8, 0.0);
            elementosInternos.set(9, 0.0);
            elementosInternos.set(10, 0.0);
            elementosInternos.set(11, 0.0);

            //Recupera todos los elementos con el nombre brindado
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("Periodo");

            int indicePeriodo = 0;
            boolean continuar = true;
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if (indicePeriodo > 11) {
                        this.agregarError("4");
                        continuar = false;
                        break;
                    } else {
                        if ("PrecioEnergia".equals(childNode.getNodeName())) {
                            elementosInternos.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                        }
                    }
                }
                if (!continuar) {
                    break;
                }
            }
            indicePeriodo = 0;

            for (int i = 0; i < 6; i++) {
                elementos.set(i, (elementosInternos.get(i) + elementosInternos.get(i + 6)));
            }

            for (int i = 0; i < elementos.size(); i++) {
                System.out.println("<" + i + "> PrecioEnergia Total " + (i + 1) + "     " + elementos.get(i));
            }

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerEnergiaActivaImporteTotalXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual              Importe Total Energía Activa");
        System.out.println("Obtiene 1 elemento denomidado ImporteTotalEnergiaActiva en el nodo EnergiaActiva");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = new ArrayList<>();
        elementos.add(0, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("EnergiaActiva");

            int indicePeriodo = 0;
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if ("ImporteTotalEnergiaActiva".equals(childNode.getNodeName())) {
                        elementos.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }
            indicePeriodo = 0;

            System.out.println("<0> ImporteTotalEnergiaActiva " + elementos.get(0));

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerTerminoPotenciaImporteTotalXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual              Importe Total Termino Potencia");
        System.out.println("Obtiene 1 elemento denomidado ImporteTotalTerminoPotencia en el nodo Potencia");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = new ArrayList<>();
        elementos.add(0, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("Potencia");

            int indicePeriodo = 0;
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if ("ImporteTotalTerminoPotencia".equals(childNode.getNodeName())) {
                        elementos.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }
            indicePeriodo = 0;

            System.out.println("<0> ImporteTotalTerminoPotencia " + elementos.get(0));

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerImpuestoElectricoXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          ImpuestoElectrico");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = new ArrayList<>();
        elementos.add(0, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            int indice = 0;
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("ImpuestoElectrico");
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childList = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("Importe".equals(childNode.getNodeName())) {
                        elementos.set(indice++, Double.parseDouble(childList.item(j).getTextContent().trim()));
                    }
                }
            }
            indice = 0;

            System.out.println("<0> Importe              " + elementos.get(0));

            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerAlquileresXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          Alquileres");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = new ArrayList<>();
        elementos.add(0, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            int indice = 0;
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("Alquileres");
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childList = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("ImporteFacturacionAlquileres".equals(childNode.getNodeName())) {
                        elementos.set(indice++, Double.parseDouble(childList.item(j).getTextContent().trim()));
                    }
                }
            }
            indice = 0;

            System.out.println("<0> Importe Facturacion Alquileres " + elementos.get(0));

            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerIvaXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          IVA");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = new ArrayList<>();
        elementos.add(0, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            int indice = 0;
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("IVA");
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childList = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("BaseImponible".equals(childNode.getNodeName())) {
                        elementos.set(indice++, Double.parseDouble(childList.item(j).getTextContent().trim()));
                    }
                }
            }
            indice = 0;

            System.out.println("<0>Base Imponible              " + elementos.get(0));

            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<String> LeerIntegradorXML(String ruta, String tarifa) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          Integrador");
        System.out.println("------------------------------------------------------------------------------");

        List<String> elementos = new ArrayList<>();

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            String codigoPeriodo = null;

            elementos.add(tarifa.trim());

            List<String> elementosIntegrador = new ArrayList<>();
            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("Integrador");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("Magnitud".equals(childNode.getNodeName())) {
                        codigoPeriodo = childList.item(j).getTextContent().trim();
                    } else if ("CodigoPeriodo".equals(childNode.getNodeName())) {
                        elementosIntegrador.add(codigoPeriodo + childList.item(j).getTextContent().trim());
                    } else if ("ConsumoCalculado".equals(childNode.getNodeName())) {
                        elementosIntegrador.add(childList.item(j).getTextContent().trim());
                    }
                }
            }

            List<String> elementosLecturasDesde = new ArrayList<>();
            //Recupera todos los elementos con el nombre brindado
            NodeList flowListLecturaDesde = documentoXml.getElementsByTagName("LecturaDesde");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childListLecturaDesde = flowListLecturaDesde.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaDesde.getLength(); j++) {
                    Node childNode = childListLecturaDesde.item(j);
                    if ("Fecha".equals(childNode.getNodeName())) {
                        elementosLecturasDesde.add(childListLecturaDesde.item(j).getTextContent().trim());
                    } else if ("Procedencia".equals(childNode.getNodeName())) {
                        elementosLecturasDesde.add(childListLecturaDesde.item(j).getTextContent().trim());
                    } else if ("Lectura".equals(childNode.getNodeName())) {
                        elementosLecturasDesde.add(childListLecturaDesde.item(j).getTextContent().trim());
                    }
                }
            }

            List<String> elementosLecturasHasta = new ArrayList<>();
            //Recupera todos los elementos con el nombre brindado
            NodeList flowListLecturaHasta = documentoXml.getElementsByTagName("LecturaHasta");

            for (int i = 0; i < flowListLecturaHasta.getLength(); i++) {
                NodeList childListLecturaHasta = flowListLecturaHasta.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if ("Fecha".equals(childNode.getNodeName())) {
                        elementosLecturasHasta.add(childListLecturaHasta.item(j).getTextContent().trim());
                    } else if ("Procedencia".equals(childNode.getNodeName())) {
                        elementosLecturasHasta.add(childListLecturaHasta.item(j).getTextContent().trim());
                    } else if ("Lectura".equals(childNode.getNodeName())) {
                        elementosLecturasHasta.add(childListLecturaHasta.item(j).getTextContent().trim());
                    }
                }
            }

            int totalIntegrador = 0;
            int indiceLecturasDesde = 0;
            int indiceLecturasHasta = 0;
            for (int i = 0; i < elementosIntegrador.size(); i++) {
                elementos.add(elementosIntegrador.get(i++));
                elementos.add(elementosIntegrador.get(i));
                elementos.add(elementosLecturasDesde.get(indiceLecturasDesde++));
                elementos.add(elementosLecturasDesde.get(indiceLecturasDesde++));
                elementos.add(elementosLecturasDesde.get(indiceLecturasDesde++));
                elementos.add(elementosLecturasHasta.get(indiceLecturasHasta++));
                elementos.add(elementosLecturasHasta.get(indiceLecturasHasta++));
                elementos.add(elementosLecturasHasta.get(indiceLecturasHasta++));
                totalIntegrador++;
            }

            System.out.println("<" + 0 + ">Tarifa del cliente          " + elementos.get(0));
            System.out.println("Integradores Totales        " + totalIntegrador + "\n\n");

            int indiceElementos = 1;
            for (int i = 0; i < totalIntegrador; i++) {
                System.out.println("***********Datos de integrador(" + (i + 1) + ")************");
                System.out.println("<" + (indiceElementos) + ">Magnitud y códigoPeriodo    " + elementos.get(indiceElementos++));
                System.out.println("<" + (indiceElementos) + ">Consumo calculado           " + elementos.get(indiceElementos++));
                System.out.println("<" + (indiceElementos) + ">Fecha desde                 " + elementos.get(indiceElementos++));
                System.out.println("<" + (indiceElementos) + ">Procedencia                 " + elementos.get(indiceElementos++));
                System.out.println("<" + (indiceElementos) + ">Lectura                     " + elementos.get(indiceElementos++));
                System.out.println("<" + (indiceElementos) + ">Fecha hasta                 " + elementos.get(indiceElementos++));
                System.out.println("<" + (indiceElementos) + ">Procedencia                 " + elementos.get(indiceElementos++));
                System.out.println("<" + (indiceElementos) + ">Lectura                     " + elementos.get(indiceElementos++));
                System.out.println("***********Fin Datos de integrador(" + (i + 1) + ")************\n");
            }

            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    //Datos AE
    private List<Double> LeerIntegradorAEConsumoXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          (Integrador)Datos de consumo AE");
        System.out.println("Obtiene 12 posibles valores, los organiza en 6 elementos correspondientes y los suma");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = Arrays.asList(new Double[7]);
        elementos.set(0, 0.0);
        elementos.set(1, 0.0);
        elementos.set(2, 0.0);
        elementos.set(3, 0.0);
        elementos.set(4, 0.0);
        elementos.set(5, 0.0);
        elementos.set(6, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("Integrador");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("ConsumoCalculado".equals(childNode.getNodeName())) {

                        Node sibling = childNode.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }
                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }
                        String defPeriodo = sibling3.getTextContent().substring(sibling3.getTextContent().length() - 1);

                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        //System.out.println(sibling4.getNodeName() + " " + sibling4.getTextContent());
                        if ("AE".equals(sibling4.getTextContent().substring(0, 2)) && !"0".equals(defPeriodo)) {
                            switch (defPeriodo) {
                                case "1":
                                    elementos.set(0, elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "2":
                                    elementos.set(1, elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "3":
                                    elementos.set(2, elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "4":
                                    elementos.set(3, elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "5":
                                    elementos.set(4, elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "6":
                                    elementos.set(5, elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                default:
                                    System.out.println("No se encontro el codigo de periodo valido");
                            }
                        }

                    }
                }
            }
            int indice = 0;
            for (Double elemento : elementos) {
                System.out.println("<" + (indice++) + ">" + "Consumo Calculado AE" + indice + "        " + elemento);
                if (indice == 6) {
                    break;
                }
            }
            indice = 0;

            for (int i = 0; i < 6; i++) {
                elementos.set(6, elementos.get(i) + elementos.get(6));
            }

            System.out.println("<6>Suma                         " + elementos.get(6));

            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerIntegradorAELecturaDesdeXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          (Integrador)Lectura Desde AE");
        System.out.println("Obtiene 12 lecturas  y las suma en 6 correspondientes");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = Arrays.asList(new Double[6]);
        elementos.set(0, 0.0);
        elementos.set(1, 0.0);
        elementos.set(2, 0.0);
        elementos.set(3, 0.0);
        elementos.set(4, 0.0);
        elementos.set(5, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("LecturaDesde");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("Lectura".equals(childNode.getNodeName())) {

                        Node padre = childNode.getParentNode();
                        while (null != childNode && childNode.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getParentNode();
                        }

                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }

                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }
                        String defPeriodo = sibling4.getTextContent().substring(sibling4.getTextContent().length() - 1);

                        Node sibling5 = sibling4.getPreviousSibling();
                        while (null != sibling5 && sibling5.getNodeType() != Node.ELEMENT_NODE) {
                            sibling5 = sibling5.getPreviousSibling();
                        }

                        //System.out.println(sibling4.getNodeName() + " " + sibling4.getTextContent());
                        if ("AE".equals(sibling5.getTextContent().substring(0, 2)) && !"0".equals(defPeriodo)) {
                            switch (defPeriodo) {
                                case "1":
                                    elementos.set(0, elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "2":
                                    elementos.set(1, elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "3":
                                    elementos.set(2, elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "4":
                                    elementos.set(3, elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "5":
                                    elementos.set(4, elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "6":
                                    elementos.set(5, elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                default:
                                    System.out.println("No se encontro el codigo de periodo valido");
                            }
                        }

                    }
                }
            }
            int indice = 0;
            for (Double elemento : elementos) {
                System.out.println("<" + indice + ">" + "Lectura Desde AE " + (++indice) + "      " + elemento);
            }
            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerIntegradorAELecturaHastaXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          (Integrador)Lectura Hasta AE");
        System.out.println("Obtiene 12 lecturas  y las suma en 6 correspondientes");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = Arrays.asList(new Double[6]);
        elementos.set(0, 0.0);
        elementos.set(1, 0.0);
        elementos.set(2, 0.0);
        elementos.set(3, 0.0);
        elementos.set(4, 0.0);
        elementos.set(5, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("LecturaHasta");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);

                    if ("Lectura".equals(childNode.getNodeName())) {

                        Node padre1 = childNode.getParentNode();
                        while (null != childNode && childNode.getNodeType() != Node.ELEMENT_NODE) {
                            padre1 = padre1.getParentNode();
                        }

                        Node padre = padre1.getPreviousSibling();
                        while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getPreviousSibling();
                        }

                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }

                        //Codigo Periodo
                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        String defPeriodo = sibling4.getTextContent().substring(sibling4.getTextContent().length() - 1);

                        //Magnitud
                        Node sibling5 = sibling4.getPreviousSibling();
                        while (null != sibling5 && sibling5.getNodeType() != Node.ELEMENT_NODE) {
                            sibling5 = sibling5.getPreviousSibling();
                        }

                        if ("AE".equals(sibling5.getTextContent().trim().substring(0, 2)) && !"0".equals(defPeriodo)) {
                            switch (defPeriodo) {
                                case "1":
                                    elementos.set(0, elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "2":
                                    elementos.set(1, elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "3":
                                    elementos.set(2, elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "4":
                                    elementos.set(3, elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "5":
                                    elementos.set(4, elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "6":
                                    elementos.set(5, elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                default:
                                    System.out.println("No se encontro el codigo de periodo valido");
                            }
                        }

                    }
                }
            }
            int indice = 0;
            for (Double elemento : elementos) {
                System.out.println("<" + indice + ">" + "Lectura Hasta AE " + (indice++ + 1) + "    " + elemento);
            }
            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Integer> LeerIntegradorAEProcedenciaDesdeXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          (Integrador)Procedencia Desde AE");
        System.out.println("------------------------------------------------------------------------------");

        List<Integer> elementos = Arrays.asList(new Integer[6]);
        elementos.set(0, 0);
        elementos.set(1, 0);
        elementos.set(2, 0);
        elementos.set(3, 0);
        elementos.set(4, 0);
        elementos.set(5, 0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("LecturaDesde");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("Procedencia".equals(childNode.getNodeName())) {

                        Node padre = childNode.getParentNode();
                        while (null != childNode && childNode.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getParentNode();
                        }

                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }

                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        String defPeriodo = sibling4.getTextContent().substring(sibling4.getTextContent().length() - 1);

                        Node sibling5 = sibling4.getPreviousSibling();
                        while (null != sibling5 && sibling5.getNodeType() != Node.ELEMENT_NODE) {
                            sibling5 = sibling5.getPreviousSibling();
                        }

                        //System.out.println(sibling4.getNodeName() + " " + sibling4.getTextContent());
                        if ("AE".equals(sibling5.getTextContent().substring(0, 2)) && !"0".equals(defPeriodo)) {
                            switch (defPeriodo) {
                                case "1":
                                    elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                case "2":
                                    elementos.set(1, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                case "3":
                                    elementos.set(2, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                case "4":
                                    elementos.set(3, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                case "5":
                                    elementos.set(4, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                case "6":
                                    elementos.set(5, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                default:
                                    System.out.println("No se encontro el codigo de periodo valido");
                            }
                        }

                    }
                }
            }
            /*
            int indice = 0;
            for (Integer elemento : elementos) {
                System.out.println("<" + indice + ">" + "Procedencia Desde AE " + (++indice) + "      " + elemento);
            }*/

            System.out.println("<0> Procedencia Desde AE " + elementos.get(0));
            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Integer> LeerIntegradorAEProcedenciaHastaXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          (Integrador)Procedencia Hasta AE");
        System.out.println("------------------------------------------------------------------------------");

        List<Integer> elementos = Arrays.asList(new Integer[6]);
        elementos.set(0, 0);
        elementos.set(1, 0);
        elementos.set(2, 0);
        elementos.set(3, 0);
        elementos.set(4, 0);
        elementos.set(5, 0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("LecturaHasta");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);

                    if ("Procedencia".equals(childNode.getNodeName())) {

                        Node padre1 = childNode.getParentNode();
                        while (null != childNode && childNode.getNodeType() != Node.ELEMENT_NODE) {
                            padre1 = padre1.getParentNode();
                        }

                        Node padre = padre1.getPreviousSibling();
                        while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getPreviousSibling();
                        }

                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }

                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        String defPeriodo = sibling4.getTextContent().substring(sibling4.getTextContent().length() - 1);

                        Node sibling5 = sibling4.getPreviousSibling();
                        while (null != sibling5 && sibling5.getNodeType() != Node.ELEMENT_NODE) {
                            sibling5 = sibling5.getPreviousSibling();
                        }

                        //System.out.println(sibling4.getNodeName() + " " + sibling4.getTextContent());
                        if ("AE".equals(sibling5.getTextContent().substring(0, 2)) && !"0".equals(defPeriodo)) {
                            switch (defPeriodo) {
                                case "1":
                                    elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                case "2":
                                    elementos.set(1, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                case "3":
                                    elementos.set(2, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                case "4":
                                    elementos.set(3, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                case "5":
                                    elementos.set(4, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                case "6":
                                    elementos.set(5, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    break;
                                default:
                                    System.out.println("No se encontro el codigo de periodo valido");
                            }
                        }

                    }
                }
            }
            /*
            int indice = 0;
            for (Integer elemento : elementos) {
                System.out.println("<" + indice + ">" + "Procedencia Hasta AE " + (indice++ + 1) + "    " + elemento);
            }*/
            System.out.println("<0> Procedencia Hasta AE " + elementos.get(0));
            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    //Reactiva
    private List<Double> LeerIntegradorRConsumoXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          (Integrador)Datos de consumo R (Reactiva)");
        System.out.println("Obtiene 12 posibles datos, los organiza en 6 correspondientes y los suma");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = Arrays.asList(new Double[7]);
        elementos.set(0, 0.0);
        elementos.set(1, 0.0);
        elementos.set(2, 0.0);
        elementos.set(3, 0.0);
        elementos.set(4, 0.0);
        elementos.set(5, 0.0);
        elementos.set(6, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("Integrador");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("ConsumoCalculado".equals(childNode.getNodeName())) {

                        Node sibling = childNode.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }
                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }
                        String defPeriodo = sibling3.getTextContent().substring(sibling3.getTextContent().length() - 1);

                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        //System.out.println(sibling4.getNodeName() + " " + sibling4.getTextContent());
                        if ("R".equals(sibling4.getTextContent().substring(0, 1)) && !"0".equals(defPeriodo)) {
                            switch (defPeriodo) {
                                case "1":
                                    elementos.set(0, elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "2":
                                    elementos.set(1, elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "3":
                                    elementos.set(2, elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "4":
                                    elementos.set(3, elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "5":
                                    elementos.set(4, elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "6":
                                    elementos.set(5, elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                default:
                                    System.out.println("No se encontro el codigo de periodo valido");
                            }
                        }

                    }
                }
            }
            int indice = 0;
            for (Double elemento : elementos) {
                System.out.println("<" + (indice++) + ">" + "Consumo Calculado R" + indice + "        " + elemento);
                if (indice == 6) {
                    break;
                }
            }
            indice = 0;

            for (int i = 0; i < 6; i++) {
                elementos.set(6, elementos.get(i) + elementos.get(6));
            }

            System.out.println("<6>Suma                        " + elementos.get(6));

            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerIntegradorRLecturaDesdeXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          (Integrador)Lectura Desde R");
        System.out.println("Obtiene 12 posibles valores, los organiza en 6 elementos correspondientes y los suma");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = Arrays.asList(new Double[6]);
        elementos.set(0, 0.0);
        elementos.set(1, 0.0);
        elementos.set(2, 0.0);
        elementos.set(3, 0.0);
        elementos.set(4, 0.0);
        elementos.set(5, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("LecturaDesde");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("Lectura".equals(childNode.getNodeName())) {

                        Node padre = childNode.getParentNode();
                        while (null != childNode && childNode.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getParentNode();
                        }

                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }

                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }
                        String defPeriodo = sibling4.getTextContent().substring(sibling4.getTextContent().length() - 1);

                        Node sibling5 = sibling4.getPreviousSibling();
                        while (null != sibling5 && sibling5.getNodeType() != Node.ELEMENT_NODE) {
                            sibling5 = sibling5.getPreviousSibling();
                        }

                        //System.out.println(sibling4.getNodeName() + " " + sibling4.getTextContent());
                        if ("R".equals(sibling5.getTextContent().substring(0, 1)) && !"0".equals(defPeriodo)) {
                            switch (defPeriodo) {
                                case "1":
                                    elementos.set(0, elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "2":
                                    elementos.set(1, elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "3":
                                    elementos.set(2, elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "4":
                                    elementos.set(3, elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "5":
                                    elementos.set(4, elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "6":
                                    elementos.set(5, elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                default:
                                    System.out.println("No se encontro el codigo de periodo valido");
                            }
                        }

                    }
                }
            }
            int indice = 0;
            for (Double elemento : elementos) {
                System.out.println("<" + indice + ">" + "Lectura Desde R" + (++indice) + "      " + elemento);
            }
            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerIntegradorRLecturaHastaXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          (Integrador)Lectura Hasta R");
        System.out.println("Obtiene 12 posibles valores, los organiza en 6 elementos correspondientes y los suma");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = Arrays.asList(new Double[6]);
        elementos.set(0, 0.0);
        elementos.set(1, 0.0);
        elementos.set(2, 0.0);
        elementos.set(3, 0.0);
        elementos.set(4, 0.0);
        elementos.set(5, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("LecturaHasta");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);

                    if ("Lectura".equals(childNode.getNodeName())) {

                        Node padre1 = childNode.getParentNode();
                        while (null != childNode && childNode.getNodeType() != Node.ELEMENT_NODE) {
                            padre1 = padre1.getParentNode();
                        }

                        Node padre = padre1.getPreviousSibling();
                        while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getPreviousSibling();
                        }

                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }

                        //Codigo Periodo
                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        String defPeriodo = sibling4.getTextContent().substring(sibling4.getTextContent().length() - 1);
                        //Magnitud
                        Node sibling5 = sibling4.getPreviousSibling();
                        while (null != sibling5 && sibling5.getNodeType() != Node.ELEMENT_NODE) {
                            sibling5 = sibling5.getPreviousSibling();
                        }

                        if ("R".equals(sibling5.getTextContent().substring(0, 1)) && !"0".equals(defPeriodo)) {
                            switch (defPeriodo) {
                                case "1":
                                    elementos.set(0, elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "2":
                                    elementos.set(1, elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "3":
                                    elementos.set(2, elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "4":
                                    elementos.set(3, elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "5":
                                    elementos.set(4, elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "6":
                                    elementos.set(5, elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                default:
                                    System.out.println("No se encontro el codigo de periodo valido");
                            }
                        }

                    }
                }
            }
            int indice = 0;
            for (Double elemento : elementos) {
                System.out.println("<" + indice + ">" + "Lectura Hasta R" + (++indice) + "    " + elemento);
            }
            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private ArrayList<String> ReactivaImporteTotal(String ruta) {

        ArrayList<String> elementos = new ArrayList<>(1);
        elementos.add(0, "0.0");

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowListPeriodo = documentoXml.getElementsByTagName("EnergiaReactiva");

            int indicePeriodo = 0;
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if ("ImporteTotalEnergiaReactiva".equals(childNode.getNodeName())) {
                        elementos.set(indicePeriodo++, String.valueOf(Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim())));
                    }
                }
            }
            indicePeriodo = 0;

            System.out.println(Cadenas.LINEA + "Rea-ImporteTotal" + elementos + Cadenas.LINEA);

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    //Datos PM
    private List<Double> LeerIntegradorPMConsumoXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          (Integrador)Datos de consumo PM");
        System.out.println("Obtiene 12 posibles datos, los organiza en 6 correspondientes y los suma");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = Arrays.asList(new Double[7]);
        elementos.set(0, 0.0);
        elementos.set(1, 0.0);
        elementos.set(2, 0.0);
        elementos.set(3, 0.0);
        elementos.set(4, 0.0);
        elementos.set(5, 0.0);
        elementos.set(6, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("Integrador");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("ConsumoCalculado".equals(childNode.getNodeName())) {

                        Node sibling = childNode.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }
                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }
                        String defPeriodo = sibling3.getTextContent().substring(sibling3.getTextContent().length() - 1);

                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        //System.out.println(sibling4.getNodeName() + " " + sibling4.getTextContent());
                        if ("PM".equals(sibling4.getTextContent().substring(0, 2)) && !"0".equals(defPeriodo)) {
                            switch (defPeriodo) {
                                case "1":
                                    elementos.set(0, elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "2":
                                    elementos.set(1, elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "3":
                                    elementos.set(2, elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "4":
                                    elementos.set(3, elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "5":
                                    elementos.set(4, elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "6":
                                    elementos.set(5, elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                default:
                                    System.out.println("No se encontro el codigo de periodo valido");
                            }
                        }

                    }
                }
            }
            int indice = 0;
            for (Double elemento : elementos) {
                System.out.println("<" + (indice++) + ">" + "Consumo Calculado PM" + indice + "        " + elemento);
                if (indice == 6) {
                    break;
                }
            }
            indice = 0;

            for (int i = 0; i < 6; i++) {
                elementos.set(6, elementos.get(i) + elementos.get(6));
            }

            System.out.println("<6>Suma                         " + elementos.get(6));

            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    private List<Double> LeerIntegradorPMLecturaHastaXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          (Integrador)Lectura Hasta PM");
        System.out.println("Obtiene 12 posibles datos, los organiza en 6 correspondientes");
        System.out.println("------------------------------------------------------------------------------");

        List<Double> elementos = Arrays.asList(new Double[6]);
        elementos.set(0, 0.0);
        elementos.set(1, 0.0);
        elementos.set(2, 0.0);
        elementos.set(3, 0.0);
        elementos.set(4, 0.0);
        elementos.set(5, 0.0);

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre brindado
            NodeList flowList = documentoXml.getElementsByTagName("LecturaHasta");

            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);

                    if ("Lectura".equals(childNode.getNodeName())) {

                        Node padre1 = childNode.getParentNode();
                        while (null != childNode && childNode.getNodeType() != Node.ELEMENT_NODE) {
                            padre1 = padre1.getParentNode();
                        }

                        Node padre = padre1.getPreviousSibling();
                        while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getPreviousSibling();
                        }

                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }

                        //Codigo Periodo
                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        String defPeriodo = sibling4.getTextContent().substring(sibling4.getTextContent().length() - 1);

                        //Magnitud
                        Node sibling5 = sibling4.getPreviousSibling();
                        while (null != sibling5 && sibling5.getNodeType() != Node.ELEMENT_NODE) {
                            sibling5 = sibling5.getPreviousSibling();
                        }

                        if ("PM".equals(sibling5.getTextContent().substring(0, 2)) && !"0".equals(defPeriodo)) {
                            switch (defPeriodo) {
                                case "1":
                                    elementos.set(0, elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "2":
                                    elementos.set(1, elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "3":
                                    elementos.set(2, elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "4":
                                    elementos.set(3, elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "5":
                                    elementos.set(4, elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                case "6":
                                    elementos.set(5, elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    break;
                                default:
                                    System.out.println("No se encontro el codigo de periodo valido");
                            }
                        }

                    }
                }
            }
            int indice = 0;
            for (Double elemento : elementos) {
                System.out.println("<" + indice + ">" + "Lectura Hasta PM" + (indice++ + 1) + "    " + elemento);
            }
            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    //Datos Pie
    private List<String> LeerFinDeRegistroXML(String ruta) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Nombre Nodo actual          RegistroFin");
        System.out.println("------------------------------------------------------------------------------");

        List<String> elementos = new ArrayList<>();
        elementos.add("0.0");
        elementos.add("0.0");
        elementos.add("0");
        elementos.add("No encontrada");
        elementos.add("No encontrada");
        elementos.add("No encontrada");

        try {
            //Indica el archivo que será tomado
            File archivoXml = new File(ruta);

            //Preparación del procesamiento del XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document documentoXml = builder.parse(archivoXml);
            documentoXml.getDocumentElement().normalize();

            //Recupera todos los elementos con el nombre de Cabecera
            NodeList flowList = documentoXml.getElementsByTagName("RegistroFin");

            int indice = 0;
            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childList = flowList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("ImporteTotal".equals(childNode.getNodeName())) {
                        elementos.set(0, childList.item(j).getTextContent().trim());
                    } else if ("SaldoTotalFacturacion".equals(childNode.getNodeName())) {
                        elementos.set(1, childList.item(j).getTextContent().trim());
                    } else if ("TotalRecibos".equals(childNode.getNodeName())) {
                        elementos.set(2, childList.item(j).getTextContent().trim());
                    } else if ("FechaValor".equals(childNode.getNodeName())) {
                        elementos.set(3, childList.item(j).getTextContent().trim());
                    } else if ("FechaLimitePago".equals(childNode.getNodeName())) {
                        elementos.set(4, childList.item(j).getTextContent().trim());
                    } else if ("IdRemesa".equals(childNode.getNodeName())) {
                        if (childList.item(j).getTextContent().trim().substring(0, 1).equals("0")) {
                            elementos.set(5, String.valueOf(Integer.parseInt(childList.item(j).getTextContent().trim())));
                        } else {
                            elementos.set(5, childList.item(j).getTextContent().trim());
                        }

                    }
                }
            }

            System.out.println("<" + 0 + ">ImporteTotal              " + elementos.get(0));
            System.out.println("<" + 1 + ">SaldoTotalFacturación     " + elementos.get(1));
            System.out.println("<" + 2 + ">TotalRecibos              " + elementos.get(2));
            System.out.println("<" + 3 + ">FechaValor                " + elementos.get(3));
            System.out.println("<" + 4 + ">FechaLimitePago           " + elementos.get(4));
            System.out.println("<" + 5 + ">IdRemesa                  " + elementos.get(5));

            System.out.println("------------------------------------------------------------------------------");

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Error " + e.getMessage());
        }
        return elementos;
    }

    /*--------------------------Utilidades---------------------------*/
    private void agregarError(String codError) {
        if (this.errores.equals("0")) {
            this.errores = codError;
        } else {
            this.errores += ", " + codError;
        }
    }

    private void agregarComentario(String comentario) {
        this.comentarios.set(0, comentarios.get(0) + comentario + "<br/>");
    }
}
