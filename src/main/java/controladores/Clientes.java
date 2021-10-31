package controladores;

import datos.entity.Cliente;
import datos.entity.Tarifa;
import datos.interfaces.ClienteService;
import datos.interfaces.CrudDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier(value = "tarifasServiceImp")
    private CrudDao tarifasService;

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

        List<Tarifa> tarifas = this.tarifasService.listar();
        model.addAttribute("tarifas", tarifas);

        ClientesHelper.reiniciarVariables();
        return "cliente/clienteForm";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("cliente") Cliente cliente, Model model) {
        if (ClientesHelper.validarCliente(cliente)) {
            clienteService.guardar(cliente);
            ClientesHelper.cliente = null;
            return "redirect:/clientes";
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
        return "redirect:/clientes";
    }

    @GetMapping("/detalles")
    public String detalles(Model model, @RequestParam("idCliente") long idCliente) {
        Cliente cliente = this.clienteService.encontrarId(idCliente);
        model.addAttribute("tituloPagina", "Detalles cliente");
        model.addAttribute("titulo", "Detalles Cliente");
        model.addAttribute("mensaje", "Estos son los datos que se encontraron con el id de cliente " + idCliente);
        model.addAttribute("cliente", cliente);
        return "cliente/detalle_cliente";
    }

    @PostMapping("/busqueda")
    public String busqueda(Model model, @RequestParam("valor") String valor) {
        try {
            long idCliente = Long.parseLong(valor);
            Cliente cliente = this.clienteService.encontrarId(idCliente);
            if (cliente == null) {
                ClientesHelper.mensaje = "No se encontró algun registro con el id " + idCliente;
                return "redirect:/clientes";
            }
            model.addAttribute("tituloPagina", "Detalles cliente");
            model.addAttribute("titulo", "Detalles Cliente");
            model.addAttribute("mensaje", "Estos son los datos que se encontraron con el id de cliente " + idCliente);
            model.addAttribute("cliente", cliente);
            model.addAttribute("ultimaBusqueda", idCliente);
            return "cliente/detalle_cliente";
        } catch (NumberFormatException e) {
            e.printStackTrace(System.out);
            ClientesHelper.mensaje = "El filtro de <Strong>Cliente</Strong> solo acepta valores enteros, revisar el valor ingresado <Strong>" + valor + "</Strong>";
            return "redirect:/clientes";
        } catch (Exception e) {
            e.printStackTrace(System.out);
            ClientesHelper.mensaje = "Algo ha salido mal :( reporte el bug o revise la consola para saber más";
            return "redirect:/clientes";
        }
    }

}
