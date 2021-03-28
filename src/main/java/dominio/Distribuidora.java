package dominio;

public class Distribuidora {
    private int idDistribuidora;
    private String nombreDistribuidora;

    public Distribuidora(int idDistribuidora, String nombreDistribuidora) {
        this.idDistribuidora = idDistribuidora;
        this.nombreDistribuidora = nombreDistribuidora;
    }

    public Distribuidora(int idDistribuidora) {
        this.idDistribuidora = idDistribuidora;
    }

    public int getIdDistribuidora() {
        return idDistribuidora;
    }

    public void setIdDistribuidora(int idDistribuidora) {
        this.idDistribuidora = idDistribuidora;
    }

    public String getNombreDistribuidora() {
        return nombreDistribuidora;
    }

    public void setNombreDistribuidora(String nombreDistribuidora) {
        this.nombreDistribuidora = nombreDistribuidora;
    }
    
    
    
    @Override
    public String toString() {
        return "Distribuidora{" + "idDistribuidora=" + idDistribuidora + ", nombreDistribuidora=" + nombreDistribuidora + '}';
    }
    
    
}
