
package dominio.componentesxml;

import java.util.List;

public class DatosAeProcedenciaHasta {
    private int procedencia;

    public DatosAeProcedenciaHasta() {
    }

    public DatosAeProcedenciaHasta(List <Integer> datos) {
        this.procedencia = datos.get(0);
    }

    public int getProcedencia() {
        return procedencia;
    }

    @Override
    public String toString() {
        return "DatosAeProcedenciaHasta{" + "procedencia=" + procedencia + '}';
    }
    
}
