package excepciones.medidas;

import controladores.helper.Etiquetas;

public class NombreArchivoTamanoDiferente extends Exception{

    public NombreArchivoTamanoDiferente(){
        super("el archivo no tiene exactamente 24 caracteres por nombre, debe seguir alguno de estos patrones" + Etiquetas.MEDIDAS_PATRON_NOMBRE_ARCHIVO + "<br/>Recuerde que los archivos solo pueden tener la extensión del 0 al 9.");
    }

}
