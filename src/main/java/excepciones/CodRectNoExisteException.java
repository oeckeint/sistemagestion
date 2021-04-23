package excepciones;
public class CodRectNoExisteException extends Exception{
    
    public CodRectNoExisteException(String codFacRec){
        super("<Strong>no existe la factura (" + codFacRec + ")</Strong> para rectificar.");
    }
    
}
