package excepciones.comercializador;

public class CodigoProcesoNoPuedeSerProcesadoException extends Exception {

    public CodigoProcesoNoPuedeSerProcesadoException(String codigoProceso) {
        super("el codigo de proceso " + codigoProceso + " no puede ser procesado, es probable que no este soportado.");
    }
}
