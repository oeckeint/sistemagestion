package controladores.helper;

import java.util.HashMap;
import java.util.logging.Logger;
import datos.entity.Peaje;
import excepciones.RegistroVacioException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import controladores.Procesamiento.TablaBusqueda;
import datos.entity.Factura;
import datos.entity.OtraFactura;
import datos.interfaces.DocumentoXmlService;
import excepciones.PeajeMasDeUnRegistroException;

public class ConsultaFacturacion{

	private Logger logger = Logger.getLogger(getClass().getName());
    private Document doc;
	private String nombreArchivo;
	private DocumentoXmlService contenidoXmlService;
    private StringBuilder errores;
    private StringBuilder comentarios;
	

	public ConsultaFacturacion(Document doc, String nombreArchivo, DocumentoXmlService contenidoXmlService) throws PeajeMasDeUnRegistroException {
		this.contenidoXmlService = contenidoXmlService;
		this.nombreArchivo = nombreArchivo;
		this.doc = doc;
	}

	/**
	 * Actualizar los filtros de una tabla a partir del uso de un archivo xml buscando por codigoFiscalFactura
	 * @param tabla
	 * @throws PeajeMasDeUnRegistroException
	 */
   public void actualizarFiltro(TablaBusqueda tabla) throws PeajeMasDeUnRegistroException {
		HashMap<String, String> elementos = new HashMap<String, String>(2);
		NodeList flowList = doc.getElementsByTagName("Factura");
		for (int i = 0; i < flowList.getLength(); i++) {
			NodeList childList = flowList.item(i).getChildNodes();
			for (int j = 0; j < childList.getLength(); j++) {
				Node childNode = childList.item(j);
				if (null != childNode.getNodeName()) {
					switch (childNode.getNodeName()) {
						case NombresNodos.COD_FIS_FAC:
							elementos.put(NombresNodos.COD_FIS_FAC, childList.item(j).getTextContent().trim());
							break;
						case NombresNodos.FIL:
							elementos.put(NombresNodos.FIL, childList.item(j).getTextContent().trim());
							break;
						default:
							break;
					}
				}
			}

			//Actualizacion en la tabla correspondiente
			try {
				switch (tabla) {
					case peajes:
						Peaje p = (Peaje) this.contenidoXmlService.buscarByCodFiscalEspecifico(elementos.get(NombresNodos.COD_FIS_FAC));
						p.setFiltro(elementos.get(NombresNodos.FIL));
						this.contenidoXmlService.actualizar(p);
						break;
					case facturas:
						Factura f = (Factura) this.contenidoXmlService.buscarByCodFiscalEspecifico(elementos.get(NombresNodos.COD_FIS_FAC));
						f.setFiltro(elementos.get(NombresNodos.FIL));
						this.contenidoXmlService.actualizar(f);
						break;
					case otrasFacturas:
						OtraFactura of = (OtraFactura) this.contenidoXmlService.buscarByCodFiscalEspecifico(elementos.get(NombresNodos.COD_FIS_FAC));
						of.setFiltro(elementos.get(NombresNodos.FIL));
						this.contenidoXmlService.actualizar(of);
						break;
				}
			} catch (RegistroVacioException e){
				this.errores = new StringBuilder("No se encontró un registro con el código fiscal ")
						.append(elementos.get(NombresNodos.COD_FIS_FAC))
						.append(" en la tabla ").append(tabla)
						.append(" por lo que no se actualizo el filtro en el archivo ")
						.append(this.nombreArchivo);
				utileria.ArchivoTexto.escribirError(this.errores.toString());
				this.errores = null;
			}

		}
	}

}
