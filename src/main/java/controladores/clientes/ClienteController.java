package controladores.clientes;

import controladores.GenericController;
import datos.dao.cliente.ClienteRepository;
import datos.entity.Cliente;
import datos.service.cliente.medidas.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/clientes")
public class ClienteController extends GenericController<Cliente> {

    ClienteService clienteService;

    public ClienteController(ClienteRepository repository, ClienteService clienteService) {
        super(repository);
        this.clienteService = clienteService;
    }

}
