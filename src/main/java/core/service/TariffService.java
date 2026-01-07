package core.service;

import core.repository.TariffRepository;
import datos.entity.Tarifa;
import datos.service.TarifasServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TariffService extends TarifasServiceImp {

    private final TariffRepository tariffRepository;

    @Override
    public List<Tarifa> listar() {
        return tariffRepository.findAll();
    }

}
