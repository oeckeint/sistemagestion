
package controladores;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class Inicio extends HttpServlet{
    
    @RequestMapping("")
    public String inicio(Model model){
        String icono = "<i class='fas fa-home'></i>";
        String titulo = "Sistema de gestión";
        model.addAttribute("tituloPagina", titulo);
        model.addAttribute("titulo", icono + " " + titulo);
        return "inicio";
    }
    
}
