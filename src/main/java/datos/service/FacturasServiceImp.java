package datos.service;

import datos.entity.Factura;
import datos.interfaces.DocumentoXmlDao;
import excepciones.NoEsUnNumeroException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacturasServiceImp implements datos.interfaces.DocumentoXmlService<Factura>{
    
    @Autowired
    @Qualifier(value = "facturaDaoImp")
    private DocumentoXmlDao documentoXmlDao;
    
    @Override
    @Transactional
    public List<Factura> listar() {
        return this.documentoXmlDao.listar();
    }

    @Override
    @Transactional
    public void guardar(Factura documentoXml) {
        this.documentoXmlDao.guardar(documentoXml);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.documentoXmlDao.eliminar(id);
    }

    @Override
    @Transactional
    public Factura buscarByCodFiscal(String cod) {
        return (Factura) this.documentoXmlDao.buscarByCodFiscal(cod);
    }

    @Override
    @Transactional
    public List<Factura> buscarByIdCliente(String idCliente) throws NoEsUnNumeroException{
        return this.documentoXmlDao.buscarByIdCliente(idCliente);
    }

    @Override
    @Transactional
    public List<Factura> buscarByRemesa(String remesa) {
        return this.documentoXmlDao.buscarByRemesa(remesa);
    }

    @Override
    @Transactional
    public void rectificar(Factura documento, String nuevaRemesa, String nuevoNombreArchivo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public void actualizar(Factura documento) {
        this.documentoXmlDao.actualizar(documento);
    }

    @Override
    @Transactional
    public List<Factura> listar(int rows, int page) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public int contarPaginacion(int rows) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public int contarRegistros() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
