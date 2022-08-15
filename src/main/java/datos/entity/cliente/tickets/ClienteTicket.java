package datos.entity.cliente.tickets;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import datos.entity.Cliente;

@Entity
@Table(name = "cliente_tickets")
public class ClienteTicket {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cliente_ticket")
	private long idTicket;
	
	@Column(name = "detalles")
	private String detalles;
	
	@Column(name = "comentarios")
	private String comentarios;
	
	@ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_incidencia")
	private TicketTipoIncidencia ticketTipoIncidencia;
	
	@ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_estado_ticket")
	private TicketEstadoIncidencia ticketEstadoIncidencia;

	@ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@Column(name = "is_deleted")
	private int isDeleted;
	
	public ClienteTicket(){
	}
	
	public long getIdTicket() {
		return idTicket;
	}

	public void setIdTicket(long idTicket) {
		this.idTicket = idTicket;
	}

	public String getDetalles() {
		return detalles;
	}

	public void setDetalles(String detalles) {
		this.detalles = detalles;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	
	public TicketTipoIncidencia getTicketTipoIncidencia() {
		return ticketTipoIncidencia;
	}

	public void setTicketTipoIncidencia(TicketTipoIncidencia ticketTipoIncidencia) {
		this.ticketTipoIncidencia = ticketTipoIncidencia;
	}

	public TicketEstadoIncidencia getTicketEstadoIncidencia() {
		return ticketEstadoIncidencia;
	}

	public void setTicketEstadoIncidencia(TicketEstadoIncidencia ticketEstadoIncidencia) {
		this.ticketEstadoIncidencia = ticketEstadoIncidencia;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}
	
}
