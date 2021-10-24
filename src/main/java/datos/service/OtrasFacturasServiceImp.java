package datos.service;

import datos.entity.OtraFactura;
import datos.interfaces.DocumentoXmlDao;
import excepciones.NoEsUnNumeroException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
@Service
public class OtrasFacturasServiceImp implements datos.interfaces.DocumentoXmlService<OtraFactura>{

    @Autowired
    @Qualifier(value = "otrasFacturasDaoImp")
    private DocumentoXmlDao documentoXmlDao;
    
    @Override
    @Transactional
    public List<OtraFactura> listar() {
        return this.documentoXmlDao.listar();
    }

    @Override
    @Transactional
    public void guardar(OtraFactura documento) {
        this.documentoXmlDao.guardar(documento);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.documentoXmlDao.eliminar(id);
    }

    @Override
    @Transactional
    public OtraFactura buscarByCodFiscal(String cod) {
        return (OtraFactura) this.documentoXmlDao.buscarByCodFiscal(cod);
    }

    @Override
    @Transactional
    public List<OtraFactura> buscarByIdCliente(String idCliente) throws NoEsUnNumeroException{
        return this.documentoXmlDao.buscarByIdCliente(idCliente);
    }

    @Override
    @Transactional
    public List<OtraFactura> buscarByRemesa(String remesa) {
        return this.documentoXmlDao.buscarByRemesa(remesa);
    }

    @Override
    @Transactional
    public void rectificar(OtraFactura documento, String nuevaRemesa, String nuevoNombreArchivo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
