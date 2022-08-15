package datos.dao.cliente.tickets;

import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import datos.entity.cliente.tickets.ClienteTicket;
import datos.interfaces.CrudDao;

@Repository
public class ClienteTicketDaoImp implements CrudDao<ClienteTicket>{
	
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public List<ClienteTicket> listar() {
		return sessionFactory.getCurrentSession()
				.createQuery("from ClienteTicket t order by t.idTicket desc", ClienteTicket.class)
				.getResultList();
	}
	
	@Override
	public List<ClienteTicket> listar(int rows, int page) {
		return sessionFactory.getCurrentSession()
				.createQuery("from ClienteTicket t where t.isDeleted = 0 order by t.idTicket desc", ClienteTicket.class)
				.setFirstResult(rows * page)
				.setMaxResults(rows)
				.getResultList();
	}

	@Override
	public void guardar(ClienteTicket clienteTicket) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(clienteTicket);
		
	}

	@Override
	public void actualizar(ClienteTicket object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eliminar(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ClienteTicket buscarId(long id) {
		try {
        	return sessionFactory.getCurrentSession().createQuery("from ClienteTicket ct where ct.idTicket = :id", ClienteTicket.class)
        			.setParameter("id", id).getSingleResult(); 
        } catch (NoResultException e) {
            return null;
        }
	}

	@Override
	public int contarRegistros() {
		Query query = this.sessionFactory.getCurrentSession()
				.createNativeQuery("select count(*) from cliente_tickets ct where is_deleted = 0");
		Long a = Long.parseLong(query.uniqueResult().toString());
        return a.intValue();
	}

	@Override
	public int contarPaginacion(int rows) {
		Query query = this.sessionFactory.getCurrentSession()
				.createNativeQuery("select count(*) from cliente_tickets ct where is_deleted = 0");
		Long a = Long.parseLong(query.uniqueResult().toString());
        Double b = Math.ceil((double) a / rows);
        return b.intValue();
	}

}
