package datos.service.cliente.tickets;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import datos.entity.Cliente;
import datos.entity.cliente.tickets.TicketTipoIncidencia;
import datos.interfaces.CrudDao;

@Service
public class TicketTipoServiceImp implements CrudDao<TicketTipoIncidencia>{

	@Autowired
    @Qualifier(value = "ticketTipoDaoImp")
    private CrudDao<TicketTipoIncidencia> ticketsDao;

	@Override
	@Transactional
	public List<TicketTipoIncidencia> listar() {
		return this.ticketsDao.listar();
	}
	
	@Override
	@Transactional
	public List<TicketTipoIncidencia> listar(int rows, int page) {
		return this.ticketsDao.listar(rows, page);
	}

	@Override
	@Transactional
	public TicketTipoIncidencia buscarId(long id) {
		return this.ticketsDao.buscarId(id);
	}

	@Override
	@Transactional
	public void guardar(TicketTipoIncidencia object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public void actualizar(TicketTipoIncidencia object) {
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
