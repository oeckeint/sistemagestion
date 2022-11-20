package datos.dao;

import datos.entity.Factura;
import datos.entity.OtraFactura;
import datos.entity.Peaje;
import excepciones.NoEsUnNumeroException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import excepciones.PeajeMasDeUnRegistroException;
import excepciones.RegistroVacioException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FacturaDaoImp implements datos.interfaces.DocumentoXmlDao<Factura> {

    private Logger logger = Logger.getLogger(getClass().getName());

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
    public List<Factura> listar(int rows, int page) {
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
                .setFirstResult(rows * page)
                .setMaxResults(rows)
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
    public Factura buscarByCodFiscal(String cod) throws PeajeMasDeUnRegistroException, RegistroVacioException {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createQuery("from Factura f where f.codFisFac = :cod1 or f.codFisFac = :cod2", Factura.class)
                    .setParameter("cod1", cod)
                    .setParameter("cod2", cod + "-A")
                    .getSingleResult();
        } catch (NonUniqueResultException e) {
            logger.log(Level.INFO, ">>> FacturaDaoImp={0}", e.getMessage());
            throw new PeajeMasDeUnRegistroException(cod);
        } catch (NoResultException e) {
            logger.log(Level.INFO, ">>> FacturaDaoImp={0}", "No se encontró algún registro con el cod " + cod + " / " + cod + "-A");
            throw  new RegistroVacioException();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    /**
     * Busca un registro con codigo fiscal especifico
     * @param cod
     * @return
     * @throws PeajeMasDeUnRegistroException
     */
    @Override
    public Factura buscarByCodFiscalEspecifico(String cod) throws PeajeMasDeUnRegistroException, RegistroVacioException {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createQuery("from Factura f where f.codFisFac = :cod1", Factura.class)
                    .setParameter("cod1", cod)
                    .getSingleResult();
        } catch (NonUniqueResultException e) {
            logger.log(Level.INFO, ">>> FacturaDaoImp={0}", e.getMessage());
            throw new PeajeMasDeUnRegistroException(cod);
        } catch (NoResultException e) {
            logger.log(Level.INFO, ">>> FacturaDaoImp={0}", "No se encontró algún registro con el codFisFac especifico " + cod);
            throw new RegistroVacioException();
        } catch (Exception e) {
            e.printStackTrace(System.out);
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
    public int contarPaginacion(int rows) {
        Query query = this.sessionFactory.getCurrentSession().createNativeQuery("select count(*) from contenido_xml_factura where is_deleted = 0");
        Long a = Long.parseLong(query.uniqueResult().toString());
        Double b = Math.ceil((double)a / rows);
        return b.intValue();
    }

    @Override
    public int contarRegistros() {
        Query query = this.sessionFactory.getCurrentSession().createNativeQuery("select count(*) from contenido_xml_factura where is_deleted = 0");
        Long a = Long.parseLong(query.uniqueResult().toString());
        return a.intValue();
    }

}
