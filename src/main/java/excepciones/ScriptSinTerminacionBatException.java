package excepciones;

public class ScriptSinTerminacionBatException extends Exception{

    public ScriptSinTerminacionBatException(String extension){
        super("El script no tiene la terminacion .bat (" + extension + ")");
    }
}
