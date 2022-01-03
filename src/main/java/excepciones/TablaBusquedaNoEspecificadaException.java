package excepciones;

public class TablaBusquedaNoEspecificadaException extends Exception{
    public TablaBusquedaNoEspecificadaException(){
        super("El nodo <TablaBusqueda> no tiene especificada una tabla");
    }
}
