
package dominio.componentesxml;

import java.util.List;

public class DatosIva {
    private double baseImponible;

    public DatosIva() {
    }

    public DatosIva(List <Double> datos) {
        this.baseImponible = datos.get(0);
    }

    public double getBaseImponible() {
        return baseImponible;
    }

    @Override
    public String toString() {
        return "DatosIva{" + "baseImponible=" + baseImponible + '}';
    }
}
