package controladores;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
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
public class ClientesTickets extends Operaciones {

    private Logger logger = Logger.getLogger(getClass().getName());

    private ClienteTicket ticketGuardado = null;

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
    public ModelAndView listar(@RequestParam(required = false, defaultValue = "1", name = "page") Integer paginaActual,
                               @RequestParam(required = false, defaultValue = "50", name = "rows") Integer rows) {
        super.mv = new ModelAndView("cliente/tickets");
        super.mv.addObject("busquedaTicket", new BusquedaTicket());
        super.mv.addObject("titulo", "Tickets");
        super.mv.addObject("controller", "clientes/tickets");
        if (this.ticketGuardado != null) {
            ClienteTicket aux = this.ticketGuardado;
            super.mv.addObject("ticketGuardado", aux);
            ticketGuardado = null;
        }
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
    public ModelAndView detalle(@RequestParam("valor") String valor, @RequestParam("filtro") String filtro) {
        ClienteTicket ticket = null;
        List<ClienteTicket> tickets = null;
        super.mv = new ModelAndView("cliente/ticket_detalle");
        BusquedaTicket busquedaTicket = new BusquedaTicket();
        busquedaTicket.setValor(valor);
        busquedaTicket.setFiltro(filtro);
        switch (filtro) {
            case "Ticket":
                ticket = this.ticketsService.buscarId(Integer.parseInt(valor));
                super.mv.addObject("ticket", ticket);
                super.mv.addObject("mensaje", "registroEncontrado");
                if (ticket == null) {
                    this.listar(1, 50);
                    super.mv.addObject("error", "sinregistro");
                }
                break;
            case "Tipo":
            case "Estado":
            case "Cliente":
            case "CUPS":
                tickets = this.ticketsService.buscarFiltro(valor, filtro);
                if (tickets.size() == 1) {
                    super.mv.addObject("ticket", tickets.get(0));
                } else {
                    this.createViewForTicketsFilter(tickets);
                }
                break;
        }
        super.mv.addObject("busquedaTicket", busquedaTicket);
        super.mv.addObject("titulo", "Tickets");
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
    public ModelAndView listarFiltro() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @GetMapping("/agregar")
    public ModelAndView agregar() {
        super.mv = new ModelAndView("/cliente/ticket_formulario");
        super.mv.addObject("titulo", "Tickets");
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

        if (ticket.getTicketEstadoIncidencia() != null && ticket.getTicketTipoIncidencia() != null
                && !ticket.getComentarios().equals("") && !ticket.getDetalles().equals("")) {
            TicketEstadoIncidencia te = this.ticketEstadosService.buscarId(clienteTicket.getTicketEstadoIncidencia().getId());
            TicketTipoIncidencia tt = this.ticketTiposService.buscarId(clienteTicket.getTicketTipoIncidencia().getId());
            clienteTicket.setTicketEstadoIncidencia(te);
            clienteTicket.setTicketTipoIncidencia(tt);
            this.ticketsService.guardar(clienteTicket);
            this.listar(1, 50);
            this.ticketGuardado = clienteTicket;
            super.mv = new ModelAndView("redirect:/clientes/tickets");
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
    ModelAndView editar(@RequestParam("id") String id) throws ParseException {
        ClienteTicket ticket = this.ticketsService.buscarId(Long.parseLong(id));
        //Date date = Utilidades.agregarDias(ticket.getCreatedOn(), 1);
        //ticket.setCreatedOn(date);
        super.mv = new ModelAndView("/cliente/ticket_formulario");
        super.mv.addObject("clienteEncontrado", true);
        super.mv.addObject("editar", true);
        super.mv.addObject("clienteTicket", ticket);
        super.mv.addObject("idClienteActual", ticket.getCliente().getIdCliente());
        super.mv.addObject("estadosIncidencia", this.ticketEstadosService.listar());
        super.mv.addObject("tiposIncidencia", this.ticketTiposService.listar());
        super.mv.addObject("titulo", "Tickets");
        //this.ticketsService.guardar(ticket);
        return super.mv;
    }

    @GetMapping("/archivar")
    ModelAndView archivar(@RequestParam(required = true, name = "id") final String id) {
        ClienteTicket ct = this.ticketsService.buscarId(Long.parseLong(id));
        if (ct == null) return new ModelAndView("redirect:/clientes/tickets");
        ct.setIsDeleted((ct.getIsDeleted() == 0) ? 1 : 0);
        this.ticketsService.actualizar(ct);
        return new ModelAndView("redirect:/clientes/tickets/detalles?valor=" + ct.getIdTicket() + "&filtro=Ticket");
    }

    @Override
    public ModelAndView listar() {
        // TODO Auto-generated method stub
        return null;
    }

    ModelAndView createViewForTicketsFilter(List<ClienteTicket> tickets) {
        if (tickets.size() == 0) {
            this.listar(1, 50);
            super.mv.addObject("error", "sinregistro");
            return super.mv;
        }
        super.mv = new ModelAndView("cliente/tickets");
        super.mv.addObject("tickets", tickets);
        super.mv.addObject("titulo", "Tickets");
        super.mv.addObject("controller", "clientes/tickets");
        super.mv.addObject("totalRegistros", tickets.size());
        super.mv.addObject("desactivarPaginacion", true);
        super.mv.addObject("mensaje", "registrosEncontrados");
        return super.mv;
    }

}

