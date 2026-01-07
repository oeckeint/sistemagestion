package datos.entity.cliente.tickets;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

@Getter @Setter @ToString
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
	
}
