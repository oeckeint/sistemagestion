package datos.dao.medidas;

import datos.entity.medidas.MedidaQH;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MedidaQHRepository {

    private final SessionFactory sessionFactory;

    public MedidaQHRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void guardar(MedidaQH medida) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(medida);
    }

    public List<MedidaQH> findAllByIdCliente(Long idCliente) {
        return sessionFactory.getCurrentSession().createQuery("FROM MedidaQH WHERE cliente.id = :idCliente", MedidaQH.class)
                .setParameter("idCliente", idCliente)
                .getResultList();
    }

    public boolean existeOrigen(String origen) {
        return sessionFactory.getCurrentSession().createQuery("SELECT COUNT(*) FROM MedidaQH WHERE origen = :origen", Long.class)
                .setParameter("origen", origen)
                .getSingleResult() > 0;
    }

}
