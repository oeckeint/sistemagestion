package excepciones;

import controladores.Procesamiento.TablaBusqueda;
import utileria.ArchivoTexto;

public class FacturaNoExisteException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public FacturaNoExisteException(String codFisFac, TablaBusqueda tb) {
		super("<Strong>no existe la factura</Strong> con el codigo Fiscal Factura " + codFisFac + " en la tabla de " + tb.toString());
		ArchivoTexto.escribirError("No existe la factura con el codFisFac " + codFisFac + " en la tabla de " + tb.toString());
	}

}
