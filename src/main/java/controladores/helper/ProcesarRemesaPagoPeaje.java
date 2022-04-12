package controladores.helper;

import datos.interfaces.DocumentoXmlService;
import excepciones.PeajeMasDeUnRegistroException;

import org.w3c.dom.Document;

public class ProcesarRemesaPagoPeaje extends xmlHelper{

    public ProcesarRemesaPagoPeaje(Document doc, DocumentoXmlService contenidoXmlService) throws PeajeMasDeUnRegistroException {
        super(doc, contenidoXmlService);
        super.procesarRemesaPagoPeaje();
    }

}
