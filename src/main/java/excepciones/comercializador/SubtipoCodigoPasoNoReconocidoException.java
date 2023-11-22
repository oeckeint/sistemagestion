package excepciones.comercializador;

public class SubtipoCodigoPasoNoReconocidoException extends Exception {

    public SubtipoCodigoPasoNoReconocidoException(String codigoPaso, String codigoProceso) {
        super("no se reconoce el subtipo del codigo de paso " + codigoPaso + " y codigo del proceso " + codigoProceso + ".");
    }
}
