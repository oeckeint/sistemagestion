package controladores.helper;

import datos.interfaces.DocumentoXmlService;
import excepciones.PeajeMasDeUnRegistroException;

import org.w3c.dom.Document;

public class ProcesarRemesaPagoOtrasFacturas extends xmlHelper{

    public ProcesarRemesaPagoOtrasFacturas(Document doc, DocumentoXmlService contenidoXmlService) throws PeajeMasDeUnRegistroException {
        super(doc, contenidoXmlService);
        super.procesarRemesaPagoOtraFactura();
    }
    
}
