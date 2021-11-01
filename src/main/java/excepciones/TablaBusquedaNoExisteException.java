package excepciones;

public class TablaBusquedaNoExisteException  extends Exception{
    
    public TablaBusquedaNoExisteException(String nombreTabla){
        super("la tabla indicada para la busqueda <Strong>\"" + nombreTabla + "\" no esta soportada</Strong> para procesar los pagos de remesa.");
    }
    
}
