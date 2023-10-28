package excepciones.medidas;

import controladores.helper.Etiquetas;

public class NombreArchivoContieneEspacios extends Exception{

    public NombreArchivoContieneEspacios(){
        super("el archivo contiene espacios en el nombre, debe seguir alguno de estos patrones" + Etiquetas.MEDIDAS_PATRON_NOMBRE_ARCHIVO + "<br/>Recuerde que los archivos solo pueden tener la extensión del 0 al 9.");
    }

}
