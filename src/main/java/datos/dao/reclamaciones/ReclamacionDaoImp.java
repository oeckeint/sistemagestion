package datos.dao.reclamaciones;

import datos.entity.reclamaciones.Reclamacion;
import datos.interfaces.CrudDao;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class ReclamacionDaoImp implements CrudDao<Reclamacion> {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Reclamacion> listar() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Reclamacion r order by r.id desc", Reclamacion.class)
                .getResultList();
    }

    @Override
    public List<Reclamacion> listar(int rows, int page) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Reclamacion r where r.isDeleted = 0 order by r.id desc", Reclamacion.class)
                .setFirstResult(rows * page)
                .setMaxResults(rows)
                .getResultList();
    }

    @Override
    public Reclamacion buscarId(long id) {
        try{
            return sessionFactory.getCurrentSession().createQuery("from Reclamacion r where r.idReclamacion = :id", Reclamacion.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public List<Reclamacion> buscarFiltro(String valor, String filtro) {
        Query<Reclamacion> q = null;
        try{
            switch (filtro){
                case "cliente":
                    q =	this.sessionFactory.getCurrentSession()
                            .createQuery("from Reclamacion r where r.cliente.idCliente = :valor order by r.id desc", Reclamacion.class)
                            .setParameter("valor", Long.parseLong(valor));
                    break;
                case "codigoSolicitud":
                    q =	this.sessionFactory.getCurrentSession()
                            .createQuery("from Reclamacion r where r.codigoDeSolicitud = :valor order by r.id desc", Reclamacion.class)
                            .setParameter("valor", Long.parseLong(valor));
                    break;
            }
        } catch (NoResultException e){
            e.printStackTrace(System.out);
            return null;
        }
        return q.getResultList().size() == 0 ? null : q.getResultList();
    }

    @Override
    public void guardar(Reclamacion reclamacion) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(reclamacion);
    }

    @Override
    public void actualizar(Reclamacion reclamacion) {

    }

    @Override
    public void eliminar(long id) {

    }

    @Override
    public int contarRegistros() {
        Query query = this.sessionFactory.getCurrentSession()
                .createNativeQuery("select  count(*) from reclamaciones r where is_deleted = 0");
        Long a = Long.parseLong(query.uniqueResult().toString());
        return a.intValue();
    }

    @Override
    public int contarPaginacion(int rows) {
        Query query = this.sessionFactory.getCurrentSession()
                .createNativeQuery("select count(*) from reclamaciones r where is_deleted = 0");
        Long a = Long.parseLong(query.uniqueResult().toString());
        Double b = Math.ceil((double) a / rows);
        return b.intValue();
    }
}
