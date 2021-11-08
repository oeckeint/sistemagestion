package excepciones;

public class ErrorAlConectarConElServidorException extends Exception{
    
    public ErrorAlConectarConElServidorException(String servidor, int respuesta){
        super("El servidor " + servidor + " no se encuentra disponible o no existe (cod " + respuesta + ")");
    }
    
}
