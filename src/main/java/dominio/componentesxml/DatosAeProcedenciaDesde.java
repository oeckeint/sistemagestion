package dominio.componentesxml;

import java.util.List;

public class DatosAeProcedenciaDesde {
    private int Procedencia;

    public DatosAeProcedenciaDesde() {
    }

    public DatosAeProcedenciaDesde(List<Integer> datos) {
        this.Procedencia = datos.get(0);
    }

    public int getProcedencia() {
        return Procedencia;
    }

    @Override
    public String toString() {
        return "DatosAeProcedenciaDesde{" + "Procedencia=" + Procedencia + '}';
    }
    
}
