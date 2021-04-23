package utileria;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArchivoTexto {

    public static void escribirErrores(String contenido) {
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
            System.out.println("(ArchivoTexto). ha ocurrido un error, no se pudo crear el archivo de errores");
        }
    }
    
    private static String momentoActual(){
        return new SimpleDateFormat("<dd/MM/yyyy HH:mm:ss> ").format(new Date());
    }
}
