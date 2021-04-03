package excepciones;
public class ArchivoVacioException extends Exception{
    public ArchivoVacioException(){
        super("<Strong>está vacío</Strong>");
    }
}
