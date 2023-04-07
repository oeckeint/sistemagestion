
package dominio.componentesxml.reclamaciones;

import java.util.List;

public class DatosSolicitud {
    private String tipo, subtipo;

    public DatosSolicitud() {
    }

    public DatosSolicitud(List<String> datos) {
        this.tipo = datos.get(0);
        this.subtipo = datos.get(1);
    }

    public String getTipo(){
        return this.tipo;
    }

    public String getSubtipo(){
        return this.tipo;
    }

    @Override
    public String toString() {
        return "DatosSolicitud{" + "tipo=" + this.tipo + ", subtipo= " + this.subtipo + "}";
    }
}
