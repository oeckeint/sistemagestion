package datos.dao.cliente.tickets;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import datos.entity.cliente.tickets.TicketTipoIncidencia;
import datos.interfaces.CrudDao;

@Repository
public class TicketTipoDaoImp implements CrudDao<TicketTipoIncidencia>{
	
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public List<TicketTipoIncidencia> listar() {
		return this.sessionFactory.getCurrentSession()
				.createQuery("from TicketTipoIncidencia t order by t.id", TicketTipoIncidencia.class)
				.getResultList();
	}
	
	@Override
	public List<TicketTipoIncidencia> listar(int rows, int page) {
		return null;
	}

	@Override
	public TicketTipoIncidencia buscarId(long id) {
		return this.sessionFactory.getCurrentSession()
				.createQuery("from TicketTipoIncidencia t where t.id = :id", TicketTipoIncidencia.class)
				.setParameter("id", id)
				.getSingleResult();
	}

	@Override
	public List<TicketTipoIncidencia> buscarFiltro(String valor, String filtro) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void guardar(TicketTipoIncidencia object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actualizar(TicketTipoIncidencia object) {
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
