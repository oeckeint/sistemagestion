package core.controller.advice;

import core.web.flash.FlashType;
import core.web.notifications.NotificationProcess;
import excepciones.MasDeUnClienteEncontrado;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utileria.ArchivoTexto;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final String redirect = "redirect:/";

    @ExceptionHandler(MasDeUnClienteEncontrado.class)
    public String handleDuplicateCustomer(@NonNull MasDeUnClienteEncontrado ex, @NonNull RedirectAttributes ra) {
        // Usamos flash attributes para que el mensaje sobreviva al redirect y se vea en el JSP
        ra.addFlashAttribute(FlashType.ERROR.getAttributeName(), ex.getMessage());
        this.logError(ex);
        return redirect + "procesar";
    }

    // Manejo para cuando NO se encuentra un cliente (limpiando el CustomerService)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex, @NonNull RedirectAttributes ra) {
        this.logError(ex);
        ra.addFlashAttribute(FlashType.WARNING.getAttributeName(), ex.getMessage());
        return redirect + "clientes";
    }

    @ExceptionHandler(IOException.class) // Captura FileNotFoundException y otros errores de lectura
    public String handleFileIOError(IOException ex, @NonNull RedirectAttributes ra) {
        this.logError(ex);
        ra.addFlashAttribute(FlashType.ERROR.getAttributeName(),
                "Error crítico de acceso al archivo: " + ex.getMessage());
        return redirect + "procesar";
    }

    @ExceptionHandler(Exception.class)
    public String handleGlobalError(Exception ex, RedirectAttributes ra) {
        List<NotificationProcess> notifications = new ArrayList<>();
        notifications.add(new NotificationProcess(
                NotificationProcess.NotificationType.ERROR,
                "Error crítico en el sistema: " + ex.getMessage()
        ));

        ra.addFlashAttribute("notificaciones", notifications);
        return "redirect:/procesar";
    }

    private void logError(@NonNull Exception ex) {
        log.error("Excepción capturada", ex);
        ArchivoTexto.escribirError(
                ex.getClass().getSimpleName() + " - " + ex.getMessage()
        );
    }

}
