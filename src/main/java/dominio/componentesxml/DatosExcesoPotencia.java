
package dominio.componentesxml;

import java.util.List;

public class DatosExcesoPotencia {
    private double P1, P2,P3, P4, P5, P6 ,T;

    public DatosExcesoPotencia() {
    }

    public DatosExcesoPotencia(List<Double> datos) {
        this.P1 = datos.get(0);
        this.P2 = datos.get(1);
        this.P3 = datos.get(2);
        this.P4 = datos.get(3);
        this.P5 = datos.get(4);
        this.P6 = datos.get(5);
        this.T = datos.get(6);
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

    public double getT() {
        return T;
    }

    @Override
    public String toString() {
        return "DatosExcesoPotencia{" + "ValorExcesoPotencia1=" + P1 + ", ValorExcesoPotencia2=" + P2 + ", ValorExcesoPotencia3=" + P3 + ", ValorExcesoPotencia4=" + P4 + ", PValorExcesoPotencia=" + P5 + ", ValorExcesoPotencia6=" + P6 + ", Total=" + T + '}';
    }
    
    
}
