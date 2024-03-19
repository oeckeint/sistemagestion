package controladores.medidas;

import controladores.GenericController;
import controladores.medidas.helpers.Utilidades;
import datos.dao.medidas.MedidaQHRepository;
import datos.entity.medidas.MedidaQH;
import datos.entity.medidas.MedidaValidatorForm;
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
public class MedidasController extends GenericController<MedidaQH> {

    private final MedidaQHRepository repository;

    public MedidasController(MedidaQHRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @GetMapping("/form")
    public ModelAndView listar(Model model) {
        MedidaValidatorForm medidaValidatorForm = model.containsAttribute("medidaValidatorForm") ? (MedidaValidatorForm) model.asMap().get("medidaValidatorForm") : new MedidaValidatorForm();
        ModelAndView mv = new ModelAndView("medidas/inicio");
        mv.addObject("medidaValidatorForm", medidaValidatorForm);
        mv.addObject("info", model.asMap().get("info"));
        return mv;
    }

    @PostMapping("/validar")
    public ModelAndView validar(
            @ModelAttribute("medidaValidatorForm") @Valid final MedidaValidatorForm medidaValidatorForm,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes,
            HttpServletResponse response) throws IOException {

        redirectAttributes.addFlashAttribute("medidaValidatorForm", medidaValidatorForm);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.medidaValidatorForm", bindingResult);
            redirectAttributes.addFlashAttribute("info", "errorDatosEnviados");
            return new ModelAndView("redirect:/medidas/form");
        }

        switch (medidaValidatorForm.getFiltro()){
            case "QH":
                List<MedidaQH> medidas = repository.findAllByClienteIdCliente(Long.parseLong(medidaValidatorForm.getValorSeleccionado()));
                if (medidas.isEmpty()) {
                    redirectAttributes.addFlashAttribute("info", "sinMedidas");
                    return new ModelAndView("redirect:/medidas/form");
                }
                Utilidades.crearCsvMedidasQH(response, medidas);
                break;
        }

        response.getWriter().flush();

        return null;
    }

}
