
package dominio.componentesxml;

import java.util.List;

public class DatosEnergiaActivaValores {
    private double v1, v2, v3, v4, v5, v6, total;

    public DatosEnergiaActivaValores() {
    }

    public DatosEnergiaActivaValores(List<Double> datos) {
        this.v1 = datos.get(0);
        this.v2 = datos.get(1);
        this.v3 = datos.get(2);
        this.v4 = datos.get(3);
        this.v5 = datos.get(4);
        this.v6 = datos.get(5);
        this.total = datos.get(6);
    }

    public double getV1() {
        return v1;
    }

    public double getV2() {
        return v2;
    }

    public double getV3() {
        return v3;
    }

    public double getV4() {
        return v4;
    }

    public double getV5() {
        return v5;
    }

    public double getV6() {
        return v6;
    }

    public double getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "DatosEnergiaActivaValores{" + "v1=" + v1 + ", v2=" + v2 + ", v3=" + v3 + ", v4=" + v4 + ", v5=" + v5 + ", v6=" + v6 + ", total=" + total + '}';
    }
    
}
