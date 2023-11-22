package excepciones.comercializador;

public class CodigoPasoNoReconocidoExeption extends Exception {

    public CodigoPasoNoReconocidoExeption(String codigoPaso, String codigoProceso) {
        super("el código de paso " + codigoPaso + " no esta soportado con el codigo proceso " + codigoProceso + ".");
    }
}
