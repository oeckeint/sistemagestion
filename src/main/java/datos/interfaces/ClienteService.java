package datos.interfaces;

import datos.entity.Cliente;
import excepciones.MasDeUnClienteEncontrado;
import java.util.List;

public interface ClienteService {

    public List<Cliente> listar();

    public Cliente encontrarId(long id);

    public Cliente encontrarCups(String cups) throws MasDeUnClienteEncontrado;

    public void guardar(Cliente cliente);

    public void eliminar(long id);

}
