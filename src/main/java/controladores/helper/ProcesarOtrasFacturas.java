package controladores.helper;

import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.*;
import org.w3c.dom.Document;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
public class ProcesarOtrasFacturas extends xmlHelper {

    public ProcesarOtrasFacturas(Document doc, DocumentoXmlService contenidoXmlService, ClienteService clienteService, String nombreArchivo)
            throws FacturaYaExisteException, ClienteNoExisteException, PeajeTipoFacturaNoSoportadaException, CodRectNoExisteException, MasDeUnClienteEncontrado, PeajeMasDeUnRegistroException, RegistroVacioException {
        super(doc, contenidoXmlService, clienteService);
        super.procesarOtrasFacturas(nombreArchivo);
    }
}
