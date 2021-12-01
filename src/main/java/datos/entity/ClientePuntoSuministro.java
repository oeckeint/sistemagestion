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
@Table(name = "cliente_punto_suministro")
public class ClientePuntoSuministro implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente_punto_suministro")
    private long idClientePuntoSuministro;
    
    @Column(name = "direccion_suministro")
    private String direccionSuministro;
    
    @Column(name = "codigo_postal")
    private String codigoPostal;
    
    @Column(name = "poblacion")
    private String poblacion;
    
    @Column(name = "provincia")
    private String provincia;
    
    @Column(name = "distribuidora")
    private String distribuidora;
    
    @Column(name = "atr")
    private String atr;
    
    @Column(name = "contador")
    private String contador;
    
    @Column(name = "tipo_pm")
    private String tipoPM;
    
    @Column(name = "modo_lectura")
    private String modoLectura;
    
    @Column(name = "tarifa")
    private String tarifa;
    
    @Column(name = "activado")
    private int activado;
    
    @Column(name = "p1")
    private int p1;
    
    @Column(name = "p2")
    private int p2;
    
    @Column(name = "p3")
    private int p3;
    
    @Column(name = "p4")
    private int p4;
    
    @Column(name = "p5")
    private int p5;
    
    @Column(name = "p6")
    private int p6;

    @OneToOne(mappedBy = "clientePuntoSuministro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Cliente cliente;
    
    public ClientePuntoSuministro() {
    }

    public long getIdClientePuntoSuministro() {
        return idClientePuntoSuministro;
    }

    public void setIdClientePuntoSuministro(long idClientePuntoSuministro) {
        this.idClientePuntoSuministro = idClientePuntoSuministro;
    }

    public String getDireccionSuministro() {
        return direccionSuministro;
    }

    public void setDireccionSuministro(String direccionSuministro) {
        this.direccionSuministro = direccionSuministro;
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

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistribuidora() {
        return distribuidora;
    }

    public void setDistribuidora(String distribuidora) {
        this.distribuidora = distribuidora;
    }

    public String getAtr() {
        return atr;
    }

    public void setAtr(String atr) {
        this.atr = atr;
    }

    public String getContador() {
        return contador;
    }

    public void setContador(String contador) {
        this.contador = contador;
    }

    public String getTipoPM() {
        return tipoPM;
    }

    public void setTipoPM(String tipoPM) {
        this.tipoPM = tipoPM;
    }

    public String getModoLectura() {
        return modoLectura;
    }

    public void setModoLectura(String modoLectura) {
        this.modoLectura = modoLectura;
    }

    public String getTarifa() {
        return tarifa;
    }

    public void setTarifa(String tarifa) {
        this.tarifa = tarifa;
    }

    public int getActivado() {
        return activado;
    }

    public void setActivado(int activado) {
        this.activado = activado;
    }

    public int getP1() {
        return p1;
    }

    public void setP1(int p1) {
        this.p1 = p1;
    }

    public int getP2() {
        return p2;
    }

    public void setP2(int p2) {
        this.p2 = p2;
    }

    public int getP3() {
        return p3;
    }

    public void setP3(int p3) {
        this.p3 = p3;
    }

    public int getP4() {
        return p4;
    }

    public void setP4(int p4) {
        this.p4 = p4;
    }

    public int getP5() {
        return p5;
    }

    public void setP5(int p5) {
        this.p5 = p5;
    }

    public int getP6() {
        return p6;
    }

    public void setP6(int p6) {
        this.p6 = p6;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "ClientePuntoSuministro{" + "idClientePuntoSuministro=" + idClientePuntoSuministro + ", direccionSuministro=" + direccionSuministro + ", codigoPostal=" + codigoPostal + ", poblacion=" + poblacion + ", provincia=" + provincia + ", distribuidora=" + distribuidora + ", atr=" + atr + ", contador=" + contador + ", tipoPM=" + tipoPM + ", modoLectura=" + modoLectura + ", tarifa=" + tarifa + ", activado=" + activado + ", p1=" + p1 + ", p2=" + p2 + ", p3=" + p3 + ", p4=" + p4 + ", p5=" + p5 + ", p6=" + p6 + ", cliente=" + cliente + '}';
    }
    
}
