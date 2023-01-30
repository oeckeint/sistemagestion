package excepciones;

public class FacturaCodRectNoExisteException extends Exception{

    public FacturaCodRectNoExisteException(String codFacRec){
        super("<Strong>no existe la factura (" + codFacRec + ")</Strong> para rectificar.");
    }

}
