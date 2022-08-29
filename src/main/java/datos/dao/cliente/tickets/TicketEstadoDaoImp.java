package datos.dao.cliente.tickets;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import datos.entity.cliente.tickets.ClienteTicket;
import datos.entity.cliente.tickets.TicketEstadoIncidencia;
import datos.interfaces.CrudDao;

@Repository
public class TicketEstadoDaoImp implements CrudDao<TicketEstadoIncidencia>{
	
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public List<TicketEstadoIncidencia> listar() {
		return sessionFactory.getCurrentSession()
				.createQuery("from TicketEstadoIncidencia t order by t.id", TicketEstadoIncidencia.class)
				.getResultList();
	}
	
	@Override
	public List<TicketEstadoIncidencia> listar(int rows, int page) {
		return null;
	}

	@Override
	public TicketEstadoIncidencia buscarId(long id) {
		return sessionFactory.getCurrentSession()
				.createQuery("from TicketEstadoIncidencia t where t.id = :id", TicketEstadoIncidencia.class)
				.setParameter("id", id)
				.getSingleResult();
	}
	
	@Override
	public List<TicketEstadoIncidencia> buscarFiltro(String valor, String filtro) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void guardar(TicketEstadoIncidencia object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actualizar(TicketEstadoIncidencia object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eliminar(long id) {
		// TODO Auto-generated method stub
		
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
