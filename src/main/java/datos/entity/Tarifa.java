package datos.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tarifa")
public class Tarifa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarifa")
    private long idTarifa;
    
    @Column(name = "valor")
    private String nombreTarifa;
    
    @Column(name = "tarifa_codigo")
    private String tarifaCodigo;
    
    @Column(name = "status")
    private int status;

    public Tarifa() {
    }

    public Tarifa(String nombreTarifa, int status) {
        this.nombreTarifa = nombreTarifa;
        this.status = status;
    }

    public Tarifa(String nombreTarifa) {
        this.nombreTarifa = nombreTarifa;
    }

    public long getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(long idTarifa) {
        this.idTarifa = idTarifa;
    }

    public String getNombreTarifa() {
        return nombreTarifa;
    }

    public void setNombreTarifa(String nombreTarifa) {
        this.nombreTarifa = nombreTarifa;
    }

    public String getTarifaCodigo() {
        return tarifaCodigo;
    }

    public void setTarifaCodigo(String tarifaCodigo) {
        this.tarifaCodigo = tarifaCodigo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
}
