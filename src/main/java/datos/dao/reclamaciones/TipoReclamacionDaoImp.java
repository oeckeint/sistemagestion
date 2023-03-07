package datos.dao.reclamaciones;

import datos.entity.reclamaciones.TipoReclamacion;
import datos.interfaces.CrudDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TipoReclamacionDaoImp implements CrudDao<TipoReclamacion> {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<TipoReclamacion> listar() {
        return sessionFactory.getCurrentSession()
                .createQuery("from TipoReclamacion r order by r.id desc", TipoReclamacion.class)
                .getResultList();
    }

    @Override
    public List<TipoReclamacion> listar(int rows, int page) {
        return sessionFactory.getCurrentSession()
                .createQuery("from TipoReclamacion r order by r.id desc", TipoReclamacion.class)
                .setFirstResult(rows * page)
                .setMaxResults(rows)
                .getResultList();
    }

    @Override
    public TipoReclamacion buscarId(long id) {
        return null;
    }

    @Override
    public List<TipoReclamacion> buscarFiltro(String valor, String filtro) {
        return null;
    }

    @Override
    public void guardar(TipoReclamacion tipoReclamacion) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(tipoReclamacion);
    }

    @Override
    public void actualizar(TipoReclamacion tipoReclamacion) {

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
