package controladores.helper;

import datos.interfaces.DocumentoXmlService;
import excepciones.PeajeMasDeUnRegistroException;

import excepciones.RegistroVacioException;
import org.w3c.dom.Document;

public class ProcesarRemesaPagoFactura extends xmlHelper{

    public ProcesarRemesaPagoFactura(Document doc, DocumentoXmlService contenidoXmlService) throws PeajeMasDeUnRegistroException, RegistroVacioException {
        super(doc, contenidoXmlService);
        super.procesarRemesaPagoFactura();
    }

}
