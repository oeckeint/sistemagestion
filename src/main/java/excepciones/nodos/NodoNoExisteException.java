package excepciones.nodos;

public class NodoNoExisteException extends Exception {

    public NodoNoExisteException(String nombreArchivo, String nodeName) {
        super("Error: No se existe el nodo " + nodeName + " en el archivo " + nombreArchivo + ".");
    }

    public NodoNoExisteException(String nodeName) {
        super("Error: No existe el nodo " + nodeName + ".");
    }

}
