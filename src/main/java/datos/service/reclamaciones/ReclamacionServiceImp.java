package datos.service.reclamaciones;

import controladores.helper.Utilidades;
import datos.entity.reclamaciones.Reclamacion;
import datos.interfaces.CrudDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

@Service
public class ReclamacionServiceImp implements CrudDao<Reclamacion> {

    private final CrudDao<Reclamacion> reclamacionDao;

    public ReclamacionServiceImp(@Qualifier(value = "reclamacionDaoImp") CrudDao<Reclamacion> reclamacionDao) {
        this.reclamacionDao = reclamacionDao;
    }

    @Override
    @Transactional
    public List<Reclamacion> listar() {
        return this.reclamacionDao.listar();
    }

    @Override
    @Transactional
    public List<Reclamacion> listar(int rows, int page) {
        return this.reclamacionDao.listar(rows, page);
    }

    @Override
    @Transactional
    public Reclamacion buscarId(long id) {
        return this.reclamacionDao.buscarId(id);
    }

    @Override
    @Transactional
    public List<Reclamacion> buscarFiltro(String valor, String filtro) {
        return this.reclamacionDao.buscarFiltro(valor, filtro);
    }

    @Override
    @Transactional
    public void guardar(Reclamacion reclamacion) {
        if (reclamacion.getCreatedBy() == null || reclamacion.getCreatedBy().length() == 0){
            reclamacion.setCreatedOn(Calendar.getInstance());
            reclamacion.setCreatedBy(Utilidades.currentUser());
        } else {
            reclamacion.setUpdatedOn(Calendar.getInstance());
            reclamacion.setUpdatedBy(Utilidades.currentUser());
        }
        this.reclamacionDao.guardar(reclamacion);
    }

    @Override
    @Transactional
    public void actualizar(Reclamacion reclamacion) {
        this.reclamacionDao.actualizar(reclamacion);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.reclamacionDao.eliminar(id);
    }

    @Override
    @Transactional
    public int contarRegistros() {
        return this.reclamacionDao.contarRegistros();
    }

    @Override
    @Transactional
    public int contarPaginacion(int rows) {
        return this.reclamacionDao.contarPaginacion(rows);
    }

}
