
package dominio.componentesxml;

import java.util.List;

public class DatosAlquileres {
    private double importe;

    public DatosAlquileres() {
    }

    public DatosAlquileres(List <Double> datos) {
        this.importe = datos.get(0);
    }

    public double getImporte() {
        return importe;
    }

    @Override
    public String toString() {
        return "DatosAlquileres{" + "importe=" + importe + '}';
    }
    
}
