package excepciones.comercializador;

import controladores.helper.Etiquetas;
import excepciones.medidas.NombreArchivoContieneEspacios;

public class NombreArchivoContieneEspaciosComercializador extends Exception{

    public NombreArchivoContieneEspaciosComercializador(){
        super("el archivo contiene espacios en el nombre, debe seguir alguno de esos patrones" + Etiquetas.COMERCIALIZADOR_PATRON_NOMBRE_ARCHIVO);
    }
}
