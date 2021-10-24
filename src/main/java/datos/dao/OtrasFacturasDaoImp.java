package datos.dao;

import datos.entity.OtraFactura;
import excepciones.NoEsUnNumeroException;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OtrasFacturasDaoImp implements datos.interfaces.DocumentoXmlDao<OtraFactura>{

    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public List<OtraFactura> listar() {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from OtraFactura o order by o.idOtraFactura desc", OtraFactura.class)
                .getResultList();
    }

    @Override
    public void guardar(OtraFactura documento) {
        this.sessionFactory.getCurrentSession().save(documento);
    }

    @Override
    public void eliminar(long id) {
        this.sessionFactory.getCurrentSession()
                .createQuery("delete from OtraFactura o where o.idOtraFactura = :id", OtraFactura.class)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public OtraFactura buscarByCodFiscal(String cod) {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createQuery("from OtraFactura o where o.codFisFac = :cod1 or o.codFisFac = :cod2 order by o.idOtraFactura desc", OtraFactura.class)
                    .setParameter("cod1", cod)
                    .setParameter("cod2", cod + "-A")
                    .getResultList().get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public List<OtraFactura> buscarByIdCliente(String idCliente) throws NoEsUnNumeroException{
        try {
            return this.sessionFactory.getCurrentSession()
                .createQuery("from OtraFactura o where o.idCliente = :id", OtraFactura.class)
                .setParameter("id", Long.parseLong(idCliente))
                .getResultList();
        } catch (NumberFormatException e) {
            System.out.println("(PeajesDAOImp) no se inserto un numero");
            throw new NoEsUnNumeroException();
        }
    }

    @Override
    public List<OtraFactura> buscarByRemesa(String remesa) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from OtraFactura o where o.idRem = :remesa", OtraFactura.class)
                .setParameter("remesa", remesa)
                .getResultList();
    }
    
}
