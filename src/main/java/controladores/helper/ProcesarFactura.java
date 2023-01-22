package controladores.helper;

import datos.entity.Factura;
import datos.interfaces.DocumentoXmlService;
import excepciones.*;
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
public class ProcesarFactura extends xmlHelper {

    private final DocumentoXmlService service;
    @Value("${abono.factura.validaterectificadaanulada}")
    private boolean isValidAbonoFacturaActive;

    private final Logger logger = Logger.getLogger(getClass().getName());

    public ProcesarFactura(@Qualifier(value = "facturasServiceImp") DocumentoXmlService service){
        this.service = service;
    }

    /**
     * Aplica para todos los registros que tengan 0894 como empresaEmisora Lee
     * los datos y registra en la tabla denominada "contenido_xml_facturas"
     *
     * @param nombreArchivo referencia al nombre del archivo actual
     * @throws FacturaYaExisteException
     * @throws ClienteNoExisteException
     * @throws PeajeTipoFacturaNoSoportadaException
     * @throws CodRectNoExisteException
     * @throws PeajeMasDeUnRegistroException
     */
    public void procesarFactura(Document doc, String nombreArchivo)
            throws FacturaYaExisteException, ClienteNoExisteException, PeajeTipoFacturaNoSoportadaException, CodRectNoExisteException, MasDeUnClienteEncontrado, TarifaNoExisteException, PeajeMasDeUnRegistroException {
        this.doc = doc;
        this.nombreArchivo = nombreArchivo;
        this.iniciarVariables();

        if (this.cliente != null) {
            //Se revisa que la factura no exista
            try {
                this.service.buscarByCodFiscal(this.codFactura);
                logger.log(Level.INFO, ">>> Ya existe una factura con el codigo Fiscal {0}", this.codFactura);
                throw new FacturaYaExisteException(this.codFactura, "factura");
            } catch (RegistroVacioException e) {
                logger.log(Level.INFO, ">>> Nuevo registro en Facturas {0}", this.codFactura);
            }

            this.comentarios.append("Nombre de archivo original: <Strong>").append(this.nombreArchivo).append("</Strong><br/>");
            logger.log(Level.INFO, ">>> Tipo Factura {0}", this.tipoFactura);
            switch (this.tipoFactura) {
                case "A":
                    this.registrarFacturaA();
                    break;
                case "N":
                case "G":
                    this.registrarFacturaN();
                    break;
                case "R":
                    this.registrarFacturaR(nombreArchivo);
                    break;
                default:
                    throw new PeajeTipoFacturaNoSoportadaException(this.tipoFactura);
            }
        } else {
            throw new ClienteNoExisteException(this.cups);
        }
    }

    /**
     * Registra los el tipos de Factura A (Abono)
     * Se obtienen todos los valores absolutos y se registran con valores negativos en la tabla de contenido_xml_facturas
     * No se necesita un registro existente para ejecutar esta factura
     *
     */
    private void registrarFacturaA() {
        logger.log(Level.INFO, ">>> Registrando factura del tipo Abono con el codFisFac {0}", xml.obtenerContenidoNodo(NombresNodos.COD_FIS_FAC, this.doc));
        try {
            Factura f = this.crearFactura();
            f = this.prepareAbonoFactura(f);
            this.service.guardar(f);
            System.out.println("isValidAbonoFacturaActive = " + isValidAbonoFacturaActive);
        } catch (Exception e) {

        }
        /*
        String codRectificada = xml.obtenerContenidoNodo(NombresNodos.COD_FAC_REC_ANU, this.doc);
        try {
            Factura f = (Factura) this.contenidoXmlService.buscarByCodFiscal(codRectificada);
            System.out.println("Se intenta realizar registro");
            //this.registrarFacturaA();
        }catch (RegistroVacioException e) {
            logger.log(Level.INFO, ">>> No se encontró una factura para abonar con {0}", codRectificada);
            throw new CodRectNoExisteException(codRectificada);
        }
         */
    }

    /**
     * Registra el Factura de tipo N
     */
    private void registrarFacturaN() throws MasDeUnClienteEncontrado, TarifaNoExisteException {
        this.service.guardar(this.crearFactura());
        System.out.print("\n\nComentarios : " + comentarios.toString());
        System.out.print("Codigo Errores : " + errores.toString());
    }

    /**
     * Busca que el Factura a rectificar exista, cuando la factura ya existe
     * arroja una excepcion, de lo contraro hara un proceso de rectificación y
     * usara el metodo registrarN para guardar en registro en la BD
     *
     * @param nombreArchivo
     * @throws CodRectNoExisteException
     * @throws PeajeMasDeUnRegistroException
     */
    private void registrarFacturaR(String nombreArchivo) throws CodRectNoExisteException, MasDeUnClienteEncontrado, TarifaNoExisteException, PeajeMasDeUnRegistroException {
        String codRectificada = xml.obtenerContenidoNodo(NombresNodos.COD_FAC_REC_ANU, this.doc);
        try {
            Factura factura = (Factura) this.service.buscarByCodFiscal(codRectificada);
            String nuevaRemesa = String.valueOf(Long.parseLong(xml.obtenerContenidoNodo(NombresNodos.ID_REM, this.doc)));
            this.service.rectificar(factura, nuevaRemesa, nombreArchivo);
            this.registrarFacturaN();
        } catch (RegistroVacioException e) {
            logger.log(Level.INFO, ">>> No se encontró una factura para rectificar con {0}", codRectificada);
            throw new CodRectNoExisteException(codRectificada);
        }
    }

    protected Factura prepareAbonoFactura(Factura factura){
        logger.log(Level.INFO, ">>> Pasando valores especificos a negativo del abono de factura {0}", factura.getCodFisFac());
        factura.setTipFac("A");
        String fecDes1 = factura.getEaFecDes1();
        String fecHas1 = factura.getEaFecHas1();
        String fecDes2 = factura.getEaFecDes2();
        String fecHas2 = factura.getEaFecHas2();
        factura.setEaFecDes1(fecHas1);
        factura.setEaFecHas1(fecDes1);
        factura.setEaFecDes2(fecHas2);
        factura.setEaFecHas2(fecDes2);

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

        return factura;
    }

    private Factura crearFactura() throws MasDeUnClienteEncontrado, TarifaNoExisteException {
        return new Factura(
                this.cliente, this.cabecera(), this.datosGenerales(), this.datosFacturaAtr(),
                this.potenciaExcesos(), this.potenciaContratada(), this.potenciaDemandada(), this.potenciaAFacturar(), this.potenciaPrecio(), this.potenciaImporteTotal(),
                this.energiaActivaDatos(), this.energiaActivaValores(), this.energiaActivaPrecio(), this.energiaActivaImporteTotal(),
                this.impuestoElectrico(), this.alquileres(), this.iva(),
                this.aeConsumo(), this.aeLecturaDesde(), this.aeLecturaHasta(), this.aeProcedenciaDesde(), this.aeProcedenciaHasta(),
                this.rConsumo(), this.rLecturaDesde(), this.rLecturaHasta(), this.rImporteTotal(),
                this.pmConsumo(), this.pmLecturaHasta(),
                this.registroFin(), this.comentarios.toString(), this.errores.toString()
        );
    }

}
