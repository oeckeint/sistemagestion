package excepciones;

public class ErrorDesconocidoException extends Exception{

    public ErrorDesconocidoException() {
        super("ha <Strong>ocurrido un error desconocido</Strong> revisar informacion en la consola");
    }
    
}
