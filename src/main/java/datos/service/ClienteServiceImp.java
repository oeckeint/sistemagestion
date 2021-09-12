package datos.service;

import datos.entity.Cliente;
import datos.interfaces.ClienteDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteServiceImp implements datos.interfaces.ClienteService {

    @Autowired
    private ClienteDao clienteDao;

    @Override
    @Transactional
    public List<Cliente> listar() {
        return clienteDao.listar();
    }

    @Override
    @Transactional
    public Cliente encontrarId(long id) {
        return clienteDao.encontrarId(id);
    }

    @Override
    @Transactional
    public Cliente encontrarCups(String cups) {
        return clienteDao.encontrarCups(cups);
    }

    @Override
    @Transactional
    public void guardar(Cliente cliente) {
        this.clienteDao.guardar(cliente);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        this.clienteDao.eliminar(id);
    }

}
