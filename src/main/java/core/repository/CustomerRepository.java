package core.repository;

import datos.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findAllByIsDeleted(short isDeleted);

    Optional<Cliente> findById(Long id);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.tickets WHERE c.idCliente = :id")
    Optional<Cliente> findByIdWithTicketsEager(@Param("id") Long id);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.tickets WHERE c.cups LIKE :cups")
    Optional<Cliente> findByCupsWithTicketsEager(@Param("cups") String cups);

    Optional<Cliente> findByCupsLike(String cups);

    default List<Cliente> findActive() {
        return findAllByIsDeleted((short) 0);
    }

    default List<Cliente> findDeleted() {
        return findAllByIsDeleted((short) 1);
    }

}
