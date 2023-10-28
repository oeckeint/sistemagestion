package excepciones.medidas;

import controladores.helper.Etiquetas;

public class NombreArchivoElementosTamanoDiferente extends Exception{

    public NombreArchivoElementosTamanoDiferente(){
        super("el nombre del archivo no tiene los 5 elementos necesarios para su identificación, debe seguir alguno de estos patrones" + Etiquetas.MEDIDAS_PATRON_NOMBRE_ARCHIVO + "<br/>Recuerde que los archivos solo pueden tener la extensión del 0 al 9.");
    }

}
