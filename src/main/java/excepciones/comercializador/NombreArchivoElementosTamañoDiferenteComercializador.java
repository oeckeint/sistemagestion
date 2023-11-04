package excepciones.comercializador;

import controladores.helper.Etiquetas;

public class NombreArchivoElementosTamañoDiferenteComercializador extends Exception{

    public NombreArchivoElementosTamañoDiferenteComercializador(){
        super("el nombre del archivo no tiene los 9 elementos necesarios para su identificacion, debe seguir alguno de estos patrones" + Etiquetas.COMERCIALIZADOR_PATRON_NOMBRE_ARCHIVO);
    }
}
