package excepciones;

import utileria.ArchivoTexto;

public class MedidaTipoNoReconocido extends Exception{

    public MedidaTipoNoReconocido(String nombreArchivo) {
        super("el tipo de medida especificado en el archivo " + nombreArchivo + "no es reconocido");
    }
}