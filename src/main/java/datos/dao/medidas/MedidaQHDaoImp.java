package datos.dao.medidas;

import com.jcraft.jsch.Session;
import datos.entity.medidas.MedidaQH;
import datos.interfaces.CrudDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class MedidaQHDaoImp implements CrudDao<MedidaQH> {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<MedidaQH> listar() {
        return sessionFactory.getCurrentSession()
                .createQuery("from MedidaQH m order by m.idMedidaQH desc", MedidaQH.class)
                .getResultList();
    }

    @Override
    public List<MedidaQH> listar(int rows, int page) {
        return sessionFactory.getCurrentSession()
                .createQuery("from MedidaQH m order by m.idMedidaQH desc", MedidaQH.class)
                .setFirstResult(rows * page)
                .setMaxResults(rows)
                .getResultList();
    }

    @Override
    public MedidaQH buscarId(long id) {
        return null;
    }

    @Override
    public List<MedidaQH> buscarFiltro(String valor, String filtro) {
        return null;
    }

    @Override
    public void guardar(MedidaQH medida) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(medida);
    }

    @Override
    public void actualizar(MedidaQH object) {

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
