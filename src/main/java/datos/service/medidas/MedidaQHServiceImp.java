package datos.service.medidas;

import controladores.helper.Utilidades;
import datos.entity.medidas.MedidaQH;
import datos.interfaces.CrudDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
@Service
public class MedidaQHServiceImp implements CrudDao<MedidaQH> {
    private final CrudDao<MedidaQH> medidaQHCrudDao;

    public MedidaQHServiceImp(@Qualifier(value = "medidaQHDaoImp") CrudDao<MedidaQH> medidaQHCrudDao) {
        this.medidaQHCrudDao = medidaQHCrudDao;
    }

    @Override
    @Transactional
    public List<MedidaQH> listar() {
        return this.medidaQHCrudDao.listar();
    }

    @Override
    @Transactional
    public List<MedidaQH> listar(int rows, int page) {
        return this.medidaQHCrudDao.listar(rows, page);
    }

    @Override
    @Transactional
    public MedidaQH buscarId(long id) {
        return this.medidaQHCrudDao.buscarId(id);
    }

    @Override
    @Transactional
    public List<MedidaQH> buscarFiltro(String valor, String filtro) {
        return this.medidaQHCrudDao.buscarFiltro(valor, filtro);
    }

    @Override
    @Transactional
    public void guardar(MedidaQH medida) {
        if(medida.getCreatedBy() == null || medida.getCreatedBy().length() == 0){
            medida.setCreatedOn(Calendar.getInstance());
            medida.setCreatedBy(Utilidades.currentUser());
         } else{
            medida.setUpdatedOn(Calendar.getInstance());
            medida.setUpdatedBy(Utilidades.currentUser());
        }
        this.medidaQHCrudDao.guardar(medida);
    }

    @Override
    @Transactional
    public void actualizar(MedidaQH medida) {
        this.medidaQHCrudDao.actualizar(medida);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.medidaQHCrudDao.eliminar(id);
    }

    @Override
    @Transactional
    public int contarRegistros() {
        return this.medidaQHCrudDao.contarRegistros();
    }

    @Override
    @Transactional
    public int contarPaginacion(int rows) {
        return this.medidaQHCrudDao.contarPaginacion(rows);
    }
}
