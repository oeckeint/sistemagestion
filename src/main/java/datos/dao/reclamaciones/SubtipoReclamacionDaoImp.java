package datos.dao.reclamaciones;

import datos.entity.reclamaciones.SubtipoReclamacion;
import datos.interfaces.CrudDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubtipoReclamacionDaoImp implements CrudDao<SubtipoReclamacion> {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<SubtipoReclamacion> listar() {
        return sessionFactory.getCurrentSession()
                .createQuery("from SubtipoReclamacion sr order by sr.id desc", SubtipoReclamacion.class)
                .getResultList();
    }

    @Override
    public List<SubtipoReclamacion> listar(int rows, int page) {
        return sessionFactory.getCurrentSession()
                .createQuery("from SubtipoReclamacion sr order by sr.id desc", SubtipoReclamacion.class)
                .setFirstResult(rows * page)
                .setMaxResults(rows)
                .getResultList();
    }

    @Override
    public SubtipoReclamacion buscarId(long id) {
        return null;
    }

    @Override
    public List<SubtipoReclamacion> buscarFiltro(String valor, String filtro) {
        return null;
    }

    @Override
    public void guardar(SubtipoReclamacion subtipoReclamacion) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(subtipoReclamacion);
    }

    @Override
    public void actualizar(SubtipoReclamacion subtipoReclamacion) {

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
