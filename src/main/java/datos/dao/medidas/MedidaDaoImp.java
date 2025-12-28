package datos.dao.medidas;

import utileria.spring.AppFeatureProperties;
import datos.entity.medidas.Medida;
import datos.interfaces.CrudDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MedidaDaoImp implements CrudDao<Medida> {

    @Autowired
    SessionFactory sessionFactory;
    private final AppFeatureProperties appFeatureProperties;

    @Override
    public List<Medida> listar() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Medida m order by m.idMedida desc", Medida.class)
                .getResultList();
    }

    @Override
    public List<Medida> listar(int rows, int page) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Medida m order by m.idMedida desc", Medida.class)
                .setFirstResult(rows * page)
                .setMaxResults(rows)
                .getResultList();
    }

    @Override
    public Medida buscarId(long id) {
        return null;
    }

    @Override
    public List<Medida> buscarFiltro(String valor, String filtro) {
        return null;
    }

    @Override
    public void guardar(Medida medida) {
        if (appFeatureProperties.isEnabled("persist")) {
            this.sessionFactory.getCurrentSession().saveOrUpdate(medida);
        } else {
            log.error("Persistence disabled. Medida not saved: {}", medida);
        }
    }

    @Override
    public void actualizar(Medida object) {

    }

    @Override
    public void eliminar(long id) {

    }

    @Override
    public int contarRegistros() {
        return 0;
    }

    @Override
    public int contarPaginacion(int rows) {
        return 0;
    }
}
