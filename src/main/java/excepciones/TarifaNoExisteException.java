package excepciones;

public class TarifaNoExisteException extends Exception{
    
    public TarifaNoExisteException(String tarifa){
        super("la tarifa " + tarifa + " no existe.");
    }
    
}
