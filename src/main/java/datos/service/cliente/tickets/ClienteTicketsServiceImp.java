package datos.service.cliente.tickets;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import controladores.helper.Utilidades;
import datos.entity.cliente.tickets.ClienteTicket;
import datos.interfaces.CrudDao;

@Service
public class ClienteTicketsServiceImp implements CrudDao<ClienteTicket>{

	@Autowired
	@Qualifier(value = "clienteTicketDaoImp")
	private CrudDao<ClienteTicket> clienteTicketDao;
	
	@Override
	@Transactional
	public List<ClienteTicket> listar() {
		return clienteTicketDao.listar();
	}
	
	@Override
	@Transactional
	public List<ClienteTicket> listar(int rows, int page) {
		return this.clienteTicketDao.listar(rows, page);
	}

	@Override
	@Transactional
	public void guardar(ClienteTicket clienteTicket) {
		if (clienteTicket.getCreatedBy() == null || clienteTicket.getCreatedBy().length() == 0) {
			clienteTicket.setCreatedOn(Calendar.getInstance());
			clienteTicket.setCreatedBy(Utilidades.currentUser());
		} else {
			clienteTicket.setUpdatedOn(Calendar.getInstance());
			clienteTicket.setUpdatedBy(Utilidades.currentUser());
		}
		this.clienteTicketDao.guardar(clienteTicket);
	}

	@Override
	@Transactional
	public void actualizar(ClienteTicket object) {
		object.setUpdatedOn(Calendar.getInstance());
		object.setUpdatedBy(Utilidades.currentUser());
		this.clienteTicketDao.guardar(object);
	}

	@Override
	@Transactional
	public void eliminar(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public ClienteTicket buscarId(long id) {
		return this.clienteTicketDao.buscarId(id);
	}
	
	@Override
	@Transactional
	public List<ClienteTicket> buscarFiltro(String valor, String filtro) {
		return this.clienteTicketDao.buscarFiltro(valor, filtro);
	}

	@Override
	@Transactional
	public int contarRegistros() {
		return this.clienteTicketDao.contarRegistros();
	}

	@Override
	@Transactional
	public int contarPaginacion(int rows) {
		return this.clienteTicketDao.contarPaginacion(rows);
	}

}
