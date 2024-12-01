package datos.service.cliente.medidas;

import datos.dao.cliente.ClienteRepository;
import datos.dao.medidas.MedidaQHRepository;
import datos.entity.Cliente;
import datos.service.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ClienteService extends GenericService<Cliente> {

    MedidaQHRepository medidaQHRepository;

    public ClienteService(ClienteRepository repository, MedidaQHRepository medidaQHRepository) {
        super(repository);
        this.medidaQHRepository = medidaQHRepository;
    }

}
