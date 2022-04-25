package excepciones;

public class TablaBusquedaNoExisteException  extends Exception{
    
    private static final long serialVersionUID = 1L;

	public TablaBusquedaNoExisteException(String nombreTabla){
        super("la tabla indicada para la busqueda <Strong>\"" + nombreTabla + "\" no esta soportada</Strong> para procesar <Strong>pagosRemesa y/o archivarFacturas</Strong> por favor revise que este bien escrita");
    }
    
}
