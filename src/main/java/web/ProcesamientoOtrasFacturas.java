package web;

import datos.OtrasFacturasDao;
import dominio.otrasfactuas.*;
import java.util.*;
import org.w3c.dom.*;

public class ProcesamientoOtrasFacturas {

    private static final String LINEA = "\n------------------------------------------------------------------------------\n";
    private String comentarios = "";
    private String errores = "";

    public ProcesamientoOtrasFacturas() {
    }

    public ProcesamientoOtrasFacturas(Document documentoXml, String nombreArchivo) {
        this.registrar(documentoXml, nombreArchivo);
    }

    public String registrar(Document documentoXml, String nombreArchivo) {
        if (!new OtrasFacturasDao().existeCodFiscal(this.datosGeneralesFactura(documentoXml).get(0))) {
            new OtrasFacturasDao().insertar(new DocumentoOtraFactura(new Cabecera(this.cabecera(documentoXml)),
                    new DatosGeneralesFactura(this.datosGeneralesFactura(documentoXml)),
                    new ConceptoRepercutible(this.conceptoRepercutible(documentoXml)),
                    new RegistroFin(this.registroFin(documentoXml)),
                    comentarios, errores
            ));
            return "";
        } else{
            return "La factura <Strong>(" + this.datosGeneralesFactura(documentoXml).get(0) + ")</Strong> del archivo " +  nombreArchivo + " ya ha sido registrada en <Strong>Otras Facturas</Strong>";
        }
    }

    private ArrayList<String> cabecera(Document documentoXml) {

        ArrayList<String> elementos = new ArrayList<>(6);
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");

        NodeList flowList = documentoXml.getElementsByTagName("Cabecera");
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
                        case "CUPS":
                            elementos.set(5, childList.item(j).getTextContent().trim());
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        System.out.print(LINEA + "Cabecera" + LINEA + elementos + LINEA);
        return elementos;
    }

    private ArrayList<String> datosGeneralesFactura(Document documentoXml) {

        ArrayList<String> elementos = new ArrayList<>(6);
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");
        elementos.add("");

        NodeList flowList = documentoXml.getElementsByTagName("DatosGeneralesFactura");
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
                        case "FechaFactura":
                            elementos.set(3, childList.item(j).getTextContent().trim());
                            break;
                        case "Comentarios":
                            elementos.set(4, childList.item(j).getTextContent().trim());
                            break;
                        case "ImporteTotalFactura":
                            elementos.set(5, String.valueOf(Double.parseDouble(childList.item(j).getTextContent().trim())));
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        System.out.print(LINEA + "DatosGeneralesFactura" + LINEA + elementos + LINEA);
        return elementos;
    }

    private ArrayList<String> conceptoRepercutible(Document documentoXml) {

        ArrayList elementos = new ArrayList<>(2);
        elementos.add("");
        elementos.add(0.0);

        NodeList flowList = documentoXml.getElementsByTagName("ConceptoRepercutible");
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
                            elementos.set(1, Double.parseDouble(childList.item(j).getTextContent().trim()) + ((Double) elementos.get(1)));
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        System.out.print(LINEA + "conceptoRepercutible" + LINEA + elementos + LINEA);
        return elementos;
    }

    private ArrayList<String> registroFin(Document documentoXml) {

        ArrayList elementos = new ArrayList<>(1);
        elementos.add(0);

        NodeList flowList = documentoXml.getElementsByTagName("RegistroFin");
        for (int i = 0; i < flowList.getLength(); i++) {
            NodeList childList = flowList.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if (null != childNode.getNodeName()) {
                    switch (childNode.getNodeName()) {
                        case "IdRemesa":
                            elementos.set(0, Integer.parseInt(childList.item(j).getTextContent().trim()));
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        System.out.print(LINEA + "registroFin" + LINEA + elementos + LINEA);
        return elementos;
    }

}
