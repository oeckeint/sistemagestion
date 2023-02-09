package utileria;

import org.w3c.dom.Document;

import java.util.logging.Level;
import java.util.logging.Logger;

public class xml {

    private static final Logger logger = Logger.getLogger("xml");
    
    /**
     * Obtiene el contenido de un nodo en un String
     *
     * @param nodo
     * @return
     */
    public static String obtenerContenidoNodo(String nodo, Document doc) {
        String resultado = null;
        try{
            resultado = doc.getElementsByTagName(nodo).item(0).getTextContent();
        } catch (NullPointerException e){
            logger.log(Level.INFO, "No se encontro un nodo con el nombre {0}", nodo);
        }
        return resultado;
    }

    public static boolean existeNodo(String nodo, Document doc){
        return obtenerContenidoNodo(nodo, doc) != null;
    }

}
