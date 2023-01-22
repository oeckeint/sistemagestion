package controladores.helper;

import datos.entity.EnergiaExcedentaria;
import datos.entity.Peaje;
import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.*;

import javax.persistence.NonUniqueResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import utileria.xml;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
@Component
public class ProcesarPeaje extends xmlHelper {

    private final DocumentoXmlService service;

    @Value("${abono.factura.validaterectificadaanulada}")
    private boolean isValidAbonoPeajeActive;

    private final Logger logger = Logger.getLogger(getClass().getName());

    public ProcesarPeaje(@Qualifier(value = "peajesServiceImp") DocumentoXmlService service){
        this.service = service;
    }

    /**
     * Lee los datos y registra en la tabla denominada "contenido_xml"
     *
     * @param nombreArchivo referencia al nombre del archivo actual
     * @throws FacturaYaExisteException
     * @throws ClienteNoExisteException
     * @throws PeajeTipoFacturaNoSoportadaException
     * @throws CodRectNoExisteException
     * @throws excepciones.MasDeUnClienteEncontrado
     * @throws PeajeMasDeUnRegistroException
     */
    public void procesar(Document doc, String nombreArchivo)
            throws FacturaYaExisteException, ClienteNoExisteException, PeajeTipoFacturaNoSoportadaException, CodRectNoExisteException, NonUniqueResultException, MasDeUnClienteEncontrado, PeajeCodRectNoExisteException, TarifaNoExisteException, PeajeMasDeUnRegistroException {
        this.doc = doc;
        this.nombreArchivo = nombreArchivo;
        this.iniciarVariables();

        if (this.cliente != null) {
            //Se revisa que la factura no exista
            try {
                this.service.buscarByCodFiscal(codFactura);
                logger.log(Level.INFO, ">>> Ya existe un peaje con el codigo Fiscal {0}", this.codFactura);
                throw new FacturaYaExisteException(codFactura, "peajes");
            }catch (RegistroVacioException e){
                logger.log(Level.INFO, ">>> Nuevo registro en Peajes {0}", this.codFactura);
            }

            this.nombreArchivo = nombreArchivo;
            this.comentarios.append("Nombre de archivo original: <Strong>").append(this.nombreArchivo).append("</Strong><br/>");
            logger.log(Level.INFO, ">>> Tipo Factura {0}", this.tipoFactura);
            switch (this.tipoFactura) {
                case "A":
                    this.registrarPeajeA();
                    break;
                case "N":
                case "G":
                    this.registrarPeajeN();
                    break;
                case "R":
                    this.registrarPeajeR(nombreArchivo);
                    break;
                default:
                    throw new PeajeTipoFacturaNoSoportadaException(tipoFactura);
            }

        } else {
            throw new ClienteNoExisteException(cups);
        }
    }

    /**
     * Busca la factura para Abonar, de lo contrario arroja una exception
     * Registra los el tipos de Peajes A
     * @throws PeajeMasDeUnRegistroException
     *
     * @throws CodRectNoExisteException
     */
    private void registrarPeajeA() throws MasDeUnClienteEncontrado, PeajeCodRectNoExisteException, TarifaNoExisteException, PeajeMasDeUnRegistroException {
        System.out.println("isValidAbonoPeajeActive = " + isValidAbonoPeajeActive);
        String codRectificada = null;
        try {
            codRectificada = xml.obtenerContenidoNodo(NombresNodos.COD_FAC_REC_ANU, this.doc);
            Peaje peaje = (Peaje) this.service.buscarByCodFiscal(codRectificada);
            comentarios.append(", este abono hace referencia al CodigoFacturaRectificadaAnulada <Strong> ").append(peaje.getCodFisFac()).append("</Strong>");
            this.registrarPeajeNA();
        }catch (RegistroVacioException e) {
            logger.log(Level.INFO, ">>> No se encontró un Peaje para rectificar con {0}", codRectificada);
            throw new PeajeCodRectNoExisteException(codFactura);
        } catch (NullPointerException e) {
            logger.log(Level.INFO, ">>> El Abono de peaje {0} no especifica un codigo de factura para rectificar", xml.obtenerContenidoNodo(NombresNodos.COD_FIS_FAC, this.doc));
            throw new PeajeCodRectNoExisteException(codFactura);
        }
    }

