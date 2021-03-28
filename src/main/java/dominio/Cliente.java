package dominio;

public class Cliente {
    private int idCliente;
    private String cups;
    private String nombreCliente;
    private String tarifa;

    public Cliente() {
    }

    public Cliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public Cliente(String cups) {
        this.cups = cups;
    }
    
    public Cliente(String cups, String nombreCliente, String tarifa) {
        this.cups = cups;
        this.nombreCliente = nombreCliente;
        this.tarifa = tarifa;
    }

    public Cliente(int idCliente, String cups, String nombreCliente, String tarifa) {
        this.idCliente = idCliente;
        this.cups = cups;
        this.nombreCliente = nombreCliente;
        this.tarifa = tarifa;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getCups() {
        return cups;
    }

    public void setCups(String cups) {
        this.cups = cups;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTarifa() {
        return tarifa;
    }

    public void setTarifa(String tarifa) {
        this.tarifa = tarifa;
    }

    @Override
    public String toString() {
        return "cliente{" + "idCliente=" + idCliente + ", cups=" + cups + ", nombreCliente=" + nombreCliente + ", tarifa=" + tarifa + '}';
    }
    
}
