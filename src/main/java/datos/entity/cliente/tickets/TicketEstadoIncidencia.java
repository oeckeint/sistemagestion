package datos.entity.cliente.tickets;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tickets_estado_incidencia")
public class TicketEstadoIncidencia {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_estado_ticket")
	private long id;
	
	@Column(name = "detalles_estado_incidencia")
	private String detalles;
	
	@Column(name = "is_deleted")
	private int isDeleted;
	
	@OneToMany(mappedBy = "ticketEstadoIncidencia", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ClienteTicket> clienteTickets;

	public TicketEstadoIncidencia() {
	}
	
	public TicketEstadoIncidencia(String id) {
		this.id = Long.parseLong(id);
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDetalles() {
		return detalles;
	}

	public void setDetalles(String detalles) {
		this.detalles = detalles;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	public List<ClienteTicket> getClienteTickets() {
		return clienteTickets;
	}

	public void setClienteTickets(List<ClienteTicket> clienteTickets) {
		this.clienteTickets = clienteTickets;
	}

	@Override
	public String toString() {
		return "TicketEstadoIncidencia [id=" + id + ", detalles=" + detalles + ", isDeleted=" + isDeleted
				+ ", clienteTickets=" + clienteTickets + "]";
	}

}
