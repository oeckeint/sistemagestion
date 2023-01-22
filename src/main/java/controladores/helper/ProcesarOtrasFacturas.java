package controladores.helper;

import datos.entity.OtraFactura;
import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
@Component
public class ProcesarOtrasFacturas extends xmlHelper {

    private final DocumentoXmlService service;

    private final Logger logger = Logger.getLogger(getClass().getName());

    public ProcesarOtrasFacturas(@Qualifier(value = "otrasFacturasServiceImp") DocumentoXmlService service) {
        this.service = service;
    }

    /**
     * Aplica para todos los archivos que vengan con el nodo identificador
     * "Otras Facturas" guarda el registro en la tabla denominada
     * "contenido_xml_otras_facturas"
     *
     * @param nombreArchivo referencia al nombre del archivo actual
     * @throws excepciones.FacturaYaExisteException
     * @throws excepciones.ClienteNoExisteException
     * @throws excepciones.PeajeTipoFacturaNoSoportadaException
     * @throws excepciones.CodRectNoExisteException
     * @throws excepciones.MasDeUnClienteEncontrado
     * @throws PeajeMasDeUnRegistroException
     */
    public void procesar(Document doc, String nombreArchivo)
            throws FacturaYaExisteException, ClienteNoExisteException, MasDeUnClienteEncontrado, PeajeMasDeUnRegistroException {
        this.doc = doc;
        this.nombreArchivo = nombreArchivo;
        this.iniciarVariables();

        if (this.cliente != null) {
            //Se revisa que la factura no exista
            try {
                this.service.buscarByCodFiscal(codFactura);
                logger.log(Level.INFO, ">>> Ya existe un registro con el codigo Fiscal {0} en OtrasFacturas", this.codFactura);
                throw new FacturaYaExisteException(codFactura, "otraFactura");
            }catch (RegistroVacioException e){
                logger.log(Level.INFO, ">>> Nuevo registro en OtrasFacturas {0}", this.codFactura);
            }

            this.nombreArchivo = nombreArchivo;
            this.comentarios.append("Nombre de archivo original: <Strong>").append(this.nombreArchivo).append("</Strong><br/>");
            this.registrarOtraFactura();

        } else {
            throw new ClienteNoExisteException(cups);
        }
    }

    /**
     * Registra OtrasFacturas
     */
    private void registrarOtraFactura() {
        this.service.guardar(
                new OtraFactura(
                        this.cliente, this.cabecera(), this.datosGenerales(),
                        this.conceptoRepercutible(), this.registroFin(),
                        this.comentarios.toString(), this.errores.toString()
                )
        );
        System.out.print("\n\nComentarios : " + comentarios.toString());
        System.out.print("Codigo Errores : " + errores.toString());
    }

}
