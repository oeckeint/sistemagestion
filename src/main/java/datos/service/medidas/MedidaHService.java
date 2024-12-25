package datos.service.medidas;

import datos.entity.medidas.MedidaH;
import datos.repositories.medidas.MedidaHRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class MedidaHService {

    private final MedidaHRepository medidaHRepository;

    public MedidaHService(MedidaHRepository medidaHRepository) {
        this.medidaHRepository = medidaHRepository;
    }

    public void guardar(MedidaH medida) {
        this.medidaHRepository.guardar(medida);
    }

    @Transactional(readOnly = true)
    public boolean existeOrigen(String origen) {
        return this.medidaHRepository.existeOrigen(origen);
    }

}
