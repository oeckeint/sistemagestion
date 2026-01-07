package datos.entity.cliente.tickets;

import datos.entity.Cliente;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Calendar;

@Getter @Setter
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
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
    @Column(name = "created_on")
    private Calendar createdOn;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "updated_on")
    private Calendar updatedOn;
    
    @Column(name = "updated_by")
    private String updatedBy;
	
}
