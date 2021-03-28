
package dominio.componentesxml;

import java.util.List;

public class DatosEnergiaActivaImporteTotal {
    private double importeTotal;

    public DatosEnergiaActivaImporteTotal() {
    }

    public DatosEnergiaActivaImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public DatosEnergiaActivaImporteTotal(List <Double> datos) {
        this.importeTotal = datos.get(0);
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    @Override
    public String toString() {
        return "DatosEnergiaActivaImporteTotal{" + "importeTotal=" + importeTotal + '}';
    }
    
}
