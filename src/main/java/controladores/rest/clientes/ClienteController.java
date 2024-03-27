package controladores.rest.clientes;

import controladores.GenericController;
import datos.dao.cliente.ClienteRepository;
import datos.entity.Cliente;
import datos.service.cliente.medidas.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/clientes")
public class ClienteController extends GenericController<Cliente> {

    ClienteService clienteService;

    public ClienteController(ClienteRepository repository, ClienteService clienteService) {
        super(repository);
        this.clienteService = clienteService;
    }

}
