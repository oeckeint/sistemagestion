package controladores.helper;

import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import org.w3c.dom.Document;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
public class ProcesarPeaje extends xmlHelper{

    public ProcesarPeaje(Document doc, DocumentoXmlService contenidoXmlService, ClienteService clienteService) {
        super(doc, contenidoXmlService, clienteService);
    }
    
}
