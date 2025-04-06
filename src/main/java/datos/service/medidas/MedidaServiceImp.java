package datos.service.medidas;

import controladores.helper.Utilidades;
import datos.entity.medidas.Medida;
import datos.interfaces.CrudDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utileria.PaginacionUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Service
public class MedidaServiceImp implements CrudDao<Medida> {

    private final CrudDao<Medida> medidaCrudDao;

    public MedidaServiceImp(@Qualifier(value = "medidaDaoImp") CrudDao<Medida> medidaCrudDao) {
        this.medidaCrudDao = medidaCrudDao;
    }

    @Override
    @Transactional
    public List<Medida> listar() {
        return this.medidaCrudDao.listar();
    }

    @Override
    @Transactional
    public List<Medida> listar(int rows, int page) {
        return PaginacionUtil.validarPaginacion(rows, page, () -> this.medidaCrudDao.listar(rows, page));
    }

    @Override
    @Transactional
    public Medida buscarId(long id) {
        return this.medidaCrudDao.buscarId(id);
    }

    @Override
    @Transactional
    public List<Medida> buscarFiltro(String valor, String filtro) {
        return this.medidaCrudDao.buscarFiltro(valor, filtro);
    }

    @Override
    @Transactional
    public void guardar(Medida medida) {
        if (medida.getCreatedBy() == null || medida.getCreatedBy().length() == 0){
            medida.setCreatedOn(Calendar.getInstance());
            medida.setCreatedBy(Utilidades.currentUser());
        } else {
            medida.setUpdatedOn(Calendar.getInstance());
            medida.setUpdatedBy(Utilidades.currentUser());
        }
        this.medidaCrudDao.guardar(medida);
    }

    @Override
    @Transactional
    public void actualizar(Medida medida) {
        this.medidaCrudDao.actualizar(medida);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.medidaCrudDao.eliminar(id);
    }

    @Override
    @Transactional
    public int contarRegistros() {
        return this.medidaCrudDao.contarRegistros();
    }

    @Override
    @Transactional
    public int contarPaginacion(int rows) {
        return this.medidaCrudDao.contarPaginacion(rows);
    }

}
