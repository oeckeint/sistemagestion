package controladores;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/utilidades")
public class utilidades {

    @GetMapping("/respaldardb")
    public String respaldarDB(Model model) throws IOException {
        System.out.println("Respaldando DB");
        
        Runtime runTime = Runtime.getRuntime();
        //Process runtimeProcess = runTime.exec("C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump " + "sge -h localhost -u root -pmysql -r " + "C:\\Peajes\\sge" + "" + ".sql");
        
        Process runtimeProcess = runTime.exec("C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump "
                + " -u " + "root" + " -p" + "admin" + " --add-drop-database -B " + "sge" + " -r " + "C:\\Peajes\\");

        return "redirect:/";
    }
}
