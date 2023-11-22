package excepciones.comercializador;

import controladores.helper.Etiquetas;

public class NombreArchivoTamanoDiferenteComercializador extends Exception{
    public NombreArchivoTamanoDiferenteComercializador(){
        super ("el archivo no contiene exactamente 65 carácteres en su nombre, debe seguir alguno de estos patrones" + Etiquetas.COMERCIALIZADOR_PATRON_NOMBRE_ARCHIVO);
    }
}
