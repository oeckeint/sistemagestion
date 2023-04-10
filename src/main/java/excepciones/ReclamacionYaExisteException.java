package excepciones;

public class ReclamacionYaExisteException extends Exception{

    public ReclamacionYaExisteException(String codigoSolicitud){
        super("la reclamacion (<Strong>"
                + "<a href=\"javascript:window.open('/sistemagestion/clientes/reclamaciones/detalles?valor=" + codigoSolicitud + "&filtro=codigoSolicitud', 'ventana1', 'width=1300,height=700')\">"
                + codigoSolicitud
                + "</a></Strong>) ya ha sido registrado.");
    }

}
