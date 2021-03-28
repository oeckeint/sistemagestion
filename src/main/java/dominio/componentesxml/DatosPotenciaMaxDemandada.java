
package dominio.componentesxml;

import java.util.List;

public class DatosPotenciaMaxDemandada {
    private double P1, P2, P3, P4, P5, P6;

    public DatosPotenciaMaxDemandada() {
    }

    public DatosPotenciaMaxDemandada(List <Double> datos) {
        this.P1 = datos.get(0);
        this.P2 = datos.get(1);
        this.P3 = datos.get(2);
        this.P4 = datos.get(3);
        this.P5 = datos.get(4);
        this.P6 = datos.get(5);
    }

    public double getP1() {
        return P1;
    }

    public double getP2() {
        return P2;
    }

    public double getP3() {
        return P3;
    }

    public double getP4() {
        return P4;
    }

    public double getP5() {
        return P5;
    }

    public double getP6() {
        return P6;
    }

    @Override
    public String toString() {
        return "DatosPotenciaMaxDemandada{" + "PotenciaMaxDemandada1=" + P1 + ", PotenciaMaxDemandada2=" + P2 + ", PotenciaMaxDemandada3=" + P3 + ", PotenciaMaxDemandada4=" + P4 + ", PotenciaMaxDemandada5=" + P5 + ", PotenciaMaxDemandada6=" + P6 + '}';
    }
        
}
