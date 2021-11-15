package controladores;

import controladores.helper.Etiquetas;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/configuraciones")
public class Configuraciones {

    @GetMapping("")
    public String inicio(Model model) {
        model.addAttribute("tituloPagina", Etiquetas.CONFIGURACIONES__TITULO_PAGINA);
        model.addAttribute("titulo", Etiquetas.CONFIGURACIONES__ENCABEZADO);
        model.addAttribute("mensaje", Etiquetas.CONFIGURACIONES__MENSAJE);
        this.creacionDirectorios();
        this.reiniciarVariables();
        return "comunes/utilidades";
    }

    @GetMapping("/respaldardb")
    public String respaldarDB(Model model) throws IOException {
        String rutaMySqlDump = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe";
        String rutaMySql = "C:\\Archivos de programa\\MySQL\\MySQL Server 5.0\\bin\\mysql.exe";
        String rutaRespaldoDefault = "C:\\Peajes\\Backups\\" + this.momentoActual() + ".sql";

        //Revisión de que exista el directorio de Backups de lo contrario lo creará
        Process p = Runtime.getRuntime().exec("C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump -u root -padmin sge");
        //Process p = Runtime.getRuntime().exec("mysqldump -u root -padmin sge");
        InputStream is = p.getInputStream();
        FileOutputStream fos = new FileOutputStream(rutaRespaldoDefault);

        byte[] buffer = new byte[1000];
        int leido = is.read(buffer);
        while (leido > 0) {
            fos.write(buffer, 0, leido);
            leido = is.read(buffer);
        }

        fos.close();

        Etiquetas.CONFIGURACIONES__MENSAJE = "Base de datos respaldada con éxito en " + rutaRespaldoDefault;
        return "redirect:/configuraciones";
    }

    @GetMapping("/restaurardb")
    public String formularioDB(Model model) {
        model.addAttribute("tituloPagina", Etiquetas.RESTAURAR_DB_FORMULARIO_TITULO_PAGINA);
        model.addAttribute("titulo", Etiquetas.RESTAURAR_DB_FORMULARIO_ENCABEZADO);
        model.addAttribute("mensajeRegistro", Etiquetas.RESTAURAR_DB_FORMULARIO_MENSAJE);
        model.addAttribute("etiquetaBoton", Etiquetas.RESTAURAR_DB_FORMULARIO_ETIQUETA_BOTON);
        model.addAttribute("controller", Etiquetas.RESTAURAR_DB_FORMULARIO_CONTROLLER);
        //this.reiniciarVariables();
        return "comunes/formulario_db";
    }

    @PostMapping("/procesar")
    public String restaurarDB(@RequestParam("archivosql") MultipartFile file, Model model) throws IOException {
        File f = new File("C:\\Peajes\\Backups\\HistorialDeRestauraciones\\" + file.getOriginalFilename());
        file.transferTo(f);
        Process p = Runtime.getRuntime().exec("C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql -u root -padmin sge");
        //Process p = Runtime.getRuntime().exec("mysql -u root -padmin sge");
        OutputStream os = p.getOutputStream();
        FileInputStream fis = new FileInputStream("C:\\Peajes\\Backups\\HistorialDeRestauraciones\\" + file.getOriginalFilename());

        byte[] buffer = new byte[1000];
        int leido = fis.read(buffer);
        while (leido > 0) {
            os.write(buffer, 0, leido);
            leido = fis.read(buffer);
        }

        os.flush();
        os.close();
        fis.close();

        Etiquetas.CONFIGURACIONES__MENSAJE = "Se ha restaurado la información de la base de datos desde el archivo " + file.getOriginalFilename();
        return "redirect:/configuraciones";
    }

    public String momentoActual() {
        return new SimpleDateFormat("ddMMyyyy_HH-mm-ss").format(new Date());
    }

    public void creacionDirectorios() {
        try {
            String fileName = "C:\\Peajes\\Backups\\HistorialDeRestauraciones";
            Path pathDB = Paths.get(fileName);
            if (!Files.exists(pathDB)) {
                Files.createDirectories(pathDB);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void reiniciarVariables(){
        Etiquetas.CONFIGURACIONES__MENSAJE = "Utilidades y configuraciones para el funcionamiento de la aplicación";
    }
}
