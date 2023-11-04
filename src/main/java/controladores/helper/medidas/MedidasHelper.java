package controladores.helper.medidas;

import excepciones.MedidaTipoNoReconocido;
import excepciones.medidas.NombreArchivoContieneEspacios;
import excepciones.medidas.NombreArchivoElementosTamanoDiferente;
import excepciones.medidas.NombreArchivoSinExtension;
import excepciones.medidas.NombreArchivoTamanoDiferente;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MedidasHelper {

    public TIPO_MEDIDA definirTipoMedida(String nombreArchivo) throws MedidaTipoNoReconocido, NombreArchivoTamanoDiferente, NombreArchivoContieneEspacios, NombreArchivoSinExtension, NombreArchivoElementosTamanoDiferente {
        TIPO_MEDIDA t = TIPO_MEDIDA.DESCONOCIDO;
        ArrayList<String> datos = this.extraerDatosNombreArchivoMedida(nombreArchivo);

        String inicioArchivo = datos.get(0).trim().substring(0, 2).toLowerCase();
        switch (inicioArchivo){
            case "f5":
                t = TIPO_MEDIDA.F5;
                break;
            case "p5":
                t = TIPO_MEDIDA.P5;
                break;
            case "p2":
                t = TIPO_MEDIDA.P2;
                break;
            default:
                throw new MedidaTipoNoReconocido();
        }

        return t;
    }

    private ArrayList<String> extraerDatosNombreArchivoMedida(String nombreArchivo) throws NombreArchivoTamanoDiferente, NombreArchivoContieneEspacios, NombreArchivoSinExtension, NombreArchivoElementosTamanoDiferente {

        ArrayList<String> datos = null;

        if (validarNombreArchivoMedida(nombreArchivo)){
            //Se dividen todos los datos separados por guiones y se agregan a un arrayList, se esperan 4 datos, el ultimo incluye la extensión
            datos = new ArrayList<>(Arrays.asList(nombreArchivo.split("_")));

            //Valida que la extensión este dentro de los rangos de 0 a 9 y solo aplica para los archivos de medidas, despues separa el ultimo dato y la extensión
            String[] elementosDivididos = datos.get(datos.size() - 1).split("\\.");
            if (elementosDivididos.length == 2){
                String extension = elementosDivididos [1];
                if (extension.length() == 1 && Character.isDigit(extension.charAt(0))){
                    int numero = Integer.parseInt(extension);
                    if (numero >= 0 && numero <= 9) {
                        datos.set(datos.size() - 1, elementosDivididos[0]);
                        datos.add(extension);
                    } else {
                        System.out.println("No esta dentro del rango de extensiones de medidas");
                    }
                } else {
                    System.out.println("No es una extensión con numero");
                }
            } else {
                throw new NombreArchivoSinExtension();
            }
        }

        if (datos.size() != 5) throw new NombreArchivoElementosTamanoDiferente();

        return datos;
    }

    private boolean validarNombreArchivoMedida(String nombreArchivo) throws NombreArchivoTamanoDiferente, NombreArchivoContieneEspacios {
        //Valida que no contenga espacios
        if (nombreArchivo.contains(" ")){
            throw new NombreArchivoContieneEspacios();
        }

        //Se valida que el tamaño del nombre del archvivo sea exactamente de 24
        if (nombreArchivo.length() != 24){
            throw new NombreArchivoTamanoDiferente();
        }

        return true;
    }

    public enum TIPO_MEDIDA{
        F5, DESCONOCIDO, P5, P2
    }

}
