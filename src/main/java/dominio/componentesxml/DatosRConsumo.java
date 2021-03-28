
package dominio.componentesxml;

import java.util.List;

public class DatosRConsumo {
    private double consumo1, consumo2, consumo3, consumo4, consumo5, consumo6, suma;

    public DatosRConsumo() {
    }

    public DatosRConsumo(List<Double> datos) {
        this.consumo1 = datos.get(0);
        this.consumo2 = datos.get(1);
        this.consumo3 = datos.get(2);
        this.consumo4 = datos.get(3);
        this.consumo5 = datos.get(4);
        this.consumo6 = datos.get(5);
        this.suma = datos.get(6);
    }

    public double getConsumo1() {
        return consumo1;
    }

    public double getConsumo2() {
        return consumo2;
    }

    public double getConsumo3() {
        return consumo3;
    }

    public double getConsumo4() {
        return consumo4;
    }

    public double getConsumo5() {
        return consumo5;
    }

    public double getConsumo6() {
        return consumo6;
    }

    public double getSuma() {
        return suma;
    }

    @Override
    public String toString() {
        return "DatosRConsumo{" + "consumo1=" + consumo1 + ", consumo2=" + consumo2 + ", consumo3=" + consumo3 + ", consumo4=" + consumo4 + ", consumo5=" + consumo5 + ", consumo6=" + consumo6 + ", suma=" + suma + '}';
    }
    
    
}
