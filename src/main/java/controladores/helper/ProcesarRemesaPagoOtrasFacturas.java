package controladores.helper;

import datos.interfaces.DocumentoXmlService;
import org.w3c.dom.Document;

public class ProcesarRemesaPagoOtrasFacturas extends xmlHelper{

    public ProcesarRemesaPagoOtrasFacturas(Document doc, DocumentoXmlService contenidoXmlService) {
        super(doc, contenidoXmlService);
        super.procesarRemesaPagoOtraFactura();
    }
    
}
