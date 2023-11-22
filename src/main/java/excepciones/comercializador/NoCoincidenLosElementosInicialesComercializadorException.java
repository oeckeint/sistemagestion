package excepciones.comercializador;

import controladores.helper.Etiquetas;

public class NoCoincidenLosElementosInicialesComercializadorException extends Exception{

    public NoCoincidenLosElementosInicialesComercializadorException() {
        super("los elementos iniciales obtenidos no coinciden con los esperados, por favor asegurese de estar usando este patron " + Etiquetas.COMERCIALIZADOR_PATRON_NOMBRE_ARCHIVO);
    }
}