    /**
     * Registra el Peaje de tipo A poniendo en negativo los valores de AE, R, PM
     * y los importes totales
     */
    private void registrarPeajeNA() throws MasDeUnClienteEncontrado, TarifaNoExisteException {
        TIPO_FACTURA tp = TIPO_FACTURA.A_ABONO;
        Peaje peaje = new Peaje(
                this.cliente, this.cabecera(), this.datosGenerales(), this.datosTerminoPotencia(), this.datosFacturaAtr(),
                this.potenciaExcesos(), this.potenciaContratada(), this.potenciaDemandada(), this.potenciaAFacturar(), this.potenciaPrecio(), this.potenciaImporteTotal_A(),
                this.energiaActivaDatos(), this.energiaActivaValores(), this.energiaActivaPrecio(), this.energiaActivaImporteTotal_A(),
                this.Cargos("01", tp), this.Cargos("02", tp), this.ImporteTotalCargos("01", tp), this.ImporteTotalCargos("02", tp),
                this.impuestoElectrico(), this.alquileres(), this.iva(),
                this.aeConsumo_A(), this.aeLecturaDesde_A(), this.aeLecturaHasta_A(), this.aeProcedenciaDesde_A(), this.aeProcedenciaHasta_A(),
                this.rConsumo_A(), this.rLecturaDesde_A(), this.rLecturaHasta_A(), this.rImporteTotal_A(),
                this.pmConsumo_A(), this.pmLecturaHasta_A(),
                this.registroFin(), this.comentarios.toString(), this.errores.toString()
        );
        this.prepareAbonoPeaje(peaje);
        System.out.println("isValidAbonoPeajeActive = " + isValidAbonoPeajeActive);
        System.out.println("preparandoAbono");
        this.service.guardar(peaje);
        System.out.print("\n\nComentarios : " + comentarios.toString());
        System.out.print("Codigo Errores : " + errores.toString());
    }

    /**
     * Registra el peaje de tipo N
     */
    private void registrarPeajeN() throws MasDeUnClienteEncontrado, TarifaNoExisteException {
        TIPO_FACTURA tp = TIPO_FACTURA.N_NORMAL;
        Peaje peaje = new Peaje(
                this.cliente, this.cabecera(), this.datosGenerales(), this.datosTerminoPotencia(), this.datosFacturaAtr(),
                this.potenciaExcesos(), this.potenciaContratada(), this.potenciaDemandada(), this.potenciaAFacturar(), this.potenciaPrecio(), this.potenciaImporteTotal(),
                this.energiaActivaDatos(), this.energiaActivaValores(), this.energiaActivaPrecio(), this.energiaActivaImporteTotal(),
                this.Cargos("01", tp), this.Cargos("02", tp), this.ImporteTotalCargos("01", tp), this.ImporteTotalCargos("02", tp),
                this.impuestoElectrico(), this.alquileres(), this.iva(),
                this.aeConsumo(), this.aeLecturaDesde(), this.aeLecturaHasta(), this.aeProcedenciaDesde(), this.aeProcedenciaHasta(),
                this.rConsumo(), this.rLecturaDesde(), this.rLecturaHasta(), this.rImporteTotal(),
                this.pmConsumo(), this.pmLecturaHasta(),
                this.registroFin(), this.comentarios.toString(), this.errores.toString()
        );
        EnergiaExcedentaria energiaExcedentaria = this.energiaExcedentaria();
        if (this.existeEnergiaExcedentaria) {
            this.existeEnergiaExcedentaria = false;
            peaje.setEnergiaExcedentaria(energiaExcedentaria);
        }
        this.service.guardar(peaje);
        System.out.print("\n\nComentarios : " + comentarios.toString());
        System.out.print("Codigo Errores : " + errores.toString());
    }

    /**
     * Busca que el Peaje a rectificar exista, cuando la factura ya existe
     * arroja una excepcion, de lo contraro hara un proceso de rectificación y
     * usara el metodo registrarN para guardar en registro en la BD
     *
     * @param nombreArchivo
     * @throws CodRectNoExisteException
     * @throws PeajeMasDeUnRegistroException
     */
    private void registrarPeajeR(String nombreArchivo) throws CodRectNoExisteException, MasDeUnClienteEncontrado, TarifaNoExisteException, PeajeMasDeUnRegistroException {
        String codRectificada = xml.obtenerContenidoNodo(NombresNodos.COD_FAC_REC_ANU, this.doc);
        try {
            Peaje peaje = (Peaje) this.service.buscarByCodFiscal(codRectificada);
            String nuevaRemesa = String.valueOf(Long.parseLong(xml.obtenerContenidoNodo(NombresNodos.ID_REM, this.doc)));
            this.service.rectificar(peaje, nuevaRemesa, nombreArchivo);
            this.registrarPeajeN();
        } catch (RegistroVacioException e) {
            logger.log(Level.INFO, ">>> No se encontró una factura para rectificar con {0}", codRectificada);
            throw new CodRectNoExisteException(codRectificada);
        }
    }

    private Peaje prepareAbonoPeaje(Peaje peaje) {
        logger.log(Level.INFO, ">>> Pasando valores especificos a negativo del abono de peaje {0}", peaje.getCodFisFac());
        peaje.setTipFac("A");
        String fecDes1 = peaje.getEaFecDes1();
        String fecHas1 = peaje.getEaFecHas1();
        String fecDes2 = peaje.getEaFecDes2();
        String fecHas2 = peaje.getEaFecHas2();
        peaje.setEaFecDes1(fecHas1);
        peaje.setEaFecHas1(fecDes1);
        peaje.setEaFecDes2(fecHas2);
        peaje.setEaFecHas2(fecDes2);
        return peaje;
    }

}
