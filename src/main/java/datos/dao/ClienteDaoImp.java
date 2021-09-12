package datos.dao;

import datos.entity.Cliente;
import java.util.List;
import org.hibernate.SessionFactory;
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
    public Cliente encontrarId(long id) {
        return sessionFactory.getCurrentSession().get(Cliente.class, id);
    }

    @Override
    public Cliente encontrarCups(String cups) {
        return (Cliente) sessionFactory.getCurrentSession().createQuery("from Cliente c where c.cups = :cups").setParameter("cups", cups).getSingleResult();
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
}
