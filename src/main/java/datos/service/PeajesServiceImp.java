package datos.service;

import datos.entity.Peaje;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import datos.interfaces.DocumentoXmlDao;
import excepciones.NoEsUnNumeroException;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class PeajesServiceImp implements datos.interfaces.DocumentoXmlService<Peaje> {

    @Autowired
    @Qualifier(value = "peajesImp")
    private DocumentoXmlDao documentoXmlDao;

    @Override
    @Transactional
    public List<Peaje> listar() {
        return this.documentoXmlDao.listar();
    }

    @Override
    @Transactional
    public void guardar(Peaje documento) {
        this.documentoXmlDao.guardar(documento);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.documentoXmlDao.eliminar(id);
    }

    @Override
    @Transactional
    public Peaje buscarByCodFiscal(String cod) {
        return (Peaje) this.documentoXmlDao.buscarByCodFiscal(cod);
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
        
        peaje.setEaVal1(peaje.getEaVal1() * -1);
        peaje.setEaVal2(peaje.getEaVal2() * -1);
        peaje.setEaVal3(peaje.getEaVal3() * -1);
        peaje.setEaVal4(peaje.getEaVal4() * -1);
        peaje.setEaVal5(peaje.getEaVal5() * -1);
        peaje.setEaVal6(peaje.getEaVal6() * -1);
        peaje.setEaValSum(peaje.getEaValSum() * -1);
        
        peaje.setEaImpTot(peaje.getEaImpTot() * -1);
        
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
        this.documentoXmlDao.guardar(peaje);
    }

}
