package excepciones.nodos;

public class NodoNoExisteException extends Exception {

    public NodoNoExisteException(String nombreArchivo, String nodeName) {
        super("Error: No se encontró el nodo " + nodeName + " en el archivo " + nombreArchivo + ".");
    }

}
