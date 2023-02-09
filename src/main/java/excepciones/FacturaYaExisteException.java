package excepciones;

public class FacturaYaExisteException extends Exception{
    
	private static final long serialVersionUID = 1L;

	public FacturaYaExisteException(String codigoFiscalFactura){
		super("la factura (<Strong>"
				+ "<a href=\"javascript:window.open('/sistemagestion/facturas/detalles?codFisFac="+ codigoFiscalFactura + "', 'ventana1', 'width=1300,height=700')\">"
				+ codigoFiscalFactura
				+ "</a></Strong>) ya ha sido registrada.");
    }
}
