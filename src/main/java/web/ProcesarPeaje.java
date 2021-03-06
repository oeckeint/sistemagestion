package web;

import datos.ClienteDao;
import datos.ContenidoXmlDao;
import datos.DatosFacturaDao;
import dominio.Cliente;
import dominio.componentesxml.*;
import excepciones.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import org.w3c.dom.*;
import utileria.texto.Cadenas;

public class ProcesarPeaje {

    private ArrayList elementos;
    private Document doc;
    private StringBuilder errores;
    private StringBuilder comentarios;

    //Datos para el control del flujo
    private String cups;
    private String codFactura;
    private int empEmi;
    private String tipoFactura;
    private Cliente cliente;
    private String EmpresaEmisora;
    private String tarifaAtrFac;
    private String nombreArchivo;

    public ProcesarPeaje() {
        this.errores = new StringBuilder("");
    }

    public ProcesarPeaje(Document doc) {
        this.doc = doc;
        this.iniciarVariables();
    }

    public void procesar(String nombreArchivo) throws FacturaYaExisteException, ClienteNoExisteException, PeajeTipoFacturaNoSoportadaException, CodRectNoExisteException {
        this.cliente = new ClienteDao().encontrarCups(new Cliente(this.cups));
        if (this.cliente.getIdCliente() != 0) {
            if (!new DatosFacturaDao().encontrarCodFiscal(codFactura, empEmi)) {
                this.nombreArchivo = nombreArchivo;
                this.comentarios.append("Nombre del archivo original: <Strong>").append(this.nombreArchivo).append("</Strong><br/>");
                switch (this.tipoFactura) {
                    case "A":
                        this.registrarA();
                        break;
                    case "N":
                    case "G":
                        this.registrarN();
                        break;
                    case "R":
                        this.registrarR(nombreArchivo);
                        break;
                    default:
                        throw new PeajeTipoFacturaNoSoportadaException(tipoFactura);
                }
                if (this.errores.toString().contains("13")) {
                    System.out.println("Inconsistencia en tarifa detectada");
                }
            } else {
                throw new FacturaYaExisteException(codFactura);
            }
        } else {
            throw new ClienteNoExisteException(cups);
        }
    }

    /*------------------------Registro de facturas--------------------------------------*/
    private void registrarA() throws CodRectNoExisteException {
        String codRectificada = this.doc.getElementsByTagName("CodigoFacturaRectificadaAnulada").item(0).getTextContent();
        DocumentoXml documento = new ContenidoXmlDao().buscarCodFiscal(codRectificada, this.empEmi);
        if (documento != null) {
            this.registrarN();
        } else {
            System.out.println("(Procesar Peaje) No se encontro una factura para rectificar");
            throw new CodRectNoExisteException(codFactura);
        }
    }

    private void registrarN() {
        DocumentoXml documento = new DocumentoXml(
                this.cliente, this.cabecera(), this.datosGenerales(), this.datosFacturaAtr(),
                this.potenciaExcesos(), this.potenciaContratada(), this.potenciaDemandada(), this.potenciaAFacturar(), this.potenciaPrecio(), this.potenciaImporteTotal(),
                this.energiaActivaDatos(), this.energiaActivaValores(), this.energiaActivaPrecio(), this.energiaActivaImporteTotal(),
                this.Cargos01(), this.Cargos02(), this.ImporteTotalCargo01(), this.ImporteTotalCargo02(),
                this.impuestoElectrico(), this.alquileres(), this.iva(),
                this.aeConsumo(), this.aeLecturaDesde(), this.aeLecturaHasta(), this.aeProcedenciaDesde(), this.aeProcedenciaHasta(),
                this.rConsumo(), this.rLecturaDesde(), this.rLecturaHasta(), this.rImporteTotal(),
                this.pmConsumo(), this.pmLecturaHasta(),
                this.registroFin(), this.comentarios.toString(), this.errores.toString()
        );
        new ContenidoXmlDao().insertar(documento);

        System.out.print("\n\nComentarios : " + comentarios.toString());
        System.out.print("Codigo Errores : " + errores.toString());
    }

