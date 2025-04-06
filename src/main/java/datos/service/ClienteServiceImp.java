package datos.service;

import datos.entity.Cliente;
import datos.interfaces.ClienteDao;
import excepciones.MasDeUnClienteEncontrado;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utileria.PaginacionUtil;

@Service
public class ClienteServiceImp implements datos.interfaces.ClienteService {

    @Autowired
    private ClienteDao clienteDao;

    @Override
    @Transactional
    public List<Cliente> listar() {
        return this.clienteDao.listar();
    }
    
    @Override
    @Transactional
    public List<Cliente> listar(int rows, int page) {
        return PaginacionUtil.validarPaginacion(rows, page, () -> this.clienteDao.listar(rows, page));
    }

    @Override
    @Transactional
    public Cliente encontrarId(long id) {
        return this.clienteDao.encontrarId(id);
    }
    
    @Override
    @Transactional
    public List<Cliente> encontrarByNombre(String nombreCliente) {
        return this.clienteDao.encontrarByNombre(nombreCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente encontrarCups(String cups) throws MasDeUnClienteEncontrado{
        return this.clienteDao.encontrarCups(cups);
    }
    
    @Override
    @Transactional
	public List<Cliente> encontrarCupsParcial(String cups) {
		return this.clienteDao.encontrarCupsParcial(cups);
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

    @Override
    @Transactional
    public int contarPaginacion(int rows) {
        return this.clienteDao.contarPaginacion(rows);
    }

    @Override
    @Transactional
    public int contarRegistros() {
        return this.clienteDao.contarRegistros();
    }

}
