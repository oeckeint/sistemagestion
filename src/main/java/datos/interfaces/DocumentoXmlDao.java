package datos.interfaces;

import excepciones.NoEsUnNumeroException;
import java.util.List;

public interface DocumentoXmlDao<T> {

    public List<T> listar();
    
    public void guardar(T documento);
    
    public void actualizar(T documento);
    
    public void eliminar(long id);
    
    public T buscarByCodFiscal(String cod);
    
    public List<T> buscarByIdCliente(String idCliente) throws NoEsUnNumeroException;
    
    public List<T> buscarByRemesa(String remesa);
    
}
