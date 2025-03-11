package excepciones.nodos;

public class NoCoincidenLosNodosEsperadosException extends Exception {

    public NoCoincidenLosNodosEsperadosException(String nodo, int esperados, int encontrados) {
        super("Error: Se esperaban " + esperados + " nodos con el nombre '" + nodo + "' pero se encontraron " + encontrados + ".");
    }

}
