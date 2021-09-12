package datos.dao;

import datos.entity.Factura;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FacturaDaoImp implements datos.interfaces.DocumentoXmlDao<Factura> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Factura> listar() {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Factura f where f.isDeleted = 0 order by f.idFactura desc", Factura.class)
                .getResultList();
    }

    @Override
    public void guardar(Factura factura) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(factura);
    }

    @Override
    public void eliminar(long id) {
        this.sessionFactory.getCurrentSession()
                .createQuery("delete from Factura f where f.idFactura = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public Factura buscarByCodFiscal(String cod) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Factura f where f.codFisFac = :cod1 or f.codFisFac = :cod2 order by f.idFactura desc", Factura.class)
                .setParameter("cod1", cod)
                .setParameter("cod2", cod + "-A")
                .getSingleResult();
    }

    @Override
    public List<Factura> buscarByIdCliente(String idCliente) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Factura f where f.idCliente = :id order by f.idFactura desc", Factura.class)
                .setParameter("id", Long.parseLong(idCliente))
                .getResultList();
    }

    @Override
    public List<Factura> buscarByRemesa(String remesa) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Factura f where f.rfIdRem = :remesa", Factura.class)
                .setParameter("remesa", remesa)
                .getResultList();
    }

}
