package controladores.helper;

import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.*;
import org.w3c.dom.Document;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
public class ProcesarFactura extends xmlHelper {

    public ProcesarFactura(Document doc, DocumentoXmlService contenidoXmlService, ClienteService clienteService, String nombreArchivo)
            throws FacturaYaExisteException, ClienteNoExisteException, PeajeTipoFacturaNoSoportadaException, CodRectNoExisteException, MasDeUnClienteEncontrado, TarifaNoExisteException, PeajeMasDeUnRegistroException, RegistroVacioException {
        super(doc, contenidoXmlService, clienteService);
        super.procesarFactura(nombreArchivo);
    }

}
