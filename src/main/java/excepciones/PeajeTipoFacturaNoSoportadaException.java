
package excepciones;

public class PeajeTipoFacturaNoSoportadaException extends Exception{
    public PeajeTipoFacturaNoSoportadaException(String tipoFactura){
        super("el tipo de factura (<Strong>" +  tipoFactura + "</Strong>) no está soportada");
    }
}
