package datos.interfaces;

import java.util.List;

public interface CrudDao<T> {

    public List<T> listar();

    public void guardar(T object);

    public void actualizar(T object);

    public void eliminar(long id);

}
