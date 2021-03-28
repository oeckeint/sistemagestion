package dominio.componentesxml;

import java.util.List;

public class DatosPotenciaPrecio {
    private double p1, p2, p3, p4, p5, p6;

    public DatosPotenciaPrecio() {
    }

    public DatosPotenciaPrecio(List<Double> datos) {
        this.p1 = datos.get(0);
        this.p2 = datos.get(1);
        this.p3 = datos.get(2);
        this.p4 = datos.get(3);
        this.p5 = datos.get(4);
        this.p6 = datos.get(5);
    }

    public double getP1() {
        return p1;
    }

    public double getP2() {
        return p2;
    }

    public double getP3() {
        return p3;
    }

    public double getP4() {
        return p4;
    }

    public double getP5() {
        return p5;
    }

    public double getP6() {
        return p6;
    }

    @Override
    public String toString() {
        return "DatosPotenciaPrecio{" + "potenciaPrecio1=" + p1 + ", potenciaPrecio2=" + p2 + ", potenciaPrecio3=" + p3 + ", potenciaPrecio4=" + p4 + ", potenciaPrecio5=" + p5 + ", potenciaPrecio6=" + p6 + '}';
    }
    
}
