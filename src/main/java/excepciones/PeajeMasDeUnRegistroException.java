package excepciones;

import utileria.ArchivoTexto;

public class PeajeMasDeUnRegistroException extends Exception {

	private static final long serialVersionUID = 1L;

	public PeajeMasDeUnRegistroException(String codFisFac) {
		super("Se ha encontrado más de un registro con el codigo Fiscal <Strong>" + codFisFac + "</Strong>");
		ArchivoTexto.escribirError("Se ha encontrado más de un registro con el codigo Fiscal " + codFisFac);
	}

}
