package datos.service;

import datos.entity.EnergiaExcedentaria;
import datos.entity.Peaje;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import controladores.helper.Utilidades;
import datos.interfaces.DocumentoXmlDao;
import excepciones.NoEsUnNumeroException;
import excepciones.PeajeMasDeUnRegistroException;

import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class PeajesServiceImp implements datos.interfaces.DocumentoXmlService<Peaje> {

	private Logger logger = Logger.getLogger(getClass().getName());
	
    @Autowired
    @Qualifier(value = "peajesImp")
    private DocumentoXmlDao<Peaje> documentoXmlDao;

    @Override
    @Transactional
    public List<Peaje> listar() {
        return this.documentoXmlDao.listar();
    }
    
    @Override
    @Transactional
    public List<Peaje> listar(int rows, int page) {
        return this.documentoXmlDao.listar(rows, page);
    }

    @Override
    @Transactional
    public void guardar(Peaje documento) {
    	documento.setCreatedOn(new Date());
    	documento.setCreatedBy(Utilidades.currentUser());
        this.documentoXmlDao.guardar(documento);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.documentoXmlDao.eliminar(id);
    }

    @Override
    @Transactional
    public Peaje buscarByCodFiscal(String cod) throws PeajeMasDeUnRegistroException {
        return this.documentoXmlDao.buscarByCodFiscal(cod);
    }

    @Override
    @Transactional
    public List<Peaje> buscarByIdCliente(String idCliente) throws NoEsUnNumeroException{
        return this.documentoXmlDao.buscarByIdCliente(idCliente);
    }

    @Override
    @Transactional
    public List<Peaje> buscarByRemesa(String remesa) {
        return this.documentoXmlDao.buscarByRemesa(remesa);
    }

    @Override
    @Transactional
    public void rectificar(Peaje documento, String nuevaRemesa, String nuevoNombreArchivo) {
        Peaje peaje = documento;
        peaje.setIdPeaje(0L);
        peaje.setCodFisFac(documento.getCodFisFac() + "-A");
        peaje.setTipFac("Av");
        peaje.setNumDias(documento.getNumDias() * -1);
        
        peaje.setExcPot1(documento.getExcPot1() * -1);
        peaje.setExcPot2(documento.getExcPot2() * -1);
        peaje.setExcPot3(documento.getExcPot3() * -1);
        peaje.setExcPot4(documento.getExcPot4() * -1);
        peaje.setExcPot5(documento.getExcPot5() * -1);
        peaje.setExcPot6(documento.getExcPot6() * -1);
        peaje.setExcImpTot(documento.getExcImpTot() * -1);
        
        peaje.setPotCon1(peaje.getPotCon1() * -1 );
        peaje.setPotCon2(peaje.getPotCon2() * -1 );
        peaje.setPotCon3(peaje.getPotCon3() * -1 );
        peaje.setPotCon4(peaje.getPotCon4() * -1 );
        peaje.setPotCon5(peaje.getPotCon5() * -1 );
        peaje.setPotCon6(peaje.getPotCon6() * -1 );
        
        peaje.setPotMax1(peaje.getPotMax1() * -1);
        peaje.setPotMax2(peaje.getPotMax2() * -1);
        peaje.setPotMax3(peaje.getPotMax3() * -1);
        peaje.setPotMax4(peaje.getPotMax4() * -1);
        peaje.setPotMax5(peaje.getPotMax5() * -1);
        peaje.setPotMax6(peaje.getPotMax6() * -1);
        
        peaje.setPotFac1(peaje.getPotFac1() * -1);
        peaje.setPotFac2(peaje.getPotFac2() * -1);
        peaje.setPotFac3(peaje.getPotFac3() * -1);
        peaje.setPotImpTot(peaje.getPotImpTot() * -1);
        
        //Fechas EA
        String fecDes1 = peaje.getEaFecDes1();
        String fecHas1 = peaje.getEaFecHas1();
        String fecDes2 = peaje.getEaFecDes2();
        String fecHas2 = peaje.getEaFecHas2();
        peaje.setEaFecDes1(fecHas1);
        peaje.setEaFecHas1(fecDes1);
        peaje.setEaFecDes2(fecHas2);
        peaje.setEaFecHas2(fecDes2);
        
        peaje.setEaVal1(peaje.getEaVal1() * -1);
        peaje.setEaVal2(peaje.getEaVal2() * -1);
        peaje.setEaVal3(peaje.getEaVal3() * -1);
        peaje.setEaVal4(peaje.getEaVal4() * -1);
        peaje.setEaVal5(peaje.getEaVal5() * -1);
        peaje.setEaVal6(peaje.getEaVal6() * -1);
        peaje.setEaValSum(peaje.getEaValSum() * -1);
        
        peaje.setEaImpTot(peaje.getEaImpTot() * -1);
        
        
        //Cargos
    	peaje.setCar1_01(peaje.getCar1_01() * -1);
        peaje.setCar2_01(peaje.getCar2_01() * -1);
        peaje.setCar3_01(peaje.getCar3_01() * -1);
        peaje.setCar4_01(peaje.getCar4_01() * -1);
        peaje.setCar5_01(peaje.getCar5_01() * -1);
        peaje.setCar6_01(peaje.getCar6_01() * -1);
        peaje.setCarImpTot_01(peaje.getCarImpTot_01() * -1);
    
    	peaje.setCar1_02(peaje.getCar1_02() * -1);
        peaje.setCar2_02(peaje.getCar2_02() * -1);
        peaje.setCar3_02(peaje.getCar3_02() * -1);
        peaje.setCar4_02(peaje.getCar4_02() * -1);
        peaje.setCar5_02(peaje.getCar5_02() * -1);
        peaje.setCar6_02(peaje.getCar6_02() * -1);
        peaje.setCarImpTot_02(peaje.getCarImpTot_02() * -1);
        
        peaje.setIeImp(peaje.getIeImp() * -1);
        peaje.setaImpFac(peaje.getaImpFac() * -1);
        peaje.setiBasImp(peaje.getiBasImp() * -1);
        
        peaje.setAeCon1(peaje.getAeCon1() * -1);
        peaje.setAeCon2(peaje.getAeCon2() * -1);
        peaje.setAeCon3(peaje.getAeCon3() * -1);
        peaje.setAeCon4(peaje.getAeCon4() * -1);
        peaje.setAeCon5(peaje.getAeCon5() * -1);
        peaje.setAeCon6(peaje.getAeCon6() * -1);
        peaje.setAeConSum(peaje.getAeConSum()* -1);
        
        peaje.setAeLecDes1(peaje.getAeLecDes1() * -1);
        peaje.setAeLecDes2(peaje.getAeLecDes2() * -1);
        peaje.setAeLecDes3(peaje.getAeLecDes3() * -1);
        peaje.setAeLecDes4(peaje.getAeLecDes4() * -1);
        peaje.setAeLecDes5(peaje.getAeLecDes5() * -1);
        peaje.setAeLecDes6(peaje.getAeLecDes6() * -1);
        
        peaje.setAeLecHas1(peaje.getAeLecHas1() * -1);
        peaje.setAeLecHas2(peaje.getAeLecHas2() * -1);
        peaje.setAeLecHas3(peaje.getAeLecHas3() * -1);
        peaje.setAeLecHas4(peaje.getAeLecHas4() * -1);
        peaje.setAeLecHas5(peaje.getAeLecHas5() * -1);
        peaje.setAeLecHas6(peaje.getAeLecHas6() * -1);
        
        peaje.setrCon1(peaje.getrCon1() * -1);
        peaje.setrCon2(peaje.getrCon2() * -1);
        peaje.setrCon3(peaje.getrCon3() * -1);
        peaje.setrCon4(peaje.getrCon4() * -1);
        peaje.setrCon5(peaje.getrCon5() * -1);
        peaje.setrCon6(peaje.getrCon6() * -1);
        peaje.setrConSum(peaje.getrConSum()* -1);
        
        peaje.setrLecDes1(peaje.getrLecDes1() * -1);
        peaje.setrLecDes2(peaje.getrLecDes2() * -1);
        peaje.setrLecDes3(peaje.getrLecDes3() * -1);
        peaje.setrLecDes4(peaje.getrLecDes4() * -1);
        peaje.setrLecDes5(peaje.getrLecDes5() * -1);
        peaje.setrLecDes6(peaje.getrLecDes6() * -1);
        
        peaje.setrLecHas1(peaje.getrLecHas1() * -1);
        peaje.setrLecHas2(peaje.getrLecHas2() * -1);
        peaje.setrLecHas3(peaje.getrLecHas3() * -1);
        peaje.setrLecHas4(peaje.getrLecHas4() * -1);
        peaje.setrLecHas5(peaje.getrLecHas5() * -1);
        peaje.setrLecHas6(peaje.getrLecHas6() * -1);
        
        peaje.setrImpTot(peaje.getrImpTot() * -1);
        
        peaje.setPmCon1(peaje.getPmCon1() * -1);
        peaje.setPmCon2(peaje.getPmCon2() * -1);
        peaje.setPmCon3(peaje.getPmCon3() * -1);
        peaje.setPmCon4(peaje.getPmCon4() * -1);
        peaje.setPmCon5(peaje.getPmCon5() * -1);
        peaje.setPmCon6(peaje.getPmCon6() * -1);
        peaje.setPmConSum(peaje.getPmConSum() * -1);
        
        peaje.setPmLecHas1(peaje.getPmLecHas1() * -1);
        peaje.setPmLecHas2(peaje.getPmLecHas2() * -1);
        peaje.setPmLecHas3(peaje.getPmLecHas3() * -1);
        peaje.setPmLecHas4(peaje.getPmLecHas4() * -1);
        peaje.setPmLecHas5(peaje.getPmLecHas5() * -1);
        peaje.setPmLecHas6(peaje.getPmLecHas6() * -1);
        
        peaje.setRfImpTot(peaje.getRfImpTot() * -1);
        peaje.setRfSalTotFac(peaje.getRfSalTotFac() * -1);
        peaje.setEstadoPago(2);
        
        peaje.setComentarios(peaje.getComentarios() + "El archivo ha sido rectificado por la factura <Strong>" + nuevoNombreArchivo + "</Strong><br/>"
                + "Se cambio la remensa <Strong>" + peaje.getRfIdRem() + "</Strong> por <Strong>" + nuevaRemesa + "</Strong>.");
                
        peaje.setRfIdRem(nuevaRemesa);
        
        if (peaje.getEnergiaExcedentaria() != null) {
        	EnergiaExcedentaria eE = new EnergiaExcedentaria();
            eE.setEnergiaExcedentaria01(peaje.getEnergiaExcedentaria().getEnergiaExcedentaria01() * -1);
            eE.setEnergiaExcedentaria02(peaje.getEnergiaExcedentaria().getEnergiaExcedentaria02() * -1);
            eE.setEnergiaExcedentaria03(peaje.getEnergiaExcedentaria().getEnergiaExcedentaria03() * -1);
            eE.setEnergiaExcedentaria04(peaje.getEnergiaExcedentaria().getEnergiaExcedentaria04() * -1);
            eE.setEnergiaExcedentaria05(peaje.getEnergiaExcedentaria().getEnergiaExcedentaria05() * -1);
            eE.setEnergiaExcedentaria06(peaje.getEnergiaExcedentaria().getEnergiaExcedentaria06() * -1);
            eE.setValorTotalEnergiaExcedentaria(peaje.getEnergiaExcedentaria().getValorTotalEnergiaExcedentaria() * - 1);
            peaje.setEnergiaExcedentaria(eE);
		}
        
        this.guardar(peaje);
        
        logger.log(Level.INFO, ">>> PeajeServiceImp=\"Se ha registrado una factura rectificada codFisFac = {0}\"", peaje.getCodFisFac());
    }

    @Override
    @Transactional
    public void actualizar(Peaje documento) {
    	documento.setUpdatedOn(new Date());
    	documento.setUpdatedBy(Utilidades.currentUser());
        this.documentoXmlDao.actualizar(documento);
    }

    @Override
    @Transactional
    public int contarPaginacion(int rows) {
        return this.documentoXmlDao.contarPaginacion(rows);
    }

    @Override
    @Transactional
    public int contarRegistros() {
		return this.documentoXmlDao.contarRegistros();
	}

}
