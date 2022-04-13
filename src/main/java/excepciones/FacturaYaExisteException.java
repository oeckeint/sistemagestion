package excepciones;

public class FacturaYaExisteException extends Exception{
    
	private static final long serialVersionUID = 1L;

	public FacturaYaExisteException(String codFactura, String tipoFactura){
        super("la factura (<Strong>"
        		+ "<a href=\"javascript:window.open('/sistemagestion/" + tipoFactura + "/detalles?codFisFac="+ codFactura + "', 'ventana1', 'width=1300,height=700')\">"  
        		+ codFactura 
        		+ "</a></Strong>) ya ha sido registrada.");
    }
}
