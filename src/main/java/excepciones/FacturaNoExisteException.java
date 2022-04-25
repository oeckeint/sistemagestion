package excepciones;

import controladores.ProcesamientoXml.TablaBusqueda;
import utileria.ArchivoTexto;

public class FacturaNoExisteException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public FacturaNoExisteException(String codFisFac, TablaBusqueda tb) {
		super("No existe la factura con el codFisFac " + codFisFac + " en la tabla de " + tb.toString());
		ArchivoTexto.escribirError("No existe la factura con el codFisFac " + codFisFac + " en la tabla de " + tb.toString());
	}

}
