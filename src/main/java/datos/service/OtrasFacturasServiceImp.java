package datos.service;

import datos.entity.OtraFactura;
import datos.interfaces.DocumentoXmlDao;
import excepciones.NoEsUnNumeroException;
import excepciones.PeajeMasDeUnRegistroException;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import excepciones.RegistroVacioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import controladores.helper.Utilidades;
import utileria.PaginacionUtil;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
@Service
public class OtrasFacturasServiceImp implements datos.interfaces.DocumentoXmlService<OtraFactura> {

    @Autowired
    @Qualifier(value = "otrasFacturasDaoImp")
    private DocumentoXmlDao<OtraFactura> documentoXmlDao;

    @Override
    @Transactional
    public List<OtraFactura> listar() {
        return this.documentoXmlDao.listar();
    }

    @Override
    @Transactional
    public List<OtraFactura> listar(int rows, int page) {
        return PaginacionUtil.validarPaginacion(rows, page, () -> this.documentoXmlDao.listar(rows, page));
    }

    @Override
    @Transactional
    public void guardar(OtraFactura documento) {
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
    public OtraFactura buscarByCodFiscal(String cod) throws PeajeMasDeUnRegistroException, RegistroVacioException {
        return this.documentoXmlDao.buscarByCodFiscal(cod);
    }

    @Override
    @Transactional
    public OtraFactura buscarByCodFiscalEspecifico(String cod) throws PeajeMasDeUnRegistroException, RegistroVacioException {
        return this.documentoXmlDao.buscarByCodFiscalEspecifico(cod);
    }

    @Override
    @Transactional
    public List<OtraFactura> buscarByIdCliente(String idCliente) throws NoEsUnNumeroException {
        return this.documentoXmlDao.buscarByIdCliente(idCliente);
    }

    @Override
    @Transactional
    public List<OtraFactura> buscarByRemesa(String remesa) {
        return this.documentoXmlDao.buscarByRemesa(remesa);
    }

    @Override
    @Transactional
    public void rectificar(OtraFactura documento, String nuevaRemesa, String nuevoNombreArchivo, String nuevaFechaLimitePago) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public void actualizar(OtraFactura documento) {
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
