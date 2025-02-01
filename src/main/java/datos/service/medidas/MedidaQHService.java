package datos.service.medidas;

import datos.dao.medidas.MedidaQHRepository;
import datos.entity.medidas.MedidaQH;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class MedidaQHService {

    private final MedidaQHRepository medidaQHRepository;

    public MedidaQHService(MedidaQHRepository medidaQHRepository) {
        this.medidaQHRepository = medidaQHRepository;
    }

    public void guardar(MedidaQH medida) {
        this.medidaQHRepository.guardar(medida);
    }

    @Transactional(readOnly = true)
    public List<MedidaQH> findAllByIdCliente(Long idCliente) {
        return this.medidaQHRepository.findAllByIdCliente(idCliente);
    }

    @Transactional(readOnly = true)
    public boolean existeOrigen(String origen) {
        return this.medidaQHRepository.existeOrigen(origen);
    }

}
