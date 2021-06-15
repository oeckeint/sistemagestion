package dominio.componentesxml;

import java.util.List;

public class CargoImporteTotal {

    private double impTot;

    public CargoImporteTotal() {
    }

    public CargoImporteTotal(List<Double> datos) {
        this.impTot = datos.get(0);
    }

    public double getImpTot() {
        return impTot;
    }

    public void setImpTot(double impTot) {
        this.impTot = impTot;
    }

    @Override
    public String toString() {
        return "CargoImporteTotal{" + "impTot=" + impTot + '}';
    }

}
