package utileria;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArchivoTexto {

    public static void escribirError(String contenido) {
        File file = new File("C:/Peajes/Procesados/errores.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, true);

            try (BufferedWriter bw = new BufferedWriter(fw)) {
                String formateoContenido = contenido.replaceAll("<Strong>", "").replaceAll("</Strong>", "");
                if (file.length() == 0) {
                    bw.write(momentoActual() + formateoContenido);
                } else {
                    bw.write("\n" + momentoActual() + formateoContenido);
                }
            }

        } catch (IOException e) {
            System.out.println("(ArchivoTexto.escribirError). ha ocurrido un error, no se pudo escribir en el archivo de errores");
        }
    }

    public static void escribirAdvertencia(String nombreArchivo, String codAdvertencia) {
        File file = new File("C:/Peajes/Procesados/advertencias.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, true);

            try (BufferedWriter bw = new BufferedWriter(fw)) {
                if (file.length() == 0) {
                    bw.write(momentoActual() + identificarAdvertencia(codAdvertencia) + " en el archivo " + nombreArchivo);
                } else {
                    bw.write("\n" + momentoActual() + identificarAdvertencia(codAdvertencia) + " en el archivo " + nombreArchivo);
                }
            }

        } catch (IOException e) {
            System.out.println("(ArchivoTexto.escribirAdvertencia). ha ocurrido un error, no se pudo escribir en el archivo de advertencias");
        }
    }

    //Utilileria
    private static String momentoActual() {
        return new SimpleDateFormat("<dd/MM/yyyy HH:mm:ss> ").format(new Date());
    }

    private static String identificarAdvertencia(String cod) {

        StringBuilder advertencia = new StringBuilder("¡ADVERTENCIA! (" + cod + ") - ");

        switch (cod) {
            case "1":
                advertencia.append("Se encontraron más de 12 valores de <PotenciaContratada>");
                break;
            case "2":
                advertencia.append("Se encontraron más de 12 valores de <PotenciaMaxDemandada>");
                break;
            case "3":
                advertencia.append("Se encontraron más de 6 valores de <PotenciaAFacturar>");
                break;
            case "4":
                advertencia.append("Se encontraron más de 12 valores de <PrecioPotencia>");
                break;
            case "5":
                advertencia.append("Se encontró más de 1 valor de <ImporteTotalTerminoPotencia>");
                break;
            case "6":
                advertencia.append("Se encontradon más de 2 valores de <FechaDesde> y <FechaHasta>");
                break;
            case "7":
                advertencia.append("Se encontraron más de 12 valores de <ValorEnergiaActiva>");
                break;
            case "8":
                advertencia.append("Se encontraron más de 12 valores de <PrecioEnergia>");
                break;
            case "9":
                advertencia.append("Se encontró más de 1 valor de <ImporteTotalEnergiaActiva>");
                break;
            case "10":
                advertencia.append("Se encontró más de 1 valor de <Importe> del nodo de <ImpuestoElectrico>");
                break;
            case "11":
                advertencia.append("Se encontró más de 1 valor de <ImporteFacturacionAlquileres>");
                break;
            case "12":
                advertencia.append("Se encontró más de 1 valor de <BaseImponible>");
                break;
            case "13":
                advertencia.append("Se encontraron más de 12 valores de <ConsumoCalculado> de AE");
                break;
            case "14":
                advertencia.append("Se encontraron más de 12 valores de <Lectura> en <LecturaDesde> de AE");
                break;
            case "15":
                advertencia.append("Se encontraron más de 12 valores de <Lectura> en <LecturaHasta> de AE");
                break;
            case "16":
                advertencia.append("Se encontraron más de 12 valores de <ConsumoCalculado> de R");
                break;
            case "17":
                advertencia.append("Se encontraron más de 12 valores de <Lectura> en <LecturaDesde> de R");
                break;
            case "18":
                advertencia.append("Se encontraron más de 12 valores de <Lectura> en <LecturaHasta> de R");
                break;
            case "19":
                advertencia.append("Se encontraron más de 12 valores de <ConsumoCalculado> de PM");
                break;
            case "20":
                advertencia.append("Se encontraron más de 12 valores de <Lectura> en <LecturaHasta> de PM");
                break;
            case "21":
                advertencia.append("No corresponde la tarifa del cliente con la obtenida (Uno o varios elementos podrían no haber sido leídos)");
                break;
            case "22":
                advertencia.append("No corresponde <ImporteTotalTerminoPotencia> con las obtenidas");
                break;
            case "23":
                advertencia.append("Se encontraron más de 6 cargos del tipo 01");
                break;
            case "24":
                advertencia.append("Se encontraron más de 6 cargos del tipo 02");
                break;
            case "25":
                advertencia.append("Se encontraró más de 1 TotalImporteTipoCargo en el tipo de cargo 01");
                break;
            case "26":
                advertencia.append("Se encontraró más de 1 TotalImporteTipoCargo en el tipo de cargo 02");
                break;
            case "27":
                advertencia.append("Se encontraró más de 6 ValorEnergiaExcedentaria");
                break;
            case "28":
                advertencia.append("Los valores de EnergiaExcedentaria no corresponden con el total");
                break;
            default:
                advertencia.append("No se reconoce la advertencia con el código, contacte con el adminstrador del sistema");
                break;
        }

        return advertencia.toString();
    }
}
