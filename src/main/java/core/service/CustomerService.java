package core.service;

import core.repository.CustomerRepository;
import datos.entity.Cliente;
import datos.service.ClienteServiceImp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Primary
@RequiredArgsConstructor
@Service
@Slf4j
public class CustomerService extends ClienteServiceImp {

    private final CustomerRepository customerRepository;

    @Override
    public List<Cliente> listar() {
        return customerRepository.findActive();
    }

    @Override
    public Cliente encontrarId(long id) {
        return this.customerRepository.findById(id)
                .orElseGet(() -> throwNotFound("ID", id));
    }

    public Cliente findByCups(String cups) {
        return this.customerRepository.findByCupsLike(cups)
                .orElseGet(() -> throwNotFound("CUPS", cups));
    }

    public Cliente findByIdWithTickets(long id) {
        return this.customerRepository.findByIdWithTicketsEager(id)
                .orElseGet(() -> throwNotFound("ID", id));
    }

    public Cliente findByCupsWithTickets(String cups) {
        return this.customerRepository.findByCupsWithTicketsEager(cups)
                .orElseGet(() -> throwNotFound("CUPS", cups));
    }

    private Cliente throwNotFound(String tipo, Object valor) {
        throw new EntityNotFoundException(String.format("No se encontró el cliente con %s: %s", tipo, valor));
    }

}
