package controladores.helper;

import datos.interfaces.DocumentoXmlService;
import org.w3c.dom.Document;

public class ProcesarRemesaPagoPeaje extends xmlHelper{

    public ProcesarRemesaPagoPeaje(Document doc, DocumentoXmlService contenidoXmlService) {
        super(doc, contenidoXmlService);
        super.procesarRemesaPagoPeaje();
    }

}
