package datos.service;

import datos.entity.Tarifa;
import datos.interfaces.CrudDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TarifasServiceImp implements datos.interfaces.CrudDao<Tarifa>{

    @Autowired
    @Qualifier(value = "tarifaDaoImp")
    private CrudDao<Tarifa> tarifasDao;
    
    @Override
    @Transactional
    public List<Tarifa> listar() {
        return tarifasDao.listar();
    }
    
    @Override
    @Transactional
	public List<Tarifa> listar(int rows, int page) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    @Transactional
    public void guardar(Tarifa tarifa) {
        tarifasDao.guardar(tarifa);
    }

    @Override
    @Transactional
    public void actualizar(Tarifa tarifa) {
        tarifasDao.actualizar(tarifa);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        tarifasDao.eliminar(id);
    }

	@Override
	@Transactional
	public Tarifa buscarId(long id) {
		return null;
	}

	@Override
	@Transactional
	public int contarRegistros() {
		return 0;
	}

	@Override
	@Transactional
	public int contarPaginacion(int rows) {
		return 0;
	}

}
