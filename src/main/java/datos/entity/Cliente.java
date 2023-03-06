package datos.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import datos.entity.cliente.tickets.ClienteTicket;
import datos.entity.medidas.Medida;
import datos.entity.medidas.MedidaCCH;
import datos.entity.reclamaciones.Reclamacion;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Data
@Entity
@Table(name = "cliente")
public class Cliente implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private long idCliente;

    @Column(name = "cups")
    private String cups;

    @Column(name = "nombre_cliente")
    private String nombreCliente;

    @Column(name = "tarifa")
    private String tarifa;

    @Column(name = "is_deleted")
    private short isDeleted;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cliente_punto_suministro")
    private ClientePuntoSuministro clientePuntoSuministro;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cliente_datos")
    private ClienteDatosGenerales clienteDatosGenerales;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cliente_contrato")
    private ClienteContrato clienteContrato;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClienteTicket> clienteTickets;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Medida> medidas;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MedidaCCH> medidasCCH;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reclamacion> reclamaciones;

    public Cliente() {
    }

    public Cliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public Cliente(long idCliente, String cups, String nombreCliente, String tarifa, short isDeleted) {
        this.idCliente = idCliente;
        this.cups = cups;
        this.nombreCliente = nombreCliente;
        this.tarifa = tarifa;
        this.isDeleted = isDeleted;
    }

    public Cliente(String cups) {
        this.cups = cups;
    }

}
