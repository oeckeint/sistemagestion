package datos.service.medidas;

import controladores.helper.Utilidades;
import datos.entity.medidas.MedidaH;
import datos.interfaces.CrudDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class MedidaHServiceImp implements CrudDao<MedidaH> {

    private final CrudDao<MedidaH> medidaHDao;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public MedidaHServiceImp(@Qualifier("medidaHDaoImp") CrudDao<MedidaH> medidaHDao) {
        this.medidaHDao = medidaHDao;
    }

    @Override
    @Transactional
    public List<MedidaH> listar() {
        return this.medidaHDao.listar();
    }

    @Override
    @Transactional
    public List<MedidaH> listar(int rows, int page) {
        return this.medidaHDao.listar(rows, page);
    }

    @Override
    @Transactional
    public MedidaH buscarId(long id) {
        return this.medidaHDao.buscarId(id);
    }

    @Override
    @Transactional
    public List<MedidaH> buscarFiltro(String valor, String filtro) {
        return this.medidaHDao.buscarFiltro(valor, filtro);
    }

    @Override
    @Transactional
    public void guardar(MedidaH medida) {
        this.medidaHDao.guardar(medida);
    }

    @Override
    @Transactional
    public void actualizar(MedidaH medida) {
        this.medidaHDao.actualizar(medida);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.medidaHDao.eliminar(id);
    }

    @Override
    @Transactional
    public int contarRegistros() {
        return this.medidaHDao.contarRegistros();
    }

    @Override
    @Transactional
    public int contarPaginacion(int rows) {
        return this.medidaHDao.contarPaginacion(rows);
    }
}
