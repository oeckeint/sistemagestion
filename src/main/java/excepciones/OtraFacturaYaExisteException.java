package excepciones;

public class OtraFacturaYaExisteException extends Exception{
    public OtraFacturaYaExisteException(String codigoFiscalFactura) {
        super("\"otra factura\" (<Strong>"
                + "<a href=\"javascript:window.open('/sistemagestion/otrasfacturas/detalles?codFisFac="+ codigoFiscalFactura + "', 'ventana1', 'width=1300,height=700')\">"
                + codigoFiscalFactura
                + "</a></Strong>) ya ha sido registrada.");
    }
}
