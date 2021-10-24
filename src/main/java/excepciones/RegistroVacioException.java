package excepciones;

public class RegistroVacioException extends Exception{
    
    public RegistroVacioException(){
        super("La consulta regreso un registro nulo");
    }

}
