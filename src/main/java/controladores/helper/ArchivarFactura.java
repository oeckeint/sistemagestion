package controladores.helper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import app.config.ApplicationContextUtils;
import controladores.ProcesamientoXml.TablaBusqueda;
import datos.entity.Factura;
import datos.entity.OtraFactura;
import datos.entity.Peaje;
import datos.interfaces.DocumentoXmlService;
import excepciones.FacturaNoExisteException;
import excepciones.PeajeMasDeUnRegistroException;

public class ArchivarFactura {

	private ApplicationContext appContext = ApplicationContextUtils.getApplicationContext();
	private DocumentoXmlService documentoXmlService;
	private Document doc;
	private TablaBusqueda tablaBusqueda;

	public ArchivarFactura(TablaBusqueda tb, Document doc) {
		this.doc = doc;
		this.tablaBusqueda = tb;
		this.documentoXmlService = appContext.getBean(tb.toString() + "ServiceImp", DocumentoXmlService.class);
		this.cambiarIsDeleted();
	}

	private void cambiarIsDeleted() {
		NodeList nodeList = this.doc.getElementsByTagName("CodigoFiscalFactura");
		List<Node> nodes = IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item)
				.collect(Collectors.toList());
		switch (this.tablaBusqueda) {
		case peajes:
			this.recorrerNodosPeajes(nodes);
			break;
		case facturas:
			this.recorrerNodosFacturas(nodes);
			break;
		case otrasFacturas:
			this.recorrerNodosOtrasFacturas(nodes);
			break;
		}
	}

	private void recorrerNodosPeajes(List<Node> nodes) {
		for (Node node : nodes) {
			try {
				Peaje p = (Peaje) this.documentoXmlService.buscarByCodFiscal(node.getTextContent());
				if (p != null) {
					Node isDeleted = node.getNextSibling();
					while (null != isDeleted && isDeleted.getNodeType() != Node.ELEMENT_NODE
							|| !isDeleted.getNodeName().equals("isDeleted")) {
						isDeleted = isDeleted.getNextSibling();
					}
					p.setIsDeleted(Integer.parseInt(isDeleted.getTextContent()));
					this.documentoXmlService.actualizar(p);
				} else {
					new FacturaNoExisteException(node.getTextContent(), tablaBusqueda);
				}
			} catch (PeajeMasDeUnRegistroException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private void recorrerNodosFacturas(List<Node> nodes) {
		for (Node node : nodes) {
			try {
				Factura f = (Factura) this.documentoXmlService.buscarByCodFiscal(node.getTextContent());
				if (f != null) {
					Node isDeleted = node.getNextSibling();
					while (null != isDeleted && isDeleted.getNodeType() != Node.ELEMENT_NODE
							|| !isDeleted.getNodeName().equals("isDeleted")) {
						isDeleted = isDeleted.getNextSibling();
					}
					f.setIsDeleted(Integer.parseInt(isDeleted.getTextContent()));
					this.documentoXmlService.actualizar(f);
				} else {
					new FacturaNoExisteException(node.getTextContent(), tablaBusqueda);
				}
			} catch (PeajeMasDeUnRegistroException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private void recorrerNodosOtrasFacturas(List<Node> nodes) {
		for (Node node : nodes) {
			try {
				OtraFactura f = (OtraFactura) this.documentoXmlService.buscarByCodFiscal(node.getTextContent());
				if (f != null) {
					Node isDeleted = node.getNextSibling();
					while (null != isDeleted && isDeleted.getNodeType() != Node.ELEMENT_NODE
							|| !isDeleted.getNodeName().equals("isDeleted")) {
						isDeleted = isDeleted.getNextSibling();
					}
					f.setIsDeleted(Integer.parseInt(isDeleted.getTextContent()));
					this.documentoXmlService.actualizar(f);
				} else {
					new FacturaNoExisteException(node.getTextContent(), tablaBusqueda);
				}
			} catch (PeajeMasDeUnRegistroException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
