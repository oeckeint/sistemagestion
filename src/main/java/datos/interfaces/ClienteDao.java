package datos.interfaces;

import datos.entity.Cliente;
import excepciones.MasDeUnClienteEncontrado;
import java.util.List;

public interface ClienteDao {

    public List<Cliente> listar();

    public List<Cliente> listar(int rows, int page);

    public Cliente encontrarId(long id);
    
    public List<Cliente> encontrarByNombre(String nombreCliente);

    public Cliente encontrarCups(String cups) throws MasDeUnClienteEncontrado;
    
    public List<Cliente> encontrarCupsParcial(String cups);

    public void guardar(Cliente cliente);

    public void eliminar(long id);

    public int contarPaginacion(int rows);

    public int contarRegistros();
}
