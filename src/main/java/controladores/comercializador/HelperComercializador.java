package controladores.comercializador;

import excepciones.comercializador.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HelperComercializador {

    /**
     * Define el tipo de archivo C que ha de ser procesado
     * @param nombreArchivo
     * @param datosAdicionales
     * @return
     * @throws NombreArchivoTamanoDiferenteComercializador
     * @throws NombreArchivoContieneEspaciosComercializador
     * @throws NombreArchivoElementosTamanoDiferenteComercializador
     * @throws ComercializadorNoReconocido
     * @throws NoCoincidenLosElementosInicialesComercializadorException
     * @throws CodigoProcesoNoReconocidoExeption
     * @throws CodigoPasoNoReconocidoExeption
     * @throws SubtipoCodigoPasoNoReconocidoException
     */
    public TIPO_COMERCIALIZADOR definirTipoMedida(String nombreArchivo, Map<String, Boolean> datosAdicionales) throws NombreArchivoTamanoDiferenteComercializador, NombreArchivoContieneEspaciosComercializador, NombreArchivoElementosTamanoDiferenteComercializador, ComercializadorNoReconocido, NoCoincidenLosElementosInicialesComercializadorException, CodigoProcesoNoReconocidoExeption, CodigoPasoNoReconocidoExeption, SubtipoCodigoPasoNoReconocidoException {
        TIPO_COMERCIALIZADOR tc;
        ArrayList<String> datos = this.extraerDatosNombreArchivoMedida(nombreArchivo);

        String codigoProceso = datos.get(4).toLowerCase();
        String codigoPaso = datos.get(5).toLowerCase();

        switch (codigoProceso){
            case "c1":
                switch (codigoPaso){
                    case "01":
                        tc = TIPO_COMERCIALIZADOR.C1_01;
                        break;
                    case "02":
                        if (datosAdicionales.get("aceptacion") != null && datosAdicionales.get("aceptacion")) {
                            tc = TIPO_COMERCIALIZADOR.C1_02_ACEPTACION;
                        } else if (datosAdicionales.get("rechazo") != null && datosAdicionales.get("rechazo")) {
                            tc = TIPO_COMERCIALIZADOR.C1_02_RECHAZO;
                        } else {
                            throw new SubtipoCodigoPasoNoReconocidoException(codigoPaso, codigoProceso);
                        }
                        break;
                    case "05":
                        tc = TIPO_COMERCIALIZADOR.C1_05;
                        break;
                    case "06":
                        tc = TIPO_COMERCIALIZADOR.C1_06;
                        break;
                    case "11":
                        tc = TIPO_COMERCIALIZADOR.C1_11;
                        break;
                    default:
                        throw new CodigoPasoNoReconocidoExeption(codigoPaso, codigoProceso);
                }
                break;
            case "c2":
                switch (codigoPaso){
                    case "01":
                        tc = TIPO_COMERCIALIZADOR.C2_01;
                        break;
                    case "02":
                        if (datosAdicionales.get("aceptacion") != null && datosAdicionales.get("aceptacion")) {
                            tc = TIPO_COMERCIALIZADOR.C1_02_ACEPTACION;
                        } else if (datosAdicionales.get("rechazo") != null && datosAdicionales.get("rechazo")) {
                            tc = TIPO_COMERCIALIZADOR.C1_02_RECHAZO;
                        } else {
                            throw new SubtipoCodigoPasoNoReconocidoException(codigoPaso, codigoProceso);
                        }
                        break;
                    case "05":
                        tc = TIPO_COMERCIALIZADOR.C2_05;
                        break;
                    case "06":
                        tc = TIPO_COMERCIALIZADOR.C2_06;
                        break;
                    case "11":
                        tc = TIPO_COMERCIALIZADOR.C2_11;
                        break;
                    default:
                        throw new CodigoPasoNoReconocidoExeption(codigoPaso, codigoProceso);
                }
                break;
            default:
                throw new CodigoProcesoNoReconocidoExeption(codigoProceso);
        }
        return tc;
    }

    /**
     * El metodo recibe el nombre del archivo desde donde extraera los datos individuales incrustados en el nombre del archivos
     * @param nombreArchivo
     * @return un ArrayList en donde estan incluidos los 9 datos extraidos desde el nombre del archivo
     * @throws NombreArchivoTamanoDiferenteComercializador
     * @throws NombreArchivoContieneEspaciosComercializador
     * @throws NombreArchivoElementosTamanoDiferenteComercializador
     * @throws NoCoincidenLosElementosInicialesComercializadorException
     */
    private ArrayList<String> extraerDatosNombreArchivoMedida(String nombreArchivo) throws NombreArchivoTamanoDiferenteComercializador, NombreArchivoContieneEspaciosComercializador, NombreArchivoElementosTamanoDiferenteComercializador, NoCoincidenLosElementosInicialesComercializadorException {
        ArrayList<String> datos = null;
        ArrayList<String> elementos = null;

        if (validarNombreArchivoMedida(nombreArchivo)){
            datos = new ArrayList<>(Arrays.asList(nombreArchivo.split("_")));
            if (datos.size() != 7) throw new NoCoincidenLosElementosInicialesComercializadorException();
            elementos = new ArrayList<>();
            String [] elementosDivididos = null;

            //Obtiene el primer dato del arreglo y lo divide en 2 separados por '-'
            elementosDivididos = datos.get(0).split("-");
            if (elementosDivididos.length == 2){
                elementos.add(elementosDivididos[0]);
                elementos.add(elementosDivididos[1]);
                datos.remove(0);
                elementos.addAll(datos);
            }

            //Obtiene el ultimo dato que contiene un numero y la extensión del archivo y los divide separados por '.'
            elementosDivididos = elementos.get(elementos.size() - 1).split("\\.");
            if (elementosDivididos.length == 2){
                elementos.remove(elementos.size() - 1);
                elementos.add(elementosDivididos[0]);
                elementos.add(elementosDivididos[1]);
            }
        }

        if (elementos.size() != 9) throw new NombreArchivoElementosTamanoDiferenteComercializador();
        return elementos;
    }

    /**
     * El metodo valida que el archivo no contenga espacios y que coincida con exactamente 65 caracteres de lo contrario arroja excepcion y el archivo no es válido
     *
     * @param nombreArchivo
     * @return true en caso de que el nombre de archivo se haya terminado correctamente
     * @throws NombreArchivoContieneEspaciosComercializador
     * @throws NombreArchivoTamanoDiferenteComercializador
     */
    private boolean validarNombreArchivoMedida(String nombreArchivo) throws NombreArchivoContieneEspaciosComercializador, NombreArchivoTamanoDiferenteComercializador{
        if (nombreArchivo.contains(" ")) throw new NombreArchivoContieneEspaciosComercializador();
        if (nombreArchivo.length() != 65) throw new NombreArchivoTamanoDiferenteComercializador();
        return true;
    }

    public enum TIPO_COMERCIALIZADOR {
        C1_01, C1_02_ACEPTACION, C1_02_RECHAZO, C1_05, C1_06, C1_11,
        C2_01, C2_02_ACEPTACION, C2_02_RECHAZO, C2_05, C2_06, C2_11,
        DESCONOCIDO
    }

}
