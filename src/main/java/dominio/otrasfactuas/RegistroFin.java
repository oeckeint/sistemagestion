
package dominio.otrasfactuas;

import java.util.ArrayList;

public class RegistroFin {
    
    private String idRemesa;

    public RegistroFin() {
    }

    public RegistroFin(ArrayList data) {
        this.idRemesa = String.valueOf(data.get(0));
    }

    public String getIdRemesa() {
        return idRemesa;
    }

    public void setIdRemesa(String idRemesa) {
        this.idRemesa = idRemesa;
    }

    @Override
    public String toString() {
        return "RegistroFin{" + "idRemesa=" + idRemesa + '}';
    }
    
    
}
