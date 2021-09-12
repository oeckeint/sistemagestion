package controladores;

import datos.entity.Cliente;
import datos.interfaces.ClienteService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/clientes")
public class Clientes {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("")
    public String listar(Model model) {
        model.addAttribute("tituloPagina", ClientesHelper.TITULO_PAGINA);
        model.addAttribute("titulo", ClientesHelper.ENCABEZADO);
        
        ClientesHelper.mensaje = (ClientesHelper.mensaje == null) ? ClientesHelper.INSTRUCCION_LISTAR : ClientesHelper.mensaje;
        model.addAttribute("mensaje", ClientesHelper.mensaje);
        
        List<Cliente> clientes = clienteService.listar();
        model.addAttribute("clientes", clientes);
        model.addAttribute("totalClientes", clientes.size());
        
        ClientesHelper.reiniciarVariables();
        return "cliente/clientes";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("tituloPagina", ClientesHelper.TITULO_PAGINA);
        model.addAttribute("titulo", ClientesHelper.ENCABEZADO);
        
        ClientesHelper.ETIQUETA_TABLA_TITULO = (ClientesHelper.cliente == null) ? "Registrando" : "Editando ";
        model.addAttribute("tablaTitulo", ClientesHelper.ETIQUETA_TABLA_TITULO);
        
        ClientesHelper.mensaje = (ClientesHelper.mensaje == null) ? ClientesHelper.INSTRUCCION_FORMULARIO : ClientesHelper.mensaje;
        model.addAttribute("mensaje", ClientesHelper.mensaje);
        
        ClientesHelper.cliente = (ClientesHelper.cliente == null) ? new Cliente() : ClientesHelper.cliente;
        model.addAttribute("cliente", ClientesHelper.cliente);
        
        model.addAttribute("showDelete", ClientesHelper.SHOW_DELETE);
        
        ClientesHelper.reiniciarVariables();
        return "cliente/clienteForm";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("cliente") Cliente cliente, Model model) {
        if (ClientesHelper.validarCliente(cliente)) {
            clienteService.guardar(cliente);
            ClientesHelper.cliente = null;
            return "redirect:/clientes/listar";
        } else {
            ClientesHelper.cliente = cliente;
            return "redirect:/clientes/formulario";
        }
    }

    @GetMapping("/editar")
    public String editar(@RequestParam("idCliente") long idCliente) {
        ClientesHelper.cliente = this.clienteService.encontrarId(idCliente);
        ClientesHelper.SHOW_DELETE = "y";
        return "redirect:/clientes/formulario";
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam("idCliente") long idCliente, @RequestParam("cups") String cups) {
        this.clienteService.eliminar(idCliente);
        ClientesHelper.mensaje = ClientesHelper.FORMULARIO_EXITO_ELIMINAR + cups;
        return "redirect:/clientes/listar";
    }


}
