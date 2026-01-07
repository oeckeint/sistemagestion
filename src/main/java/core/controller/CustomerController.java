package core.controller;

import controladores.Clientes;
import controladores.ClientesHelper;
import core.service.CustomerService;
import core.service.TariffService;
import datos.entity.BusquedaCliente;
import datos.entity.Cliente;
import datos.entity.Tarifa;
import excepciones.MasDeUnClienteEncontrado;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
public class CustomerController extends Clientes {

    private final CustomerService customerService;
    private final TariffService tariffService;

    @Override
    public String formulario(Model model, @RequestParam(required = false, name = "idCliente") String idCliente) {
        if (idCliente != null){
            ClientesHelper.cliente = this.customerService.encontrarId(Long.parseLong(idCliente));
        }
        model.addAttribute("tituloPagina", ClientesHelper.TITULO_PAGINA);
        model.addAttribute("titulo", ClientesHelper.ENCABEZADO);

        ClientesHelper.ETIQUETA_TABLA_TITULO = (ClientesHelper.cliente == null) ? "Registrando" : "Editando ";
        model.addAttribute("tablaTitulo", ClientesHelper.ETIQUETA_TABLA_TITULO);

        ClientesHelper.mensaje = (ClientesHelper.mensaje == null) ? ClientesHelper.INSTRUCCION_FORMULARIO
                : ClientesHelper.mensaje;
        model.addAttribute("mensaje", ClientesHelper.mensaje);

        ClientesHelper.cliente = (ClientesHelper.cliente == null) ? new Cliente() : ClientesHelper.cliente;
        model.addAttribute("cliente", ClientesHelper.cliente);

        model.addAttribute("showDelete", ClientesHelper.SHOW_DELETE);

        List<Tarifa> tarifas = this.tariffService.listar();
        model.addAttribute("tarifas", tarifas);

        ClientesHelper.reiniciarVariables();
        return "cliente/clienteForm";
    }

    @Override
    public String detalles2(
            @ModelAttribute("busquedaCliente") @Valid final BusquedaCliente busquedaCliente, RedirectAttributes redirectAttributes,
            @RequestParam(required = false, name = "valor") final String valor,
            @RequestParam(required = false, name = "filtro") final String filtro,
            Model model) {
        redirectAttributes.addFlashAttribute("busquedaCliente", busquedaCliente);
        Cliente cliente = null;
        if (valor != null) {
            busquedaCliente.setValor(valor);
            busquedaCliente.setFiltro(filtro);
        }
        try {
            switch (busquedaCliente.getFiltro()) {
                case "id":
                    cliente = this.customerService.findByIdWithTickets(Long.parseLong(busquedaCliente.getValor()));
                    break;
                case "cups":
                    cliente = this.customerService.findByCupsWithTickets(busquedaCliente.getValor());
                    break;
            }
            if (cliente != null) {
                model.addAttribute("tituloPagina", "Detalles cliente");
                model.addAttribute("titulo", "Detalles Cliente");
                model.addAttribute("mensaje", "Estos son los datos que se encontraron");
                model.addAttribute("cliente", cliente);
                model.addAttribute("busquedaCliente", busquedaCliente);
                return "cliente/detalle_cliente";
            }
            return "redirect:/clientes?sinregistro&v=" + busquedaCliente.getValor() + "&f="
                    + busquedaCliente.getFiltro();
        } catch (NumberFormatException e) {
            return "redirect:/clientes?idnan";
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return "redirect:/clientes?unknown";
        }
    }

}
