package datos.service;

import datos.entity.Factura;
import datos.entity.OtraFactura;
import datos.interfaces.DocumentoXmlDao;
import excepciones.NoEsUnNumeroException;
import excepciones.PeajeMasDeUnRegistroException;

import java.util.Date;
import java.util.List;

import excepciones.RegistroVacioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import controladores.helper.Utilidades;

@Service
public class FacturasServiceImp implements datos.interfaces.DocumentoXmlService<Factura> {

    @Autowired
    @Qualifier(value = "facturaDaoImp")
    private DocumentoXmlDao<Factura> documentoXmlDao;

    @Override
    @Transactional
    public List<Factura> listar() {
        return this.documentoXmlDao.listar();
    }

    @Override
    @Transactional
    public List<Factura> listar(int rows, int page) {
        return this.documentoXmlDao.listar(rows, page);
    }

    @Override
    @Transactional
    public void guardar(Factura documentoXml) {
    	documentoXml.setCreatedOn(new Date());
    	documentoXml.setCreatedBy(Utilidades.currentUser());
        this.documentoXmlDao.guardar(documentoXml);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.documentoXmlDao.eliminar(id);
    }

    @Override
    @Transactional
    public Factura buscarByCodFiscal(String cod) throws PeajeMasDeUnRegistroException, RegistroVacioException {
        return this.documentoXmlDao.buscarByCodFiscal(cod);
    }

    @Override
    @Transactional
    public Factura buscarByCodFiscalEspecifico(String cod) throws PeajeMasDeUnRegistroException, RegistroVacioException {
        return this.documentoXmlDao.buscarByCodFiscalEspecifico(cod);
    }

    @Override
    @Transactional
    public List<Factura> buscarByIdCliente(String idCliente) throws NoEsUnNumeroException {
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
