package controladores.helper;

import datos.interfaces.DocumentoXmlService;
import org.w3c.dom.Document;

public class ProcesarRemesaPago extends xmlHelper{

    public ProcesarRemesaPago(Document doc, DocumentoXmlService contenidoXmlService) {
        super(doc, contenidoXmlService);
        super.procesarRemesaPago();
    }

}
