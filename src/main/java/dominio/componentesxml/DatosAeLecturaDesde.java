
package dominio.componentesxml;

import java.util.List;

public class DatosAeLecturaDesde {
    private double Lectura1;
    private double Lectura2;
    private double Lectura3;
    private double Lectura4;
    private double Lectura5;
    private double Lectura6;

    public DatosAeLecturaDesde() {
    }

    public DatosAeLecturaDesde(List <Double> datos) {
        this.Lectura1 = datos.get(0);
        this.Lectura2 = datos.get(1);
        this.Lectura3 = datos.get(2);
        this.Lectura4 = datos.get(3);
        this.Lectura5 = datos.get(4);
        this.Lectura6 = datos.get(5);
    }

    public double getLectura1() {
        return Lectura1;
    }

    public double getLectura2() {
        return Lectura2;
    }

    public double getLectura3() {
        return Lectura3;
    }

    public double getLectura4() {
        return Lectura4;
    }

    public double getLectura5() {
        return Lectura5;
    }

    public double getLectura6() {
        return Lectura6;
    }

    @Override
    public String toString() {
        return "DatosAeLecturaDesde{" + "Lectura1=" + Lectura1 + ", Lectura2=" + Lectura2 + ", Lectura3=" + Lectura3 + ", Lectura4=" + Lectura4 + ", Lectura5=" + Lectura5 + ", Lectura6=" + Lectura6 + '}';
    }
    
    
}
