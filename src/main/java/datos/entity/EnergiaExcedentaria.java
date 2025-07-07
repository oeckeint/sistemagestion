package datos.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.*;

@Getter
@Setter
@ToString
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

    @Column(name = "fecha_desde")
    private LocalDateTime fechaDesde;

    @Column(name = "fecha_hasta")
    private LocalDateTime fechaHasta;

    //Neta generada

    @Column(name = "neta_generada_01")
    private double netaGenerada01;

    @Column(name = "neta_generada_02")
    private double netaGenerada02;

    @Column(name = "neta_generada_03")
    private double netaGenerada03;

    @Column(name = "neta_generada_04")
    private double netaGenerada04;

    @Column(name = "neta_generada_05")
    private double netaGenerada05;

    @Column(name = "neta_generada_06")
    private double netaGenerada06;

    //Autoconsumida

    @Column(name = "tipo_autoconsumo")
    private int tipoAutoconsumo;

    @Column(name = "autoconsumida_01")
    private double autoconsumida01;

    @Column(name = "pago_tda_01")
    private double pagoTda01;

    @Column(name = "autoconsumida_02")
    private double autoconsumida02;

    @Column(name = "pago_tda_02")
    private double pagoTda02;

    @Column(name = "autoconsumida_03")
    private double autoconsumida03;

    @Column(name = "pago_tda_03")
    private double pagoTda03;

    @Column(name = "autoconsumida_04")
    private double autoconsumida04;

    @Column(name = "pago_tda_04")
    private double pagoTda04;

    @Column(name = "autoconsumida_05")
    private double autoconsumida05;

    @Column(name = "pago_tda_05")
    private double pagoTda05;

    @Column(name = "autoconsumida_06")
    private double autoconsumida06;

    @Column(name = "pago_tda_06")
    private double pagoTda06;

    @OneToMany(mappedBy = "energiaExcedentaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Peaje> peajes;

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

    public void setAllNetaGenerada(List<Double> datos) {
        this.netaGenerada01 = datos.get(0);
        this.netaGenerada02 = datos.get(1);
        this.netaGenerada03 = datos.get(2);
        this.netaGenerada04 = datos.get(3);
        this.netaGenerada05 = datos.get(4);
        this.netaGenerada06 = datos.get(5);
    }

    public void setAllAutoconsumida(List<Double> datosAutoconsumida) {
        this.autoconsumida01 = datosAutoconsumida.get(0);
        this.autoconsumida02 = datosAutoconsumida.get(1);
        this.autoconsumida03 = datosAutoconsumida.get(2);
        this.autoconsumida04 = datosAutoconsumida.get(3);
        this.autoconsumida05 = datosAutoconsumida.get(4);
        this.autoconsumida06 = datosAutoconsumida.get(5);
    }

    public void setAllPagoTDA(List<Double> datos) {
        this.pagoTda01 = datos.get(0);
        this.pagoTda02 = datos.get(1);
        this.pagoTda03 = datos.get(2);
        this.pagoTda04 = datos.get(3);
        this.pagoTda05 = datos.get(4);
        this.pagoTda06 = datos.get(5);
    }

}
