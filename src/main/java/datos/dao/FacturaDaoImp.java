package datos.dao;

import datos.entity.Factura;
import excepciones.NoEsUnNumeroException;
import java.util.List;
import javax.persistence.NoResultException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FacturaDaoImp implements datos.interfaces.DocumentoXmlDao<Factura> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Factura> listar() {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Factura f where f.isDeleted = 0 "
                            + "order by "
                            + "case f.eaFecHas2"
                            + " when '' then"
                            + "     f.eaFecHas1"
                            + " else"
                            + "     f.eaFecHas2"
                            + " end "
                            + "desc", Factura.class)
                .getResultList();
    }

    @Override
    public void guardar(Factura factura) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(factura);
    }

    @Override
    public void eliminar(long id) {
        this.sessionFactory.getCurrentSession()
                .createQuery("delete from Factura f where f.idFactura = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public Factura buscarByCodFiscal(String cod) {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createQuery("from Factura f where f.codFisFac = :cod1 or f.codFisFac = :cod2 order by f.idFactura desc", Factura.class)
                    .setParameter("cod1", cod)
                    .setParameter("cod2", cod + "-A")
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public List<Factura> buscarByIdCliente(String idCliente) throws NoEsUnNumeroException {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createQuery("from Factura f where f.idCliente = :id "
                            + "order by "
                            + "case f.eaFecHas2"
                            + " when '' then"
                            + "     f.eaFecHas1"
                            + " else"
                            + "     f.eaFecHas2"
                            + " end "
                            + "desc", Factura.class)
                    .setParameter("id", Long.parseLong(idCliente))
                    .getResultList();
        } catch (NumberFormatException e) {
            System.out.println("(FacturasDAOImp) no se inserto un numero");
            throw new NoEsUnNumeroException();
        }
    }

    @Override
    public List<Factura> buscarByRemesa(String remesa) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Factura f where f.rfIdRem = :remesa "
                            + "order by "
                            + "case f.eaFecHas2"
                            + " when '' then"
                            + "     f.eaFecHas1"
                            + " else"
                            + "     f.eaFecHas2"
                            + " end "
                            + "desc", Factura.class)
                .setParameter("remesa", remesa)
                .getResultList();
    }
    
    @Override
    public void actualizar(Factura documento) {
        this.sessionFactory.getCurrentSession().update(documento);
    }

    @Override
    public List<Factura> listar(int rows, int page) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int contarPaginacion(int rows) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int contarRegistros() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
