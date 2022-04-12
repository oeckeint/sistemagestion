package controladores.helper;

import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.ClienteNoExisteException;
import excepciones.CodRectNoExisteException;
import excepciones.FacturaYaExisteException;
import excepciones.MasDeUnClienteEncontrado;
import excepciones.PeajeMasDeUnRegistroException;
import excepciones.PeajeTipoFacturaNoSoportadaException;
import org.w3c.dom.Document;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
public class ProcesarOtrasFacturas extends xmlHelper {

    public ProcesarOtrasFacturas(Document doc, DocumentoXmlService contenidoXmlService, ClienteService clienteService, String nombreArchivo)
            throws FacturaYaExisteException, ClienteNoExisteException, PeajeTipoFacturaNoSoportadaException, CodRectNoExisteException, MasDeUnClienteEncontrado, PeajeMasDeUnRegistroException {
        super(doc, contenidoXmlService, clienteService);
        super.procesarOtrasFacturas(nombreArchivo);
    }
}
