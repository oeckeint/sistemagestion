package controladores;

import java.util.logging.Logger;

import org.springframework.web.servlet.ModelAndView;

public abstract class Operaciones {
	
	ModelAndView mv;
	
	abstract ModelAndView listar();
	
	abstract ModelAndView listar(Integer paginaActual, Integer rows);
	
	abstract ModelAndView listarFiltro();
	
	abstract ModelAndView detalle(String valor, String filtro);
	
	abstract ModelAndView agregar();
	
}
