package excepciones.nodos.energiaexcedentaria.autoconsumo;


public class ExisteMasDeUnAutoconsumoException extends Exception {

    public ExisteMasDeUnAutoconsumoException(String nombreArchivo) {
        super("Existe mas de nodo de autoconsumo en el archivo " + nombreArchivo);
    }

}
