package datos.dao;

import datos.entity.Tarifa;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TarifaDaoImp implements datos.interfaces.CrudDao<Tarifa>{

    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public List<Tarifa> listar() {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from Tarifa t where t.status = 1 order by t.nombreTarifa asc", Tarifa.class)
                .getResultList();
    }
    
    @Override
	public List<Tarifa> listar(int rows, int page) {
		return null;
	}

    @Override
    public void guardar(Tarifa object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actualizar(Tarifa object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void eliminar(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	@Override
	public Tarifa buscarId(long id) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	@Override
	public List<Tarifa> buscarFiltro(String valor, String filtro) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int contarRegistros() {
		return 0;
	}

	@Override
	public int contarPaginacion(int rows) {
		return 0;
	}

}
