package excepciones;

public class ReclamacionYaExisteException extends Exception{

    public ReclamacionYaExisteException(String codigoSolicitud, int codigoDePaso){
        super("la reclamacion (<Strong>"
                + "<a href=\"javascript:window.open('/sistemagestion/clientes/reclamaciones/detalles?valor=" + codigoSolicitud + "-" + codigoDePaso + "&filtro=codigoSolicitud', 'ventana1', 'width=1300,height=700')\">"
                + codigoSolicitud
                + "</a></Strong>)" + " con el codigo de paso " + "(<Strong>"
                + codigoDePaso
                + "</Strong>)"+ " ya ha sido registrado.");
    }

}
