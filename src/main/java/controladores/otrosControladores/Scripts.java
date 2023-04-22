package controladores.otrosControladores;

import excepciones.ScriptSinTerminacionBatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/scripts")
public class Scripts {

    ModelAndView mv;
    @Value("${scripts.ruta}")
    String rutaScripts;

    @Value("${scripts.moverArchivos}")
    String moverArchivo;

    private Logger logger = Logger.getLogger(getClass().getName());

    @GetMapping("/moverarchivos")
    public ModelAndView moverArchivos(RedirectAttributes redirectAttributes){
        try {
            this.mv = new ModelAndView("redirect:/");
            redirectAttributes.addFlashAttribute("nombreScript", this.moverArchivo);
            if(!moverArchivo.endsWith(".bat")){
                Runtime.getRuntime().exec("cmd /c start cmd.exe /K \" cd " + rutaScripts + " && " + moverArchivo + " && exit");
                redirectAttributes.addFlashAttribute("mensaje", "scriptEjecutado");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "scriptNoValido");
                throw new ScriptSinTerminacionBatException(moverArchivo);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        } catch (ScriptSinTerminacionBatException e) {
            logger.log(Level.INFO, e.getMessage());
        }
        return this.mv;
    }
}
