package controladores.helper;

import datos.interfaces.DocumentoXmlService;
import excepciones.PeajeMasDeUnRegistroException;

import excepciones.RegistroVacioException;
import org.w3c.dom.Document;

public class ProcesarRemesaPagoPeaje extends xmlHelper{

    public ProcesarRemesaPagoPeaje(Document doc, DocumentoXmlService contenidoXmlService) throws PeajeMasDeUnRegistroException, RegistroVacioException {
        super(doc, contenidoXmlService);
        super.procesarRemesaPagoPeaje();
    }

}
