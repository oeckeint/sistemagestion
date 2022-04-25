package datos.dao;

import datos.entity.Peaje;
import excepciones.NoEsUnNumeroException;
import excepciones.PeajeMasDeUnRegistroException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PeajesImp implements datos.interfaces.DocumentoXmlDao<Peaje> {
	
	private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Peaje> listar() {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Peaje p where p.isDeleted = 0 "
                        + "order by "
                        + " case p.eaFecHas2"
                        + "     when '' then"
                        + "         p.eaFecHas1"
                        + "     else"
                        + "         p.eaFecHas2"
                        + "     end "
                        + "desc", Peaje.class)
                .getResultList();
    }

    @Override
    public List<Peaje> listar(int rows, int page) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Peaje p "
                        + "where (p.isDeleted = 0) "
                        + "order by "
                        + "     case p.eaFecHas2 "
                        + "         when '' then "
                        + "             p.eaFecHas1 "
                        + "         else "
                        + "             p.eaFecHas2 "
                        + "     end "
                        + "desc", Peaje.class)
                .setFirstResult(rows * page)
                .setMaxResults(rows)
                .getResultList();
    }

    @Override
    public void guardar(Peaje documento) {
        this.sessionFactory.getCurrentSession().save(documento);
    }

    @Override
    public void actualizar(Peaje documento) {
        this.sessionFactory.getCurrentSession().update(documento);
    }

    @Override
    public void eliminar(long id) {
        this.sessionFactory.getCurrentSession()
                .createQuery("delete from Peaje p where p.idPeaje = :id", Peaje.class)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public Peaje buscarByCodFiscal(String cod) throws PeajeMasDeUnRegistroException {
        try {
            Peaje peaje = this.sessionFactory.getCurrentSession().createQuery("from Peaje p where p.codFisFac = :cod1", Peaje.class).setParameter("cod1", cod).getSingleResult();
            if (peaje == null) {
				peaje =	this.sessionFactory.getCurrentSession().createQuery("from Peaje p where p.codFisFac = :cod1", Peaje.class).setParameter("cod1", cod + "-A").getSingleResult();
			}
            return peaje;
        } catch (NonUniqueResultException e) {
        	logger.log(Level.INFO, ">>> PeajesDaoImp={0}", e.getMessage());
        	throw new PeajeMasDeUnRegistroException(cod);
        } catch (NoResultException e) {
        	return null;
        } catch (Exception e) {
        	e.printStackTrace(System.out);
            return null;
        }
    }

    @Override
    public List<Peaje> buscarByIdCliente(String idCliente) throws NoEsUnNumeroException {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createQuery("from Peaje p where p.idCliente = :id "
                            + "order by "
                            + "case p.eaFecHas2"
                            + " when '' then"
                            + "     p.eaFecHas1"
                            + " else"
                            + "     p.eaFecHas2"
                            + " end "
                            + "desc", Peaje.class)
                    .setParameter("id", Long.parseLong(idCliente))
                    .getResultList();
        } catch (NumberFormatException e) {
            System.out.println("(PeajesDAOImp) no se inserto un numero");
            throw new NoEsUnNumeroException();
        }
    }

    @Override
    public List<Peaje> buscarByRemesa(String remesa) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Peaje p where p.rfIdRem = :remesa "
                        + "order by "
                        + "case p.eaFecHas2"
                        + " when '' then"
                        + "     p.eaFecHas1"
                        + " else"
                        + "     p.eaFecHas2"
                        + " end "
                        + "desc", Peaje.class)
                .setParameter("remesa", remesa)
                .getResultList();
    }

    @Override
    public int contarPaginacion(int rows) {
        Query query = this.sessionFactory.getCurrentSession().createNativeQuery("select count(*) from contenido_xml where is_deleted = 0");
        Long a = Long.parseLong(query.uniqueResult().toString());
        Double b = Math.ceil((double) a / rows);
        return b.intValue();
    }

    @Override
    public int contarRegistros() {
        Query query = this.sessionFactory.getCurrentSession().createNativeQuery("select count(*) from contenido_xml where is_deleted = 0");
        Long a = Long.parseLong(query.uniqueResult().toString());
        return a.intValue();
    }

}
