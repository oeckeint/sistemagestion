package datos.service.medidas;

import controladores.helper.Utilidades;
import datos.entity.medidas.MedidaCCH;
import datos.interfaces.CrudDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

@Service
public class MedidaCCHServiceImp implements CrudDao<MedidaCCH> {

    private final CrudDao<MedidaCCH> medidaCrudDao;

    public MedidaCCHServiceImp(@Qualifier(value = "medidaCCHDaoImp") CrudDao<MedidaCCH> medidaCrudDao) {
        this.medidaCrudDao = medidaCrudDao;
    }

    @Override
    @Transactional
    public List<MedidaCCH> listar() {
        return this.medidaCrudDao.listar();
    }

    @Override
    @Transactional
    public List<MedidaCCH> listar(int rows, int page) {
        return this.medidaCrudDao.listar(rows, page);
    }

    @Override
    @Transactional
    public MedidaCCH buscarId(long id) {
        return this.medidaCrudDao.buscarId(id);
    }

    @Override
    @Transactional
    public List<MedidaCCH> buscarFiltro(String valor, String filtro) {
        return this.medidaCrudDao.buscarFiltro(valor, filtro);
    }

    @Override
    @Transactional
    public void guardar(MedidaCCH medida) {
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
    public void actualizar(MedidaCCH medida) {
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
