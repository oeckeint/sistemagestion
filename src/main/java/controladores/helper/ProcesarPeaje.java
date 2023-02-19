package controladores.helper;

import datos.entity.EnergiaExcedentaria;
import datos.entity.Peaje;
import datos.interfaces.DocumentoXmlService;
import excepciones.*;

import javax.persistence.NonUniqueResultException;

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
            throws ClienteNoExisteException, PeajeTipoFacturaNoSoportadaException, CodRectNoExisteException, NonUniqueResultException, MasDeUnClienteEncontrado, PeajeCodRectNoExisteException, TarifaNoExisteException, PeajeMasDeUnRegistroException, PeajeYaExisteException {
        this.doc = doc;
        this.nombreArchivo = nombreArchivo;
        this.iniciarVariables();

        if (this.cliente != null) {
            //Se revisa que la factura no exista
            try {
                this.service.buscarByCodFiscal(this.codFactura);
                logger.log(Level.INFO, ">>> Ya existe un peaje con el codigo Fiscal {0}", this.codFactura);
                throw new PeajeYaExisteException(this.codFactura);
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
                case "C":
                    this.registrarPeajeN(this.tipoFactura.charAt(0));
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
     *
     * @throws PeajeMasDeUnRegistroException
     * @throws CodRectNoExisteException
     */
    private void registrarPeajeA() throws MasDeUnClienteEncontrado, PeajeCodRectNoExisteException, TarifaNoExisteException, PeajeMasDeUnRegistroException {
        logger.log(Level.INFO, ">>> Registrando Peaje del tipo Abono con el codFisFac {0}", xml.obtenerContenidoNodo(NombresNodos.COD_FIS_FAC, this.doc));
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
     * Registra el Peaje de tipo Abono
     * y los importes totales
     */
    private void registrarPeajeNA() throws MasDeUnClienteEncontrado, TarifaNoExisteException {
        Peaje peaje = this.prepareAbono(this.crearPeaje(TIPO_FACTURA.A_ABONO));
        this.service.guardar(peaje);
    }

    /**
     * Registra el peaje de tipo Normnal
     */
    private void registrarPeajeN(char tipoFactura) throws MasDeUnClienteEncontrado, TarifaNoExisteException {
        Peaje p = null;
        switch (tipoFactura) {
            case 'N':
            case 'G':
                p = this.crearPeaje(TIPO_FACTURA.N_NORMAL);
                break;
            case 'C':
                p = this.crearPeaje(TIPO_FACTURA.C_);
                break;
        }
        this.service.guardar(p);
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
            String nuevaFechaLimitePago = xml.obtenerContenidoNodo(NombresNodos.FECHA_LIMITE_PAGO, this.doc);
            this.service.rectificar(peaje, nuevaRemesa, nombreArchivo, nuevaFechaLimitePago);
            this.registrarPeajeN('N');
        } catch (RegistroVacioException e) {
            logger.log(Level.INFO, ">>> No se encontró una factura para rectificar con {0}", codRectificada);
            throw new CodRectNoExisteException(codRectificada);
        }
    }

    /**
     * Pasa los valores obtenidos en la factura a negativo en los tipos de factura de abono
     * @param factura
     * @return
     */
    protected Peaje prepareAbono(Peaje factura){
        logger.log(Level.INFO, ">>> Pasando valores especificos a negativo del abono {0}", factura.getCodFisFac());
        String fecDes1 = factura.getEaFecDes1();
        String fecHas1 = factura.getEaFecHas1();
        String fecDes2 = factura.getEaFecDes2();
        String fecHas2 = factura.getEaFecHas2();
        factura.setEaFecDes1(fecHas1);
        factura.setEaFecHas1(fecDes1);
        factura.setEaFecDes2(fecHas2);
        factura.setEaFecHas2(fecDes2);

        //FechaDesdeFactura || FechaHastaFactura
        fecDes1 = factura.getTpFechaDesde();
        fecHas1 = factura.getTpFechaHasta();
        factura.setTpFechaDesde(fecHas1);
        factura.setTpFechaHasta(fecDes1);

        factura.setNumDias(Utilidades.valorAbsolutoNegativo(factura.getNumDias()));

        //ExcesoPotencia
        factura.setExcPot1(Utilidades.valorAbsolutoNegativo(factura.getExcPot1()));
        factura.setExcPot2(Utilidades.valorAbsolutoNegativo(factura.getExcPot2()));
        factura.setExcPot3(Utilidades.valorAbsolutoNegativo(factura.getExcPot3()));
        factura.setExcPot4(Utilidades.valorAbsolutoNegativo(factura.getExcPot4()));
        factura.setExcPot5(Utilidades.valorAbsolutoNegativo(factura.getExcPot5()));
        factura.setExcPot6(Utilidades.valorAbsolutoNegativo(factura.getExcPot6()));
        factura.setExcImpTot(Utilidades.valorAbsolutoNegativo(factura.getExcImpTot()));

        //PotenciaContratada
        factura.setPotCon1(Utilidades.valorAbsolutoNegativo(factura.getPotCon1()));
        factura.setPotCon2(Utilidades.valorAbsolutoNegativo(factura.getPotCon2()));
        factura.setPotCon3(Utilidades.valorAbsolutoNegativo(factura.getPotCon3()));
        factura.setPotCon4(Utilidades.valorAbsolutoNegativo(factura.getPotCon4()));
        factura.setPotCon5(Utilidades.valorAbsolutoNegativo(factura.getPotCon5()));
        factura.setPotCon6(Utilidades.valorAbsolutoNegativo(factura.getPotCon6()));

        //PotenciaMaxDemandada
        factura.setPotMax1(Utilidades.valorAbsolutoNegativo(factura.getPotMax1()));
        factura.setPotMax2(Utilidades.valorAbsolutoNegativo(factura.getPotMax2()));
        factura.setPotMax3(Utilidades.valorAbsolutoNegativo(factura.getPotMax3()));
        factura.setPotMax4(Utilidades.valorAbsolutoNegativo(factura.getPotMax4()));
        factura.setPotMax5(Utilidades.valorAbsolutoNegativo(factura.getPotMax5()));
        factura.setPotMax6(Utilidades.valorAbsolutoNegativo(factura.getPotMax6()));

        //PotenciaAFacturar
        factura.setPotFac1(Utilidades.valorAbsolutoNegativo(factura.getPotFac1()));
        factura.setPotFac2(Utilidades.valorAbsolutoNegativo(factura.getPotFac2()));
        factura.setPotFac3(Utilidades.valorAbsolutoNegativo(factura.getPotFac3()));

        //PotenciaPrecio
        factura.setPotPre1(Utilidades.valorAbsolutoNegativo(factura.getPotPre1()));
        factura.setPotPre2(Utilidades.valorAbsolutoNegativo(factura.getPotPre2()));
        factura.setPotPre3(Utilidades.valorAbsolutoNegativo(factura.getPotPre3()));
        factura.setPotPre4(Utilidades.valorAbsolutoNegativo(factura.getPotPre4()));
        factura.setPotPre5(Utilidades.valorAbsolutoNegativo(factura.getPotPre5()));
        factura.setPotPre6(Utilidades.valorAbsolutoNegativo(factura.getPotPre6()));
        factura.setPotImpTot(Utilidades.valorAbsolutoNegativo(factura.getPotImpTot()));

        //EnergíaActiva
        factura.setEaVal1(Utilidades.valorAbsolutoNegativo(factura.getEaVal1()));
        factura.setEaVal2(Utilidades.valorAbsolutoNegativo(factura.getEaVal2()));
        factura.setEaVal3(Utilidades.valorAbsolutoNegativo(factura.getEaVal3()));
        factura.setEaVal4(Utilidades.valorAbsolutoNegativo(factura.getEaVal4()));
        factura.setEaVal5(Utilidades.valorAbsolutoNegativo(factura.getEaVal5()));
        factura.setEaVal6(Utilidades.valorAbsolutoNegativo(factura.getEaVal6()));
        factura.setEaValSum(Utilidades.valorAbsolutoNegativo(factura.getEaValSum()));

        //Precios
        factura.setEaPre1(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaPre2(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaPre3(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaPre4(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaPre5(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaPre6(Utilidades.valorAbsolutoNegativo(factura.getEaPre1()));
        factura.setEaImpTot(Utilidades.valorAbsolutoNegativo(factura.getEaImpTot()));

        factura.setIeImp(Utilidades.valorAbsolutoNegativo(factura.getIeImp()));
        factura.setaImpFac(Utilidades.valorAbsolutoNegativo(factura.getaImpFac()));
        factura.setiBasImp(Utilidades.valorAbsolutoNegativo(factura.getiBasImp()));

        //AEConsumo
        factura.setAeCon1(Utilidades.valorAbsolutoNegativo(factura.getAeCon1()));
        factura.setAeCon2(Utilidades.valorAbsolutoNegativo(factura.getAeCon2()));
        factura.setAeCon3(Utilidades.valorAbsolutoNegativo(factura.getAeCon3()));
        factura.setAeCon4(Utilidades.valorAbsolutoNegativo(factura.getAeCon4()));
        factura.setAeCon5(Utilidades.valorAbsolutoNegativo(factura.getAeCon5()));
        factura.setAeCon6(Utilidades.valorAbsolutoNegativo(factura.getAeCon6()));
        factura.setAeConSum(Utilidades.valorAbsolutoNegativo(factura.getAeConSum()));

        //AELecturasDesde
        factura.setAeLecDes1(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes1()));
        factura.setAeLecDes2(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes2()));
        factura.setAeLecDes3(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes3()));
        factura.setAeLecDes4(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes4()));
        factura.setAeLecDes5(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes5()));
        factura.setAeLecDes6(Utilidades.valorAbsolutoNegativo(factura.getAeLecDes6()));

        //AELecturasHasta
        factura.setAeLecHas1(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas1()));
        factura.setAeLecHas2(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas2()));
        factura.setAeLecHas3(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas3()));
        factura.setAeLecHas4(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas4()));
        factura.setAeLecHas5(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas5()));
        factura.setAeLecHas6(Utilidades.valorAbsolutoNegativo(factura.getAeLecHas6()));

        factura.setAeProDes(Utilidades.valorAbsolutoNegativo(factura.getAeProDes()));
        factura.setAeProHas(Utilidades.valorAbsolutoNegativo(factura.getAeProHas()));

        //Reactiva
        factura.setrCon1(Utilidades.valorAbsolutoNegativo(factura.getrCon1()));
        factura.setrCon2(Utilidades.valorAbsolutoNegativo(factura.getrCon2()));
        factura.setrCon3(Utilidades.valorAbsolutoNegativo(factura.getrCon3()));
        factura.setrCon4(Utilidades.valorAbsolutoNegativo(factura.getrCon4()));
        factura.setrCon5(Utilidades.valorAbsolutoNegativo(factura.getrCon5()));
        factura.setrCon6(Utilidades.valorAbsolutoNegativo(factura.getrCon6()));
        factura.setrConSum(Utilidades.valorAbsolutoNegativo(factura.getrConSum()));

        factura.setrLecDes1(Utilidades.valorAbsolutoNegativo(factura.getrLecDes1()));
        factura.setrLecDes2(Utilidades.valorAbsolutoNegativo(factura.getrLecDes2()));
        factura.setrLecDes3(Utilidades.valorAbsolutoNegativo(factura.getrLecDes3()));
        factura.setrLecDes4(Utilidades.valorAbsolutoNegativo(factura.getrLecDes4()));
        factura.setrLecDes5(Utilidades.valorAbsolutoNegativo(factura.getrLecDes5()));
        factura.setrLecDes6(Utilidades.valorAbsolutoNegativo(factura.getrLecDes6()));

        factura.setrLecHas1(Utilidades.valorAbsolutoNegativo(factura.getrLecHas1()));
        factura.setrLecHas2(Utilidades.valorAbsolutoNegativo(factura.getrLecHas2()));
        factura.setrLecHas3(Utilidades.valorAbsolutoNegativo(factura.getrLecHas3()));
        factura.setrLecHas4(Utilidades.valorAbsolutoNegativo(factura.getrLecHas4()));
        factura.setrLecHas5(Utilidades.valorAbsolutoNegativo(factura.getrLecHas5()));
        factura.setrLecHas6(Utilidades.valorAbsolutoNegativo(factura.getrLecHas6()));
        factura.setrImpTot(Utilidades.valorAbsolutoNegativo(factura.getrImpTot()));

        factura.setPmCon1(Utilidades.valorAbsolutoNegativo(factura.getPmCon1()));
        factura.setPmCon2(Utilidades.valorAbsolutoNegativo(factura.getPmCon2()));
        factura.setPmCon3(Utilidades.valorAbsolutoNegativo(factura.getPmCon3()));
        factura.setPmCon4(Utilidades.valorAbsolutoNegativo(factura.getPmCon4()));
        factura.setPmCon5(Utilidades.valorAbsolutoNegativo(factura.getPmCon5()));
        factura.setPmCon6(Utilidades.valorAbsolutoNegativo(factura.getPmCon6()));
        factura.setPmConSum(Utilidades.valorAbsolutoNegativo(factura.getPmConSum()));

        factura.setPmLecHas1(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas1()));
        factura.setPmLecHas2(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas2()));
        factura.setPmLecHas3(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas3()));
        factura.setPmLecHas4(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas4()));
        factura.setPmLecHas5(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas5()));
        factura.setPmLecHas6(Utilidades.valorAbsolutoNegativo(factura.getPmLecHas6()));

        factura.setRfImpTot(Utilidades.valorAbsolutoNegativo(factura.getRfImpTot()));
        factura.setRfSalTotFac(Utilidades.valorAbsolutoNegativo(factura.getRfSalTotFac()));

        EnergiaExcedentaria excedentaria = factura.getEnergiaExcedentaria();
        if (excedentaria != null){
            excedentaria.setEnergiaExcedentaria01(Utilidades.valorAbsolutoNegativo(excedentaria.getEnergiaExcedentaria01()));
            excedentaria.setEnergiaExcedentaria02(Utilidades.valorAbsolutoNegativo(excedentaria.getEnergiaExcedentaria02()));
            excedentaria.setEnergiaExcedentaria03(Utilidades.valorAbsolutoNegativo(excedentaria.getEnergiaExcedentaria03()));
            excedentaria.setEnergiaExcedentaria04(Utilidades.valorAbsolutoNegativo(excedentaria.getEnergiaExcedentaria04()));
            excedentaria.setEnergiaExcedentaria05(Utilidades.valorAbsolutoNegativo(excedentaria.getEnergiaExcedentaria05()));
            excedentaria.setEnergiaExcedentaria06(Utilidades.valorAbsolutoNegativo(excedentaria.getEnergiaExcedentaria06()));
            excedentaria.setValorTotalEnergiaExcedentaria(Utilidades.valorAbsolutoNegativo(excedentaria.getValorTotalEnergiaExcedentaria()));
            factura.setEnergiaExcedentaria(excedentaria);
        }

        return factura;
    }

    private Peaje crearPeaje(TIPO_FACTURA tp) throws MasDeUnClienteEncontrado, TarifaNoExisteException {
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
        if (tp == TIPO_FACTURA.C_) return peaje;
        EnergiaExcedentaria energiaExcedentaria = this.energiaExcedentaria();
        if (this.existeEnergiaExcedentaria) {
            this.existeEnergiaExcedentaria = false;
            peaje.setEnergiaExcedentaria(energiaExcedentaria);
        }
        return peaje;
    }

    /**
     * Crea un peaje del tipo C, este peaje es similar a los del tipo N, pero tiene menos datos embebidos en el documento xml
     * @return
     */
    private Peaje crearPeajeC(){
        Peaje peaje = new Peaje();
        return peaje;
    }

}
