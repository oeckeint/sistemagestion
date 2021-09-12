package datos.interfaces;

import datos.entity.Cliente;
import java.util.List;

public interface ClienteDao {
    
    public List<Cliente> listar();
    
    public Cliente encontrarId(long id);
    
    public Cliente encontrarCups(String cups);
    
    public void guardar(Cliente cliente);
    
    public void eliminar(long id);
}
