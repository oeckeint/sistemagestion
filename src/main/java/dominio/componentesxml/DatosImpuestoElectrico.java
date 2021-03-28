
package dominio.componentesxml;

import java.util.List;

public class DatosImpuestoElectrico {
    private double importe;

    public DatosImpuestoElectrico() {
    }

    public DatosImpuestoElectrico(List <Double> datos) {
        this.importe = datos.get(0);
    }

    public double getImporte() {
        return importe;
    }

    @Override
    public String toString() {
        return "DatosImpuestoElectrico{" + "importe=" + importe + '}';
    }
        
}
