package web;

import datos.ClienteDao;
import datos.ContenidoXmlDao;
import dominio.Cliente;
import dominio.componentesxml.*;
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ControlXML")
@MultipartConfig
public class ControlXml extends HttpServlet {

    String mensaje = "Por favor, indique cuál es la actividad que desea realizar";
    String titulo = "Peajes";
    String tituloTabla = "Registros XML";
    String nombreServlet = "ControlXML";
    String icono = "<i class='far fa-address-card'></i>";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String a = request.getParameter("accion");
        if (a != null) {
            switch (a) {
                case "ver":
                    this.ver(request, response);
                    break;
                default:
                    this.inicio(request, response);
                    break;
            }
        } else {
            this.inicio(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filtro = request.getParameter("filtro").trim().toLowerCase();
        String valor = request.getParameter("valor").trim();
        if (filtro == null || filtro.isEmpty()) {
            mensaje = "Filtro no proporcionado";
            this.inicio(request, response);
        } else {
            if (valor == null || valor.isEmpty()) {
                mensaje = "<Strong>Debe proporcionar un valor</Strong> para su búsqueda";
                this.inicio(request, response);
            } else {
                List<DocumentoXml> documentos = new ArrayList<>();
                titulo = "Registros XML por " + filtro;
                switch (filtro) {
                    case "cliente":
                        documentos = new ContenidoXmlDao().buscarIdCliente(valor, 0);
                        break;
                    case "remesa":
                        documentos = new ContenidoXmlDao().buscarRemesa(valor, 0);
                        break;
                    default:
                        mensaje = "Filtro no soportado (" + filtro + ")";
                        this.inicio(request, response);
                        break;
                }

                if (documentos.isEmpty()) {
                    this.sinResultado(request, valor, filtro);
                } else {
                    this.conResultado(request, documentos, valor, titulo, filtro);
                }

                request.getRequestDispatcher("/WEB-INF/paginas/cliente/xml/controlxml.jsp").forward(request, response);
                this.reiniciarVariables();
            }
        }
    }

    private void inicio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<DocumentoXml> documentos = new ContenidoXmlDao().listar(0);
        if (documentos.isEmpty()) {
            request.setAttribute("mensajeRegistro", "Use el boton de <Strong>procesar</Strong> para tener su primer registro xml.");
            request.setAttribute("tituloTabla", "Deben estar por algún lado.");
            request.setAttribute("tituloPagina", titulo);
            request.setAttribute("titulo", icono + " " + titulo);
            request.setAttribute("contenidoVisible", "no");
            request.setAttribute("botonVisible", "no");
        } else {
            request.setAttribute("documentos", documentos);
            request.setAttribute("totalRegistros", documentos.size());
            request.setAttribute("tituloPagina", titulo);
            request.setAttribute("titulo", icono + " " + titulo);
            request.setAttribute("tituloTabla", tituloTabla);
            request.setAttribute("mensajeRegistro", mensaje);
            request.setAttribute("nombreServlet", this.nombreServlet);
        }
        request.getRequestDispatcher("/WEB-INF/paginas/cliente/xml/controlxml.jsp").forward(request, response);
        this.reiniciarVariables();
    }

    private void ver(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cod = request.getParameter("cod");
        if (cod == null || cod.isEmpty() || cod.length() < 5) {
            this.mensaje = "No se ingresó una factura válida.";
            this.inicio(request, response);
        } else {
            DocumentoXml documento = new ContenidoXmlDao().buscarCodFiscal(cod, 0);
            request.setAttribute("documento", documento);
            request.setAttribute("tituloPagina", "Registro XML");
            request.setAttribute("titulo", icono + " Registro XML");
            if (documento == null) {
                request.setAttribute("mensajeRegistro", "!Vaya¡ No se encontraron datos de la factura (" + request.getParameter("cod") + ")");
            } else {
                request.setAttribute("mensajeRegistro", "Se encontraron estos datos de la factura (" + documento.getDatosGeneralesFactura().getCodigoFiscalFactura() + ")");
            }
            request.getRequestDispatcher("/WEB-INF/paginas/cliente/xml/controlxml_ver_registro.jsp").forward(request, response);
            this.reiniciarVariables();
        }
    }

    private DocumentoXml resumen(List<DocumentoXml> documentos) {

        DocumentoXml documentoXml = new DocumentoXml();
        Cliente cliente = new Cliente();
        cliente.setCups(documentos.get(0).getCliente().getCups());

        double impPot = 0.0;
        double impEneAct = 0.0;
        double impFac = 0.0;

        for (DocumentoXml documento : documentos) {
            impPot += documento.getDatosPotenciaImporteTotal().getImporteTotal();
            impEneAct += documento.getDatosEnergiaActivaImporteTotal().getImporteTotal();
            System.out.println(impEneAct);
            impFac += documento.getDatosFinDeRegistro().getImporteTotal();
        }

        documentoXml.setCliente(new ClienteDao().encontrarCups(cliente));
        documentoXml.setDatosPotenciaImporteTotal(new DatosPotenciaImporteTotal(impPot));
        documentoXml.setDatosEnergiaActivaImporteTotal(new DatosEnergiaActivaImporteTotal(impEneAct));
        documentoXml.setDatosFinDeRegistro(new DatosFinDeRegistro(impFac));

        return documentoXml;
    }

    private void reiniciarVariables() {
        this.mensaje = "Por favor, indique cuál es la actividad que desea realizar";
        this.titulo = "Peajes";
        this.tituloTabla = "Registros XML";
    }

    private void sinResultado(HttpServletRequest request, String valor, String filtro) {
        request.setAttribute("mensajeRegistro", "¡Vaya! no hemos encontrado ningún registro con el valor proporcionado <Strong>(" + valor + ")</Strong> con el filtro de <Strong>" + filtro + "</Strong>");
        request.setAttribute("titulo", "<i class=\"fas fa-heart-broken\"></i> Oops!...");
        request.setAttribute("tituloTabla", "Nada por aquí :(");
        request.setAttribute("contenidoVisible", "no");
        request.setAttribute("botonVisible", "si");
        request.setAttribute("nombreServlet", this.nombreServlet);
    }

    private void conResultado(HttpServletRequest request, List<DocumentoXml> documentos, String valor, String titulo, String filtro) {
        request.setAttribute("documentos", documentos);
        request.setAttribute("totalRegistros", documentos.size());
        request.setAttribute("documentoResumen", this.resumen(documentos));
        request.setAttribute("tituloPagina", titulo);
        request.setAttribute("titulo", icono + " " + titulo);
        request.setAttribute("tituloTabla", "Registros encontrados");
        request.setAttribute("nombreServlet", this.nombreServlet);
        request.setAttribute("mensajeRegistro", "Estos son los registros que se encontraron con el valor <Strong>(" + valor + ")</Strong> con el filtro <Strong>" + filtro + "</Strong>");
    }
}
