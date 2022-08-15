package datos.entity.cliente.tickets;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class BusquedaTicket {
	
	@Size(min = 1, message = "El valor no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Solo se aceptan valores alfanuméricos")
    private String valor;
    
    private String filtro;
    
    private List<String> filtros;
    
    public BusquedaTicket(){
        this.filtros = new ArrayList<>();
        this.filtros.add("Ticket");
        this.filtros.add("Tipo");
        this.filtros.add("Estado");
        this.filtros.add("Cliente");
        this.filtros.add("CUPS");
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
		this.valor=valor.trim();
    }
    
    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<String> getFiltros() {
        return filtros;
    }

    public void setFiltros(List<String> filtros) {
        this.filtros = filtros;
    }
}
