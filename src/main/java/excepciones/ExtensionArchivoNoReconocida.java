package excepciones;

public class ExtensionArchivoNoReconocida extends Exception{

    public ExtensionArchivoNoReconocida(){
       super("la extensión del archivo no es valida para ser procesada por la aplicación, asegurese de estar subiendo los archivos correctos.");
    }

}
