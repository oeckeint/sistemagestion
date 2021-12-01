package datos.entity;

import java.io.Serializable;
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
@Table(name = "cliente_datos")
public class ClienteDatosGenerales implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcliente_datos")
    private long idClienteDatosGenerales;
    
    @Column(name = "titular")
    private String titular;
    
    @Column(name = "cuenta")
    private String cuenta;
    
    @Column(name = "cif")
    private String cif;
    
    @Column(name = "dominio_social")
    private String dominioSocial;
    
    @Column(name = "codigo_postal")
    private String codigoPostal;
    
    @Column(name = "poblacion")
    private String poblacion;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "cnae")
    private String cnae;
    
    @Column(name = "grupo")
    private String grupo;
    
    @Column(name = "comercial")
    private String comercial;
    
    @OneToOne(mappedBy = "clienteDatosGenerales", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Cliente cliente;

    public ClienteDatosGenerales() {
    }

    public long getIdClienteDatosGenerales() {
        return idClienteDatosGenerales;
    }

    public void setIdClienteDatosGenerales(long idClienteDatosGenerales) {
        this.idClienteDatosGenerales = idClienteDatosGenerales;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getDominioSocial() {
        return dominioSocial;
    }

    public void setDominioSocial(String dominioSocial) {
        this.dominioSocial = dominioSocial;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnae() {
        return cnae;
    }

    public void setCnae(String cnae) {
        this.cnae = cnae;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getComercial() {
        return comercial;
    }

    public void setComercial(String comercial) {
        this.comercial = comercial;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "ClienteDatosGenerales{" + "idClienteDatosGenerales=" + idClienteDatosGenerales + ", titular=" + titular + ", cuenta=" + cuenta + ", cif=" + cif + ", dominioSocial=" + dominioSocial + ", codigoPostal=" + codigoPostal + ", poblacion=" + poblacion + ", email=" + email + ", cnae=" + cnae + ", grupo=" + grupo + ", comercial=" + comercial + ", cliente=" + cliente + '}';
    }
    
}
