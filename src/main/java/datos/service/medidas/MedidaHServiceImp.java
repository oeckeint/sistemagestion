package datos.service.medidas;

import datos.dao.medidas.MedidaHDaoImp;
import datos.entity.medidas.MedidaH;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class MedidaHServiceImp {

    private final MedidaHDaoImp medidaHDaoImp;

    public MedidaHServiceImp(MedidaHDaoImp medidaHDaoImp) {
        this.medidaHDaoImp = medidaHDaoImp;
    }

    public void guardar(MedidaH medida) {
        this.medidaHDaoImp.guardar(medida);
    }

    @Transactional(readOnly = true)
    public boolean existeOrigen(String origen) {
        return true;
    }

}
