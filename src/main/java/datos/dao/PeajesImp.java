package datos.dao;

import datos.entity.Peaje;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PeajesImp implements datos.interfaces.DocumentoXmlDao<Peaje> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Peaje> listar() {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Peaje p order by p.idPeaje desc", Peaje.class)
                .getResultList();
    }

    @Override
    public void guardar(Peaje documento) {
        this.sessionFactory.getCurrentSession().save(documento);
    }

    @Override
    public void eliminar(long id) {
        this.sessionFactory.getCurrentSession()
                .createQuery("delete from Peaje p where p.idPeaje = :id", Peaje.class)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public Peaje buscarByCodFiscal(String cod) {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createQuery("from Peaje p where p.codFisFac = :cod1 or p.codFisFac = :cod2 order by p.idPeaje desc", Peaje.class)
                    .setParameter("cod1", cod)
                    .setParameter("cod2", cod + "-A")
                    .getResultList().get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Peaje> buscarByIdCliente(String idCliente) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Peaje p where p.idCliente = :id", Peaje.class)
                .setParameter("id", Long.parseLong(idCliente))
                .getResultList();
    }

    @Override
    public List<Peaje> buscarByRemesa(String remesa) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Peaje p where p.rfIdRem = :remesa", Peaje.class)
                .setParameter("remesa", remesa)
                .getResultList();
    }

}
