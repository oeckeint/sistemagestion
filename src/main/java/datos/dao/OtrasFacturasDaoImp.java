package datos.dao;

import datos.entity.OtraFactura;
import datos.entity.Peaje;
import excepciones.NoEsUnNumeroException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import excepciones.PeajeMasDeUnRegistroException;
import excepciones.RegistroVacioException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

@Repository
public class OtrasFacturasDaoImp implements datos.interfaces.DocumentoXmlDao<OtraFactura>{

    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public List<OtraFactura> listar() {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from OtraFactura o order by o.idOtraFactura desc", OtraFactura.class)
                .getResultList();
    }

    @Override
    public List<OtraFactura> listar(int rows, int page) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from OtraFactura o order by o.idOtraFactura desc", OtraFactura.class)
                .setFirstResult(rows * page)
                .setMaxResults(rows)
                .getResultList();
    }
    
    @Override
    public void guardar(OtraFactura documento) {
        this.sessionFactory.getCurrentSession().save(documento);
    }

    @Override
    public void eliminar(long id) {
        this.sessionFactory.getCurrentSession()
                .createQuery("delete from OtraFactura o where o.idOtraFactura = :id", OtraFactura.class)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public OtraFactura buscarByCodFiscal(String cod) throws PeajeMasDeUnRegistroException, RegistroVacioException {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createQuery("from OtraFactura o where o.codFisFac = :cod1 or o.codFisFac = :cod2", OtraFactura.class)
                    .setParameter("cod1", cod)
                    .setParameter("cod2", cod + "-A")
                    .getSingleResult();
        } catch (NonUniqueResultException e) {
            logger.log(Level.INFO, ">>> OtrasFacturasDaoImp={0}", e.getMessage());
            throw new PeajeMasDeUnRegistroException(cod);
        } catch (NoResultException e) {
            logger.log(Level.INFO, ">>> OtrasFacturasDaoImp={0}", "No se encontró algún registro con el cod " + cod + " / " + cod + "-A");
            throw new RegistroVacioException();
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
    public OtraFactura buscarByCodFiscalEspecifico(String cod) throws PeajeMasDeUnRegistroException, RegistroVacioException {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createQuery("from OtraFactura of where of.codFisFac = :cod1", OtraFactura.class)
                    .setParameter("cod1", cod)
                    .getSingleResult();
        } catch (NonUniqueResultException e) {
            logger.log(Level.INFO, ">>> OtrasFacturasDaoImp={0}", e.getMessage());
            throw new PeajeMasDeUnRegistroException(cod);
        } catch (NoResultException e) {
            logger.log(Level.INFO, ">>> OtrasFacturasDaoImp={0}", "No se encontró algún registro con el codFisFac especifico " + cod);
            throw new RegistroVacioException();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    @Override
    public List<OtraFactura> buscarByIdCliente(String idCliente) throws NoEsUnNumeroException{
        try {
            return this.sessionFactory.getCurrentSession()
                .createQuery("from OtraFactura o where o.idCliente = :id", OtraFactura.class)
                .setParameter("id", Long.parseLong(idCliente))
                .getResultList();
        } catch (NumberFormatException e) {
            System.out.println("(PeajesDAOImp) no se inserto un numero");
            throw new NoEsUnNumeroException();
        }
    }

    @Override
    public List<OtraFactura> buscarByRemesa(String remesa) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from OtraFactura o where o.idRem = :remesa", OtraFactura.class)
                .setParameter("remesa", remesa)
                .getResultList();
    }

    @Override
    public void actualizar(OtraFactura documento) {
        this.sessionFactory.getCurrentSession().update(documento);
    }

    @Override
    public int contarPaginacion(int rows) {
        Query query = this.sessionFactory.getCurrentSession().createNativeQuery("select count(*) from contenido_xml_otras_facturas where is_deleted = 0");
        Long a = Long.parseLong(query.uniqueResult().toString());
        Double b = Math.ceil((double)a / rows);
        return b.intValue();
    }

    @Override
    public int contarRegistros() {
        Query query = this.sessionFactory.getCurrentSession().createNativeQuery("select count(*) from contenido_xml_otras_facturas where is_deleted = 0");
        Long a = Long.parseLong(query.uniqueResult().toString());
        return a.intValue();
    }
    
}
