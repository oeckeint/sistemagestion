package controladores.helper;

import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.ClienteNoExisteException;
import excepciones.CodRectNoExisteException;
import excepciones.FacturaYaExisteException;
import excepciones.MasDeUnClienteEncontrado;
import excepciones.PeajeTipoFacturaNoSoportadaException;
import org.w3c.dom.Document;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
public class ProcesarFactura extends xmlHelper {

    public ProcesarFactura(Document doc, DocumentoXmlService contenidoXmlService, ClienteService clienteService, String nombreArchivo)
            throws FacturaYaExisteException, ClienteNoExisteException, PeajeTipoFacturaNoSoportadaException, CodRectNoExisteException, MasDeUnClienteEncontrado {
        super(doc, contenidoXmlService, clienteService);
        super.procesarFactura(nombreArchivo);
    }

}
