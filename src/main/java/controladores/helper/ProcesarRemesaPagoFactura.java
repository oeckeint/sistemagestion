package controladores.helper;

import datos.interfaces.DocumentoXmlService;
import org.w3c.dom.Document;

public class ProcesarRemesaPagoFactura extends xmlHelper{

    public ProcesarRemesaPagoFactura(Document doc, DocumentoXmlService contenidoXmlService) {
        super(doc, contenidoXmlService);
        super.procesarRemesaPagoFactura();
    }

}
