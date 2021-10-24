package excepciones;
public class PeajeCodRectNoExisteException extends Exception{
    
    public PeajeCodRectNoExisteException(String codFacRec){
        super("<Strong>no existe el peaje (" + codFacRec + ")</Strong> para rectificar.");
    }
    
}
