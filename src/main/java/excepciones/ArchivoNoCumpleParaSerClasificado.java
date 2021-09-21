package excepciones;

public class ArchivoNoCumpleParaSerClasificado extends Exception{

    public ArchivoNoCumpleParaSerClasificado() {
        super("el archivo <Strong>no cumple con los nodos necesarios de indentificación</Strong>.");
    }

}
