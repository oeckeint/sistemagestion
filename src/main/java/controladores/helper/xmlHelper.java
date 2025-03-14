package controladores.helper;

import datos.entity.Cliente;
import datos.entity.EnergiaExcedentaria;
import datos.entity.Factura;
import datos.entity.OtraFactura;
import datos.entity.Peaje;
import datos.entity.reclamaciones.SubtipoReclamacion;
import datos.entity.reclamaciones.TipoReclamacion;
import datos.interfaces.ClienteService;
import datos.interfaces.CrudDao;
import dominio.componentesxml.*;
import dominio.componentesxml.reclamaciones.*;
import excepciones.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import excepciones.nodos.NoCoincidenLosNodosEsperadosException;
import excepciones.nodos.energiaexcedentaria.autoconsumo.ExisteMasDeUnAutoconsumoException;
import lombok.NonNull;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import utileria.documentos.NodosUtil;
import utileria.texto.Cadenas;
import datos.interfaces.DocumentoXmlService;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import utileria.StringHelper;
import utileria.xml;

@Component
public class xmlHelper {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(xmlHelper.class);
    private DocumentoXmlService contenidoXmlService;
    @Autowired
    protected ClienteService clienteService;
    private ArrayList elementos;
    Document doc;
    StringBuilder errores;
    StringBuilder comentarios;
    protected String cups;
    protected String codFactura;
    private int empEmi;
    protected String tipoFactura;
    protected Cliente cliente;
    private String EmpresaEmisora;
    private String tarifaAtrFac;
    protected String nombreArchivo;
    private String codigoRemesa;
    protected boolean existeEnergiaExcedentaria;

    private Logger logger = Logger.getLogger(getClass().getName());

    public xmlHelper(){}

    public xmlHelper(Document doc, DocumentoXmlService contenidoXmlService, ClienteService clienteService) throws MasDeUnClienteEncontrado {
        this.doc = doc;
        this.contenidoXmlService = contenidoXmlService;
        this.clienteService = clienteService;
        this.iniciarVariables();
    }

    public xmlHelper(Document doc, DocumentoXmlService contenidoXmlService) {
        this.doc = doc;
        this.contenidoXmlService = contenidoXmlService;
        this.iniciarVariablesPagoRemesa();
    }

    /**
     * Constructor usado para CambiodeComercializador SinCambios
     *
     * @param doc
     * @param clienteService
     */
    public xmlHelper(Document doc, ClienteService clienteService) {
        this.errores = new StringBuilder("");
        this.comentarios = new StringBuilder("");
        this.doc = doc;
        this.clienteService = clienteService;
        this.iniciarVariablesCambioComercializador();
    }

    public boolean existeClientebyCups() throws MasDeUnClienteEncontrado, ClienteNoExisteException {
        this.cliente = clienteService.encontrarCups(this.cups);
        if (this.cliente != null) {
            return true;
        } else {
            throw new ClienteNoExisteException(cups);
        }
    }

