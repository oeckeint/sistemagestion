package controladores;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import controladores.helper.Utilidades;
import datos.entity.BusquedaCliente;
import datos.entity.cliente.tickets.BusquedaTicket;
import datos.entity.cliente.tickets.ClienteTicket;
import datos.entity.cliente.tickets.TicketEstadoIncidencia;
import datos.entity.cliente.tickets.TicketTipoIncidencia;
import datos.interfaces.ClienteService;
import datos.interfaces.CrudDao;
import datos.service.cliente.tickets.TicketEstadoServiceImp;
import datos.entity.Cliente;

@Controller
@RequestMapping("/clientes/tickets")
public class ClientesTickets extends Operaciones{

	private Logger logger = Logger.getLogger(getClass().getName());
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	@Qualifier(value = "clienteTicketsServiceImp")
	private CrudDao<ClienteTicket> ticketsService;
	
	@Autowired
	@Qualifier(value = "ticketEstadoServiceImp")
	private CrudDao<TicketEstadoIncidencia> ticketEstadosService;
	
	@Autowired
	@Qualifier(value = "ticketTipoServiceImp")
	private CrudDao<TicketTipoIncidencia> ticketTiposService;
	
	@GetMapping("")
	@Override
	ModelAndView listar(@RequestParam(required = false, defaultValue = "1", name = "page") Integer paginaActual,
			@RequestParam(required = false, defaultValue = "50", name = "rows") Integer rows) {
		super.mv = new ModelAndView("cliente/tickets");
		super.mv.addObject("busquedaTicket", new BusquedaTicket());
		super.mv.addObject("titulo", "Tickets");
		super.mv.addObject("controller", "clientes/tickets");
		//Paginacion
		paginaActual = Utilidades.revisarPaginaActual(paginaActual);
		int ultimaPagina = this.ticketsService.contarPaginacion(rows);
		rows = Utilidades.revisarRangoRows(rows, 25);
		List<ClienteTicket> tickets = this.ticketsService.listar(rows, paginaActual - 1);
		int registrosMostrados = rows * paginaActual;
		super.mv.addObject("tickets", tickets);
		super.mv.addObject("totalRegistros", this.ticketsService.contarRegistros());
		super.mv.addObject("paginaActual", paginaActual);
		super.mv.addObject("ultimaPagina", ultimaPagina);
		super.mv.addObject("rows", rows);
		super.mv.addObject("registrosMostrados", registrosMostrados);
		return super.mv;
	}

	@GetMapping("/detalles")
	@Override
	ModelAndView detalle(@RequestParam("valor") String valor, @RequestParam("filtro") String filtro) {
		ClienteTicket ticket = null;
		super.mv = new ModelAndView("cliente/ticket_detalle");
		BusquedaTicket busquedaTicket = new BusquedaTicket();
		busquedaTicket.setValor(valor);
		busquedaTicket.setFiltro(filtro);
		switch (filtro) {
			case "Ticket":
				ticket = this.ticketsService.buscarId(Integer.parseInt(valor));
				break;
			default:
				break;
		}
		if (ticket == null) {
			this.listar(1, 50);
			super.mv.addObject("error", "sinregistro");
		}
		super.mv.addObject("ticket", ticket);
		super.mv.addObject("busquedaTicket", busquedaTicket);
		return super.mv;
	}
	
	@PostMapping("/busqueda")
	ModelAndView busqueda(@ModelAttribute("busquedaTicket") @Valid final BusquedaTicket busquedaTicket, final BindingResult binding,
			RedirectAttributes redirectAttributes) {
		if (!binding.hasErrors()) {
			//super.mv = new ModelAndView("cliente/ticket_detalle");
			//super.mv.addObject("busquedaTicket", busquedaTicket);
			return this.detalle(busquedaTicket.getValor(), busquedaTicket.getFiltro());
		}
		super.mv.addObject("busquedaTicket", busquedaTicket);
		return super.mv;
	}

	@Override
	ModelAndView listarFiltro() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@GetMapping("/agregar")	
	ModelAndView agregar() {
		super.mv = new ModelAndView("/cliente/ticket_formulario");
		super.mv.addObject("clienteTicket", new ClienteTicket());
		return super.mv;
	}
	
	@PostMapping("/agregar")
	ModelAndView agregarForm(@ModelAttribute("clienteTicket") ClienteTicket clienteTicket, 
			@RequestParam(required = false, name = "idClienteActual") String idClienteActualRef) {
		ClienteTicket ticket = clienteTicket;
		if (!idClienteActualRef.equals("") && Long.parseLong(idClienteActualRef) != ticket.getCliente().getIdCliente()) {
			ticket.setCliente(this.clienteService.encontrarId(clienteTicket.getCliente().getIdCliente()));
			super.mv.addObject("clienteTicket", ticket);
			super.mv.addObject("idClienteActual", ticket.getCliente().getIdCliente());
			return super.mv;
		}
		
		//ticket.setCliente(this.clienteService.encontrarId(Long.parseLong(idClienteActualRef)));
		ticket.setCliente(this.clienteService.encontrarId(ticket.getCliente().getIdCliente()));
		
		if (ticket.getCliente() == null) ticket.setCliente(new Cliente());
		
		if (ticket.getTicketEstadoIncidencia() !=null && ticket.getTicketTipoIncidencia() !=  null 
				&& !ticket.getComentarios().equals("") && !ticket.getDetalles().equals("")) {
			TicketEstadoIncidencia te = this.ticketEstadosService.buscarId(clienteTicket.getTicketEstadoIncidencia().getId());
			TicketTipoIncidencia tt = this.ticketTiposService.buscarId(clienteTicket.getTicketTipoIncidencia().getId());
			clienteTicket.setTicketEstadoIncidencia(te);
			clienteTicket.setTicketTipoIncidencia(tt);
			this.ticketsService.guardar(clienteTicket);
			this.listar(1, 50);
		} else {
			super.mv = new ModelAndView("/cliente/ticket_formulario");
			super.mv.addObject("clienteEncontrado", ticket.getCliente().getIdCliente() != 0);
			super.mv.addObject("clienteTicket", ticket);
			super.mv.addObject("idClienteActual", ticket.getCliente().getIdCliente());
			super.mv.addObject("estadosIncidencia", this.ticketEstadosService.listar());
			super.mv.addObject("tiposIncidencia", this.ticketTiposService.listar());
		}
		
		return super.mv;
	}
	
	@GetMapping("/editar")
	ModelAndView editar(@RequestParam("id") String id) {
		ClienteTicket ticket = this.ticketsService.buscarId(Long.parseLong(id));
		super.mv = new ModelAndView("/cliente/ticket_formulario");
		super.mv.addObject("clienteEncontrado", true);
		super.mv.addObject("editar", true);
		super.mv.addObject("clienteTicket", ticket);
		super.mv.addObject("idClienteActual", ticket.getCliente().getIdCliente());
		super.mv.addObject("estadosIncidencia", this.ticketEstadosService.listar());
		super.mv.addObject("tiposIncidencia", this.ticketTiposService.listar());
		this.ticketsService.guardar(ticket);
		return super.mv;
	}

	@Override
	ModelAndView listar() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

