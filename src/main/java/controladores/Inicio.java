 
package controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class Inicio{

    ModelAndView mv;

    @RequestMapping("")
    public ModelAndView inicio(){
        this.mv = new ModelAndView("inicio");
        String icono = "<i class='fas fa-home'></i>";
        String titulo = "Sistema de gestión";
        this.mv.addObject("tituloPagina", titulo);
        this.mv.addObject("titulo", icono + " " + titulo);
        return this.mv;
    }
    
}