    private void registrarR(String nombreArchivo) throws CodRectNoExisteException {
        String codRectificada = this.doc.getElementsByTagName("CodigoFacturaRectificadaAnulada").item(0).getTextContent();
        DocumentoXml documento = new ContenidoXmlDao().buscarCodFiscal(codRectificada, empEmi);
        if (documento != null) {
            new ContenidoXmlDao().actualizarR(documento, this.codFactura, nombreArchivo);
            this.registrarN();
        } else {
            System.out.println("(Procesar Peaje) No se encontro una factura para rectificar");
            throw new CodRectNoExisteException(codFactura);
        }
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
        elementos = new ArrayList<String>(5);
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

        this.imprimirResultado("datosGenerales", elementos);
        return new DatosGeneralesFactura(elementos);
    }

    private DatosFacturaAtr datosFacturaAtr() {
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

        //Obtiene 6 posibles datos, organizandoles en 3 el valor m??ximo entre dos medidas (1:4, 2:5, 3:6)
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

        //B??squeda en el nodo
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

        //B??squeda en el nodo
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

    //Cargos
    private Cargos Cargos01() {

        elementos = new ArrayList<>(6);
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
                if (indice > elementos.size()) {
                    continuar = false;
                    this.agregarError("23");
                }
                Node childNode = childList.item(j);
                if ("PrecioCargo".equals(childNode.getNodeName())) {
                    Node tipoCargo = childNode.getParentNode().getParentNode().getPreviousSibling();
                    while (null != tipoCargo && tipoCargo.getNodeType() != Node.ELEMENT_NODE) {
                        tipoCargo = tipoCargo.getPreviousSibling();
                    }
                    if (tipoCargo.getTextContent().equals("01")) {
                        elementos.set(indice++, Double.parseDouble(childList.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("Cargos01", elementos);
        return new Cargos(elementos);
    }

    private CargoImporteTotal ImporteTotalCargo01() {
        elementos = new ArrayList<>(1);
        elementos.add(0.0);

        int indice = 0;
        boolean continuar = true;
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Cargo");
        //Se iteran los nodos correspondan con el flowList
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            //Se obtienen los nodos internos
            NodeList childList = flowListPeriodo.item(i).getChildNodes();
            //Se recorren los nodos internos
            for (int j = 0; j < childList.getLength(); j++) {
                if (indice > elementos.size()) {
                    continuar = false;
                    this.agregarError("25");
                }
                Node childNode = childList.item(j);
                if ("TotalImporteTipoCargo".equals(childNode.getNodeName())) {
                    Node terminoCargo = childNode.getPreviousSibling();
                    while (null != terminoCargo && terminoCargo.getNodeType() != Node.ELEMENT_NODE) {
                        terminoCargo = terminoCargo.getPreviousSibling();
                    }

                    Node tipoCargo = terminoCargo.getPreviousSibling();
                    while (null != tipoCargo && tipoCargo.getNodeType() != Node.ELEMENT_NODE) {
                        tipoCargo = tipoCargo.getPreviousSibling();
                    }

                    if (tipoCargo.getTextContent().equals("01")) {
                        elementos.set(indice++, Double.parseDouble(childList.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("TotalImporteTipoCargo01", elementos);
        return new CargoImporteTotal(elementos);
    }

    private Cargos Cargos02() {

        elementos = new ArrayList<>(6);
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
                if (indice > elementos.size()) {
                    continuar = false;
                    this.agregarError("24");
                }
                Node childNode = childList.item(j);
                if ("PrecioCargo".equals(childNode.getNodeName())) {
                    Node tipoCargo = childNode.getParentNode().getParentNode().getPreviousSibling();
                    while (null != tipoCargo && tipoCargo.getNodeType() != Node.ELEMENT_NODE) {
                        tipoCargo = tipoCargo.getPreviousSibling();
                    }
                    if (tipoCargo.getTextContent().equals("02")) {
                        elementos.set(indice++, Double.parseDouble(childList.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("Cargos02", elementos);
        return new Cargos(elementos);
    }

    private CargoImporteTotal ImporteTotalCargo02() {
        elementos = new ArrayList<>(1);
        elementos.add(0.0);

        int indice = 0;
        boolean continuar = true;
        NodeList flowListPeriodo = this.doc.getElementsByTagName("Cargo");
        //Se iteran los nodos correspondan con el flowList
        for (int i = 0; i < flowListPeriodo.getLength(); i++) {
            //Se obtienen los nodos internos
            NodeList childList = flowListPeriodo.item(i).getChildNodes();
            //Se recorren los nodos internos
            for (int j = 0; j < childList.getLength(); j++) {
                if (indice > elementos.size()) {
                    continuar = false;
                    this.agregarError("26");
                }
                Node childNode = childList.item(j);
                if ("TotalImporteTipoCargo".equals(childNode.getNodeName())) {
                    Node terminoCargo = childNode.getPreviousSibling();
                    while (null != terminoCargo && terminoCargo.getNodeType() != Node.ELEMENT_NODE) {
                        terminoCargo = terminoCargo.getPreviousSibling();
                    }

                    Node tipoCargo = terminoCargo.getPreviousSibling();
                    while (null != tipoCargo && tipoCargo.getNodeType() != Node.ELEMENT_NODE) {
                        tipoCargo = tipoCargo.getPreviousSibling();
                    }

                    if (tipoCargo.getTextContent().equals("02")) {
                        elementos.set(indice++, Double.parseDouble(childList.item(j).getTextContent().trim()));
                    }
                }
            }
            if (!continuar) {
                break;
            }
        }
        this.imprimirResultado("TotalImporteTipoCargo02", elementos);
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
            System.out.println("No se encontr?? el dato");
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
                    //Revision de la cantidad m??xima de datos permitidos (12)
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
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripci??n
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
                    //Revision de que a??n sean aceptados los elementos
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
                                        default:
                                            break;
                                    }

                                } else {
                                    //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                    if (!defPeriodo.equals("0")) {
                                        //Se evaluan los elementos que pueden coincidir con la descripci??n
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

        //B??squeda en el nodo
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
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripci??n
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
                                    //En caso de que no coincida, buscar?? un elemento con la terminaci??n de con una terminacion diferente de cero y termina el proceso
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

        //B??squeda en el nodo
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
                                    //En caso de que no coincida, buscar?? un elemento con la terminaci??n de con una terminacion diferente de cero y termina el proceso
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
                    //Revision de la cantidad m??xima de datos permitidos (12)
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
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripci??n
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
                    //Revision de que a??n sean aceptados los elementos
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
                                        default:
                                            break;
                                    }

                                } else {
                                    //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                    if (!defPeriodo.equals("0")) {
                                        //Se evaluan los elementos que pueden coincidir con la descripci??n
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

        //B??squeda en el nodo
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
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripci??n
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
                    //Revision de la cantidad m??xima de datos permitidos (12)
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
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripci??n
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

        //B??squeda en el nodo
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
                                            default:
                                                break;
                                        }

                                    } else {
                                        //Se revisa que la definicion del segundo elemento del periodo sea diferente de 0
                                        if (!defPeriodo.equals("0")) {
                                            //Se evaluan los elementos que pueden coincidir con la descripci??n
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

    /*-----------------------------Otros Metodos-------------------------*/
    private void verificarPotenciaAFacturar(double suma) {
        double importeTotalTerminoPotencia = Double.parseDouble(this.doc.getElementsByTagName("ImporteTotalTerminoPotencia").item(0).getTextContent());
        int numDias = Integer.parseInt(this.doc.getElementsByTagName("NumeroDias").item(0).getTextContent());

        double importeCalculado = new BigDecimal(numDias * suma).setScale(2, RoundingMode.HALF_UP).doubleValue();

        System.out.println(suma);
        System.out.println(numDias);
        System.out.println(importeTotalTerminoPotencia);

        if (importeCalculado != importeTotalTerminoPotencia) {
            System.out.println("Se esperaba encontrar el valor de " + importeTotalTerminoPotencia + ", en donde se calcul?? " + importeCalculado);
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

    /*--------------------------Utilidades---------------------------*/
    private void imprimirResultado(String nombreMetodo, ArrayList elementos) {
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

    private void validarTarifa() {
        Cliente c = new ClienteDao().encontrarCups(new Cliente(this.cups));
        String control = "";

        switch (c.getTarifa()) {
            case "T20A":
                control = "1";
                break;
            case "T21A":
                control = "5";
                break;
            case "T20DHA":
                control = "4";
                break;
            case "T21DHA":
                control = "6";
                break;
            case "T30A":
                control = "3";
                break;
            case "T31A":
                control = "11";
                break;
            case "T61A":
                control = "12";
                break;
            case "T62A":
                control = "13";
                break;
            default:
                break;
        }

        if (!control.equals(this.tarifaAtrFac)) {
            System.out.println("Inconsistencia en el factor de tarifa, se encontro " + this.tarifaAtrFac + " en donde se esperaba " + control);
            this.agregarError("21");
        }
    }

}
