package core.controller.advice;

import excepciones.MasDeUnClienteEncontrado;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utileria.ArchivoTexto;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MasDeUnClienteEncontrado.class)
    public String handleDuplicateCustomer(MasDeUnClienteEncontrado ex, RedirectAttributes ra) {
        // Usamos flash attributes para que el mensaje sobreviva al redirect y se vea en el JSP
        ArchivoTexto.escribirError(ex.getMessage());
        ra.addFlashAttribute("errorArchivo", ex.getMessage());
        return "redirect:/procesar";
    }

    // Manejo para cuando NO se encuentra un cliente (limpiando el CustomerService)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex, RedirectAttributes ra) {
        ArchivoTexto.escribirError(ex.getMessage());
        ra.addFlashAttribute("mensaje", ex.getMessage());
        return "redirect:/clientes";
    }

    @ExceptionHandler(IOException.class) // Captura FileNotFoundException y otros errores de lectura
    public String handleFileIOError(IOException ex, RedirectAttributes ra) {
        String mensaje = "Error crítico de acceso al archivo: " + ex.getMessage();
        ArchivoTexto.escribirError(mensaje);
        ra.addFlashAttribute("errorArchivo", mensaje);
        return "redirect:/procesar";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericError(Exception ex, Model model) {
        ArchivoTexto.escribirError(ex.getMessage());
        model.addAttribute("error", "Ha ocurrido un error inesperado: " + ex.getMessage());
        return "comunes/error_general";
    }

}
