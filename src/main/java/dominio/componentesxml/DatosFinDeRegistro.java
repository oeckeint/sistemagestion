
package dominio.componentesxml;

import java.util.List;

public class DatosFinDeRegistro {
    private double importeTotal, saldoTotalFacturacion;
    private int totalRecibos;
    private String fechaValor, FechaLimitePago, idRemesa;

    public DatosFinDeRegistro() {
    }
    
    public DatosFinDeRegistro(List<String> datos) {
        this.importeTotal = Double.parseDouble(datos.get(0));
        this.saldoTotalFacturacion = Double.parseDouble(datos.get(1));
        this.totalRecibos = Integer.parseInt(datos.get(2));
        this.fechaValor = datos.get(3);
        this.FechaLimitePago = datos.get(4);
        this.idRemesa = datos.get(5);
    }

    public DatosFinDeRegistro(double importeTotal) {
        this.importeTotal = importeTotal;
    }
    
    public double getImporteTotal() {
        return importeTotal;
    }

    public double getSaldoTotalFacturacion() {
        return saldoTotalFacturacion;
    }

    public int getTotalRecibos() {
        return totalRecibos;
    }

    public String getFechaValor() {
        return fechaValor;
    }

    public String getFechaLimitePago() {
        return FechaLimitePago;
    }

    public String getIdRemesa() {
        return idRemesa;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }
    
    @Override
    public String toString() {
        return "DatosFinDeRegistro{" + "importeTotal=" + importeTotal + ", saldoTotalFacturacion=" + saldoTotalFacturacion + ", totalRecibos=" + totalRecibos + ", fechaValor=" + fechaValor + ", FechaLimitePago=" + FechaLimitePago + ", idRemesa=" + idRemesa + '}';
    }
    
}
