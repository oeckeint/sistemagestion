package datos.service.cliente.tickets;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import datos.entity.cliente.tickets.TicketEstadoIncidencia;
import datos.interfaces.CrudDao;

@Service
public class TicketEstadoServiceImp implements CrudDao<TicketEstadoIncidencia>{

	@Autowired
    @Qualifier(value = "ticketEstadoDaoImp")
    private CrudDao<TicketEstadoIncidencia> ticketsDao;
	
	@Override
	@Transactional
	public List<TicketEstadoIncidencia> listar() {
		return this.ticketsDao.listar();
	}
	
	@Override
	@Transactional
	public List<TicketEstadoIncidencia> listar(int rows, int page) {
		return null;
	}

	@Override
	@Transactional
	public TicketEstadoIncidencia buscarId(long id) {
		return this.ticketsDao.buscarId(id);
	}

	@Override
	@Transactional
	public void guardar(TicketEstadoIncidencia object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public void actualizar(TicketEstadoIncidencia object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public void eliminar(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public int contarRegistros() {
		return 0;
	}

	@Override
	@Transactional
	public int contarPaginacion(int rows) {
		return 0;
	}

}
