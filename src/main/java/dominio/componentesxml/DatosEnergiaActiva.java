
package dominio.componentesxml;

import java.util.List;

public class DatosEnergiaActiva {
    private String fechaDesde1;
    private String fechaHasta1;
    private String fechaDesde2;
    private String fechaHasta2;

    public DatosEnergiaActiva() {
    }

    public DatosEnergiaActiva(List<String> datos) {
        this.fechaDesde1 = datos.get(0);
        this.fechaHasta1 = datos.get(1);
        this.fechaDesde2 = datos.get(2);
        this.fechaHasta2 = datos.get(3);
    }

    public String getFechaDesde1() {
        return fechaDesde1;
    }

    public String getFechaHasta1() {
        return fechaHasta1;
    }

    public String getFechaDesde2() {
        return fechaDesde2;
    }

    public String getFechaHasta2() {
        return fechaHasta2;
    }

    @Override
    public String toString() {
        return "DatosEnergiaActiva{" + "fechaDesde1=" + fechaDesde1 + ", fechaHasta1=" + fechaHasta1 + ", fechaDesde2=" + fechaDesde2 + ", fechaHasta2=" + fechaHasta2 + '}';
    }
    
}
