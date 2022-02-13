package controladores.helper;

import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.ClienteNoExisteException;
import excepciones.CodRectNoExisteException;
import excepciones.FacturaYaExisteException;
import excepciones.MasDeUnClienteEncontrado;
import excepciones.PeajeCodRectNoExisteException;
import excepciones.PeajeTipoFacturaNoSoportadaException;
import excepciones.TarifaNoExisteException;
import javax.persistence.NonUniqueResultException;
import org.w3c.dom.Document;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
public class ProcesarPeaje extends xmlHelper {

    public ProcesarPeaje(Document doc, DocumentoXmlService contenidoXmlService, ClienteService clienteService, String nombreArchivo)
            throws FacturaYaExisteException, ClienteNoExisteException, PeajeTipoFacturaNoSoportadaException, CodRectNoExisteException, MasDeUnClienteEncontrado, NonUniqueResultException, PeajeCodRectNoExisteException, TarifaNoExisteException {
        super(doc, contenidoXmlService, clienteService);
        super.procesarPeaje(nombreArchivo);
    }

}
