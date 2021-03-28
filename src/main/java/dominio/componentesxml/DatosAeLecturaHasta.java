
package dominio.componentesxml;

import java.util.List;

public class DatosAeLecturaHasta {
    private double lectura1, lectura2, lectura3, lectura4, lectura5, lectura6;

    public DatosAeLecturaHasta() {
    }

    public DatosAeLecturaHasta(List <Double> datos) {
        this.lectura1 = datos.get(0);
        this.lectura2 = datos.get(1);
        this.lectura3 = datos.get(2);
        this.lectura4 = datos.get(3);
        this.lectura5 = datos.get(4);
        this.lectura6 = datos.get(5);
    }

    public double getLectura1() {
        return lectura1;
    }

    public double getLectura2() {
        return lectura2;
    }

    public double getLectura3() {
        return lectura3;
    }

    public double getLectura4() {
        return lectura4;
    }

    public double getLectura5() {
        return lectura5;
    }

    public double getLectura6() {
        return lectura6;
    }

    @Override
    public String toString() {
        return "DatosAeLecturaHasta{" + "lectura1=" + lectura1 + ", lectura2=" + lectura2 + ", lectura3=" + lectura3 + ", lectura4=" + lectura4 + ", lectura5=" + lectura5 + ", lectura6=" + lectura6 + '}';
    }
    
}
