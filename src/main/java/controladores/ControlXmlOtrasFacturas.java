package controladores;

import datos.dao.OtrasFacturasDaoImp;
import datos.interfaces.ClienteDao;
import dominio.Cliente;
import dominio.componentesxml.*;
import dominio.otrasfactuas.DocumentoOtraFactura;
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;

@WebServlet("/ControlXMLOtrasFacturas")
@MultipartConfig
public class ControlXmlOtrasFacturas extends HttpServlet {

    @Autowired
    ClienteDao clienteDao;

    String mensaje = "Por favor, indique cuál es la actividad que desea realizar";
    String titulo = "Otras facturas";
    String tituloTabla = "Facturas";
    String nombreServlet = "ControlXMLOtrasFacturas";
    String icono = "<i class='fas fa-address-card'></i> ";

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
                List<DocumentoOtraFactura> documentos = new ArrayList<>();
                titulo = "XML por " + filtro;
                switch (filtro) {
                    case "cliente":
                        //documentos = new OtrasFacturasDao().buscarByIdCliente(valor);
                        break;
                    case "remesa":
                        //documentos = new OtrasFacturasDao().buscarByRemesa(valor);
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

                request.getRequestDispatcher("/WEB-INF/paginas/cliente/xml/controlxml_otras_facturas.jsp").forward(request, response);
                this.reiniciarVariables();
            }
        }
    }

    private void inicio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*List<DocumentoOtraFactura> documentos = new OtrasFacturasDao().listar();
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
        request.getRequestDispatcher("/WEB-INF/paginas/cliente/xml/controlxml_otras_facturas.jsp").forward(request, response);
        this.reiniciarVariables();
         */
    }

    private void ver(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
        String cod = request.getParameter("cod");
        if (cod == null || cod.isEmpty() || cod.length() < 5) {
            this.mensaje = "No se ingresó una factura válida.";
            this.inicio(request, response);
        } else {
            DocumentoOtraFactura documento = new OtrasFacturasDao().buscarByCodFiscal(cod);
            request.setAttribute("documento", documento);
            request.setAttribute("tituloPagina", "Otros XML");
            request.setAttribute("titulo", icono + "Otros XML");
            if (documento == null) {
                request.setAttribute("mensajeRegistro", "!Vaya¡ No se encontraron datos de la factura (" + cod + ")");
            } else {
                request.setAttribute("mensajeRegistro", "Se encontraron estos datos de la factura (" + documento.getDatosGeneralesFactura().getCodFisFac() + ")");
            }
            request.getRequestDispatcher("/WEB-INF/paginas/cliente/xml/controlxml_ver_registro_otras_facturas.jsp").forward(request, response);
            this.reiniciarVariables();
        }
*/
    }

    private DocumentoOtraFactura resumen(List<DocumentoOtraFactura> documentos) {

        DocumentoOtraFactura documentoXml = documentos.get(0);
        Cliente cliente = new Cliente();
        cliente.setCups(documentos.get(0).getCliente().getCups());

        double impPot = 0.0;

        for (DocumentoOtraFactura documento : documentos) {
            impPot += documento.getDatosGeneralesFactura().getImpTotFac();
        }

        documentoXml.getDatosGeneralesFactura().setImpTotFac(impPot);
        //documentoXml.setCliente(this.clienteDao.encontrarCups(cliente));
        return documentoXml;
    }

    private void reiniciarVariables() {
        this.mensaje = "Por favor, indique cuál es la actividad que desea realizar";
        this.titulo = "Otras Facturas";
        this.tituloTabla = "Facturas XML";
    }

    private void sinResultado(HttpServletRequest request, String valor, String filtro) {
        request.setAttribute("mensajeRegistro", "¡Vaya! no hemos encontrado ningún registro con el valor proporcionado <Strong>(" + valor + ")</Strong> con el filtro de <Strong>" + filtro + "</Strong>");
        request.setAttribute("titulo", "<i class=\"fas fa-heart-broken\"></i> Oops!...");
        request.setAttribute("tituloTabla", "Nada por aquí :(");
        request.setAttribute("contenidoVisible", "no");
        request.setAttribute("botonVisible", "si");
        request.setAttribute("nombreServlet", this.nombreServlet);
    }

    private void conResultado(HttpServletRequest request, List<DocumentoOtraFactura> documentos, String valor, String titulo, String filtro) {
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
