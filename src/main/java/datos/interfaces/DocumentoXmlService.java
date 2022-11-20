package datos.interfaces;

import excepciones.NoEsUnNumeroException;
import excepciones.PeajeMasDeUnRegistroException;
import excepciones.RegistroVacioException;

import java.util.List;

public interface DocumentoXmlService<T> {
    
    public List<T> listar();
    
    public List<T> listar(int rows, int page);
    
    public void guardar(T documento);
    
    public void eliminar(long id);
    
    public T buscarByCodFiscal(String cod) throws PeajeMasDeUnRegistroException, RegistroVacioException;

    public T buscarByCodFiscalEspecifico(String cod) throws PeajeMasDeUnRegistroException, RegistroVacioException;
    
    public List<T> buscarByIdCliente(String idCliente) throws NoEsUnNumeroException;
    
    public List<T> buscarByRemesa(String remesa);
    
    public void rectificar(T documento, String nuevaRemesa, String nuevoNombreArchivo);
    
    public void actualizar(T documento);
    
    public int contarPaginacion(int rows);
    
    public int contarRegistros();
}
