package datos.dao;

import datos.entity.Cliente;
import excepciones.MasDeUnClienteEncontrado;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteDaoImp implements datos.interfaces.ClienteDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Cliente> listar() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Cliente c where c.isDeleted = 0 order by c.idCliente desc", Cliente.class)
                .getResultList();
    }

    @Override
    public List<Cliente> listar(int rows, int page) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Cliente c where c.isDeleted = 0 order by c.idCliente desc", Cliente.class)
                .setFirstResult(rows * page)
                .setMaxResults(rows)
                .getResultList();
    }

    @Override
    public Cliente encontrarId(long id) {
        return sessionFactory.getCurrentSession().get(Cliente.class, id);
    }

    @Override
    public Cliente encontrarCups(String cups) throws MasDeUnClienteEncontrado {
        try {
            return (Cliente) sessionFactory.getCurrentSession().createQuery("from Cliente c where c.cups like :cups").setParameter("cups", "%" + cups.substring(0, 20) + "%").getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new MasDeUnClienteEncontrado(cups);
        }
    }
    
    @Override
    public List<Cliente> encontrarByNombre(String nombreCliente) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Cliente c where c.isDeleted = 0 and c.nombreCliente LIKE :nombreCliente order by c.idCliente desc", Cliente.class)
                .setParameter("nombreCliente", "%" + nombreCliente + "%")
                .getResultList();
    }

    @Override
    public void guardar(Cliente cliente) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(cliente);
    }

    @Override
    public void eliminar(long id) {
        this.sessionFactory.getCurrentSession()
                .createQuery("delete from Cliente c where c.idCliente = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public int contarPaginacion(int rows) {
        Query query = this.sessionFactory.getCurrentSession().createNativeQuery("select count(*) from cliente where is_deleted = 0");
        Long a = Long.parseLong(query.uniqueResult().toString());
        Double b = Math.ceil((double) a / rows);
        return b.intValue();
    }

    @Override
    public int contarRegistros() {
        Query query = this.sessionFactory.getCurrentSession().createNativeQuery("select count(*) from cliente where is_deleted = 0");
        Long a = Long.parseLong(query.uniqueResult().toString());
        return a.intValue();
    }

}
