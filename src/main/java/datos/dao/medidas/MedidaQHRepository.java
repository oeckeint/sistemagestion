package datos.dao.medidas;

import datos.GenericRepository;
import datos.entity.medidas.MedidaQH;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedidaQHRepository extends GenericRepository<MedidaQH>{

    List<MedidaQH> findAllByClienteIdCliente(long channelId);

}
