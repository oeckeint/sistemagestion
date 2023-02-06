package datos.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import datos.entity.cliente.tickets.ClienteTicket;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "cliente")
public class Cliente implements Serializable {

    /**
	 * 
	 */
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

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public String getCups() {
        return cups;
    }

    public void setCups(String cups) {
        this.cups = cups;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTarifa() {
        return tarifa;
    }

    public void setTarifa(String tarifa) {
        this.tarifa = tarifa;
    }

    public short getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(short isDeleted) {
        this.isDeleted = isDeleted;
    }

    public ClientePuntoSuministro getClientePuntoSuministro() {
        return clientePuntoSuministro;
    }

    public void setClientePuntoSuministro(ClientePuntoSuministro clientePuntoSuministro) {
        this.clientePuntoSuministro = clientePuntoSuministro;
    }

    public ClienteDatosGenerales getClienteDatosGenerales() {
        return clienteDatosGenerales;
    }

    public void setClienteDatosGenerales(ClienteDatosGenerales clienteDatosGenerales) {
        this.clienteDatosGenerales = clienteDatosGenerales;
    }

    public ClienteContrato getClienteContrato() {
        return clienteContrato;
    }

    public void setClienteContrato(ClienteContrato clienteContrato) {
        this.clienteContrato = clienteContrato;
    }
    
    public List<ClienteTicket> getClienteTickets() {
		return clienteTickets;
	}

	public void setClienteTickets(List<ClienteTicket> clienteTickets) {
		this.clienteTickets = clienteTickets;
	}

    public List<Medida> getMedidas() {
        return medidas;
    }

    public void setMedidas(List<Medida> medidas) {
        this.medidas = medidas;
    }

    @Override
	public String toString() {
		return "Cliente [idCliente=" + idCliente + ", cups=" + cups + ", nombreCliente=" + nombreCliente + ", tarifa="
				+ tarifa + ", isDeleted=" + isDeleted + ", clientePuntoSuministro=" + clientePuntoSuministro
				+ ", clienteDatosGenerales=" + clienteDatosGenerales + ", clienteContrato=" + clienteContrato
				+ ", clienteTickets=" + clienteTickets + "]";
	}

}
