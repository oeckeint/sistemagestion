package datos.service.reclamaciones;

import controladores.helper.Utilidades;
import datos.entity.reclamaciones.SubtipoReclamacion;
import datos.interfaces.CrudDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

@Service
public class SubtipoReclamacionServiceImp implements CrudDao<SubtipoReclamacion> {

    private final CrudDao<SubtipoReclamacion> subTipoReclamacionDao;

    public SubtipoReclamacionServiceImp(@Qualifier(value = "subtipoReclamacionDaoImp") CrudDao<SubtipoReclamacion> subtipoReclamacion) {
        this.subTipoReclamacionDao = subtipoReclamacion;
    }

    @Override
    @Transactional
    public List<SubtipoReclamacion> listar() {
        return this.subTipoReclamacionDao.listar();
    }

    @Override
    @Transactional
    public List<SubtipoReclamacion> listar(int rows, int page) {
        return this.subTipoReclamacionDao.listar(rows, page);
    }

    @Override
    @Transactional
    public SubtipoReclamacion buscarId(long id) {
        return this.subTipoReclamacionDao.buscarId(id);
    }

    @Override
    @Transactional
    public List<SubtipoReclamacion> buscarFiltro(String valor, String filtro) {
        return this.subTipoReclamacionDao.buscarFiltro(valor, filtro);
    }

    @Override
    @Transactional
    public void guardar(SubtipoReclamacion subtipoReclamacion) {
        if (subtipoReclamacion.getCreatedBy() == null || subtipoReclamacion.getCreatedBy().length() == 0){
            subtipoReclamacion.setCreatedOn(Calendar.getInstance());
            subtipoReclamacion.setCreatedBy(Utilidades.currentUser());
        } else {
            subtipoReclamacion.setUpdatedOn(Calendar.getInstance());
            subtipoReclamacion.setUpdatedBy(Utilidades.currentUser());
        }
        this.subTipoReclamacionDao.guardar(subtipoReclamacion);
    }

    @Override
    @Transactional
    public void actualizar(SubtipoReclamacion subtipoReclamacion) {
        this.subTipoReclamacionDao.actualizar(subtipoReclamacion);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.subTipoReclamacionDao.eliminar(id);
    }

    @Override
    @Transactional
    public int contarRegistros() {
        return this.subTipoReclamacionDao.contarRegistros();
    }

    @Override
    @Transactional
    public int contarPaginacion(int rows) {
        return this.subTipoReclamacionDao.contarPaginacion(rows);
    }

}
