package excepciones.comercializador;

import controladores.helper.Etiquetas;

public class NombreArchivoElementosTamanoDiferenteComercializador extends Exception{

    public NombreArchivoElementosTamanoDiferenteComercializador(){
        super("el nombre del archivo no tiene los 9 elementos necesarios para su identificacion, debe seguir alguno de estos patrones" + Etiquetas.COMERCIALIZADOR_PATRON_NOMBRE_ARCHIVO);
    }
}
