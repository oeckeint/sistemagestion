package excepciones;

public class NoExisteElNodoException extends Exception{
    
    public NoExisteElNodoException(String nodo){
        super("el nodo <Strong><" + nodo + "> no existe</Strong> y es necesario para continuar.");
    }

}
