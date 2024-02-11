package datos.dao.medidas;

import datos.entity.medidas.MedidaH;
import datos.interfaces.CrudDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MedidaHDaoImp implements CrudDao<MedidaH> {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<MedidaH> listar() {
        return sessionFactory.getCurrentSession()
                .createQuery("from MedidaH m order by m.id desc", MedidaH.class)
                .getResultList();
    }

    @Override
    public List<MedidaH> listar(int rows, int page) {
        return sessionFactory.getCurrentSession()
                .createQuery("from MedidaH m order by m.id desc", MedidaH.class)
                .setFirstResult(rows * page)
                .setMaxResults(rows)
                .getResultList();
    }

    @Override
    public MedidaH buscarId(long id) {
        return null;
    }

    @Override
    public List<MedidaH> buscarFiltro(String valor, String filtro) {
        return null;
    }

    @Override
    public void guardar(MedidaH medida) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(medida);
    }

    @Override
    public void actualizar(MedidaH medida) {

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
