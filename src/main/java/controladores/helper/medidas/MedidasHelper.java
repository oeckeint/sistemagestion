package controladores.helper.medidas;

import excepciones.MedidaTipoNoReconocido;
import excepciones.medidas.NombreArchivoContieneEspacios;
import excepciones.medidas.NombreArchivoElementosTamanoDiferente;
import excepciones.medidas.NombreArchivoSinExtension;
import excepciones.medidas.NombreArchivoTamanoDiferente;
import org.springframework.stereotype.Component;
import utileria.ArchivoTexto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MedidasHelper {

    private final Logger logger = Logger.getLogger(getClass().getName());
    public final Queue<String> errores = new ConcurrentLinkedQueue<>();

    public TIPO_MEDIDA definirTipoMedida(String nombreArchivo) throws MedidaTipoNoReconocido, NombreArchivoTamanoDiferente, NombreArchivoContieneEspacios, NombreArchivoSinExtension, NombreArchivoElementosTamanoDiferente {
        TIPO_MEDIDA t = TIPO_MEDIDA.DESCONOCIDO;
        ArrayList<String> datos = this.extraerDatosNombreArchivoMedida(nombreArchivo);

        //Se podría agregar una validación de
        String inicioArchivo = datos.get(0).trim().substring(0, 2).toLowerCase();

        switch (inicioArchivo){
            case "f5":
                t = TIPO_MEDIDA.F5;
                break;
            case "p5":
                t = TIPO_MEDIDA.P5;
                break;
            case "p1":
                t = TIPO_MEDIDA.P1;
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

    /* Utilidades para MedidaH */

    protected Date parsearFecha(String fecha) {
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    protected void manejarClienteNoEncontrado(String cups, String nombreArchivo, int lineaActual) {
        String mensaje = "No se encontró un cliente con el cups " + cups + " en el archivo " + nombreArchivo + " en la línea " + lineaActual;
        logger.log(Level.SEVERE, ">>> " + mensaje);
        errores.add(mensaje);
        ArchivoTexto.escribirError(mensaje);
    }

    protected void manejarMasDeUnClienteEncontrado(String cups, String nombreArchivo, int lineaActual) {
        String mensaje = "Se encontró más de un cliente con el cups " + cups + " en la línea " + lineaActual + " del archivo " + nombreArchivo;
        logger.log(Level.SEVERE, ">>> " + mensaje);
        errores.add(mensaje);
        ArchivoTexto.escribirError(mensaje);
    }

    protected void manejarErrorDesconocido(Exception e, String nombreArchivo, int lineaActual) {
        String mensaje = "Error " + e.getMessage() + " " + nombreArchivo + " en la línea " + lineaActual;
        logger.log(Level.SEVERE, ">>> " + mensaje);
        errores.add(mensaje);
        e.printStackTrace(System.out);
        ArchivoTexto.escribirError(mensaje);
    }

    protected void manejarDatosInvalidos(String[] elementos, String nombreArchivo, int lineaActual, int cantidadEsperada) {
        String mensaje = "La entrada " + Arrays.toString(elementos) + " en la línea " + lineaActual + " del archivo " + nombreArchivo + " no coincide con la cantidad de datos esperados (" + cantidadEsperada + ").";
        logger.log(Level.SEVERE, ">>> " + mensaje);
        errores.add(mensaje);
        ArchivoTexto.escribirError(mensaje);
    }

    protected void manejarErrorDeConversion(String elemento, String nombreArchivo, int lineaActual) {
        String mensaje = "No se pudo convertir el elemento " + elemento + " en la línea " + lineaActual + " del archivo " + nombreArchivo;
        logger.log(Level.SEVERE, ">>> " + mensaje);
        errores.add(mensaje);
        ArchivoTexto.escribirError(mensaje);

    }

    public void addError(String error) {
        errores.add(error);
    }

    public void clearErrores() {
        errores.clear();
    }

    public enum TIPO_MEDIDA{
        F5, P5, P1, P2, DESCONOCIDO
    }

}
