package excepciones.comercializador;

public class ComercializadorNoReconocido extends Exception{
    public ComercializadorNoReconocido(){
        super("el tipo de comercializador especificado en el archivo no es reconocido.");
    }
}
