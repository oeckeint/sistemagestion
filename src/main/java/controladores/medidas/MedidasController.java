package controladores.medidas;

import com.opencsv.CSVWriter;
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
import java.io.FileWriter;
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


    /*
    @GetMapping("/descargar/{idCliente}")
    public ResponseEntity<byte[]> downloadTestCSV(@RequestParam Long idCliente) throws IOException {
        String fileName = "test.csv";
        System.out.println("idCliente = " + idCliente);

        // Aquí puedes generar tu archivo CSV de prueba.
        // Por ahora, vamos a asumir que ya existe un archivo llamado "test.csv".
        this.createTestCSV(fileName);

        Path path = Paths.get(fileName);
        byte[] contents = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(contents);
    }*/

    public void createTestCSV(String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);

            CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            String[] header = {"id_medidaQH", "id_cliente", "tipomed", "fecha",
                    "bandera_inv_ver", "actent", "qactent", "actsal", "qactsal",
                    "r_q1", "qr_q1", "r_q2", "qr_q2", "r_q3", "qr_q3", "r_q4", "qr_q4",
                    "medres1", "qmedres1", "medres2", "qmedres2", "metod_obt",
                    "created_on", "created_by", "updated_on", "updated_by", "temporal"};


            csvWriter.writeNext(header);

            /*
            this.medidaQHService.listar().forEach(medidaQH -> {
                String[] data = {
                        String.valueOf(medidaQH.getId()), String.valueOf(medidaQH.getCliente().getIdCliente()), String.valueOf(medidaQH.getTipoMed()), medidaQH.getFecha().toString(),
                        String.valueOf(medidaQH.getBanderaInvVer()), String.valueOf(medidaQH.getActent()), String.valueOf(medidaQH.getQactent()), String.valueOf(medidaQH.getActsal()), String.valueOf(medidaQH.getQactsal()),
                        String.valueOf(medidaQH.getR_q1()), String.valueOf(medidaQH.getQr_q1()),
                        String.valueOf(medidaQH.getR_q2()), String.valueOf(medidaQH.getQr_q2()),
                        String.valueOf(medidaQH.getR_q3()), String.valueOf(medidaQH.getQr_q3()),
                        String.valueOf(medidaQH.getR_q4()), String.valueOf(medidaQH.getQr_q4()),
                        String.valueOf(medidaQH.getMedres1()), String.valueOf(medidaQH.getQmedres1()),
                        String.valueOf(medidaQH.getMedres2()), String.valueOf(medidaQH.getQmedres2()), String.valueOf(medidaQH.getMetodObt()),
                        medidaQH.getCreatedOn().toString(), medidaQH.getCreatedBy(), medidaQH.getUpdatedOn().toString(), medidaQH.getUpdatedBy(), String.valueOf(medidaQH.getTemporal())};
                csvWriter.writeNext(data);
            });*/

            csvWriter.close();
        } catch (IOException e) {
            // manejar excepción
        }
    }

}
