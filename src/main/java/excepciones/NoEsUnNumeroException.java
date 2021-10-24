package excepciones;

public class NoEsUnNumeroException extends Exception{
    
    public NoEsUnNumeroException(){
        super("Solo se aceptan valores enteros");
    }

}
