
package dominio.componentesxml;

import java.util.List;

public class DatosFacturaAtr {
    private String tarifaAtrFact;
    private String modoControlPotencia;
    private String marcaMedidaConPerdidas;
    private String VAsTrafo;
    private String PorcentajePerdidas;
    private int NumeroDias;

    public DatosFacturaAtr() {
    }

    public DatosFacturaAtr(List<String> datos) {
        this.tarifaAtrFact = datos.get(0);
        this.modoControlPotencia = datos.get(1);
        this.marcaMedidaConPerdidas = datos.get(2);
        this.VAsTrafo = datos.get(3);
        this.PorcentajePerdidas = datos.get(4);
        this.NumeroDias = Integer.parseInt(datos.get(5));
    }

    public String getTarifaAtrFact() {
        return tarifaAtrFact;
    }

    public String getModoControlPotencia() {
        return modoControlPotencia;
    }

    public String getMarcaMedidaConPerdidas() {
        return marcaMedidaConPerdidas;
    }

    public String getVAsTrafo() {
        return VAsTrafo;
    }

    public String getPorcentajePerdidas() {
        return PorcentajePerdidas;
    }

    public int getNumeroDias() {
        return NumeroDias;
    }
    

    @Override
    public String toString() {
        return "DatosFacturaAtr{" + "tarifaAtrFact=" + tarifaAtrFact + ", modoControlPotencia=" + modoControlPotencia + ", marcaMedidaConPerdidas=" + marcaMedidaConPerdidas + ", VAsTrafo=" + VAsTrafo + ", PorcentajePerdidas=" + PorcentajePerdidas + ", NumeroDias=" + NumeroDias + '}';
    }
    
    
}
