package datos.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "cliente_contrato")
public class ClienteContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente_contrato")
    private int idClienteContrato;

    @Column(name = "inicio_contrato")
    private String inicioContrato;

    @Column(name = "fin_contrato")
    private String finContrato;

    @Column(name = "activado")
    private int activado;

    @Column(name = "producto")
    private int producto;

    @Column(name = "coste_gestion")
    private double costeGestion;

    @Column(name = "alquileres")
    private double alquieres;

    @OneToOne(mappedBy = "clienteContrato", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Cliente cliente;

    @Column(name = "id_oferta")
    private int idOferta;

    public ClienteContrato() {
    }

    public ClienteContrato(String inicioContrato, String finContrato, int activado, int producto, double costeGestion, double alquieres, int idOferta) {
        this.inicioContrato = inicioContrato;
        this.finContrato = finContrato;
        this.activado = activado;
        this.producto = producto;
        this.costeGestion = costeGestion;
        this.alquieres = alquieres;
        this.idOferta = idOferta;
    }

    public int getIdClienteContrato() {
        return idClienteContrato;
    }

    public void setIdClienteContrato(int idClienteContrato) {
        this.idClienteContrato = idClienteContrato;
    }

    public String getInicioContrato() {
        return inicioContrato;
    }

    public void setInicioContrato(String inicioContrato) {
        this.inicioContrato = inicioContrato;
    }

    public String getFinContrato() {
        return finContrato;
    }

    public void setFinContrato(String finContrato) {
        this.finContrato = finContrato;
    }

    public int getActivado() {
        return activado;
    }

    public void setActivado(int activado) {
        this.activado = activado;
    }

    public int getProducto() {
        return producto;
    }

    public void setProducto(int producto) {
        this.producto = producto;
    }

    public double getCosteGestion() {
        return costeGestion;
    }

    public void setCosteGestion(double costeGestion) {
        this.costeGestion = costeGestion;
    }

    public double getAlquieres() {
        return alquieres;
    }

    public void setAlquieres(double alquieres) {
        this.alquieres = alquieres;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public int getIdOferta() {
        return idOferta;
    }

    public void setIdOferta(int idOferta) {
        this.idOferta = idOferta;
    }

}
