package excepciones.medidas;

public class NombreArchivoSinExtension extends Exception{

    public NombreArchivoSinExtension(){
        super("no se pudo extrar la extensión del archivo, reccuerde que solo pueden tener la extensión del 0 al 9.\");");
    }

}
