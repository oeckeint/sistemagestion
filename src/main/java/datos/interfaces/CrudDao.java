package datos.interfaces;

import java.util.List;

import datos.entity.Cliente;

public interface CrudDao<T> {

    public List<T> listar();
    
    /**
     * List records using specific pagination and quantity of rows
     * @param rows number of records by page
     * @param page current page
     * @return list of records on a specific page
     */
    public List<T> listar(int rows, int page);
    
    public T buscarId(long id);

    public void guardar(T object);

    public void actualizar(T object);

    public void eliminar(long id);
    
    /**
     * Count all rows from DB 
     * @return total number of rows 
     */
    public int contarRegistros();
    
    /**
     * Check the total of records and it is divided by the number of rows desired
     * @param rows desired records per page
     * @return the number of pages that are going to appear
     */
    public int contarPaginacion(int rows);

}
