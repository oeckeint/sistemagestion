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
@Table(name = "tickets_tipo_incidencia")
public class TicketTipoIncidencia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_incidencia")
	private long id;
	
	@Column(name = "detalles_incidencia")
	private String detalles;
	
	@Column(name = "is_deleted")
	private int isDeleted;
	
	@OneToMany(mappedBy = "ticketTipoIncidencia", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ClienteTicket> clienteTickets;

	public TicketTipoIncidencia() {
	}
	
	public TicketTipoIncidencia(String id) {
		this.id = Long.parseLong(id);
	} 
	
	public long getId() {
		return this.id;
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
		return "TicketTipoIncidencia [Id=" + id + ", detalles=" + detalles + ", isDeleted=" + isDeleted
				+ ", clienteTickets=" + clienteTickets + "]";
	}
	
}
