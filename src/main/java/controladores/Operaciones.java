package controladores;

import java.util.logging.Logger;

import datos.entity.reclamaciones.BusquedaReclamacion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

public abstract class Operaciones <T>{
	
	public ModelAndView mv;
	
	public abstract ModelAndView listar();
	
	public abstract ModelAndView listar(Integer paginaActual, Integer rows);

	public abstract ModelAndView listarFiltro();
	
	public abstract ModelAndView detalle(String valor, String filtro);
	
	public abstract ModelAndView agregar();

	public abstract ModelAndView busqueda(final T busqueda, final BindingResult bindingResult);
}
