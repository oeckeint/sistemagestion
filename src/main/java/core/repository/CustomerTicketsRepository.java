package core.repository;

import datos.entity.Cliente;
import datos.entity.cliente.tickets.ClienteTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerTicketsRepository extends JpaRepository<ClienteTicket, Long> {

    List<ClienteTicket> findAllByCliente(Cliente cliente);

}
