package controladores;

import java.util.logging.Logger;

import org.springframework.web.servlet.ModelAndView;

public abstract class Operaciones {
	
	public ModelAndView mv;
	
	public abstract ModelAndView listar();
	
	public abstract ModelAndView listar(Integer paginaActual, Integer rows);

	public abstract ModelAndView listarFiltro();
	
	public abstract ModelAndView detalle(String valor, String filtro);
	
	public abstract ModelAndView agregar();
	
}
