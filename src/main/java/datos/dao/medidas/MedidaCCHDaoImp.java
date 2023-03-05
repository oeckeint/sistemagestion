package datos.dao.medidas;

import datos.entity.Medida;
import datos.entity.MedidaCCH;
import datos.interfaces.CrudDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MedidaCCHDaoImp implements CrudDao<MedidaCCH> {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<MedidaCCH> listar() {
        return sessionFactory.getCurrentSession()
                .createQuery("from MedidaCCH m order by m.idMedidaCCH desc", MedidaCCH.class)
                .getResultList();
    }

    @Override
    public List<MedidaCCH> listar(int rows, int page) {
        return sessionFactory.getCurrentSession()
                .createQuery("from MedidaCCH m order by m.idMedidaCCH desc", MedidaCCH.class)
                .setFirstResult(rows * page)
                .setMaxResults(rows)
                .getResultList();
    }

    @Override
    public MedidaCCH buscarId(long id) {
        return null;
    }

    @Override
    public List<MedidaCCH> buscarFiltro(String valor, String filtro) {
        return null;
    }

    @Override
    public void guardar(MedidaCCH medida) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(medida);
    }

    @Override
    public void actualizar(MedidaCCH object) {}

    @Override
    public void eliminar(long id) {}

    @Override
    public int contarRegistros() {
        return 0;
    }

    @Override
    public int contarPaginacion(int rows) {
        return 0;
    }
}
