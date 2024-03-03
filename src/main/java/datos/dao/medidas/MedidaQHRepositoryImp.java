package datos.dao.medidas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import datos.entity.medidas.MedidaQH;

@Repository
public interface MedidaQHRepositoryImp extends JpaRepository<MedidaQH, Long> {


}
