package datos.service.reclamaciones;

import controladores.helper.Utilidades;
import datos.entity.reclamaciones.TipoReclamacion;
import datos.interfaces.CrudDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

@Service
public class TipoReclamacionServiceImp implements CrudDao<TipoReclamacion> {

    private final CrudDao<TipoReclamacion> tipoReclamacionDao;

    public TipoReclamacionServiceImp(@Qualifier(value = "tipoReclamacionDaoImp") CrudDao<TipoReclamacion> tipoReclamacion) {
        this.tipoReclamacionDao = tipoReclamacion;
    }

    @Override
    @Transactional
    public List<TipoReclamacion> listar() {
        return this.tipoReclamacionDao.listar();
    }

    @Override
    @Transactional
    public List<TipoReclamacion> listar(int rows, int page) {
        return this.tipoReclamacionDao.listar(rows, page);
    }

    @Override
    @Transactional
    public TipoReclamacion buscarId(long id) {
        return this.tipoReclamacionDao.buscarId(id);
    }

    @Override
    @Transactional
    public List<TipoReclamacion> buscarFiltro(String valor, String filtro) {
        return this.tipoReclamacionDao.buscarFiltro(valor, filtro);
    }

    @Override
    @Transactional
    public void guardar(TipoReclamacion tipoReclamacion) {
        if (tipoReclamacion.getCreatedBy() == null || tipoReclamacion.getCreatedBy().length() == 0){
            tipoReclamacion.setCreatedOn(Calendar.getInstance());
            tipoReclamacion.setCreatedBy(Utilidades.currentUser());
        } else {
            tipoReclamacion.setUpdatedOn(Calendar.getInstance());
            tipoReclamacion.setUpdatedBy(Utilidades.currentUser());
        }
        this.tipoReclamacionDao.guardar(tipoReclamacion);
    }

    @Override
    @Transactional
    public void actualizar(TipoReclamacion tipoReclamacion) {
        this.tipoReclamacionDao.actualizar(tipoReclamacion);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.tipoReclamacionDao.eliminar(id);
    }

    @Override
    @Transactional
    public int contarRegistros() {
        return this.tipoReclamacionDao.contarRegistros();
    }

    @Override
    @Transactional
    public int contarPaginacion(int rows) {
        return this.tipoReclamacionDao.contarPaginacion(rows);
    }

}
