package controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Login {
    
    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }
    
    @GetMapping("/accesodenegado")
    public String showAccessDeniedPage(){
        return "accessodenegado";
    }
    
}
