package excepciones;

public class PeajeYaExisteException extends Exception{
    public PeajeYaExisteException(String codigoFiscalFactura) {
        super("el peaje (<Strong>"
                + "<a href=\"javascript:window.open('/sistemagestion/peajes/detalles?codFisFac="+ codigoFiscalFactura + "', 'ventana1', 'width=1300,height=700')\">"
                + codigoFiscalFactura
                + "</a></Strong>) ya ha sido registrado.");
    }
}
