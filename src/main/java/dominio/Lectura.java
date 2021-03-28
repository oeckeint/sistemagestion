
package dominio;

public class Lectura {
    private int idLectura;
    private String tipoLectura;

    public Lectura(int idLectura, String tipoLectura) {
        this.idLectura = idLectura;
        this.tipoLectura = tipoLectura;
    }

    public Lectura(int idLectura) {
        this.idLectura = idLectura;
    }

    public Lectura(String tipoLectura) {
        this.tipoLectura = tipoLectura;
    }

    public int getIdLectura() {
        return idLectura;
    }

    public void setIdLectura(int idLectura) {
        this.idLectura = idLectura;
    }

    public String getTipoLectura() {
        return tipoLectura;
    }

    public void setTipoLectura(String tipoLectura) {
        this.tipoLectura = tipoLectura;
    }

    @Override
    public String toString() {
        return "Lectura{" + "idLectura=" + idLectura + ", tipoLectura=" + tipoLectura + '}';
    }
    
    
}
