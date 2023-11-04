package controladores.comercializador;

import controladores.helper.medidas.MedidasHelper;
import excepciones.MedidaTipoNoReconocido;
import excepciones.comercializador.ComercializadorNoReconocido;
import excepciones.comercializador.NombreArchivoContieneEspaciosComercializador;
import excepciones.comercializador.NombreArchivoElementosTamañoDiferenteComercializador;
import excepciones.comercializador.NombreArchivoTamañoDiferenteComercializador;
import excepciones.medidas.NombreArchivoContieneEspacios;
import excepciones.medidas.NombreArchivoElementosTamanoDiferente;
import excepciones.medidas.NombreArchivoSinExtension;
import excepciones.medidas.NombreArchivoTamanoDiferente;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Component
public class HelperComercializador {

    public MedidasHelper.TIPO_MEDIDA definirTipoMedida(String nombreArchivo) throws NombreArchivoTamanoDiferente, NombreArchivoSinExtension, NombreArchivoContieneEspaciosComercializador, NombreArchivoTamañoDiferenteComercializador, NombreArchivoElementosTamañoDiferenteComercializador, ComercializadorNoReconocido {
        MedidasHelper.TIPO_MEDIDA t = MedidasHelper.TIPO_MEDIDA.DESCONOCIDO;
        ArrayList<String> datos = this.extraerDatosNombreArchivoMedida(nombreArchivo);

        String inicioArchivo = datos.get(0).trim().substring(0, 2).toLowerCase();
        switch (inicioArchivo){
            case "f5":
                t = MedidasHelper.TIPO_MEDIDA.F5;
                break;
            case "p5":
                t = MedidasHelper.TIPO_MEDIDA.P5;
                break;
            case "p2":
                t = MedidasHelper.TIPO_MEDIDA.P2;
                break;
            default:
                throw new ComercializadorNoReconocido();
        }

        return t;
    }
    private ArrayList<String> extraerDatosNombreArchivoMedida(String nombreArchivo) throws NombreArchivoTamanoDiferente, NombreArchivoContieneEspaciosComercializador, NombreArchivoSinExtension, NombreArchivoTamañoDiferenteComercializador, NombreArchivoElementosTamañoDiferenteComercializador {

        ArrayList<String> datos = null;
        ArrayList<String> elementos = new ArrayList<>();

        if (validarNombreArchivoMedida(nombreArchivo)){
            //Se dividen todos los datos separados por guiones y se agregan a un arrayList, se esperan 4 datos, el ultimo incluye la extensión
            datos = new ArrayList<>(Arrays.asList(nombreArchivo.split("_")));

            //Valida que la extensión este dentro de los rangos de 0 a 9 y solo aplica para los archivos de medidas, despues separa el ultimo dato y la extensión
            String[] elementosDivididos = datos.get(0).split("-");
            if (elementosDivididos.length == 2){

                elementos.add(elementosDivididos[0]);
                elementos.add(elementosDivididos[1]);

                datos.remove(0);
                elementos.addAll(datos);

                elementosDivididos = elementos.get(elementos.size() - 1).split("\\.");
                if (elementosDivididos.length == 2){
                    elementos.remove(elementos.size() - 1);
                    elementos.add(elementosDivididos[0]);
                    elementos.add(elementosDivididos[1]);
                }
            } else {
                throw new NombreArchivoSinExtension();
            }
        }
        System.out.println(elementos);
        if (elementos.size() != 9) throw new NombreArchivoElementosTamañoDiferenteComercializador();
        return elementos;
    }
    private boolean validarNombreArchivoMedida(String nombreArchivo) throws NombreArchivoContieneEspaciosComercializador, NombreArchivoTamañoDiferenteComercializador {
        //Valida que no contenga espacios
        if (nombreArchivo.contains(" ")){
            throw new NombreArchivoContieneEspaciosComercializador();
        }

        //Se valida que el tamaño del nombre del archvivo sea exactamente de 24
        if (nombreArchivo.length() != 65){
            throw new NombreArchivoTamañoDiferenteComercializador();
        }

        return true;
    }
}
