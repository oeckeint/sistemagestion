package utileria;

import org.w3c.dom.Document;

public class xml {
    
    /**
     * Obtiene el contenido de un nodo en un String
     *
     * @param nodo
     * @return
     */
    public static String obtenerContenidoNodo(String nodo, Document doc) {
        return doc.getElementsByTagName(nodo).item(0).getTextContent();
    }
}
