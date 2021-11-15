package datos.entity;

import java.util.List;
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
@Table(name = "contenido_xml_energia_excedentaria")
public class EnergiaExcedentaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_energia_excedentaria")
    private int idEnergiaExcedentaria;

    @Column(name = "energia_excedentaria_01")
    private double energiaExcedentaria01;

    @Column(name = "energia_excedentaria_02")
    private double energiaExcedentaria02;

    @Column(name = "energia_excedentaria_03")
    private double energiaExcedentaria03;

    @Column(name = "energia_excedentaria_04")
    private double energiaExcedentaria04;

    @Column(name = "energia_excedentaria_05")
    private double energiaExcedentaria05;

    @Column(name = "energia_excedentaria_06")
    private double energiaExcedentaria06;

    @Column(name = "valor_total_energia_excedentaria")
    private double valorTotalEnergiaExcedentaria;

    @OneToOne(mappedBy = "energiaExcedentaria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Peaje peaje;

    public EnergiaExcedentaria() {
    }

    public EnergiaExcedentaria(List<Double> datos) {
        this.energiaExcedentaria01 = datos.get(0);
        this.energiaExcedentaria02 = datos.get(1);
        this.energiaExcedentaria03 = datos.get(2);
        this.energiaExcedentaria04 = datos.get(3);
        this.energiaExcedentaria05 = datos.get(4);
        this.energiaExcedentaria06 = datos.get(5);
        this.valorTotalEnergiaExcedentaria = datos.get(6);
    }

    public EnergiaExcedentaria(double valorTotalEnergiaExcedentaria) {
        this.valorTotalEnergiaExcedentaria = valorTotalEnergiaExcedentaria;
    }

    public int getIdEnergiaExcedentaria() {
        return idEnergiaExcedentaria;
    }

    public void setIdEnergiaExcedentaria(int idEnergiaExcedentaria) {
        this.idEnergiaExcedentaria = idEnergiaExcedentaria;
    }

    public double getEnergiaExcedentaria01() {
        return energiaExcedentaria01;
    }

    public void setEnergiaExcedentaria01(double energiaExcedentaria01) {
        this.energiaExcedentaria01 = energiaExcedentaria01;
    }

    public double getEnergiaExcedentaria02() {
        return energiaExcedentaria02;
    }

    public void setEnergiaExcedentaria02(double energiaExcedentaria02) {
        this.energiaExcedentaria02 = energiaExcedentaria02;
    }

    public double getEnergiaExcedentaria03() {
        return energiaExcedentaria03;
    }

    public void setEnergiaExcedentaria03(double energiaExcedentaria03) {
        this.energiaExcedentaria03 = energiaExcedentaria03;
    }

    public double getEnergiaExcedentaria04() {
        return energiaExcedentaria04;
    }

    public void setEnergiaExcedentaria04(double energiaExcedentaria04) {
        this.energiaExcedentaria04 = energiaExcedentaria04;
    }

    public double getEnergiaExcedentaria05() {
        return energiaExcedentaria05;
    }

    public void setEnergiaExcedentaria05(double energiaExcedentaria05) {
        this.energiaExcedentaria05 = energiaExcedentaria05;
    }

    public double getEnergiaExcedentaria06() {
        return energiaExcedentaria06;
    }

    public void setEnergiaExcedentaria06(double energiaExcedentaria06) {
        this.energiaExcedentaria06 = energiaExcedentaria06;
    }

    public double getValorTotalEnergiaExcedentaria() {
        return valorTotalEnergiaExcedentaria;
    }

    public void setValorTotalEnergiaExcedentaria(double valorTotalEnergiaExcedentaria) {
        this.valorTotalEnergiaExcedentaria = valorTotalEnergiaExcedentaria;
    }

    public Peaje getPeaje() {
        return peaje;
    }

    public void setPeaje(Peaje peaje) {
        this.peaje = peaje;
    }

    @Override
    public String toString() {
        return "energia_excedentaria{" + "idEnergiaExcedentaria=" + idEnergiaExcedentaria + ", energiaExcedentaria01=" + energiaExcedentaria01 + ", energiaExcedentaria02=" + energiaExcedentaria02 + ", energiaExcedentaria03=" + energiaExcedentaria03 + ", energiaExcedentaria04=" + energiaExcedentaria04 + ", energiaExcedentaria05=" + energiaExcedentaria05 + ", energiaExcedentaria06=" + energiaExcedentaria06 + ", valorTotalEnergiaExcedentaria=" + valorTotalEnergiaExcedentaria + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.energiaExcedentaria01) ^ (Double.doubleToLongBits(this.energiaExcedentaria01) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.energiaExcedentaria02) ^ (Double.doubleToLongBits(this.energiaExcedentaria02) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.energiaExcedentaria03) ^ (Double.doubleToLongBits(this.energiaExcedentaria03) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.energiaExcedentaria04) ^ (Double.doubleToLongBits(this.energiaExcedentaria04) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.energiaExcedentaria05) ^ (Double.doubleToLongBits(this.energiaExcedentaria05) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.energiaExcedentaria06) ^ (Double.doubleToLongBits(this.energiaExcedentaria06) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.valorTotalEnergiaExcedentaria) ^ (Double.doubleToLongBits(this.valorTotalEnergiaExcedentaria) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EnergiaExcedentaria other = (EnergiaExcedentaria) obj;
        if (Double.doubleToLongBits(this.energiaExcedentaria01) != Double.doubleToLongBits(other.energiaExcedentaria01)) {
            return false;
        }
        if (Double.doubleToLongBits(this.energiaExcedentaria02) != Double.doubleToLongBits(other.energiaExcedentaria02)) {
            return false;
        }
        if (Double.doubleToLongBits(this.energiaExcedentaria03) != Double.doubleToLongBits(other.energiaExcedentaria03)) {
            return false;
        }
        if (Double.doubleToLongBits(this.energiaExcedentaria04) != Double.doubleToLongBits(other.energiaExcedentaria04)) {
            return false;
        }
        if (Double.doubleToLongBits(this.energiaExcedentaria05) != Double.doubleToLongBits(other.energiaExcedentaria05)) {
            return false;
        }
        if (Double.doubleToLongBits(this.energiaExcedentaria06) != Double.doubleToLongBits(other.energiaExcedentaria06)) {
            return false;
        }
        if (Double.doubleToLongBits(this.valorTotalEnergiaExcedentaria) != Double.doubleToLongBits(other.valorTotalEnergiaExcedentaria)) {
            return false;
        }
        return true;
    }

}
