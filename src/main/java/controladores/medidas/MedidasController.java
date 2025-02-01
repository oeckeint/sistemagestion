package controladores.medidas;

import controladores.medidas.helpers.Utilidades;
import datos.entity.medidas.MedidaQH;
import datos.entity.medidas.MedidaValidatorForm;
import datos.service.medidas.MedidaQHService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/medidas")
public class MedidasController {

    private final MedidaQHService service;

    public MedidasController(MedidaQHService service) {
        this.service = service;
    }

    @GetMapping("/form")
    public ModelAndView listar(Model model) {
        String medidaValidatorFormString = "medidaValidatorForm";
        MedidaValidatorForm medidaValidatorForm = model.containsAttribute(medidaValidatorFormString) ? (MedidaValidatorForm) model.asMap().get(medidaValidatorFormString) : new MedidaValidatorForm();
        ModelAndView mv = new ModelAndView("medidas/inicio");
        mv.addObject(medidaValidatorFormString, medidaValidatorForm);
        mv.addObject("info", model.asMap().get("info"));
        return mv;
    }

    @PostMapping("/validar")
    public ModelAndView validar(
            @ModelAttribute("medidaValidatorForm") @Valid final MedidaValidatorForm medidaValidatorForm,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes,
            HttpServletResponse response) throws IOException {

        String redirectedView = "redirect:/medidas/form";
        redirectAttributes.addFlashAttribute("medidaValidatorForm", medidaValidatorForm);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.medidaValidatorForm", bindingResult);
            redirectAttributes.addFlashAttribute("info", "errorDatosEnviados");
            return new ModelAndView(redirectedView);
        }


        switch (medidaValidatorForm.getFiltro()){
            case "H":
                throw new UnsupportedOperationException("No implementado");
            case "QH":
                List<MedidaQH> medidas = service.findAllByIdCliente(Long.parseLong(medidaValidatorForm.getValorSeleccionado()));
                if (medidas.isEmpty()) {
                    redirectAttributes.addFlashAttribute("info", "sinMedidas");
                    return new ModelAndView(redirectedView);
                }
                Utilidades.crearCsvMedidasQH(response, medidas);
                break;
            default:
                redirectAttributes.addFlashAttribute("info", "errorDatosEnviados");
                return new ModelAndView(redirectedView);
        }

        response.getWriter().flush();

        return null;
    }

}
