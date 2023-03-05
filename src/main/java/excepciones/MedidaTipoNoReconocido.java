package excepciones;

import utileria.ArchivoTexto;

public class MedidaTipoNoReconocido extends Exception{

    public MedidaTipoNoReconocido() {
        super("el tipo de medida especificado en el archivo no es reconocido.");
    }
}