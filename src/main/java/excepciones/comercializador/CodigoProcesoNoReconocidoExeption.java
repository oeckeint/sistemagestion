package excepciones.comercializador;

public class CodigoProcesoNoReconocidoExeption extends Exception {

    public CodigoProcesoNoReconocidoExeption(String codigoProceso) {
        super("el código del proceso " + codigoProceso + " no esta soportado.");
    }
}
