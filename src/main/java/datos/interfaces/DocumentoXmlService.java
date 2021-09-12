package datos.interfaces;

import java.util.List;

public interface DocumentoXmlService<T> {
    
    public List<T> listar();
    
    public void guardar(T documento);
    
    public void eliminar(long id);
    
    public T buscarByCodFiscal(String cod);
    
    public List<T> buscarByIdCliente(String idCliente);
    
    public List<T> buscarByRemesa(String remesa);
    
    public void rectificar(T documento, String nuevaRemesa, String nuevoNombreArchivo);
    
}
