package datos.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente {

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

    @Override
    public String toString() {
        return "Cliente{" + "idCliente=" + idCliente + ", cups=" + cups + ", nombreCliente=" + nombreCliente + ", tarifa=" + tarifa + ", isDeleted=" + isDeleted + '}';
    }

}
