
package dominio.componentesxml.reclamaciones;

import java.util.List;

public class DatosInformacionReclamacion {
    private String codigoReclamacioneDistribuidora;

    public DatosInformacionReclamacion() {
    }

    public DatosInformacionReclamacion(List<String> datos) {
        this.codigoReclamacioneDistribuidora = datos.get(0);
    }

    public String getCodigoReclamacioneDistribuidora(){
        return this.codigoReclamacioneDistribuidora;
    }


    @Override
    public String toString() {
        return "RechazoReclamacion{" +  "codigoReclamacionDistribuidora=" + this.codigoReclamacioneDistribuidora + "}";
    }
}
