/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos.entity;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author Jesus Sanchez <oeckeint@gmail>
 */

public class BusquedaCliente {
    
    @Size(min = 1, message = "El valor no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Solo se aceptan valores alfanuméricos")
    private String valor;
    
    private String filtro;
    
    private List<String> filtros;
    
    public BusquedaCliente(){
        this.filtros = new ArrayList<>();
        this.filtros.add("id");
        this.filtros.add("cups");
        this.filtros.add("nombre");
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
