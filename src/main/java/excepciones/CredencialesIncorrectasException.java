package excepciones;

public class CredencialesIncorrectasException extends Exception{
    
    public CredencialesIncorrectasException(String usuario){
        super("Las credenciales para usuario " + usuario + " son incorrectas.");
    }

}
