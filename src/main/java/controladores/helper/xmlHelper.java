package controladores.helper;

import datos.entity.Cliente;
import datos.entity.EnergiaExcedentaria;
import datos.entity.Factura;
import datos.entity.OtraFactura;
import datos.entity.Peaje;
import datos.interfaces.ClienteService;
import dominio.componentesxml.*;
import excepciones.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.w3c.dom.*;
import utileria.texto.Cadenas;
import datos.interfaces.DocumentoXmlService;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.NonUniqueResultException;
import utileria.StringHelper;
import utileria.xml;

@Component
public class xmlHelper {

    public DocumentoXmlService contenidoXmlService;
    ClienteService clienteService;
    private ArrayList elementos;
    Document doc;
    StringBuilder errores;
    StringBuilder comentarios;
    private String cups;
    private String codFactura;
    private int empEmi;
    private String tipoFactura;
    Cliente cliente;
    private String EmpresaEmisora;
    private String tarifaAtrFac;
    private String nombreArchivo;
    private String codigoRemesa;
    private boolean existeEnergiaExcedentaria;
    private Logger logger = Logger.getLogger(getClass().getName());
    @Value("${abono.factura.validaterectificadaanulada}")
    private boolean isValidPeajeAbonoActive;
    @Value("${jdbc.password}")
    String anyString;

    public xmlHelper(){}

