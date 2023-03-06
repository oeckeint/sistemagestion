package controladores.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.w3c.dom.Document;
import utileria.ArchivoTexto;

public class Utilidades {
	
	private static Logger logger = Logger.getLogger("Utilidades");

    public static int revisarPaginaActual(int paginaActual){
        if (paginaActual < 1) {
            paginaActual = 1;
        }
        return paginaActual;
    }

    public static int revisarRangoRows(int rows, int rango) {
        if (rows % rango != 0) {
            if (rows <= rango) {
                rows = rango;
            } else if (rows >= (rango * 6)) {
                rows = rango * 6;
            } else {
                rows = rango * 2;
            }
        } else {
            if (rows <= rango) {
                rows = rango;
            } else if (rows >= (rango * 6)) {
                rows = rango * 6;
            }
        }
        return rows;
    }
    /**
     * Crea un nuevo archivo
     * 
     * @param nombreNuevoArchivo nombre que tendra en nuevo archivo
     * @param rutaArchivoOriginal path y nombre del archivo original del que se extraerá la información
     * @param pathNuevoArchivo carpetas en donde estará guardado el nuevo archivo
     * @return
     */
	public static boolean crearArchivo(String nombreNuevoArchivo, String rutaArchivoOriginal, String pathNuevoArchivo) {
    	boolean generado = false;
    	String pathArchivoNuevo = pathNuevoArchivo + "/" + nombreNuevoArchivo;
    	creacionDirectorios(pathNuevoArchivo);
    	File f = new File(pathArchivoNuevo);
        FileWriter fichero = null;
        PrintWriter pw;
        try {
            f.createNewFile();
            fichero = new FileWriter(pathArchivoNuevo);
            pw = new PrintWriter(fichero);

            //---------------------Escritura en el archivo-------------------------
            File archivo = null;
            FileReader fr = null;
            BufferedReader br = null;

            try {
                // Apertura del fichero y creacion de BufferedReader para poder
                // hacer una lectura comoda (disponer del metodo readLine()).
                archivo = new File(rutaArchivoOriginal);
                fr = new FileReader(archivo);
                br = new BufferedReader(fr);

                // Lectura del fichero
                String linea;
                while ((linea = br.readLine()) != null) {
                    pw.println(linea);
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            } finally {
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try {
                    if (null != fr) {
                        fr.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace(System.out);
                }
            }
            //---------------------Fin de escritura en el archivo-------------------------
            logger.log(Level.INFO, ">>> Archivo generado en la ruta {0}", pathArchivoNuevo);
            generado = true;
        } catch (IOException e) {
        	e.printStackTrace(System.out);
        } finally {
            try {
                // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (IOException e2) {

            }
        }        
    	return generado;
    }
    
    public static void creacionDirectorios(String fullPath) {
        try {
            String fileName = fullPath;
            Path pathDB = Paths.get(fileName);
            if (!Files.exists(pathDB)) {
                Files.createDirectories(pathDB);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String currentUser() {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return (principal instanceof UserDetails) ? ((UserDetails)principal).getUsername() : principal.toString(); 
    }
    
    /**
     * Revisa si existe un nodo especificado
     *
     * @param nodo
     * @return
     */
    public static boolean existeNodo(Document documento, String nodo) {
        try {
            return documento.getElementsByTagName(nodo).getLength() != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Agrega dias a una fecha de referencia
     * @param dateRef Fecha de referencia a la que se le agregaran los días
     * @param numeroDeDias Numero de dias que serán agregados a la fecha de referencia
     * @return retorna la fecha de refencia con la operacion realizada
     * @throws ParseException 
     */
    public static Date agregarDias(Date dateRef, int numeroDeDias) {
    	dateRef = formatearDate(dateRef, 1);
    	LocalDateTime today = LocalDateTime.ofInstant(dateRef.toInstant(), ZoneId.systemDefault());
    	return Date.from(today.plusDays(numeroDeDias).atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Reformatea un date extraido de la base de datos a un tipo date 
     * @param date String that is going to be assigned to the Date
     * @param format int that selects the type of format 1 = yyyy-MM-dd
     * @return formatted date
     * @throws ParseException 
     */
    public static Date formatearDate(Date date, int format) {
    	SimpleDateFormat formatter = null;
    	switch (format) {
			case 1:
				formatter = new SimpleDateFormat("yyyy-MM-dd");
				break;
		}
    	String strDate = formatter.format(date);
    	try {
			date = formatter.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return date;
    }

    public static double valorAbsoluto(double valor){
        return valor > 0 ? valor : -valor;
    }

    public static double valorAbsolutoNegativo(double valor){
        return valor > 0 ? -valor : valor;
    }

    public static int valorAbsolutoNegativo(int valor){
        return valor > 0 ? -valor : valor;
    }

    /**
     * Extrae cada linea de un archivo txt y las retorna en un arreglo del tipo String
     * Es necesario especificar el Archivo del que se extraeran las lineas y el separador que se ocupara en caso de que exista entre lineas
     * @param file
     * @param separador
     * @return
     */
    public static String[][] extraerLineasTxt(File file, String separador, int datosEsperadosPorLinea, String nombreArchivo){
        String elementosTotales [][] = new String[0][datosEsperadosPorLinea];
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader (file);
            br = new BufferedReader(fr);

            String linea;
            String elementosLinea [];
            String elementosAux[][];
            int lineaActual = 1;

            while((linea = br.readLine()) != null){
                elementosLinea = linea.split(separador);

                if (elementosLinea.length == datosEsperadosPorLinea) {
                    elementosAux = elementosTotales;
                    elementosTotales = new String[elementosTotales.length + 1][datosEsperadosPorLinea];
                    for(int i = 0 ; i < elementosAux.length; i++){
                        elementosTotales[i] = elementosAux[i];
                    }
                    elementosTotales[elementosTotales.length-1] = elementosLinea;
                } else {
                    logger.log(Level.INFO, ">>> No tiene suficientes datos especificados {0} en archivo {1} en la linea {2}", new Object[]{ Arrays.toString(elementosLinea), nombreArchivo, lineaActual});
                    ArchivoTexto.escribirAdvertencia(nombreArchivo, "No tiene suficientes datos especificados " +  Arrays.toString(elementosLinea) + " en la linea " + lineaActual);
                }
                lineaActual++;
            }
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (Exception e2) {e2.printStackTrace();}
        }
        return elementosTotales;
    }

    public static EXTENSION_ARCHIVO definirExtensionArchivo(String nombreArchivo){
            switch (nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1)){
                case "xml":
                    return EXTENSION_ARCHIVO.XML;
                case "0":
                    return EXTENSION_ARCHIVO.MEDIDAS;
                default:
                    return EXTENSION_ARCHIVO.DESCONOCIDO;
            }
    }

    public enum EXTENSION_ARCHIVO{
        XML, MEDIDAS, DESCONOCIDO;
    }

}
