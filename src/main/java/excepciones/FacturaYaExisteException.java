package excepciones;

public class FacturaYaExisteException extends Exception{
    public FacturaYaExisteException(String codFactura){
        super("la factura (<Strong>" + codFactura + "</Strong>) ya ha sido registrada.");
    }
}
