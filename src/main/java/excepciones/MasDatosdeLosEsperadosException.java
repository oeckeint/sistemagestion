package excepciones;

public class MasDatosdeLosEsperadosException extends Exception{
    
    public MasDatosdeLosEsperadosException(int datosEsperados, String nodo){
        super("la solicitud <Strong>excede los datos</Strong> que se esperaban como máximo (" + datosEsperados + " datos) <Strong>en el nodo de (" + nodo + ").<Strong/>");
    }

}
