package controladores.helper;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jcraft.jsch.Logger;

import controladores.OtrasFacturas;
import controladores.ProcesamientoXml.TablaBusqueda;
import datos.entity.Cliente;
import datos.entity.Factura;
import datos.entity.OtraFactura;
import datos.entity.Peaje;
import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.PeajeMasDeUnRegistroException;
import utileria.xml;

public class ProcesarConsultaFacturaPeaje{
	
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
	

	public ProcesarConsultaFacturaPeaje(Document doc, DocumentoXmlService contenidoXmlService, TablaBusqueda tabla) throws PeajeMasDeUnRegistroException {
		this.contenidoXmlService = contenidoXmlService;
		this.doc = doc;
		switch (tabla) {
			case peajes:
				this.consultarREgistroPeaje();
				break;
			case facturas: 
				this.consultarRegistroFacturas();
				break;
			case otrasFacturas:
				this.consultarRegistroOtrasFacturas();
				break;
			default:
				break;
		}
	}
	
	public void consultarREgistroPeaje() throws PeajeMasDeUnRegistroException {
		String codfis = xml.obtenerContenidoNodo(NombresNodos.COD_FIS_FAC, doc);
		String fil = xml.obtenerContenidoNodo(NombresNodos.FIL, doc);
			
		Peaje dokc = (Peaje) this.contenidoXmlService.buscarByCodFiscal(codfis);
		
		dokc.setFiltro(1);
				
		this.contenidoXmlService.actualizar(dokc);
		
	}
	
	public void consultarRegistroFacturas() throws PeajeMasDeUnRegistroException {
		String codfis = xml.obtenerContenidoNodo(NombresNodos.COD_FIS_FAC, doc);
		String filtro = xml.obtenerContenidoNodo(NombresNodos.FIL, doc);
		
		Factura f = (Factura) this.contenidoXmlService.buscarByCodFiscal(codfis);
		
		f.setFiltro(1);
		
		
		
		this.contenidoXmlService.actualizar(f);
	}
	
	public void consultarRegistroOtrasFacturas() throws PeajeMasDeUnRegistroException {
		String codfis = xml.obtenerContenidoNodo(NombresNodos.COD_FIS_FAC, doc);
		String fil = xml.obtenerContenidoNodo(NombresNodos.FIL, doc);
		
		OtraFactura oF = (OtraFactura) contenidoXmlService.buscarByCodFiscal(codfis);
		
		oF.setFiltro(1);
		
		this.contenidoXmlService.actualizar(oF);
	}
	
	   public void procesarConsultaFactura() throws PeajeMasDeUnRegistroException {
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
	                        case "Filtro":
	                            elementos.put("filtro", childList.item(j).getTextContent().trim());
	                            break;
	                        default:
	                            break;
	                    }
	                }
	            }
	
	        }
}
	   }
