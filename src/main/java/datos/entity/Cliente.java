package datos.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import datos.entity.cliente.tickets.ClienteTicket;
import datos.entity.medidas.Medida;
import datos.entity.medidas.MedidaCCH;
import datos.entity.medidas.MedidaH;
import datos.entity.reclamaciones.Reclamacion;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente implements Serializable, GenericEntity<Cliente> {

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

    //@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    //private List<MedidaH> medidasH;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reclamacion> reclamaciones;

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

    @Override
    public Long getId() {
        return this.idCliente;
    }

    @Override
    public Cliente createNewInstance() {
        Cliente cliente = new Cliente();
        cliente.setCups(this.cups);
        cliente.setNombreCliente(this.nombreCliente);
        cliente.setTarifa(this.tarifa);
        cliente.setIsDeleted(this.isDeleted);
        return cliente;
    }

    @Override
    public void update(Cliente source) {
        this.cups = source.getCups();
        this.nombreCliente = source.nombreCliente;
        this.tarifa = source.tarifa;
        this.isDeleted = source.isDeleted;
    }
}