    public xmlHelper(Document doc, DocumentoXmlService contenidoXmlService, ClienteService clienteService) {
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

    /**
     * Aplica para todos los archivos que vengan con el nodo identificador
     * "Otras Facturas" guarda el registro en la tabla denominada
     * "contenido_xml_otras_facturas"
     *
     * @param nombreArchivo referencia al nombre del archivo actual
     * @throws excepciones.FacturaYaExisteException
     * @throws excepciones.ClienteNoExisteException
     * @throws excepciones.PeajeTipoFacturaNoSoportadaException
     * @throws excepciones.CodRectNoExisteException
     * @throws excepciones.MasDeUnClienteEncontrado
     * @throws PeajeMasDeUnRegistroException 
     */
    public void procesarOtrasFacturas(String nombreArchivo)
            throws FacturaYaExisteException, ClienteNoExisteException, MasDeUnClienteEncontrado, PeajeMasDeUnRegistroException {
        //Se verifica que el cliente exista
        this.cliente = this.clienteService.encontrarCups(this.cups);
        if (this.cliente != null) {
            //Se revisa que la factura no exista
            try {
                this.contenidoXmlService.buscarByCodFiscal(codFactura);
                logger.log(Level.INFO, ">>> Ya existe un registro con el codigo Fiscal {0} en OtrasFacturas", this.codFactura);
                throw new FacturaYaExisteException(codFactura, "otraFactura");
            }catch (RegistroVacioException e){
                logger.log(Level.INFO, ">>> Nuevo registro en OtrasFacturas {0}", this.codFactura);
            }

            this.nombreArchivo = nombreArchivo;
            this.comentarios.append("Nombre de archivo original: <Strong>").append(this.nombreArchivo).append("</Strong><br/>");
            this.registrarOtraFactura();

        } else {
            throw new ClienteNoExisteException(cups);
        }
    }

    /**
     * Aplica para todos los registros que tengan 0894 como empresaEmisora Lee
     * los datos y registra en la tabla denominada "contenido_xml_facturas"
     *
     * @param nombreArchivo referencia al nombre del archivo actual
     * @throws FacturaYaExisteException
     * @throws ClienteNoExisteException
     * @throws PeajeTipoFacturaNoSoportadaException
     * @throws CodRectNoExisteException
     * @throws MasDeUnClienteEncontrado
     * @throws PeajeMasDeUnRegistroException 
     */
    public void procesarFactura(String nombreArchivo)
            throws FacturaYaExisteException, ClienteNoExisteException, PeajeTipoFacturaNoSoportadaException, CodRectNoExisteException, MasDeUnClienteEncontrado, TarifaNoExisteException, PeajeMasDeUnRegistroException {
        //Se verifica que el cliente exista
        this.cliente = this.clienteService.encontrarCups(this.cups);
        if (this.cliente != null) {
            //Se revisa que la factura no exista
            try {
                this.contenidoXmlService.buscarByCodFiscal(codFactura);
                logger.log(Level.INFO, ">>> Ya existe una factura con el codigo Fiscal {0}", this.codFactura);
                throw new FacturaYaExisteException(codFactura, "factura");
            } catch (RegistroVacioException e) {
                logger.log(Level.INFO, ">>> Nuevo registro en Facturas {0}", this.codFactura);
            }

            this.nombreArchivo = nombreArchivo;
            this.comentarios.append("Nombre de archivo original: <Strong>").append(this.nombreArchivo).append("</Strong><br/>");
            logger.log(Level.INFO, ">>> Tipo Factura {0}", this.tipoFactura);
            switch (this.tipoFactura) {
                case "A":
                    this.registrarFacturaA();
                    break;
                case "N":
                case "G":
                    this.registrarFacturaN();
                    break;
                case "R":
                    this.registrarFacturaR(nombreArchivo);
                    break;
                default:
                    throw new PeajeTipoFacturaNoSoportadaException(tipoFactura);
            }
        } else {
            throw new ClienteNoExisteException(cups);
        }
    }

    /**
     * Lee los datos y registra en la tabla denominada "contenido_xml"
     *
     * @param nombreArchivo referencia al nombre del archivo actual
     * @throws FacturaYaExisteException
     * @throws ClienteNoExisteException
     * @throws PeajeTipoFacturaNoSoportadaException
     * @throws CodRectNoExisteException
     * @throws excepciones.MasDeUnClienteEncontrado
     * @throws PeajeMasDeUnRegistroException 
     */
    public void procesarPeaje(String nombreArchivo)
            throws FacturaYaExisteException, ClienteNoExisteException, PeajeTipoFacturaNoSoportadaException, CodRectNoExisteException, NonUniqueResultException, MasDeUnClienteEncontrado, PeajeCodRectNoExisteException, TarifaNoExisteException, PeajeMasDeUnRegistroException {
        //Se verifica que el cliente exista
        this.cliente = this.clienteService.encontrarCups(this.cups);
        if (this.cliente != null) {
            //Se revisa que la factura no exista
            try {
                this.contenidoXmlService.buscarByCodFiscal(codFactura);
                logger.log(Level.INFO, ">>> Ya existe un peaje con el codigo Fiscal {0}", this.codFactura);
                throw new FacturaYaExisteException(codFactura, "peajes");
            }catch (RegistroVacioException e){
                logger.log(Level.INFO, ">>> Nuevo registro en Peajes {0}", this.codFactura);
            }

            this.nombreArchivo = nombreArchivo;
            this.comentarios.append("Nombre de archivo original: <Strong>").append(this.nombreArchivo).append("</Strong><br/>");
            switch (this.tipoFactura) {
                case "A":
                    this.registrarPeajeA();
                    break;
                case "N":
                case "G":
                    this.registrarPeajeN();
                    break;
                case "R":
                    this.registrarPeajeR(nombreArchivo);
                    break;
                default:
                    throw new PeajeTipoFacturaNoSoportadaException(tipoFactura);
            }

        } else {
            throw new ClienteNoExisteException(cups);
        }
    }

    /*------------------------Registro de Peajes--------------------------------------*/
    /**
     * Busca la factura para Abonar, de lo contrario arroja una exception
     * Registra los el tipos de Peajes A
     * @throws PeajeMasDeUnRegistroException 
     *
     * @throws CodRectNoExisteException
     */
    private void registrarPeajeA() throws MasDeUnClienteEncontrado, PeajeCodRectNoExisteException, TarifaNoExisteException, PeajeMasDeUnRegistroException {
        System.out.println(isValidPeajeAbonoActive);
        System.out.println(anyString);
        String codRectificada = null;
        try {
            codRectificada = xml.obtenerContenidoNodo(NombresNodos.COD_FAC_REC_ANU, this.doc);
            Peaje peaje = (Peaje) this.contenidoXmlService.buscarByCodFiscal(codRectificada);
            comentarios.append(", este abono hace referencia al CodigoFacturaRectificadaAnulada <Strong> ").append(peaje.getCodFisFac()).append("</Strong>");
            this.registrarPeajeNA();
        }catch (RegistroVacioException e) {
            logger.log(Level.INFO, ">>> No se encontró un Peaje para rectificar con {0}", codRectificada);
            throw new PeajeCodRectNoExisteException(codFactura);
        } catch (NullPointerException e) {
            logger.log(Level.INFO, ">>> El Abono de peaje {0} no especifica un codigo de factura para rectificar", xml.obtenerContenidoNodo(NombresNodos.COD_FIS_FAC, this.doc));
            throw new PeajeCodRectNoExisteException(codFactura);
        }
    }

    /**
     * Registra el peaje de tipo N
     */
    private void registrarPeajeN() throws MasDeUnClienteEncontrado, TarifaNoExisteException {
    	TIPO_FACTURA tp = TIPO_FACTURA.N_NORMAL;
        Peaje peaje = new Peaje(
                this.cliente, this.cabecera(), this.datosGenerales(), this.datosTerminoPotencia(), this.datosFacturaAtr(),
                this.potenciaExcesos(), this.potenciaContratada(), this.potenciaDemandada(), this.potenciaAFacturar(), this.potenciaPrecio(), this.potenciaImporteTotal(),
                this.energiaActivaDatos(), this.energiaActivaValores(), this.energiaActivaPrecio(), this.energiaActivaImporteTotal(),
                this.Cargos("01", tp), this.Cargos("02", tp), this.ImporteTotalCargos("01", tp), this.ImporteTotalCargos("02", tp),
                this.impuestoElectrico(), this.alquileres(), this.iva(),
                this.aeConsumo(), this.aeLecturaDesde(), this.aeLecturaHasta(), this.aeProcedenciaDesde(), this.aeProcedenciaHasta(),
                this.rConsumo(), this.rLecturaDesde(), this.rLecturaHasta(), this.rImporteTotal(),
                this.pmConsumo(), this.pmLecturaHasta(),
                this.registroFin(), this.comentarios.toString(), this.errores.toString()
        );
        EnergiaExcedentaria energiaExcedentaria = this.energiaExcedentaria();
        if (existeEnergiaExcedentaria) {
            existeEnergiaExcedentaria = false;
            peaje.setEnergiaExcedentaria(energiaExcedentaria);
        }
        this.contenidoXmlService.guardar(peaje);
        System.out.print("\n\nComentarios : " + comentarios.toString());
        System.out.print("Codigo Errores : " + errores.toString());
    }

    /**
     * Busca que el Peaje a rectificar exista, cuando la factura ya existe
     * arroja una excepcion, de lo contraro hara un proceso de rectificación y
     * usara el metodo registrarN para guardar en registro en la BD
     *
     * @param nombreArchivo
     * @throws CodRectNoExisteException
     * @throws PeajeMasDeUnRegistroException 
     */
    private void registrarPeajeR(String nombreArchivo) throws CodRectNoExisteException, MasDeUnClienteEncontrado, TarifaNoExisteException, PeajeMasDeUnRegistroException {
        String codRectificada = xml.obtenerContenidoNodo(NombresNodos.COD_FAC_REC_ANU, this.doc);
        try {
            Peaje peaje = (Peaje) this.contenidoXmlService.buscarByCodFiscal(codRectificada);
            String nuevaRemesa = String.valueOf(Long.parseLong(xml.obtenerContenidoNodo(NombresNodos.ID_REM, this.doc)));
            this.contenidoXmlService.rectificar(peaje, nuevaRemesa, nombreArchivo);
            this.registrarPeajeN();
        } catch (RegistroVacioException e) {
            logger.log(Level.INFO, ">>> No se encontró una factura para rectificar con {0}", codRectificada);
            throw new CodRectNoExisteException(codRectificada);
        }
    }

    /*------------------------Registro de Facturas--------------------------------------*/
    /**
     * Registra los el tipos de Factura A (Abono)
     * Se obtienen todos los valores absolutos y se registran con valores negativos en la tabla de contenido_xml_facturas
     * No se necesita un registro existente para ejecutar esta factura
     *
     * @throws CodRectNoExisteException
     * @throws PeajeMasDeUnRegistroException 
     */
    private void registrarFacturaA() throws CodRectNoExisteException, PeajeMasDeUnRegistroException {
        logger.log(Level.INFO, ">>> Registrando factura del tipo Abono con el codFisFac {0}", xml.obtenerContenidoNodo(NombresNodos.COD_FIS_FAC, this.doc));
        try {
            Factura f = new Factura(
                    this.cliente, this.cabecera(), this.datosGenerales(), this.datosFacturaAtr(),
                    this.potenciaExcesos(), this.potenciaContratada(), this.potenciaDemandada(), this.potenciaAFacturar(), this.potenciaPrecio(), this.potenciaImporteTotal(),
                    this.energiaActivaDatos(), this.energiaActivaValores(), this.energiaActivaPrecio(), this.energiaActivaImporteTotal(),
                    this.impuestoElectrico(), this.alquileres(), this.iva(),
                    this.aeConsumo(), this.aeLecturaDesde(), this.aeLecturaHasta(), this.aeProcedenciaDesde(), this.aeProcedenciaHasta(),
                    this.rConsumo(), this.rLecturaDesde(), this.rLecturaHasta(), this.rImporteTotal(),
                    this.pmConsumo(), this.pmLecturaHasta(),
                    this.registroFin(), this.comentarios.toString(), this.errores.toString()
            );
            this.prepareAbonoFactura(f);
            //this.contenidoXmlService.guardar(f);
        } catch (Exception e) {

        }
        /*
        String codRectificada = xml.obtenerContenidoNodo(NombresNodos.COD_FAC_REC_ANU, this.doc);
        try {
            Factura f = (Factura) this.contenidoXmlService.buscarByCodFiscal(codRectificada);
            System.out.println("Se intenta realizar registro");
            //this.registrarFacturaA();
        }catch (RegistroVacioException e) {
            logger.log(Level.INFO, ">>> No se encontró una factura para abonar con {0}", codRectificada);
            throw new CodRectNoExisteException(codRectificada);
        }
         */
    }

    /**
     * Registra el Peaje de tipo A poniendo en negativo los valores de AE, R, PM
     * y los importes totales
     */
    private void registrarPeajeNA() throws MasDeUnClienteEncontrado, TarifaNoExisteException {
    	TIPO_FACTURA tp = TIPO_FACTURA.A_ABONO;
        Peaje peaje = new Peaje(
                this.cliente, this.cabecera(), this.datosGenerales(), this.datosTerminoPotencia(), this.datosFacturaAtr(),
                this.potenciaExcesos(), this.potenciaContratada(), this.potenciaDemandada(), this.potenciaAFacturar(), this.potenciaPrecio(), this.potenciaImporteTotal_A(),
                this.energiaActivaDatos(), this.energiaActivaValores(), this.energiaActivaPrecio(), this.energiaActivaImporteTotal_A(),
                this.Cargos("01", tp), this.Cargos("02", tp), this.ImporteTotalCargos("01", tp), this.ImporteTotalCargos("02", tp),
                this.impuestoElectrico(), this.alquileres(), this.iva(),
                this.aeConsumo_A(), this.aeLecturaDesde_A(), this.aeLecturaHasta_A(), this.aeProcedenciaDesde_A(), this.aeProcedenciaHasta_A(),
                this.rConsumo_A(), this.rLecturaDesde_A(), this.rLecturaHasta_A(), this.rImporteTotal_A(),
                this.pmConsumo_A(), this.pmLecturaHasta_A(),
                this.registroFin(), this.comentarios.toString(), this.errores.toString()
        );
        this.prepareAbonoPeaje(peaje);
        System.out.println("preparandoAbono");
        //this.contenidoXmlService.guardar(peaje);
        System.out.print("\n\nComentarios : " + comentarios.toString());
        System.out.print("Codigo Errores : " + errores.toString());
    }

    /**
     * Registra el Factura de tipo N
     */
    private void registrarFacturaN() throws MasDeUnClienteEncontrado, TarifaNoExisteException {
        this.contenidoXmlService.guardar(
                new Factura(
                        this.cliente, this.cabecera(), this.datosGenerales(), this.datosFacturaAtr(),
                        this.potenciaExcesos(), this.potenciaContratada(), this.potenciaDemandada(), this.potenciaAFacturar(), this.potenciaPrecio(), this.potenciaImporteTotal(),
                        this.energiaActivaDatos(), this.energiaActivaValores(), this.energiaActivaPrecio(), this.energiaActivaImporteTotal(),
                        this.impuestoElectrico(), this.alquileres(), this.iva(),
                        this.aeConsumo(), this.aeLecturaDesde(), this.aeLecturaHasta(), this.aeProcedenciaDesde(), this.aeProcedenciaHasta(),
                        this.rConsumo(), this.rLecturaDesde(), this.rLecturaHasta(), this.rImporteTotal(),
                        this.pmConsumo(), this.pmLecturaHasta(),
                        this.registroFin(), this.comentarios.toString(), this.errores.toString()
                )
        );
        System.out.print("\n\nComentarios : " + comentarios.toString());
        System.out.print("Codigo Errores : " + errores.toString());
    }

    /**
     * Busca que el Factura a rectificar exista, cuando la factura ya existe
     * arroja una excepcion, de lo contraro hara un proceso de rectificación y
     * usara el metodo registrarN para guardar en registro en la BD
     *
     * @param nombreArchivo
     * @throws CodRectNoExisteException
     * @throws PeajeMasDeUnRegistroException 
     */
    private void registrarFacturaR(String nombreArchivo) throws CodRectNoExisteException, MasDeUnClienteEncontrado, TarifaNoExisteException, PeajeMasDeUnRegistroException {
        String codRectificada = xml.obtenerContenidoNodo(NombresNodos.COD_FAC_REC_ANU, this.doc);
        try {
            Factura factura = (Factura) this.contenidoXmlService.buscarByCodFiscal(codRectificada);
            String nuevaRemesa = String.valueOf(Long.parseLong(xml.obtenerContenidoNodo(NombresNodos.ID_REM, this.doc)));
            this.contenidoXmlService.rectificar(factura, nuevaRemesa, nombreArchivo);
            this.registrarFacturaN();
        } catch (RegistroVacioException e) {
            logger.log(Level.INFO, ">>> No se encontró una factura para rectificar con {0}", codRectificada);
            throw new CodRectNoExisteException(codRectificada);
        }
    }

    /*------------------------Registro de Otras Facturas--------------------------------------*/
    /**
     * Registra OtrasFacturas
     */
    private void registrarOtraFactura() throws MasDeUnClienteEncontrado {
        this.contenidoXmlService.guardar(
                new OtraFactura(
                        this.cliente, this.cabecera(), this.datosGenerales(),
                        this.conceptoRepercutible(), this.registroFin(),
                        this.comentarios.toString(), this.errores.toString()
                )
        );
        System.out.print("\n\nComentarios : " + comentarios.toString());
        System.out.print("Codigo Errores : " + errores.toString());
    }

    /*------------------------Obtencion de datos --------------------------------------*/
    private DatosCabecera cabecera() {
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

    private DatosGeneralesFactura datosGenerales() {
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
    
    private DatosTerminoPotencia datosTerminoPotencia() {
        elementos = new ArrayList<String>(2);
        elementos.add("");
        elementos.add("");

        int indice = 0;
        boolean continuar = true;
        NodeList flowList = doc.getElementsByTagName("TerminoPotencia");
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
                        case "FechaDesde":
                            elementos.set(0, childList.item(j).getTextContent().trim());
                            indice++;
                            break;
                        case "FechaHasta":
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

    private DatosFacturaAtr datosFacturaAtr() throws MasDeUnClienteEncontrado, TarifaNoExisteException {
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
    private DatosPotenciaContratada potenciaContratada() {
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

    private DatosPotenciaMaxDemandada potenciaDemandada() {

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

    private DatosPotenciaAFacturar potenciaAFacturar() {

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

    private DatosPotenciaPrecio potenciaPrecio() {

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

    private DatosPotenciaImporteTotal potenciaImporteTotal() {

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
                        elementos.add(indicePeriodo++, Double.parseDouble(childListLecturaHasta.item(j).getTextContent().trim()));
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

    private DatosPotenciaImporteTotal potenciaImporteTotal_A() {

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

    private DatosExcesoPotencia potenciaExcesos() {

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
    private DatosEnergiaActiva energiaActivaDatos() {

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

    private DatosEnergiaActivaValores energiaActivaValores() {

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

    private DatosEnergiaActivaPrecio energiaActivaPrecio() {

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

    private DatosEnergiaActivaImporteTotal energiaActivaImporteTotal() {
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

    private DatosEnergiaActivaImporteTotal energiaActivaImporteTotal_A() {
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
    private EnergiaExcedentaria energiaExcedentaria() {

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
     * Looks for PrecioCargo node in a xmlFile
     * Maximum values of nodes expected is 12 
     * @param tipoCargoValue value that belongs to TotalImporteTipoCargo Node used to identify a block
     * @param tp TipoFactura value that changes the tipe of operation that is going to use
     */
    private Cargos Cargos(String tipoCargoValue, TIPO_FACTURA tp) {
        elementos =  (ArrayList) IntStream.range(0, 12).mapToDouble(i -> 0.0).boxed().collect(Collectors.toList());
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
    private CargoImporteTotal ImporteTotalCargos(String tipoCargoValue, TIPO_FACTURA tp) {
    	elementos =  (ArrayList) IntStream.range(0, 1).mapToDouble(i -> 0.0).boxed().collect(Collectors.toList());

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
    private DatosImpuestoElectrico impuestoElectrico() {
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

    private DatosAlquileres alquileres() {

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

    private DatosIva iva() {

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
    private DatosAeConsumo aeConsumo() {

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

        this.imprimirResultado("aeConsumo", elementos);
        return new DatosAeConsumo(elementos);
    }

    private DatosAeLecturaDesde aeLecturaDesde() {
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

    private DatosAeLecturaHasta aeLecturaHasta() {

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

    private DatosAeProcedenciaDesde aeProcedenciaDesde() {

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

    private DatosAeProcedenciaHasta aeProcedenciaHasta() {

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
    private DatosRConsumo rConsumo() {

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

    private DatosRLecturaDesde rLecturaDesde() {
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

    private DatosRLecturaHasta rLecturaHasta() {

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

    private ReactivaImporteTotal rImporteTotal() {

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
    private DatosPmConsumo pmConsumo() {

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

    private DatosPmLecturaHasta pmLecturaHasta() {

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
    private DatosAeConsumo aeConsumo_A() {

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

    private DatosAeLecturaDesde aeLecturaDesde_A() {
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

    private DatosAeLecturaHasta aeLecturaHasta_A() {

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

    private DatosAeProcedenciaDesde aeProcedenciaDesde_A() {

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

    private DatosAeProcedenciaHasta aeProcedenciaHasta_A() {

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
    private DatosRConsumo rConsumo_A() {

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

    private DatosRLecturaDesde rLecturaDesde_A() {
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

    private DatosRLecturaHasta rLecturaHasta_A() {

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

    private ReactivaImporteTotal rImporteTotal_A() {

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
    private DatosPmConsumo pmConsumo_A() {

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

    private DatosPmLecturaHasta pmLecturaHasta_A() {

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
    private DatosFinDeRegistro registroFin() {

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
    private ConceptoRepercutible conceptoRepercutible() {

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
        double importeTotalTerminoPotencia = Double.parseDouble(this.doc.getElementsByTagName("ImporteTotalTerminoPotencia").item(0).getTextContent());
        int numDias = Integer.parseInt(this.doc.getElementsByTagName("NumeroDias").item(0).getTextContent());

        double importeCalculado = new BigDecimal(numDias * suma).setScale(2, RoundingMode.HALF_UP).doubleValue();

        System.out.println(suma);
        System.out.println(numDias);
        System.out.println(importeTotalTerminoPotencia);

        if (importeCalculado != importeTotalTerminoPotencia) {
            System.out.println("Se esperaba encontrar el valor de " + importeTotalTerminoPotencia + ", en donde se calculó " + importeCalculado);
            utileria.ArchivoTexto.escribirAdvertencia(nombreArchivo, "22");
        } else {
            System.out.println("Importe total termino potencia OK " + importeTotalTerminoPotencia);
        }
    }

    /*-----------------------------Globales-------------------------*/
    private void iniciarVariables() {
        this.errores = new StringBuilder("");
        this.comentarios = new StringBuilder("");
        this.cups = this.doc.getElementsByTagName("CUPS").item(0).getTextContent();
        this.codFactura = this.doc.getElementsByTagName("CodigoFiscalFactura").item(0).getTextContent();
        this.empEmi = Integer.parseInt(this.doc.getElementsByTagName("CodigoREEEmpresaEmisora").item(0).getTextContent());
        this.tipoFactura = this.doc.getElementsByTagName("TipoFactura").item(0).getTextContent();
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

    private Peaje prepareAbonoPeaje(Peaje peaje) {
        logger.log(Level.INFO, ">>> Pasando valores especificos a negativo del abono de peaje {0}", peaje.getCodFisFac());
        peaje.setTipFac("A");
        String fecDes1 = peaje.getEaFecDes1();
        String fecHas1 = peaje.getEaFecHas1();
        String fecDes2 = peaje.getEaFecDes2();
        String fecHas2 = peaje.getEaFecHas2();
        peaje.setEaFecDes1(fecHas1);
        peaje.setEaFecHas1(fecDes1);
        peaje.setEaFecDes2(fecHas2);
        peaje.setEaFecHas2(fecDes2);
        return peaje;
    }

    private Factura prepareAbonoFactura(Factura factura){
        logger.log(Level.INFO, ">>> Pasando valores especificos a negativo del abono de factura {0}", factura.getCodFisFac());
        factura.setTipFac("A");
        String fecDes1 = factura.getEaFecDes1();
        String fecHas1 = factura.getEaFecHas1();
        String fecDes2 = factura.getEaFecDes2();
        String fecHas2 = factura.getEaFecHas2();
        factura.setEaFecDes1(fecHas1);
        factura.setEaFecHas1(fecDes1);
        factura.setEaFecDes2(fecHas2);
        factura.setEaFecHas2(fecDes2);

        factura.setNumDias(Utilidades.valorAbsolutoNegativo(factura.getNumDias()));

        //ExcesoPotencia
        factura.setExcPot1(Utilidades.valorAbsolutoNegativo(factura.getExcPot1()));
        factura.setExcPot2(Utilidades.valorAbsolutoNegativo(factura.getExcPot2()));
        factura.setExcPot3(Utilidades.valorAbsolutoNegativo(factura.getExcPot3()));
        factura.setExcPot4(Utilidades.valorAbsolutoNegativo(factura.getExcPot4()));
        factura.setExcPot5(Utilidades.valorAbsolutoNegativo(factura.getExcPot5()));
        factura.setExcPot6(Utilidades.valorAbsolutoNegativo(factura.getExcPot6()));
        factura.setExcImpTot(Utilidades.valorAbsolutoNegativo(factura.getExcImpTot()));

        //PotenciaContratada
        factura.setPotCon1(Utilidades.valorAbsolutoNegativo(factura.getPotCon1()));
        factura.setPotCon2(Utilidades.valorAbsolutoNegativo(factura.getPotCon2()));
        factura.setPotCon3(Utilidades.valorAbsolutoNegativo(factura.getPotCon3()));
        factura.setPotCon4(Utilidades.valorAbsolutoNegativo(factura.getPotCon4()));
        factura.setPotCon5(Utilidades.valorAbsolutoNegativo(factura.getPotCon5()));
        factura.setPotCon6(Utilidades.valorAbsolutoNegativo(factura.getPotCon6()));

        //PotenciaMaxDemandada
        factura.setPotMax1(Utilidades.valorAbsolutoNegativo(factura.getPotMax1()));
        factura.setPotMax2(Utilidades.valorAbsolutoNegativo(factura.getPotMax2()));
        factura.setPotMax3(Utilidades.valorAbsolutoNegativo(factura.getPotMax3()));
        factura.setPotMax4(Utilidades.valorAbsolutoNegativo(factura.getPotMax4()));
        factura.setPotMax5(Utilidades.valorAbsolutoNegativo(factura.getPotMax5()));
        factura.setPotMax6(Utilidades.valorAbsolutoNegativo(factura.getPotMax6()));

        //PotenciaAFacturar
        factura.setPotFac1(Utilidades.valorAbsolutoNegativo(factura.getPotFac1()));
        factura.setPotFac2(Utilidades.valorAbsolutoNegativo(factura.getPotFac2()));
        factura.setPotFac3(Utilidades.valorAbsolutoNegativo(factura.getPotFac3()));

        //PotenciaPrecio
        factura.setPotPre1(Utilidades.valorAbsolutoNegativo(factura.getPotPre1()));
        factura.setPotPre2(Utilidades.valorAbsolutoNegativo(factura.getPotPre2()));
        factura.setPotPre3(Utilidades.valorAbsolutoNegativo(factura.getPotPre3()));
        factura.setPotPre4(Utilidades.valorAbsolutoNegativo(factura.getPotPre4()));
        factura.setPotPre5(Utilidades.valorAbsolutoNegativo(factura.getPotPre5()));
        factura.setPotPre6(Utilidades.valorAbsolutoNegativo(factura.getPotPre6()));
        factura.setPotImpTot(Utilidades.valorAbsolutoNegativo(factura.getPotImpTot()));

        //EnergíaActiva
        factura.setEaVal1(Utilidades.valorAbsolutoNegativo(factura.getEaVal1()));
        factura.setEaVal2(Utilidades.valorAbsolutoNegativo(factura.getEaVal2()));
        factura.setEaVal3(Utilidades.valorAbsolutoNegativo(factura.getEaVal3()));
        factura.setEaVal4(Utilidades.valorAbsolutoNegativo(factura.getEaVal4()));
        factura.setEaVal5(Utilidades.valorAbsolutoNegativo(factura.getEaVal5()));
        factura.setEaVal6(Utilidades.valorAbsolutoNegativo(factura.getEaVal6()));
        factura.setEaValSum(Utilidades.valorAbsolutoNegativo(factura.getEaValSum()));

        //Precios
        factura.setEaPre1(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaPre2(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaPre3(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaPre4(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaPre5(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaPre6(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaImpTot(Utilidades.valorAbsolutoNegativo(factura.getEaImpTot()));

        factura.setIeImp(Utilidades.valorAbsolutoNegativo(factura.getIeImp()));
        factura.setaImpFac(Utilidades.valorAbsolutoNegativo(factura.getaImpFac()));
        factura.setiBasImp(Utilidades.valorAbsolutoNegativo(factura.getiBasImp()));

        //AEConsumo
        factura.setAeCon1(Utilidades.valorAbsolutoNegativo(factura.getAeCon1()));
        factura.setAeCon2(Utilidades.valorAbsolutoNegativo(factura.getAeCon2()));
        factura.setAeCon3(Utilidades.valorAbsolutoNegativo(factura.getAeCon3()));
        factura.setAeCon4(Utilidades.valorAbsolutoNegativo(factura.getAeCon4()));
        factura.setAeCon5(Utilidades.valorAbsolutoNegativo(factura.getAeCon5()));
        factura.setAeCon6(Utilidades.valorAbsolutoNegativo(factura.getAeCon6()));
        factura.setAeConSum(Utilidades.valorAbsolutoNegativo(factura.getAeConSum()));

        //AELecturasDesde
        factura.setAeLecDes1(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes1()));
        factura.setAeLecDes2(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes2()));
        factura.setAeLecDes3(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes3()));
        factura.setAeLecDes4(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes4()));
        factura.setAeLecDes5(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes5()));
        factura.setAeLecDes6(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes6()));

        //AELecturasHasta
        factura.setAeLecHas1(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas1()));
        factura.setAeLecHas2(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas2()));
        factura.setAeLecHas3(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas3()));
        factura.setAeLecHas4(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas4()));
        factura.setAeLecHas5(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas5()));
        factura.setAeLecHas6(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas6()));

        factura.setAeProDes(Utilidades.valorAbsolutoNegativo(factura.getAeProDes()));
        factura.setAeProHas(Utilidades.valorAbsolutoNegativo(factura.getAeProHas()));

        //Reactiva
        factura.setrCon1(Utilidades.valorAbsolutoNegativo(factura.getrCon1()));
        factura.setrCon2(Utilidades.valorAbsolutoNegativo(factura.getrCon2()));
        factura.setrCon3(Utilidades.valorAbsolutoNegativo(factura.getrCon3()));
        factura.setrCon4(Utilidades.valorAbsolutoNegativo(factura.getrCon4()));
        factura.setrCon5(Utilidades.valorAbsolutoNegativo(factura.getrCon5()));
        factura.setrCon6(Utilidades.valorAbsolutoNegativo(factura.getrCon6()));
        factura.setrConSum(Utilidades.valorAbsolutoNegativo(factura.getrConSum()));

        factura.setrLecDes1(Utilidades.valorAbsolutoNegativo(factura.getrLecDes1()));
        factura.setrLecDes2(Utilidades.valorAbsolutoNegativo(factura.getrLecDes2()));
        factura.setrLecDes3(Utilidades.valorAbsolutoNegativo(factura.getrLecDes3()));
        factura.setrLecDes4(Utilidades.valorAbsolutoNegativo(factura.getrLecDes4()));
        factura.setrLecDes5(Utilidades.valorAbsolutoNegativo(factura.getrLecDes5()));
        factura.setrLecDes6(Utilidades.valorAbsolutoNegativo(factura.getrLecDes6()));

        factura.setrLecHas1(Utilidades.valorAbsolutoNegativo(factura.getrLecHas1()));
        factura.setrLecHas2(Utilidades.valorAbsolutoNegativo(factura.getrLecHas2()));
        factura.setrLecHas3(Utilidades.valorAbsolutoNegativo(factura.getrLecHas3()));
        factura.setrLecHas4(Utilidades.valorAbsolutoNegativo(factura.getrLecHas4()));
        factura.setrLecHas5(Utilidades.valorAbsolutoNegativo(factura.getrLecHas5()));
        factura.setrLecHas6(Utilidades.valorAbsolutoNegativo(factura.getrLecHas6()));
        factura.setrImpTot(Utilidades.valorAbsolutoNegativo(factura.getrImpTot()));

        factura.setPmCon1(Utilidades.valorAbsolutoNegativo(factura.getPmCon1()));
        factura.setPmCon2(Utilidades.valorAbsolutoNegativo(factura.getPmCon2()));
        factura.setPmCon3(Utilidades.valorAbsolutoNegativo(factura.getPmCon3()));
        factura.setPmCon4(Utilidades.valorAbsolutoNegativo(factura.getPmCon4()));
        factura.setPmCon5(Utilidades.valorAbsolutoNegativo(factura.getPmCon5()));
        factura.setPmCon6(Utilidades.valorAbsolutoNegativo(factura.getPmCon6()));
        factura.setPmConSum(Utilidades.valorAbsolutoNegativo(factura.getPmConSum()));

        factura.setPmLecHas1(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas1()));
        factura.setPmLecHas2(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas2()));
        factura.setPmLecHas3(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas3()));
        factura.setPmLecHas4(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas4()));
        factura.setPmLecHas5(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas5()));
        factura.setPmLecHas6(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas6()));

        factura.setRfImpTot(Utilidades.valorAbsolutoNegativo(factura.getRfImpTot()));
        factura.setRfSalTotFac(Utilidades.valorAbsolutoNegativo(factura.getRfSalTotFac()));

        return factura;
    }
    
    enum TIPO_FACTURA{
    	A_ABONO, 
    	N_NORMAL, 
    	R_RECTIFICADA;
    }

}
