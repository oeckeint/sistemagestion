
package dominio.componentesxml;

import java.util.List;

public class DatosPotenciaImporteTotal {
    private double importeTotal;

    public DatosPotenciaImporteTotal() {
    }

    public DatosPotenciaImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public DatosPotenciaImporteTotal(List<Double> datos) {
        this.importeTotal = datos.get(0);
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }
    
    @Override
    public String toString() {
        return "DatosPotenciaImporteTotal{" + "importeTotal=" + importeTotal + '}';
    }
    
}