    /**
     * Realiza el proceso de pago para los Peajes
     * @throws PeajeMasDeUnRegistroException 
     */
    public void procesarRemesaPagoPeaje() throws PeajeMasDeUnRegistroException {
        HashMap<String, String> elementos = new HashMap<String, String>();
        NodeList flowList = doc.getElementsByTagName("Factura");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "CodigoFiscalFactura":
                            elementos.put("codFisFac", childList.item(j).getTextContent().trim());
                            this.EmpresaEmisora = childList.item(j).getTextContent().trim();
                            break;
                        case "Importe":
                            elementos.put("Importe", childList.item(j).getTextContent().trim());
                            break;
                        case "Estado":
                            elementos.put("Estado", childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }

            try {
                Peaje peaje = (Peaje) this.contenidoXmlService.buscarByCodFiscal(elementos.get("codFisFac"));

                if (peaje.getEstadoPago() == 0) {
                    double importe = Double.parseDouble(elementos.get("Importe"));
                    if (importe != peaje.getImpTotFac()) {
                        utileria.ArchivoTexto.escribirError("contenido_xml. El importe total " + peaje.getImpTotFac() + " del registro con el codFisFac " + elementos.get("codFisFac") + " no coincide con el indicado en el archivo " + elementos.get("Importe") + " pero igual se proceso");
                    }
                    peaje.setRemesaPago(this.codigoRemesa);
                    peaje.setEstadoPago(Integer.parseInt(elementos.get("Estado")));
                    this.contenidoXmlService.actualizar(peaje);
                    logger.log(Level.INFO, ">>> xmlHelper=\"Se ha cambiado el estadoDePago = {0} con el codFisFac = {1}\"", new Object[]{elementos.get("Estado"), elementos.get("codFisFac")});
                } else {
                    utileria.ArchivoTexto.escribirError("contenido_xml. El registro con el codFisFac " + elementos.get("codFisFac") + " tiene el estado de " + peaje.getEstadoPago() + " por lo que no se pudo procesar");
                }
            } catch (RegistroVacioException e) {
                logger.log(Level.INFO, ">>> No se encontró un Peaje con {0}", elementos.get("codFisFac"));
                utileria.ArchivoTexto.escribirError("contenido_xml. No se encontró un registro con el codFisFac " + elementos.get("codFisFac"));
            }

        }
    }

    public void procesarRemesaPagoFactura() throws PeajeMasDeUnRegistroException {
        HashMap<String, String> elementos = new HashMap<String, String>();
        NodeList flowList = doc.getElementsByTagName("Factura");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "CodigoFiscalFactura":
                            elementos.put("codFisFac", childList.item(j).getTextContent().trim());
                            this.EmpresaEmisora = childList.item(j).getTextContent().trim();
                            break;
                        case "Importe":
                            elementos.put("Importe", childList.item(j).getTextContent().trim());
                            break;
                        case "Estado":
                            elementos.put("Estado", childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }

            try {
                Factura factura = (Factura) this.contenidoXmlService.buscarByCodFiscal(elementos.get("codFisFac"));
                if (factura.getEstadoPago() == 0) {
                    double importe = Double.parseDouble(elementos.get("Importe"));
                    if (importe != factura.getImpTotFac()) {
                        utileria.ArchivoTexto.escribirError("contenido_xml_factura. El importe total " + factura.getImpTotFac() + " del registro con el codFisFac " + elementos.get("codFisFac") + " no coincide con el indicado en el archivo " + elementos.get("Importe") + " pero igual se proceso");
                    }
                    factura.setRemesaPago(this.codigoRemesa);
                    factura.setEstadoPago(Integer.parseInt(elementos.get("Estado")));
                    this.contenidoXmlService.actualizar(factura);
                    logger.log(Level.INFO, ">>> xmlHelper=\"Se ha cambiado el estadoDePago = {0} con el codFisFac = {1}\"", new Object[]{elementos.get("Estado"), elementos.get("codFisFac")});
                } else {
                    utileria.ArchivoTexto.escribirError("contenido_xml_factura. El registro con el codFisFac " + elementos.get("codFisFac") + " tiene el estado de " + factura.getEstadoPago() + " por lo que no se pudo procesar");
                }
            } catch (RegistroVacioException e) {
                logger.log(Level.INFO, ">>> No se encontró una Factura con {0}", elementos.get("codFisFac"));
                utileria.ArchivoTexto.escribirError("contenido_xml. No se encontró un registro con el codFisFac " + elementos.get("codFisFac"));
            }
        }
    }

    public void procesarRemesaPagoOtraFactura() throws PeajeMasDeUnRegistroException {
        HashMap<String, String> elementos = new HashMap<String, String>();
        NodeList flowList = doc.getElementsByTagName("Factura");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "CodigoFiscalFactura":
                            elementos.put("codFisFac", childList.item(j).getTextContent().trim());
                            this.EmpresaEmisora = childList.item(j).getTextContent().trim();
                            break;
                        case "Importe":
                            elementos.put("Importe", childList.item(j).getTextContent().trim());
                            break;
                        case "Estado":
                            elementos.put("Estado", childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }

            try {
                OtraFactura factura = (OtraFactura) this.contenidoXmlService.buscarByCodFiscal(elementos.get("codFisFac"));
                if (factura.getEstadoPago() == 0) {
                    double importe = Double.parseDouble(elementos.get("Importe"));
                    if (importe != factura.getImpTotFac()) {
                        utileria.ArchivoTexto.escribirError("contenido_xml_otras_facturas. El importe total " + factura.getImpTotFac() + " del registro con el codFisFac " + elementos.get("codFisFac") + " no coincide con el indicado en el archivo " + elementos.get("Importe") + " pero igual se proceso");
                    }
                    factura.setRemesaPago(this.codigoRemesa);
                    factura.setEstadoPago(Integer.parseInt(elementos.get("Estado")));
                    this.contenidoXmlService.actualizar(factura);
                    logger.log(Level.INFO, ">>> xmlHelper=\"Se ha cambiado el estadoDePago = {0} con el codFisFac = {1}\"", new Object[]{elementos.get("Estado"), elementos.get("codFisFac")});
                } else {
                    utileria.ArchivoTexto.escribirError("contenido_xml_otras_facturas. El registro con el codFisFac " + elementos.get("codFisFac") + " tiene el estado de " + factura.getEstadoPago() + " por lo que no se pudo procesar");
                }
            } catch (RegistroVacioException e) {
                logger.log(Level.INFO, ">>> No se encontró OtraFactura con {0}", elementos.get("codFisFac"));
                utileria.ArchivoTexto.escribirError("contenido_xml_otras_facturas. No se encontró un registro con el codFisFac " + elementos.get("codFisFac"));
            }
        }
    }

    /*------------------------Obtencion de datos --------------------------------------*/
    protected DatosCabecera cabecera() {
        elementos = new ArrayList<String>(7);
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
                            this.EmpresaEmisora = childList.item(j).getTextContent().trim();
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
                            elementos.set(4, childList.item(j).getTextContent().trim());
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

        this.imprimirResultado("cabecera", elementos);
        return new DatosCabecera(elementos);
    }

    protected DatosCabeceraReclamacion cabeceraReclamacion() {
        elementos = new ArrayList<String>(8);
        elementos.add("0");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");

        NodeList flowList = doc.getElementsByTagName("CabeceraReclamacion");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "CodigoREEEmpresaEmisora":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            this.EmpresaEmisora = childList.item(j).getTextContent().trim();
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
                            elementos.set(4, childList.item(j).getTextContent().trim());
                            break;
                        case "FechaSolicitud":
                            elementos.set(5, childList.item(j).getTextContent().trim().substring(0, 10));
                            break;
                        case "CUPS":
                            elementos.set(6, childList.item(j).getTextContent().trim());
                            break;
                        case "SecuencialDeSolicitud":
                            elementos.set(7, childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        this.imprimirResultado("cabeceraReclamacion", elementos);
        return new DatosCabeceraReclamacion(elementos);
    }

    protected SolicitudReclamacion solicitudReclamacion() {
        elementos = new ArrayList<String>(1);
        elementos.add("");

        NodeList flowList = doc.getElementsByTagName("SolicitudReclamacion");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "Comentarios":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        this.imprimirResultado("datosInformacionReclamacion", elementos);
        return new SolicitudReclamacion(elementos);
    }

    protected RechazoReclamacion reclamacionRechazo() {
        elementos = new ArrayList<String>(3);
        elementos.add("");
        elementos.add("");
        elementos.add("");

        NodeList flowList = doc.getElementsByTagName("Rechazo");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "Secuencial":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            break;
                        case "CodigoMotivo":
                            elementos.set(1, childList.item(j).getTextContent().trim());
                            break;
                        case "Comentarios":
                            elementos.set(2, childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        this.imprimirResultado("reclamacionaRechazo", elementos);
        return new RechazoReclamacion(elementos);
    }

    protected DatosRetipificacion datosRetipificacion(CrudDao<TipoReclamacion> tipoReclamacionService, CrudDao<SubtipoReclamacion> subtipoReclamacionService) {
        TipoReclamacion tipoReclamacion = null;
        SubtipoReclamacion subtipoReclamacion = null;
        elementos = new ArrayList<String>(2);
        elementos.add("0");
        elementos.add("0");

        NodeList flowList = doc.getElementsByTagName("Retipificacion");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "Tipo":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            break;
                        case "Subtipo":
                            elementos.set(1, childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        tipoReclamacion = tipoReclamacionService.buscarId(Long.parseLong((String) elementos.get(0)));
        subtipoReclamacion = subtipoReclamacionService.buscarId(Long.parseLong((String) elementos.get(1)));


        this.imprimirResultado("datosRetipificacion", elementos);
        return new DatosRetipificacion(tipoReclamacion, subtipoReclamacion);
    }

    protected DatosInformacionReclamacion datosInformacionReclamacion(String nodoPadre) {
        elementos = new ArrayList<String>(1);
        elementos.add("");

        NodeList flowList = doc.getElementsByTagName(nodoPadre);
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "CodigoReclamacionDistribuidora":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        this.imprimirResultado("datosInformacionReclamacion", elementos);
        return new DatosInformacionReclamacion(elementos);
    }

    protected InformacionAdicionalReclamacion informacionAdicionalReclamacion() {
        elementos = new ArrayList<String>(1);
        elementos.add("");

        NodeList flowList = doc.getElementsByTagName("CierreReclamacion");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "Comentarios":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        this.imprimirResultado("informacionAdicionalReclamacion", elementos);
        return new InformacionAdicionalReclamacion(elementos);
    }

    protected DatosGeneralesFactura datosGenerales() {
        elementos = new ArrayList<String>(6);
        elementos.add("");
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
                        case "ImporteTotalFactura":
                            elementos.set(5, childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        this.imprimirResultado("datosGenerales", elementos);
        return new DatosGeneralesFactura(elementos);
    }
    
    protected DatosTerminoPotencia datosTerminoPotencia() {
        elementos = new ArrayList<String>(2);
        elementos.add("");
        elementos.add("");

        int indice = 0;
        boolean continuar = true;
        NodeList flowList = doc.getElementsByTagName("Periodo");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                	if (indice > 1) {
						continuar = false;
						this.agregarError("29");
                        break;
					}
                	
                    switch (childNode.getNodeName()) {
                        case "FechaDesdeFactura":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            indice++;
                            break;
                        case "FechaHastaFactura":
                            elementos.set(1, childList.item(j).getTextContent().trim());
                            indice++;
                            break;
                        default:
                            break;
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        this.imprimirResultado("datosTerminoPotencia", elementos);
        return new DatosTerminoPotencia(elementos);
    }

    protected DatosFacturaAtr datosFacturaAtr() throws MasDeUnClienteEncontrado, TarifaNoExisteException {
        elementos = new ArrayList<String>(7);
        elementos.add(0, "0");
        elementos.add(1, "0");
        elementos.add(2, "0");
        elementos.add(3, "0");
        elementos.add(4, "0");
        elementos.add(5, "0");
        elementos.add(6, "0");

        NodeList flowListFacturaATR = this.doc.getElementsByTagName("DatosFacturaATR");

        for (int i = 0; i < flowListFacturaATR.getLength(); i++) {
            NodeList childList = flowListFacturaATR.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "TarifaATRFact":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            this.tarifaAtrFac = String.valueOf(Integer.parseInt(elementos.get(0).toString()));
                            break;
                        case "ModoControlPotencia":
                            elementos.set(1, childList.item(j).getTextContent().trim());
                            break;
                        case "MarcaMedidaConPerdidas":
                            elementos.set(2, childList.item(j).getTextContent().trim());
                            break;
                        case "VAsTrafo":
                            elementos.set(3, childList.item(j).getTextContent().trim());
                            break;
                        case "PorcentajePerdidas":
                            elementos.set(4, childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        //Recupera todos los elementos con el nombre brindado
        NodeList flowList = this.doc.getElementsByTagName("Periodo");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("NumeroDias".equals(childNode.getNodeName())) {
                    elementos.set(5, childList.item(j).getTextContent().trim());
                }
            }
        }

        this.imprimirResultado("facturaAtr", elementos);
        this.validarTarifa();
        return new DatosFacturaAtr(elementos);
    }

    //Datos de potencia
    protected DatosPotenciaContratada potenciaContratada() {
        //Obtiene hasta 12 posibles valores y los ordena en 6 correspondientes, sumando 1+6, 2+7 ... 6+12
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        //Busqueda en el nodo
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Periodo");

        int indice = 0;
        boolean continuar = true;
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                Node childNode = childListLecturaHasta.item(j);
                if ("PotenciaContratada".equals(childNode.getNodeName())) {
                    if (indice > 11) {
                        continuar = false;
                        this.agregarError("1");
                        break;
                    } else if (indice > 5) {
                        int aux = indice++ - 6;
                        elementos.set(aux, (double) elementos.get(aux) + (Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()) / 1000));
                    } else {
                        elementos.set(indice++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()) / 1000);
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        this.imprimirResultado("potenciaContratada", elementos);
        return new DatosPotenciaContratada(elementos);
    }

    protected DatosPotenciaMaxDemandada potenciaDemandada() {

        //Obtiene hasta 12 posibles valores y los ordena en 6 correspondientes, sumando 1+6, 2+7 ... 6+12
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        //Recupera todos los elementos con el nombre brindado
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Periodo");

        int indice = 0;
        boolean continuar = true;
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                Node childNode = childListLecturaHasta.item(j);
                if ("PotenciaMaxDemandada".equals(childNode.getNodeName())) {
                    if (indice > 11) {
                        continuar = false;
                        this.agregarError("2");
                        break;
                    } else if (indice > 5) {
                        int aux = indice++ - 6;
                        elementos.set(aux, (double) elementos.get(aux) + (Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()) / 1000));
                    } else {
                        elementos.set(indice++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()) / 1000);
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        this.imprimirResultado("potenciaDemandada", elementos);
        return new DatosPotenciaMaxDemandada(elementos);
    }

    protected DatosPotenciaAFacturar potenciaAFacturar() {

        //Obtiene 6 posibles datos, organizandoles en 3 el valor máximo entre dos medidas (1:4, 2:5, 3:6)
        elementos = new ArrayList<Double>(3);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        //Busqueda en el nodo
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Periodo");

        DatosPotenciaPrecio datosPotenciaPrecio = this.potenciaPrecio();
        double suma = 0;

        int indice = 0;
        boolean continuar = true;
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                Node childNode = childListLecturaHasta.item(j);
                if ("PotenciaAFacturar".equals(childNode.getNodeName())) {
                    if (indice > 5) {
                        continuar = false;
                        this.agregarError("3");
                        break;
                    } else {
                        if (indice > 2) {
                            int aux = indice++ - 3;
                            elementos.set(aux, (double) elementos.get(aux) + (Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim())));
                        } else {
                            elementos.set(indice++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                        }

                        switch (indice) {
                            case 1:
                                suma += (datosPotenciaPrecio.getP1() * Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                                break;
                            case 2:
                                suma += (datosPotenciaPrecio.getP2() * Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                                break;
                            case 3:
                                suma += (datosPotenciaPrecio.getP3() * Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                                break;
                            case 4:
                                suma += (datosPotenciaPrecio.getP4() * Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                                break;
                            case 5:
                                suma += (datosPotenciaPrecio.getP5() * Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                                break;
                            case 6:
                                suma += (datosPotenciaPrecio.getP6() * Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        this.imprimirResultado("potenciaAFacturar", elementos);
        this.verificarPotenciaAFacturar(suma);
        return new DatosPotenciaAFacturar(elementos);
    }

    protected DatosPotenciaPrecio potenciaPrecio() {

        //Obtiene hasta 12 posibles valores y los ordena en 6 correspondientes, sumando 1+6, 2+7 ... 6+12
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        //Búsqueda en el nodo
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Periodo");

        int indice = 0;
        boolean continuar = true;
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                Node childNode = childListLecturaHasta.item(j);
                if ("PrecioPotencia".equals(childNode.getNodeName())) {
                    if (indice > 11) {
                        continuar = false;
                        this.agregarError("4");
                        break;
                    } else if (indice > 5) {
                        int aux = indice++ - 6;
                        elementos.set(aux, (double) elementos.get(aux) + (Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim())));
                    } else {
                        elementos.set(indice++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        this.imprimirResultado("potenciaPrecio", elementos);
        return new DatosPotenciaPrecio(elementos);
    }

    protected DatosPotenciaImporteTotal potenciaImporteTotal() {

        //Obtiene 1 elemento denomidado ImporteTotalTerminoPotencia en el nodo Potencia
        elementos = new ArrayList<Double>(1);
        elementos.add(0.0);

        NodeList flowListPeriodo = this.doc.getElementsByTagName("Potencia");

        int indicePeriodo = 0;
        boolean continuar = true;
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                Node childNode = childListLecturaHasta.item(j);
                if ("ImporteTotalTerminoPotencia".equals(childNode.getNodeName())) {
                    if (indicePeriodo > 0) {
                        this.agregarError("5");
                        continuar = false;
                        break;
                    } else {
                        elementos.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("potenciaImporteTotal", elementos);
        return new DatosPotenciaImporteTotal(elementos);
    }

    protected DatosPotenciaImporteTotal potenciaImporteTotal_A() {

        //Obtiene 1 elemento denomidado ImporteTotalTerminoPotencia en el nodo Potencia
        elementos = new ArrayList<Double>(1);

        NodeList flowListPeriodo = this.doc.getElementsByTagName("Potencia");

        int indicePeriodo = 0;
        boolean continuar = true;
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                Node childNode = childListLecturaHasta.item(j);
                if ("ImporteTotalTerminoPotencia".equals(childNode.getNodeName())) {
                    if (indicePeriodo > 0) {
                        this.agregarError("5");
                        continuar = false;
                        break;
                    } else {
                        elementos.add(indicePeriodo++, -Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("potenciaImporteTotal_A", elementos);
        return new DatosPotenciaImporteTotal(elementos);
    }

    protected DatosExcesoPotencia potenciaExcesos() {

        //Verifica la existencia de los excesos de potencia
        if (this.doc.getElementsByTagName("ValorExcesoPotencia").getLength() != 0) {

            //Se obtienen todos los 6 excesos de potencia y 1 importe total de excesos
            elementos = new ArrayList<Double>(7);
            elementos.add(0.0);
            elementos.add(0.0);
            elementos.add(0.0);
            elementos.add(0.0);
            elementos.add(0.0);
            elementos.add(0.0);
            elementos.add(0.0);

            //Busqueda en el nodo
            NodeList flowListPeriodo = this.doc.getElementsByTagName("Periodo");
            int indicePeriodo = 0;
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if ("ValorExcesoPotencia".equals(childNode.getNodeName())) {
                        elementos.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    } else if ("ImporteTotalExcesos".equals(childNode.getNodeName())) {
                        elementos.set(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }

            //Busqueda en el nodo
            NodeList flowList = this.doc.getElementsByTagName("ExcesoPotencia");
            for (int i = 0; i < flowList.getLength(); i++) {
                NodeList childListLecturaHasta = flowList.item(i).getChildNodes();
                for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                    Node childNode = childListLecturaHasta.item(j);
                    if ("ImporteTotalExcesos".equals(childNode.getNodeName())) {
                        elementos.set(6, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }

            this.imprimirResultado("potenciaExcesos", elementos);
            return new DatosExcesoPotencia(elementos);
        } else {
            return new DatosExcesoPotencia();
        }

    }

    //EnergiaActiva
    protected DatosEnergiaActiva energiaActivaDatos() {

        elementos = new ArrayList<>(4);
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");

        int indice = 0;
        boolean continuar = true;
        NodeList flowListPeriodo = this.doc.getElementsByTagName("TerminoEnergiaActiva");
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childList = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (indice > 3) {
                    this.agregarError("6");
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
        this.imprimirResultado("energiaActivaDatos", elementos);
        return new DatosEnergiaActiva(elementos);
    }

    protected DatosEnergiaActivaValores energiaActivaValores() {

        //Obtiene 12 posibles valores, los organiza en 6 (1+7, 2+8 ... 6+12) y los suma en un total
        elementos = new ArrayList<Double>(7);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        //Busqueda en el nodo
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Periodo");

        int indice = 0;
        boolean continuar = true;
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                Node childNode = childListLecturaHasta.item(j);
                if ("ValorEnergiaActiva".equals(childNode.getNodeName())) {
                    if (indice > 11) {
                        this.agregarError("7");
                        continuar = false;
                        break;
                    } else if (indice > 5) {
                        int aux = indice++ - 6;
                        elementos.set(aux, (double) elementos.get(aux) + (Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim())));
                    } else {
                        elementos.set(indice++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        double suma = 0;
        for (Object elemento : elementos) {
            suma += (double) elemento;
        }
        elementos.set(6, suma);

        this.imprimirResultado("energiaActivaValores", elementos);
        return new DatosEnergiaActivaValores(elementos);
    }

    protected DatosEnergiaActivaPrecio energiaActivaPrecio() {

        //Obtiene 12 posibles valores y los suma en 6 correspondiendo a pocisiones 1+7 2+8 ... 6+12
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        //Busqueda en el nodo
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Periodo");

        int indice = 0;
        boolean continuar = true;
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                Node childNode = childListLecturaHasta.item(j);
                if ("PrecioEnergia".equals(childNode.getNodeName())) {
                    if (indice > 11) {
                        this.agregarError("8");
                        continuar = false;
                        break;
                    } else if (indice > 5) {
                        int aux = indice++ - 6;
                        elementos.set(aux, (double) elementos.get(aux) + (Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim())));
                    } else {
                        elementos.set(indice++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("energiaActivaPrecio", elementos);
        return new DatosEnergiaActivaPrecio(elementos);
    }

    protected DatosEnergiaActivaImporteTotal energiaActivaImporteTotal() {
        //Obtiene 1 elemento denomidado ImporteTotalEnergiaActiva en el nodo EnergiaActiva
        elementos = new ArrayList<Double>(1);
        elementos.add(0.0);

        //Búsqueda en el nodo
        NodeList flowListPeriodo = this.doc.getElementsByTagName("EnergiaActiva");
        int indice = 0;
        boolean continuar = true;
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                Node childNode = childListLecturaHasta.item(j);
                if ("ImporteTotalEnergiaActiva".equals(childNode.getNodeName())) {
                    if (indice > 0) {
                        continuar = false;
                        this.agregarError("9");
                        break;
                    } else {
                        elementos.set(indice++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        this.imprimirResultado("energiaActivaImporteTotal", elementos);
        return new DatosEnergiaActivaImporteTotal(elementos);
    }

    protected DatosEnergiaActivaImporteTotal energiaActivaImporteTotal_A() {
        //Obtiene 1 elemento denomidado ImporteTotalEnergiaActiva en el nodo EnergiaActiva
        elementos = new ArrayList<Double>(1);
        elementos.add(0.0);

        //Búsqueda en el nodo
        NodeList flowListPeriodo = this.doc.getElementsByTagName("EnergiaActiva");
        int indice = 0;
        boolean continuar = true;
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childListLecturaHasta = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childListLecturaHasta.getLength(); j++) {
                Node childNode = childListLecturaHasta.item(j);
                if ("ImporteTotalEnergiaActiva".equals(childNode.getNodeName())) {
                    if (indice > 0) {
                        continuar = false;
                        this.agregarError("9");
                        break;
                    } else {
                        elementos.set(indice++, -Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        this.imprimirResultado("energiaActivaImporteTotal_A", elementos);
        return new DatosEnergiaActivaImporteTotal(elementos);
    }

    //EnergiaExcedentaria
    protected EnergiaExcedentaria energiaExcedentaria() {

        elementos = new ArrayList<>(7);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int indice = 0;
        boolean continuar = true;
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Periodo");
        //Se iteran los nodos correspondan con el flowList
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            //Se obtienen los nodos internos
            NodeList childList = flowListPeriodo.item(i).getChildNodes();
            //Se recorren los nodos internos
            for (int j = 0; j < childList.getLength(); j++) {
                if (indice > elementos.size() - 1) {
                    continuar = false;
                    this.agregarError("27");
                    break;
                }
                if ("ValorEnergiaExcedentaria".equals(childList.item(j).getNodeName())) {
                    this.existeEnergiaExcedentaria = true;
                    elementos.set(indice++, Double.parseDouble(childList.item(j).getTextContent().trim()));
                }
            }
            if (!continuar) {
                break;
            }
        }
        if (existeEnergiaExcedentaria) {
            double total = 0.0;
            for (int i = 0; i < elementos.size(); i++) {
                total += Double.parseDouble(elementos.get(i).toString());
            }
            elementos.set(6, Double.parseDouble(this.doc.getElementsByTagName("ValorTotalEnergiaExcedentaria").item(0).getTextContent()));
            if (total != Double.parseDouble(elementos.get(6).toString())) {
                this.agregarError("28");
            }
        }
        this.imprimirResultado("energiaExcedentaria", elementos);
        return new EnergiaExcedentaria(elementos);
    }


    /**
     * Carga los datos de autoconsumo en la instancia de EnergiaExcedentaria
     *
     * @param energiaExcedentaria instancia existente de EnergiaExcedentaria
     * @throws ExisteMasDeUnAutoconsumoException si se encuentra más de un autoconsumo en el archivo
     */
    protected void cargarAutoconsumo(@NonNull EnergiaExcedentaria energiaExcedentaria) throws ExisteMasDeUnAutoconsumoException {
        try {
            NodeList instalacionGenAutoconsumoNode = NodosUtil.getSingleNodeListByName(NodosUtil.getSingleNodeListByNameFromDocument(this.nombreArchivo, this.doc, "Autoconsumo"), "InstalacionGenAutoconsumo");

            NodeList terminoEnergiaNetaGenNode = NodosUtil.getSingleNodeListByChainedNames(instalacionGenAutoconsumoNode, "EnergiaNetaGen", "TerminoEnergiaNetaGen");
            NodeList periodoTerminoEnergiaNetaGenNode = NodosUtil.getAllNodesByNameWithSpecificExpectedNodes(terminoEnergiaNetaGenNode, "Periodo", 6);
            energiaExcedentaria.setAllNetaGenerada(NodosUtil.getAllContentNodesAsDoubleList(periodoTerminoEnergiaNetaGenNode, "ValorEnergiaNetaGen"));

            NodeList terminoEnergiaAutoconsumidaNode = NodosUtil.getSingleNodeListByChainedNames(instalacionGenAutoconsumoNode, "EnergiaAutoconsumida", "TerminoEnergiaAutoconsumida");
            energiaExcedentaria.setFechaDesde(NodosUtil.getSingleContentNodeAsLocalDateTimeWithDefaultTime(terminoEnergiaAutoconsumidaNode, "FechaDesde"));
            energiaExcedentaria.setFechaHasta(NodosUtil.getSingleContentNodeAsLocalDateTimeWithDefaultTime(terminoEnergiaAutoconsumidaNode, "FechaHasta"));

            NodeList periodosEnergiaAutoconsumidaNodes = NodosUtil.getAllNodesByNameWithSpecificExpectedNodes(terminoEnergiaAutoconsumidaNode, "Periodo", 6);
            energiaExcedentaria.setAllAutoconsumida(NodosUtil.getAllContentNodesAsDoubleList(periodosEnergiaAutoconsumidaNodes, "ValorEnergiaAutoconsumida"));
            energiaExcedentaria.setAllPagoTDA(NodosUtil.getAllContentNodesAsDoubleList(periodosEnergiaAutoconsumidaNodes, "PagoTDA"));
        } catch (NoCoincidenLosNodosEsperadosException e) {
            log.warn(e.getMessage());
        }
    }

    /**
     * Looks for PrecioCargo node in a xmlFile
     * Maximum values of nodes expected is 12 
     * @param tipoCargoValue value that belongs to TotalImporteTipoCargo Node used to identify a block
     * @param tp TipoFactura value that changes the tipe of operation that is going to use
     */
    protected Cargos Cargos(String tipoCargoValue, TIPO_FACTURA tp) {
        elementos =  (ArrayList) IntStream.range(0, 12).mapToDouble(i -> 0.0).boxed().collect(Collectors.toList());
        if (tp == TIPO_FACTURA.C_){
            this.logger.log(Level.INFO, "Las facturas de tipo C, no tienen Cargos");
            return new Cargos(elementos);
        }
        boolean continuar = true;
        int indice = 0;
        //List of blocks of parent nodes
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Periodo");
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            //internal nodes
            NodeList childList = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                if (indice > 11) {
                    continuar = false;
                    this.agregarError("23");
                    break;
                }
                Node childNode = childList.item(j);
                //Looking for specific node
                if ("PrecioCargo".equals(childNode.getNodeName())) {
                	//Going through previous sibling nodes till "TipoCargo" node is found
                    Node tipoCargo = childNode.getParentNode().getParentNode().getPreviousSibling();
                    while (null != tipoCargo && tipoCargo.getNodeType() != Node.ELEMENT_NODE || !tipoCargo.getNodeName().equals("TipoCargo")) {
                        tipoCargo = tipoCargo.getPreviousSibling();
                    }
                    if (tipoCargo.getTextContent().equals(tipoCargoValue)) {
                		switch (tp) {
							case N_NORMAL:
								elementos.set(indice ,Double.parseDouble(childList.item(j).getTextContent().trim()));
								break;
							case A_ABONO:
								elementos.set(indice, Double.parseDouble(childList.item(j).getTextContent().trim()) * - 1);
								break;
						}
                		indice++;
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("Cargos" + tipoCargoValue + ", " + tp, elementos);
        return new Cargos(elementos);
    }
    
    /**
     * Looks for TotalImporteTipoCargo node in a xmlFile
     * Maximum values of nodes expected is 4  
     * @param tipoCargoValue value that belongs to TotalImporteTipoCargo Node used to identify a block
     * @param tp TipoFactura value that changes the type of operation that is going to use
     */
    protected CargoImporteTotal ImporteTotalCargos(String tipoCargoValue, TIPO_FACTURA tp) {
    	elementos =  (ArrayList) IntStream.range(0, 1).mapToDouble(i -> 0.0).boxed().collect(Collectors.toList());
        if (tp == TIPO_FACTURA.C_){
            this.logger.log(Level.INFO, "Las facturas de tipo C, no tienen Cargos");
            return new CargoImporteTotal(elementos);
        }
        int indice = 0;
        boolean continuar = true;
        //List of blocks of parent nodes
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Cargo");
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            //Child Nodes into the block
            NodeList childList = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                if (indice > 3) {
                    continuar = false;
                    this.agregarError("25");
                    break;
                }
                //Looking for a specific node
                Node childNode = childList.item(j);
                if ("TotalImporteTipoCargo".equals(childNode.getNodeName())) {
                	//Going through previous sibling nodes till "TipoCargo" node is foundk
                    Node tipoCargo = childNode.getPreviousSibling();
                    while (null != tipoCargo && tipoCargo.getNodeType() != Node.ELEMENT_NODE || !tipoCargo.getNodeName().equals("TipoCargo")) {
                        tipoCargo = tipoCargo.getPreviousSibling();
                    }
                    if (tipoCargo.getTextContent().equals(tipoCargoValue)) {
                    	switch (tp) {
							case N_NORMAL:
								elementos.set(0, (Double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
								break;
							case A_ABONO:
								elementos.set(0, (Double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
								break;
						}
                        indice++;
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("TotalImporteTipoCargos" + tipoCargoValue + ", " + tp, elementos);
        return new CargoImporteTotal(elementos);
    }

    //Otros datos
    protected DatosImpuestoElectrico impuestoElectrico() {
        //Se obtiene un solo dato denomidao Importe de impuesto electrico

        elementos = new ArrayList<Double>(1);
        int indice = 0;
        boolean continuar = true;

        //Busqueda en el nodo
        NodeList flowListPeriodo = this.doc.getElementsByTagName("ImpuestoElectrico");
        if (flowListPeriodo.getLength() != 0) {
            for (int i = 0; i < flowListPeriodo.getLength(); i++) {
                NodeList childList = flowListPeriodo.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("Importe".equals(childNode.getNodeName())) {
                        if (indice > 0) {
                            this.agregarError("10");
                            continuar = false;
                            break;
                        } else {
                            elementos.add(indice++, Double.parseDouble(childList.item(j).getTextContent().trim()));
                        }
                    }
                }
                if (!continuar) {
                    break;
                }
            }
            this.imprimirResultado("impuestoElectrico", elementos);
        } else {
            System.out.println("No se encontró el dato");
        }
        return new DatosImpuestoElectrico(elementos);
    }

    protected DatosAlquileres alquileres() {

        //Obtiene un unico dato
        elementos = new ArrayList<Double>(1);
        elementos.add(0.0);

        int indice = 0;
        boolean continuar = false;

        //Busqueda en el nodo
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Alquileres");
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childList = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("ImporteFacturacionAlquileres".equals(childNode.getNodeName())) {
                    if (indice > 0) {
                        this.agregarError("11");
                        continuar = false;
                        break;
                    } else {
                        elementos.set(indice++, Double.parseDouble(childList.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("alquileres", elementos);
        return new DatosAlquileres(elementos);
    }

    protected DatosIva iva() {

        //Obtiene un unico elemento
        elementos = new ArrayList<Double>(1);
        int indice = 0;
        boolean continuar = true;

        //Busqueda en el nodo
        NodeList flowListPeriodo = this.doc.getElementsByTagName("IVA");
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            NodeList childList = flowListPeriodo.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("BaseImponible".equals(childNode.getNodeName())) {
                    if (indice > 0) {
                        this.agregarError("12");
                        continuar = false;
                        break;
                    } else {
                        elementos.add(indice++, Double.parseDouble(childList.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("iva", elementos);
        return new DatosIva(elementos);
    }

    //AE
    protected DatosAeConsumo aeConsumo() {

        //Obtiene 12 posibles valores, los organiza en 6 elementos correspondientes y los suma
        //Estos valores han sido declarados debido a que se pueden encontrar en distintos ordenes
        elementos = new ArrayList<Double>(7);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Recupera todos los elementos con el nombre brindado
        NodeList flowList = this.doc.getElementsByTagName("Integrador");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("ConsumoCalculado".equals(childNode.getNodeName())) {
                    //Revision de la cantidad máxima de datos permitidos (12)
                    if (contador > 11) {
                        this.agregarError("13");
                        continuar = false;
                        break;
                    } else {
                        //Se acceden a los nodos previos a consumo calculado para comprobar su tipo de magnnitud

                        //Numero Ruedas Decimales
                        Node sibling = childNode.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Enteras
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //Constante Multiplicadora
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //Codigo Periodo
                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }
                        //Obtiene solo el ultimo numero de el codigo periodo 62 -> 2
                        String defPeriodo = sibling3.getTextContent().substring(sibling3.getTextContent().length() - 1);

                        //Magnitud
                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        //Revisa si el nodo de magnitud corresponde con Ae
                        if ("AE".equals(sibling4.getTextContent().toUpperCase())) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "3":
                                                    elementos.set(2, (double) elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        //suma de los elementos
        double suma = 0;
        for (Object elemento : elementos) {
            suma += (double) elemento;
        }
        elementos.add(suma);

        this.imprimirResultado("aeConsumo", elementos);
        return new DatosAeConsumo(elementos);
    }

    protected DatosAeLecturaDesde aeLecturaDesde() {
        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Recupera todos los elementos con el nombre brindado
        NodeList flowList = this.doc.getElementsByTagName("LecturaDesde");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Lectura".equals(childNode.getNodeName())) {
                    //Revision de que aún sean aceptados los elementos
                    if (contador > 11) {
                        this.agregarError("14");
                        continuar = false;
                        break;
                    }

                    //Lee el nodo padre (LecturaDesde)
                    Node padre = childNode.getParentNode();
                    while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                        padre = padre.getParentNode();
                    }

                    //Lee todos los nodos en reversa
                    //Consumo calculado
                    Node sibling = padre.getPreviousSibling();
                    while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                        sibling = sibling.getPreviousSibling();
                    }

                    //Numero de ruedas decimales
                    Node sibling1 = sibling.getPreviousSibling();
                    while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                        sibling1 = sibling1.getPreviousSibling();
                    }

                    //Numero de ruedas enteras
                    Node sibling2 = sibling1.getPreviousSibling();
                    while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                        sibling2 = sibling2.getPreviousSibling();
                    }

                    //Constante multiplicadora
                    Node sibling3 = sibling2.getPreviousSibling();
                    while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                        sibling3 = sibling3.getPreviousSibling();
                    }

                    //Codigo periodo
                    Node sibling4 = sibling3.getPreviousSibling();
                    while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                        sibling4 = sibling4.getPreviousSibling();
                    }
                    //Lectura del ultimo caracter del codigo periodo
                    String defPeriodo = sibling4.getTextContent().substring(sibling4.getTextContent().length() - 1);

                    //Magnitud
                    Node sibling5 = sibling4.getPreviousSibling();
                    while (null != sibling5 && sibling5.getNodeType() != Node.ELEMENT_NODE) {
                        sibling5 = sibling5.getPreviousSibling();
                    }

                    //Revisa la magnitud a la que pertenece el valor de lectura Desde
                    if ("AE".equals(sibling5.getTextContent().toUpperCase())) {

                        //Variable de control para revisar la cantidad de elementos coincidentes
                        contador++;

                        //Se revisa la tarifa a considerar
                        switch (this.tarifaAtrFac) {
                            case "3":
                            case "7":
                            case "8":
                            case "11":
                            case "12":
                            case "13":
                            case "14":
                            case "19":
                            case "20":
                            case "21":
                                //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                if (!"0".equals(defPeriodo)) {
                                    //Evaluacion de los periodos
                                    switch (defPeriodo) {
                                        case "1":
                                            elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "2":
                                            elementos.set(1, (double) elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "3":
                                            elementos.set(2, (double) elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "4":
                                            elementos.set(3, (double) elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "5":
                                            elementos.set(4, (double) elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "6":
                                            elementos.set(5, (double) elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        default:
                                            System.out.println("No se encontro el codigo de periodo valido");
                                            break;
                                    }
                                }
                                break;
                            case "4":
                            case "6":
                            case "18":
                                //Se verifica la empresa para definir los casos especiales
                                if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                    //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                    char periodo = sibling4.getTextContent().charAt(0);
                                    switch (periodo) {
                                        case '1':
                                            elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case '2':
                                            elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case '3':
                                            elementos.set(2, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        default:
                                            break;
                                    }

                                } else {
                                    //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                    if (!defPeriodo.equals("0")) {
                                        //Se evaluan los elementos que pueden coincidir con la descripción
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                break;
                            case "1":
                            case "5":
                                //Se verifica que los elementos coincidan con 0
                                if (defPeriodo.equals("0")) {
                                    elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                }
                                break;
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("aeLecturaDesde", elementos);
        return new DatosAeLecturaDesde(elementos);
    }

    protected DatosAeLecturaHasta aeLecturaHasta() {

        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Búsqueda en el nodo
        NodeList flowList = this.doc.getElementsByTagName("LecturaHasta");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Lectura".equals(childNode.getNodeName())) {
                    if (contador > 11) {
                        this.agregarError("15");
                        continuar = false;
                        break;
                    } else {
                        //Acceso al nodo padre, y nodos hermanos para verificar la magnitud y codigo periodo
                        //LecturaHasta
                        Node padre1 = childNode.getParentNode();
                        while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                            padre1 = padre1.getParentNode();
                        }

                        //LecturaDesde
                        Node padre = padre1.getPreviousSibling();
                        while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getPreviousSibling();
                        }

                        //Consumo Calculado
                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Decimales
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //NumeroRuedas Enteras
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //ConstanteMultiplicadora
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

                        //Se revisa que la magnitud corresponda con lo indicado
                        if ("AE".equals(sibling5.getTextContent().toUpperCase())) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }

                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("aeLecturaHasta", elementos);
        return new DatosAeLecturaHasta(elementos);
    }

    protected DatosAeProcedenciaDesde aeProcedenciaDesde() {

        elementos = new ArrayList<Integer>(1);
        elementos.add(0);

        //Variable de control
        boolean continuar = true;

        //Busqueda en el nodo indicado
        NodeList flowList = this.doc.getElementsByTagName("LecturaDesde");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Procedencia".equals(childNode.getNodeName())) {

                    Node padre = childNode.getParentNode();
                    while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
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

                    //Verifica que corresponda con el nodo AE
                    if ("AE".equals(sibling5.getTextContent())) {
                        //Revision del tipo de tarifa a considerar
                        switch (this.tarifaAtrFac) {
                            case "3":
                            case "7":
                            case "8":
                            case "11":
                            case "12":
                            case "13":
                            case "14":
                            case "19":
                            case "20":
                            case "21":
                                if (!"0".equals(defPeriodo)) {
                                    continuar = false;
                                    elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                }
                                break;
                            //Tarifas del tipo 4,6
                            case "4":
                            case "6":
                                //Se verifica la empresa emisora
                                if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                    //Se lee el primer caracter de la definicion del periodo
                                    String periodo = sibling4.getTextContent().substring(0, 1);
                                    switch (periodo) {
                                        case "1":
                                            elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                            continuar = false;
                                            break;
                                        case "3":
                                            elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                            continuar = false;
                                            break;
                                    }

                                    continuar = false;

                                } else {
                                    //En caso de que no coincida, buscará un elemento con la terminación de con una terminacion diferente de cero y termina el proceso
                                    if (!"0".equals(defPeriodo)) {
                                        continuar = false;
                                        elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    }
                                }
                                break;
                            //Tarifas del tipo 1, 5
                            case "1":
                            case "5":
                                if ("0".equals(defPeriodo)) {
                                    continuar = false;
                                    elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("aeProcedenciaDesde", elementos);
        return new DatosAeProcedenciaDesde(elementos);
    }

    protected DatosAeProcedenciaHasta aeProcedenciaHasta() {

        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Integer>(1);
        elementos.add(0);

        boolean continuar = true;

        //Búsqueda en el nodo
        NodeList flowList = this.doc.getElementsByTagName("LecturaHasta");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Procedencia".equals(childNode.getNodeName())) {
                    //Acceso al nodo padre, y nodos hermanos para verificar la magnitud y codigo periodo
                    //LecturaHasta
                    Node padre1 = childNode.getParentNode();
                    while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                        padre1 = padre1.getParentNode();
                    }

                    //LecturaDesde
                    Node padre = padre1.getPreviousSibling();
                    while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                        padre = padre.getPreviousSibling();
                    }

                    //Consumo Calculado
                    Node sibling = padre.getPreviousSibling();
                    while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                        sibling = sibling.getPreviousSibling();
                    }

                    //Numero Ruedas Decimales
                    Node sibling1 = sibling.getPreviousSibling();
                    while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                        sibling1 = sibling1.getPreviousSibling();
                    }

                    //NumeroRuedas Enteras
                    Node sibling2 = sibling1.getPreviousSibling();
                    while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                        sibling2 = sibling2.getPreviousSibling();
                    }

                    //ConstanteMultiplicadora
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

                    //Verifica que corresponda con el nodo AE
                    if ("AE".equals(sibling5.getTextContent())) {
                        //Revision del tipo de tarifa a considerar
                        switch (this.tarifaAtrFac) {
                            case "3":
                            case "7":
                            case "8":
                            case "11":
                            case "12":
                            case "13":
                            case "14":
                            case "19":
                            case "20":
                            case "21":
                                if (!"0".equals(defPeriodo)) {
                                    continuar = false;
                                    elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                }
                                break;
                            //Tarifas del tipo 4,6
                            case "4":
                            case "6":
                                //Se verifica la empresa emisora
                                if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                    //Se lee el primer caracter de la definicion del periodo
                                    String periodo = sibling4.getTextContent().substring(0, 1);
                                    switch (periodo) {
                                        case "1":
                                            elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                            continuar = false;
                                            break;
                                        case "3":
                                            elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                            continuar = false;
                                            break;
                                    }

                                    continuar = false;

                                } else {
                                    //En caso de que no coincida, buscará un elemento con la terminación de con una terminacion diferente de cero y termina el proceso
                                    if (!"0".equals(defPeriodo)) {
                                        continuar = false;
                                        elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    }
                                }
                                break;
                            //Tarifas del tipo 1, 5
                            case "1":
                            case "5":
                                if ("0".equals(defPeriodo)) {
                                    continuar = false;
                                    elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("aeProcedenciaHasta", elementos);
        return new DatosAeProcedenciaHasta(elementos);
    }

    //Reactiva
    protected DatosRConsumo rConsumo() {

        //Obtiene 12 posibles valores, los organiza en 6 elementos correspondientes y los suma
        //Estos valores han sido declarados debido a que se pueden encontrar en distintos ordenes
        elementos = new ArrayList<Double>(7);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Recupera todos los elementos con el nombre brindado
        NodeList flowList = this.doc.getElementsByTagName("Integrador");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("ConsumoCalculado".equals(childNode.getNodeName())) {
                    //Revision de la cantidad máxima de datos permitidos (12)
                    if (contador > 11) {
                        this.agregarError("16");
                        continuar = false;
                        break;
                    } else {
                        //Se acceden a los nodos previos a consumo calculado para comprobar su tipo de magnnitud

                        //Numero Ruedas Decimales
                        Node sibling = childNode.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Enteras
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //Constante Multiplicadora
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //Codigo Periodo
                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }
                        //Obtiene solo el ultimo numero de el codigo periodo 62 -> 2
                        String defPeriodo = sibling3.getTextContent().substring(sibling3.getTextContent().length() - 1);

                        //Magnitud
                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        //Revisa si el nodo de magnitud corresponde con el valor proporcionado
                        if ("R".equals(sibling4.getTextContent().toUpperCase().substring(0, 1))) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        //suma de los elementos
        double suma = 0;
        for (Object elemento : elementos) {
            suma += (double) elemento;
        }
        elementos.add(suma);

        this.imprimirResultado("rConsumo", elementos);
        return new DatosRConsumo(elementos);
    }

    protected DatosRLecturaDesde rLecturaDesde() {
        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Recupera todos los elementos con el nombre brindado
        NodeList flowList = this.doc.getElementsByTagName("LecturaDesde");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Lectura".equals(childNode.getNodeName())) {
                    //Revision de que aún sean aceptados los elementos
                    if (contador > 11) {
                        this.agregarError("17");
                        continuar = false;
                        break;
                    }

                    //Lee el nodo padre (LecturaDesde)
                    Node padre = childNode.getParentNode();
                    while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                        padre = padre.getParentNode();
                    }

                    //Lee todos los nodos en reversa
                    //Consumo calculado
                    Node sibling = padre.getPreviousSibling();
                    while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                        sibling = sibling.getPreviousSibling();
                    }

                    //Numero de ruedas decimales
                    Node sibling1 = sibling.getPreviousSibling();
                    while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                        sibling1 = sibling1.getPreviousSibling();
                    }

                    //Numero de ruedas enteras
                    Node sibling2 = sibling1.getPreviousSibling();
                    while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                        sibling2 = sibling2.getPreviousSibling();
                    }

                    //Constante multiplicadora
                    Node sibling3 = sibling2.getPreviousSibling();
                    while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                        sibling3 = sibling3.getPreviousSibling();
                    }

                    //Codigo periodo
                    Node sibling4 = sibling3.getPreviousSibling();
                    while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                        sibling4 = sibling4.getPreviousSibling();
                    }
                    //Lectura del ultimo caracter del codigo periodo
                    String defPeriodo = sibling4.getTextContent().substring(sibling4.getTextContent().length() - 1);

                    //Magnitud
                    Node sibling5 = sibling4.getPreviousSibling();
                    while (null != sibling5 && sibling5.getNodeType() != Node.ELEMENT_NODE) {
                        sibling5 = sibling5.getPreviousSibling();
                    }

                    //Revisa si el nodo de magnitud corresponde con el valor proporcionado
                    if ("R".equals(sibling5.getTextContent().toUpperCase().substring(0, 1))) {

                        //Variable de control para revisar la cantidad de elementos coincidentes
                        contador++;

                        //Se revisa la tarifa a considerar
                        switch (this.tarifaAtrFac) {
                            case "3":
                            case "7":
                            case "8":
                            case "11":
                            case "12":
                            case "13":
                            case "14":
                            case "19":
                            case "20":
                            case "21":
                                //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                if (!"0".equals(defPeriodo)) {
                                    //Evaluacion de los periodos
                                    switch (defPeriodo) {
                                        case "1":
                                            elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "2":
                                            elementos.set(1, (double) elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "3":
                                            elementos.set(2, (double) elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "4":
                                            elementos.set(3, (double) elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "5":
                                            elementos.set(4, (double) elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "6":
                                            elementos.set(5, (double) elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        default:
                                            System.out.println("No se encontro el codigo de periodo valido");
                                            break;
                                    }
                                }
                                break;
                            case "4":
                            case "6":
                            case "18":
                                //Se verifica la empresa para definir los casos especiales
                                if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                    //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                    char periodo = sibling4.getTextContent().charAt(0);
                                    switch (periodo) {
                                        case '1':
                                            elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case '2':
                                            elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case '3':
                                            elementos.set(2, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        default:
                                            break;
                                    }

                                } else {
                                    //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                    if (!defPeriodo.equals("0")) {
                                        //Se evaluan los elementos que pueden coincidir con la descripción
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                break;
                            case "1":
                            case "5":
                                //Se verifica que los elementos coincidan con 0
                                if (defPeriodo.equals("0")) {
                                    elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                }
                                break;
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("rLecturaDesde", elementos);
        return new DatosRLecturaDesde(elementos);
    }

    protected DatosRLecturaHasta rLecturaHasta() {

        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Búsqueda en el nodo
        NodeList flowList = this.doc.getElementsByTagName("LecturaHasta");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Lectura".equals(childNode.getNodeName())) {
                    if (contador > 11) {
                        this.agregarError("18");
                        continuar = false;
                        break;
                    } else {
                        //Acceso al nodo padre, y nodos hermanos para verificar la magnitud y codigo periodo
                        //LecturaHasta
                        Node padre1 = childNode.getParentNode();
                        while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                            padre1 = padre1.getParentNode();
                        }

                        //LecturaDesde
                        Node padre = padre1.getPreviousSibling();
                        while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getPreviousSibling();
                        }

                        //Consumo Calculado
                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Decimales
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //NumeroRuedas Enteras
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //ConstanteMultiplicadora
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

                        //Revisa si el nodo de magnitud corresponde con el valor proporcionado
                        if ("R".equals(sibling5.getTextContent().toUpperCase().substring(0, 1))) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("rLecturaHasta", elementos);
        return new DatosRLecturaHasta(elementos);
    }

    protected ReactivaImporteTotal rImporteTotal() {

        elementos = new ArrayList<String>(1);

        if (this.doc.getElementsByTagName("ImporteTotalEnergiaReactiva").getLength() != 0) {
            elementos.add(
                    String.valueOf(
                            Double.parseDouble(
                                    this.doc.getElementsByTagName("ImporteTotalEnergiaReactiva").item(0).getTextContent()
                            )
                    )
            );
        } else {
            elementos.add("0.0");
        }

        this.imprimirResultado("rImporteTotal", elementos);
        return new ReactivaImporteTotal(elementos);
    }

    //PM
    protected DatosPmConsumo pmConsumo() {

        //Obtiene 12 posibles valores, los organiza en 6 elementos correspondientes y los suma
        //Estos valores han sido declarados debido a que se pueden encontrar en distintos ordenes
        elementos = new ArrayList<Double>(7);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Recupera todos los elementos con el nombre brindado
        NodeList flowList = this.doc.getElementsByTagName("Integrador");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("ConsumoCalculado".equals(childNode.getNodeName())) {
                    //Revision de la cantidad máxima de datos permitidos (12)
                    if (contador > 11) {
                        this.agregarError("19");
                        continuar = false;
                        break;
                    } else {
                        //Se acceden a los nodos previos a consumo calculado para comprobar su tipo de magnnitud

                        //Numero Ruedas Decimales
                        Node sibling = childNode.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Enteras
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //Constante Multiplicadora
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //Codigo Periodo
                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }
                        //Obtiene solo el ultimo numero de el codigo periodo 62 -> 2
                        String defPeriodo = sibling3.getTextContent().substring(sibling3.getTextContent().length() - 1);

                        //Magnitud
                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        //Revisa si el nodo de magnitud corresponde con el valor proporcionado
                        if ("PM".equals(sibling4.getTextContent().toUpperCase())) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        //suma de los elementos
        double suma = 0;
        for (Object elemento : elementos) {
            suma += (double) elemento;
        }
        elementos.add(suma);

        this.imprimirResultado("pmConsumo", elementos);
        return new DatosPmConsumo(elementos);
    }

    protected DatosPmLecturaHasta pmLecturaHasta() {

        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Búsqueda en el nodo
        NodeList flowList = this.doc.getElementsByTagName("LecturaHasta");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Lectura".equals(childNode.getNodeName())) {
                    if (contador > 11) {
                        this.agregarError("20");
                        continuar = false;
                        break;
                    } else {
                        //Acceso al nodo padre, y nodos hermanos para verificar la magnitud y codigo periodo
                        //LecturaHasta
                        Node padre1 = childNode.getParentNode();
                        while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                            padre1 = padre1.getParentNode();
                        }

                        //LecturaDesde
                        Node padre = padre1.getPreviousSibling();
                        while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getPreviousSibling();
                        }

                        //Consumo Calculado
                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Decimales
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //NumeroRuedas Enteras
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //ConstanteMultiplicadora
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

                        //Revisa si el nodo de magnitud corresponde con el valor proporcionado
                        if ("PM".equals(sibling5.getTextContent().toUpperCase())) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) + Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("pmLecturaHasta", elementos);
        return new DatosPmLecturaHasta(elementos);
    }

    //AE - A (Negactivos)
    protected DatosAeConsumo aeConsumo_A() {

        //Obtiene 12 posibles valores, los organiza en 6 elementos correspondientes y los suma
        //Estos valores han sido declarados debido a que se pueden encontrar en distintos ordenes
        elementos = new ArrayList<Double>(7);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Recupera todos los elementos con el nombre brindado
        NodeList flowList = this.doc.getElementsByTagName("Integrador");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("ConsumoCalculado".equals(childNode.getNodeName())) {
                    //Revision de la cantidad máxima de datos permitidos (12)
                    if (contador > 11) {
                        this.agregarError("13");
                        continuar = false;
                        break;
                    } else {
                        //Se acceden a los nodos previos a consumo calculado para comprobar su tipo de magnnitud

                        //Numero Ruedas Decimales
                        Node sibling = childNode.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Enteras
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //Constante Multiplicadora
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //Codigo Periodo
                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }
                        //Obtiene solo el ultimo numero de el codigo periodo 62 -> 2
                        String defPeriodo = sibling3.getTextContent().substring(sibling3.getTextContent().length() - 1);

                        //Magnitud
                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        //Revisa si el nodo de magnitud corresponde con Ae
                        if ("AE".equals(sibling4.getTextContent().toUpperCase())) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        //suma de los elementos
        double suma = 0;
        for (Object elemento : elementos) {
            suma += (double) elemento;
        }
        elementos.add(suma);

        this.imprimirResultado("aeConsumo_A", elementos);
        return new DatosAeConsumo(elementos);
    }

    protected DatosAeLecturaDesde aeLecturaDesde_A() {
        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Recupera todos los elementos con el nombre brindado
        NodeList flowList = this.doc.getElementsByTagName("LecturaDesde");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Lectura".equals(childNode.getNodeName())) {
                    //Revision de que aún sean aceptados los elementos
                    if (contador > 11) {
                        this.agregarError("14");
                        continuar = false;
                        break;
                    }

                    //Lee el nodo padre (LecturaDesde)
                    Node padre = childNode.getParentNode();
                    while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                        padre = padre.getParentNode();
                    }

                    //Lee todos los nodos en reversa
                    //Consumo calculado
                    Node sibling = padre.getPreviousSibling();
                    while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                        sibling = sibling.getPreviousSibling();
                    }

                    //Numero de ruedas decimales
                    Node sibling1 = sibling.getPreviousSibling();
                    while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                        sibling1 = sibling1.getPreviousSibling();
                    }

                    //Numero de ruedas enteras
                    Node sibling2 = sibling1.getPreviousSibling();
                    while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                        sibling2 = sibling2.getPreviousSibling();
                    }

                    //Constante multiplicadora
                    Node sibling3 = sibling2.getPreviousSibling();
                    while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                        sibling3 = sibling3.getPreviousSibling();
                    }

                    //Codigo periodo
                    Node sibling4 = sibling3.getPreviousSibling();
                    while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                        sibling4 = sibling4.getPreviousSibling();
                    }
                    //Lectura del ultimo caracter del codigo periodo
                    String defPeriodo = sibling4.getTextContent().substring(sibling4.getTextContent().length() - 1);

                    //Magnitud
                    Node sibling5 = sibling4.getPreviousSibling();
                    while (null != sibling5 && sibling5.getNodeType() != Node.ELEMENT_NODE) {
                        sibling5 = sibling5.getPreviousSibling();
                    }

                    //Revisa la magnitud a la que pertenece el valor de lectura Desde
                    if ("AE".equals(sibling5.getTextContent().toUpperCase())) {

                        //Variable de control para revisar la cantidad de elementos coincidentes
                        contador++;

                        //Se revisa la tarifa a considerar
                        switch (this.tarifaAtrFac) {
                            case "3":
                            case "7":
                            case "8":
                            case "11":
                            case "12":
                            case "13":
                            case "14":
                            case "19":
                            case "20":
                            case "21":
                                //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                if (!"0".equals(defPeriodo)) {
                                    //Evaluacion de los periodos
                                    switch (defPeriodo) {
                                        case "1":
                                            elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "2":
                                            elementos.set(1, (double) elementos.get(1) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "3":
                                            elementos.set(2, (double) elementos.get(2) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "4":
                                            elementos.set(3, (double) elementos.get(3) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "5":
                                            elementos.set(4, (double) elementos.get(4) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "6":
                                            elementos.set(5, (double) elementos.get(5) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        default:
                                            System.out.println("No se encontro el codigo de periodo valido");
                                            break;
                                    }
                                }
                                break;
                            case "4":
                            case "6":
                            case "18":
                                //Se verifica la empresa para definir los casos especiales
                                if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                    //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                    char periodo = sibling4.getTextContent().charAt(0);
                                    switch (periodo) {
                                        case '1':
                                            elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case '2':
                                            elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case '3':
                                            elementos.set(2, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        default:
                                            break;
                                    }

                                } else {
                                    //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                    if (!defPeriodo.equals("0")) {
                                        //Se evaluan los elementos que pueden coincidir con la descripción
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                break;
                            case "1":
                            case "5":
                                //Se verifica que los elementos coincidan con 0
                                if (defPeriodo.equals("0")) {
                                    elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                }
                                break;
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("aeLecturaDesde_A", elementos);
        return new DatosAeLecturaDesde(elementos);
    }

    protected DatosAeLecturaHasta aeLecturaHasta_A() {

        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Búsqueda en el nodo
        NodeList flowList = this.doc.getElementsByTagName("LecturaHasta");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Lectura".equals(childNode.getNodeName())) {
                    if (contador > 11) {
                        this.agregarError("15");
                        continuar = false;
                        break;
                    } else {
                        //Acceso al nodo padre, y nodos hermanos para verificar la magnitud y codigo periodo
                        //LecturaHasta
                        Node padre1 = childNode.getParentNode();
                        while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                            padre1 = padre1.getParentNode();
                        }

                        //LecturaDesde
                        Node padre = padre1.getPreviousSibling();
                        while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getPreviousSibling();
                        }

                        //Consumo Calculado
                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Decimales
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //NumeroRuedas Enteras
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //ConstanteMultiplicadora
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

                        //Se revisa que la magnitud corresponda con lo indicado
                        if ("AE".equals(sibling5.getTextContent().toUpperCase())) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }

                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("aeLecturaHasta_A", elementos);
        return new DatosAeLecturaHasta(elementos);
    }

    protected DatosAeProcedenciaDesde aeProcedenciaDesde_A() {

        elementos = new ArrayList<Integer>(1);
        elementos.add(0);

        //Variable de control
        boolean continuar = true;

        //Busqueda en el nodo indicado
        NodeList flowList = this.doc.getElementsByTagName("LecturaDesde");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Procedencia".equals(childNode.getNodeName())) {

                    Node padre = childNode.getParentNode();
                    while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
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

                    //Verifica que corresponda con el nodo AE
                    if ("AE".equals(sibling5.getTextContent())) {
                        //Revision del tipo de tarifa a considerar
                        switch (this.tarifaAtrFac) {
                            case "3":
                            case "7":
                            case "8":
                            case "11":
                            case "12":
                            case "13":
                            case "14":
                            case "19":
                            case "20":
                            case "21":
                                if (!"0".equals(defPeriodo)) {
                                    continuar = false;
                                    elementos.set(0, -Integer.parseInt(childList.item(j).getTextContent().trim()));
                                }
                                break;
                            //Tarifas del tipo 4,6
                            case "4":
                            case "6":
                                //Se verifica la empresa emisora
                                if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                    //Se lee el primer caracter de la definicion del periodo
                                    String periodo = sibling4.getTextContent().substring(0, 1);
                                    switch (periodo) {
                                        case "1":
                                            elementos.set(0, -Integer.parseInt(childList.item(j).getTextContent().trim()));
                                            continuar = false;
                                            break;
                                        case "3":
                                            elementos.set(0, -Integer.parseInt(childList.item(j).getTextContent().trim()));
                                            continuar = false;
                                            break;
                                    }

                                    continuar = false;

                                } else {
                                    //En caso de que no coincida, buscará un elemento con la terminación de con una terminacion diferente de cero y termina el proceso
                                    if (!"0".equals(defPeriodo)) {
                                        continuar = false;
                                        elementos.set(0, -Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    }
                                }
                                break;
                            //Tarifas del tipo 1, 5
                            case "1":
                            case "5":
                                if ("0".equals(defPeriodo)) {
                                    continuar = false;
                                    elementos.set(0, -Integer.parseInt(childList.item(j).getTextContent().trim()));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("aeProcedenciaDesde_A", elementos);
        return new DatosAeProcedenciaDesde(elementos);
    }

    protected DatosAeProcedenciaHasta aeProcedenciaHasta_A() {

        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Integer>(1);
        elementos.add(0);

        boolean continuar = true;

        //Búsqueda en el nodo
        NodeList flowList = this.doc.getElementsByTagName("LecturaHasta");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Procedencia".equals(childNode.getNodeName())) {
                    //Acceso al nodo padre, y nodos hermanos para verificar la magnitud y codigo periodo
                    //LecturaHasta
                    Node padre1 = childNode.getParentNode();
                    while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                        padre1 = padre1.getParentNode();
                    }

                    //LecturaDesde
                    Node padre = padre1.getPreviousSibling();
                    while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                        padre = padre.getPreviousSibling();
                    }

                    //Consumo Calculado
                    Node sibling = padre.getPreviousSibling();
                    while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                        sibling = sibling.getPreviousSibling();
                    }

                    //Numero Ruedas Decimales
                    Node sibling1 = sibling.getPreviousSibling();
                    while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                        sibling1 = sibling1.getPreviousSibling();
                    }

                    //NumeroRuedas Enteras
                    Node sibling2 = sibling1.getPreviousSibling();
                    while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                        sibling2 = sibling2.getPreviousSibling();
                    }

                    //ConstanteMultiplicadora
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

                    //Verifica que corresponda con el nodo AE
                    if ("AE".equals(sibling5.getTextContent())) {
                        //Revision del tipo de tarifa a considerar
                        switch (this.tarifaAtrFac) {
                            case "3":
                            case "7":
                            case "8":
                            case "11":
                            case "12":
                            case "13":
                            case "14":
                            case "19":
                            case "20":
                            case "21":
                                if (!"0".equals(defPeriodo)) {
                                    continuar = false;
                                    elementos.set(0, -Integer.parseInt(childList.item(j).getTextContent().trim()));
                                }
                                break;
                            //Tarifas del tipo 4,6
                            case "4":
                            case "6":
                                //Se verifica la empresa emisora
                                if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                    //Se lee el primer caracter de la definicion del periodo
                                    String periodo = sibling4.getTextContent().substring(0, 1);
                                    switch (periodo) {
                                        case "1":
                                            elementos.set(0, -Integer.parseInt(childList.item(j).getTextContent().trim()));
                                            continuar = false;
                                            break;
                                        case "3":
                                            elementos.set(0, -Integer.parseInt(childList.item(j).getTextContent().trim()));
                                            continuar = false;
                                            break;
                                    }

                                    continuar = false;

                                } else {
                                    //En caso de que no coincida, buscará un elemento con la terminación de con una terminacion diferente de cero y termina el proceso
                                    if (!"0".equals(defPeriodo)) {
                                        continuar = false;
                                        elementos.set(0, -Integer.parseInt(childList.item(j).getTextContent().trim()));
                                    }
                                }
                                break;
                            //Tarifas del tipo 1, 5
                            case "1":
                            case "5":
                                if ("0".equals(defPeriodo)) {
                                    continuar = false;
                                    elementos.set(0, -Integer.parseInt(childList.item(j).getTextContent().trim()));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("aeProcedenciaHasta_A", elementos);
        return new DatosAeProcedenciaHasta(elementos);
    }

    //Reactiva - A (Negactivos)
    protected DatosRConsumo rConsumo_A() {

        //Obtiene 12 posibles valores, los organiza en 6 elementos correspondientes y los suma
        //Estos valores han sido declarados debido a que se pueden encontrar en distintos ordenes
        elementos = new ArrayList<Double>(7);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Recupera todos los elementos con el nombre brindado
        NodeList flowList = this.doc.getElementsByTagName("Integrador");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("ConsumoCalculado".equals(childNode.getNodeName())) {
                    //Revision de la cantidad máxima de datos permitidos (12)
                    if (contador > 11) {
                        this.agregarError("16");
                        continuar = false;
                        break;
                    } else {
                        //Se acceden a los nodos previos a consumo calculado para comprobar su tipo de magnnitud

                        //Numero Ruedas Decimales
                        Node sibling = childNode.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Enteras
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //Constante Multiplicadora
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //Codigo Periodo
                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }
                        //Obtiene solo el ultimo numero de el codigo periodo 62 -> 2
                        String defPeriodo = sibling3.getTextContent().substring(sibling3.getTextContent().length() - 1);

                        //Magnitud
                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        //Revisa si el nodo de magnitud corresponde con el valor proporcionado
                        if ("R".equals(sibling4.getTextContent().toUpperCase().substring(0, 1))) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        //suma de los elementos
        double suma = 0;
        for (Object elemento : elementos) {
            suma += (double) elemento;
        }
        elementos.add(suma);

        this.imprimirResultado("rConsumo_A", elementos);
        return new DatosRConsumo(elementos);
    }

    protected DatosRLecturaDesde rLecturaDesde_A() {
        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Recupera todos los elementos con el nombre brindado
        NodeList flowList = this.doc.getElementsByTagName("LecturaDesde");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Lectura".equals(childNode.getNodeName())) {
                    //Revision de que aún sean aceptados los elementos
                    if (contador > 11) {
                        this.agregarError("17");
                        continuar = false;
                        break;
                    }

                    //Lee el nodo padre (LecturaDesde)
                    Node padre = childNode.getParentNode();
                    while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                        padre = padre.getParentNode();
                    }

                    //Lee todos los nodos en reversa
                    //Consumo calculado
                    Node sibling = padre.getPreviousSibling();
                    while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                        sibling = sibling.getPreviousSibling();
                    }

                    //Numero de ruedas decimales
                    Node sibling1 = sibling.getPreviousSibling();
                    while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                        sibling1 = sibling1.getPreviousSibling();
                    }

                    //Numero de ruedas enteras
                    Node sibling2 = sibling1.getPreviousSibling();
                    while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                        sibling2 = sibling2.getPreviousSibling();
                    }

                    //Constante multiplicadora
                    Node sibling3 = sibling2.getPreviousSibling();
                    while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                        sibling3 = sibling3.getPreviousSibling();
                    }

                    //Codigo periodo
                    Node sibling4 = sibling3.getPreviousSibling();
                    while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                        sibling4 = sibling4.getPreviousSibling();
                    }
                    //Lectura del ultimo caracter del codigo periodo
                    String defPeriodo = sibling4.getTextContent().substring(sibling4.getTextContent().length() - 1);

                    //Magnitud
                    Node sibling5 = sibling4.getPreviousSibling();
                    while (null != sibling5 && sibling5.getNodeType() != Node.ELEMENT_NODE) {
                        sibling5 = sibling5.getPreviousSibling();
                    }

                    //Revisa si el nodo de magnitud corresponde con el valor proporcionado
                    if ("R".equals(sibling5.getTextContent().toUpperCase().substring(0, 1))) {

                        //Variable de control para revisar la cantidad de elementos coincidentes
                        contador++;

                        //Se revisa la tarifa a considerar
                        switch (this.tarifaAtrFac) {
                            case "3":
                            case "7":
                            case "8":
                            case "11":
                            case "12":
                            case "13":
                            case "14":
                            case "19":
                            case "20":
                            case "21":
                                //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                if (!"0".equals(defPeriodo)) {
                                    //Evaluacion de los periodos
                                    switch (defPeriodo) {
                                        case "1":
                                            elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "2":
                                            elementos.set(1, (double) elementos.get(1) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "3":
                                            elementos.set(2, (double) elementos.get(2) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "4":
                                            elementos.set(3, (double) elementos.get(3) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "5":
                                            elementos.set(4, (double) elementos.get(4) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case "6":
                                            elementos.set(5, (double) elementos.get(5) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        default:
                                            System.out.println("No se encontro el codigo de periodo valido");
                                            break;
                                    }
                                }
                                break;
                            case "4":
                            case "6":
                            case "18":
                                //Se verifica la empresa para definir los casos especiales
                                if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                    //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                    char periodo = sibling4.getTextContent().charAt(0);
                                    switch (periodo) {
                                        case '1':
                                            elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case '2':
                                            elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        case '3':
                                            elementos.set(2, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                            break;
                                        default:
                                            break;
                                    }

                                } else {
                                    //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                    if (!defPeriodo.equals("0")) {
                                        //Se evaluan los elementos que pueden coincidir con la descripción
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                break;
                            case "1":
                            case "5":
                                //Se verifica que los elementos coincidan con 0
                                if (defPeriodo.equals("0")) {
                                    elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                }
                                break;
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("rLecturaDesde_A", elementos);
        return new DatosRLecturaDesde(elementos);
    }

    protected DatosRLecturaHasta rLecturaHasta_A() {

        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Búsqueda en el nodo
        NodeList flowList = this.doc.getElementsByTagName("LecturaHasta");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Lectura".equals(childNode.getNodeName())) {
                    if (contador > 11) {
                        this.agregarError("18");
                        continuar = false;
                        break;
                    } else {
                        //Acceso al nodo padre, y nodos hermanos para verificar la magnitud y codigo periodo
                        //LecturaHasta
                        Node padre1 = childNode.getParentNode();
                        while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                            padre1 = padre1.getParentNode();
                        }

                        //LecturaDesde
                        Node padre = padre1.getPreviousSibling();
                        while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getPreviousSibling();
                        }

                        //Consumo Calculado
                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Decimales
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //NumeroRuedas Enteras
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //ConstanteMultiplicadora
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

                        //Revisa si el nodo de magnitud corresponde con el valor proporcionado
                        if ("R".equals(sibling5.getTextContent().toUpperCase().substring(0, 1))) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("rLecturaHasta_A", elementos);
        return new DatosRLecturaHasta(elementos);
    }

    protected ReactivaImporteTotal rImporteTotal_A() {

        elementos = new ArrayList<String>(1);

        if (this.doc.getElementsByTagName("ImporteTotalEnergiaReactiva").getLength() != 0) {
            elementos.add(
                    String.valueOf(
                            -Double.parseDouble(
                                    this.doc.getElementsByTagName("ImporteTotalEnergiaReactiva").item(0).getTextContent()
                            )
                    )
            );
        } else {
            elementos.add("0.0");
        }

        this.imprimirResultado("rImporteTotal", elementos);
        return new ReactivaImporteTotal(elementos);
    }

    //PM - A (Negactivos)
    protected DatosPmConsumo pmConsumo_A() {

        //Obtiene 12 posibles valores, los organiza en 6 elementos correspondientes y los suma
        //Estos valores han sido declarados debido a que se pueden encontrar en distintos ordenes
        elementos = new ArrayList<Double>(7);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Recupera todos los elementos con el nombre brindado
        NodeList flowList = this.doc.getElementsByTagName("Integrador");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("ConsumoCalculado".equals(childNode.getNodeName())) {
                    //Revision de la cantidad máxima de datos permitidos (12)
                    if (contador > 11) {
                        this.agregarError("19");
                        continuar = false;
                        break;
                    } else {
                        //Se acceden a los nodos previos a consumo calculado para comprobar su tipo de magnnitud

                        //Numero Ruedas Decimales
                        Node sibling = childNode.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Enteras
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //Constante Multiplicadora
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //Codigo Periodo
                        Node sibling3 = sibling2.getPreviousSibling();
                        while (null != sibling3 && sibling3.getNodeType() != Node.ELEMENT_NODE) {
                            sibling3 = sibling3.getPreviousSibling();
                        }
                        //Obtiene solo el ultimo numero de el codigo periodo 62 -> 2
                        String defPeriodo = sibling3.getTextContent().substring(sibling3.getTextContent().length() - 1);

                        //Magnitud
                        Node sibling4 = sibling3.getPreviousSibling();
                        while (null != sibling4 && sibling4.getNodeType() != Node.ELEMENT_NODE) {
                            sibling4 = sibling4.getPreviousSibling();
                        }

                        //Revisa si el nodo de magnitud corresponde con el valor proporcionado
                        if ("PM".equals(sibling4.getTextContent().toUpperCase())) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }

        //suma de los elementos
        double suma = 0;
        for (Object elemento : elementos) {
            suma += (double) elemento;
        }
        elementos.add(suma);

        this.imprimirResultado("pmConsumo", elementos);
        return new DatosPmConsumo(elementos);
    }

    protected DatosPmLecturaHasta pmLecturaHasta_A() {

        //Obtiene 12 lecturas  y las suma en 6 correspondientes
        elementos = new ArrayList<Double>(6);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);
        elementos.add(0.0);

        int contador = 0;
        boolean continuar = true;

        //Búsqueda en el nodo
        NodeList flowList = this.doc.getElementsByTagName("LecturaHasta");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("Lectura".equals(childNode.getNodeName())) {
                    if (contador > 11) {
                        this.agregarError("20");
                        continuar = false;
                        break;
                    } else {
                        //Acceso al nodo padre, y nodos hermanos para verificar la magnitud y codigo periodo
                        //LecturaHasta
                        Node padre1 = childNode.getParentNode();
                        while (null != padre1 && padre1.getNodeType() != Node.ELEMENT_NODE) {
                            padre1 = padre1.getParentNode();
                        }

                        //LecturaDesde
                        Node padre = padre1.getPreviousSibling();
                        while (null != padre && padre.getNodeType() != Node.ELEMENT_NODE) {
                            padre = padre.getPreviousSibling();
                        }

                        //Consumo Calculado
                        Node sibling = padre.getPreviousSibling();
                        while (null != sibling && sibling.getNodeType() != Node.ELEMENT_NODE) {
                            sibling = sibling.getPreviousSibling();
                        }

                        //Numero Ruedas Decimales
                        Node sibling1 = sibling.getPreviousSibling();
                        while (null != sibling1 && sibling1.getNodeType() != Node.ELEMENT_NODE) {
                            sibling1 = sibling1.getPreviousSibling();
                        }

                        //NumeroRuedas Enteras
                        Node sibling2 = sibling1.getPreviousSibling();
                        while (null != sibling2 && sibling2.getNodeType() != Node.ELEMENT_NODE) {
                            sibling2 = sibling2.getPreviousSibling();
                        }

                        //ConstanteMultiplicadora
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

                        //Revisa si el nodo de magnitud corresponde con el valor proporcionado
                        if ("PM".equals(sibling5.getTextContent().toUpperCase())) {

                            //Variable de control para revisar la cantidad de elementos coincidentes
                            contador++;

                            //Se revisa la tarifa a considerar
                            switch (this.tarifaAtrFac) {
                                case "3":
                                case "7":
                                case "8":
                                case "11":
                                case "12":
                                case "13":
                                case "14":
                                case "19":
                                case "20":
                                case "21":
                                    //Se revisa que la definicion del segundo caracter del periodo sea diferente de 0
                                    if (!"0".equals(defPeriodo)) {
                                        //Evaluacion de los periodos
                                        switch (defPeriodo) {
                                            case "1":
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "2":
                                                elementos.set(1, (double) elementos.get(1) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "3":
                                                elementos.set(2, (double) elementos.get(2) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "4":
                                                elementos.set(3, (double) elementos.get(3) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "5":
                                                elementos.set(4, (double) elementos.get(4) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case "6":
                                                elementos.set(5, (double) elementos.get(5) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                System.out.println("No se encontro el codigo de periodo valido");
                                                break;
                                        }
                                    }
                                    break;
                                case "4":
                                case "6":
                                case "18":
                                    //Se verifica la empresa para definir los casos especiales
                                    if (this.EmpresaEmisora.equals("0022") || this.EmpresaEmisora.equals("0113") || this.EmpresaEmisora.equals("0212")) {

                                        //Se obtiene y evalua en primer caracter de la definicion del periodo}
                                        char periodo = sibling4.getTextContent().charAt(0);
                                        switch (periodo) {
                                            case '1':
                                                elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '2':
                                                elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            case '3':
                                                elementos.set(2, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                break;
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripción
                                            switch (defPeriodo) {
                                                case "1":
                                                    elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                case "2":
                                                    elementos.set(1, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                                case "1":
                                case "5":
                                    //Se verifica que los elementos coincidan con 0
                                    if (defPeriodo.equals("0")) {
                                        elementos.set(0, (double) elementos.get(0) - Double.parseDouble(childList.item(j).getTextContent().trim()));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("pmLecturaHasta_A", elementos);
        return new DatosPmLecturaHasta(elementos);
    }

    //RegistroFin
    protected DatosFinDeRegistro registroFin() {

        elementos = new ArrayList<String>();
        elementos.add("0.0");
        elementos.add("0.0");
        elementos.add("0");
        elementos.add("");
        elementos.add("");
        elementos.add("");

        //Recupera todos los elementos con el nombre de Cabecera
        NodeList flowList = this.doc.getElementsByTagName("RegistroFin");

        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "ImporteTotal":
                            elementos.set(0, String.valueOf(Double.parseDouble(childList.item(j).getTextContent().trim())));
                            break;
                        case "SaldoTotalFacturacion":
                            elementos.set(1, String.valueOf(Double.parseDouble(childList.item(j).getTextContent().trim())));
                            break;
                        case "TotalRecibos":
                            elementos.set(2, String.valueOf(Integer.parseInt(childList.item(j).getTextContent().trim())));
                            break;
                        case "FechaValor":
                            elementos.set(3, childList.item(j).getTextContent().trim());
                            break;
                        case "FechaLimitePago":
                            elementos.set(4, childList.item(j).getTextContent().trim());
                            break;
                        case "IdRemesa":
                            if (childList.item(j).getTextContent().trim().substring(0, 1).equals("0")) {
                                elementos.set(5, String.valueOf(Integer.parseInt(childList.item(j).getTextContent().trim())));
                            } else {
                                elementos.set(5, childList.item(j).getTextContent().trim());
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        this.imprimirResultado("registroFin", elementos);
        return new DatosFinDeRegistro(elementos);
    }

    //Concepto Repercutible
    protected ConceptoRepercutible conceptoRepercutible() {

        elementos = new ArrayList<String>(2);
        elementos.add("0.0");
        elementos.add("0.0");

        NodeList flowList = this.doc.getElementsByTagName("ConceptoRepercutible");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "ConceptoRepercutible":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            break;
                        case "ImporteTotalConceptoRepercutible":
                            elementos.set(1, Double.parseDouble(childList.item(j).getTextContent().trim()) + (Double.parseDouble(elementos.get(1).toString())));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        this.imprimirResultado("conceptoRepercutible", elementos);
        return new ConceptoRepercutible(elementos);
    }

    /*-----------------------------Otros Metodos-------------------------*/
    private void verificarPotenciaAFacturar(double suma) {
        String impTotTerPot = xml.obtenerContenidoNodo(NombresNodos.IMPORTE_TOTAL_TERMINO_POTENCIA, this.doc);
        if (impTotTerPot != null){
            double importeTotalTerminoPotencia = Double.parseDouble(impTotTerPot);
            int numDias = Integer.parseInt(xml.obtenerContenidoNodo(NombresNodos.NUMERO_DIAS, this.doc));
            double importeCalculado = new BigDecimal(numDias * suma).setScale(2, RoundingMode.HALF_UP).doubleValue();
            if (importeCalculado != importeTotalTerminoPotencia) {
                utileria.ArchivoTexto.escribirAdvertencia(this.nombreArchivo, "22");
            }
        }
    }

    /*-----------------------------Globales-------------------------*/
    protected void iniciarVariables() throws MasDeUnClienteEncontrado {
        this.errores = new StringBuilder("");
        this.comentarios = new StringBuilder("");
        this.cups = this.doc.getElementsByTagName("CUPS").item(0).getTextContent();
        this.codFactura = this.doc.getElementsByTagName("CodigoFiscalFactura").item(0).getTextContent();
        this.empEmi = Integer.parseInt(this.doc.getElementsByTagName("CodigoREEEmpresaEmisora").item(0).getTextContent());
        this.tipoFactura = this.doc.getElementsByTagName("TipoFactura").item(0).getTextContent();
        this.cliente = this.clienteService.encontrarCups(this.cups);
    }

    private void iniciarVariablesPagoRemesa() {
        this.codigoRemesa = this.doc.getElementsByTagName("CodigoRemesa").item(0).getTextContent();
    }

    private void iniciarVariablesCambioComercializador() {
        this.empEmi = Integer.parseInt(this.doc.getElementsByTagName("CodigoREEEmpresaEmisora").item(0).getTextContent());
        this.cups = this.doc.getElementsByTagName("CUPS").item(0).getTextContent();
    }

    /*--------------------------Utilidades---------------------------*/
    void imprimirResultado(String nombreMetodo, ArrayList elementos) {
        System.out.println(Cadenas.LINEA + nombreMetodo + elementos);
    }

    private void agregarError(String codError) {
        if (this.errores.toString().equals("")) {
            this.errores.append(codError);
        } else {
            this.errores.append(", ").append(codError);
        }
        utileria.ArchivoTexto.escribirAdvertencia(this.nombreArchivo, codError);
    }

    private void validarTarifa() throws MasDeUnClienteEncontrado, TarifaNoExisteException {
        this.cliente = this.clienteService.encontrarCups(this.cups);
        //Cliente c = new ClienteDao().encontrarCups(new Cliente(this.cups));
        String control = "";
        String tarifa = this.cliente.getTarifa();

        switch (tarifa) {
            case "20A":
                control = "1";
                break;
            case "21A":
                control = "5";
                break;
            case "20DHA":
                control = "4";
                break;
            case "21DHA":
                control = "6";
                break;
            case "30A":
                control = "3";
                break;
            case "31A":
                control = "11";
                break;
            case "61A":
                control = "12";
                break;
            case "62A":
                control = "13";
                break;
            case "20TD":
                control = "18";
                break;
            case "30TD":
                control = "19";
                break;
            case "61TD":
                control = "20";
                break;
            case "62TD":
                control = "21";
                break;
            default:
                throw new TarifaNoExisteException(tarifa);
        }

        if (!control.equals(this.tarifaAtrFac)) {
            System.out.println("Inconsistencia en el factor de tarifa, se encontro " + this.tarifaAtrFac + " en donde se esperaba " + control);
            this.agregarError("21");
        }
    }

    /**
     * Obtiene el contenido de un nodo en un String
     *
     * @param nodo
     * @return
     */
    String obtenerContenidoNodo(String nodo) {
        return this.doc.getElementsByTagName(nodo).item(0).getTextContent();
    }

    /**
     * Obtiene el valor de un nodo de una lista que generalmente viene desde un
     * ciclo for lo transforma a entero y el resultado lo divide entre 1000
     *
     * @param nodeList
     * @param index
     * @return
     */
    Integer obtenerContenidoNodoSobre1000(NodeList nodeList, int index) {
        return StringHelper.toInteger(nodeList.item(index).getTextContent()) / 1000;
    }


    
    enum TIPO_FACTURA{
    	A_ABONO, 
    	N_NORMAL, 
    	R_RECTIFICADA,
        C_;
    }

}
