package excepciones;

public class FacturaNoEspecificaCodRecticadaException extends Exception{

    /**
     * El archivo xml no ha especificado el nodo CodigoFacturaRectificadaAnulada o se encuentra vacio
     */
    public FacturaNoEspecificaCodRecticadaException(String codigoFiscalFactura){
        super("La Factura " + codigoFiscalFactura + " no especifica un codigo de factura para rectificar");
    }

}
