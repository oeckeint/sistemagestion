package datos.repositories.medidas;

import datos.entity.medidas.MedidaH;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class MedidaHRepository {

    private final SessionFactory sessionFactory;

    public MedidaHRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void guardar(MedidaH medida) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(medida);
    }

    public boolean existeOrigen(String origen) {
        return sessionFactory.getCurrentSession().createQuery("SELECT COUNT(*) FROM MedidaH WHERE origen = :origen", Long.class)
                .setParameter("origen", origen)
                .getSingleResult() > 0;
    }

}
