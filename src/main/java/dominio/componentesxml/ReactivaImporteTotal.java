
package dominio.componentesxml;

import java.util.ArrayList;

public class ReactivaImporteTotal {
    private double ImporteTotal;

    public ReactivaImporteTotal() {
    }

    public ReactivaImporteTotal(ArrayList<String> datos) {
        this.ImporteTotal = Double.parseDouble(datos.get(0));
    }

    public double getImporteTotal() {
        return ImporteTotal;
    }

    public void setImporteTotal(double ImporteTotal) {
        this.ImporteTotal = ImporteTotal;
    }

    @Override
    public String toString() {
        return "ReactivaImporteTotal{" + "ImporteTotal=" + ImporteTotal + '}';
    }
    
}
