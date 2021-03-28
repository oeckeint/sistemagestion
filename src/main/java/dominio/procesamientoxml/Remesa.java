
package dominio.procesamientoxml;

public class Remesa {
    private int idRemesa;

    public Remesa(int idRemesa) {
        this.idRemesa = idRemesa;
    }

    public int getIdRemesa() {
        return idRemesa;
    }

    public void setIdRemesa(int idRemesa) {
        this.idRemesa = idRemesa;
    }

    @Override
    public String toString() {
        return "Remesa{" + "idRemesa=" + idRemesa + '}';
    }
    
}
