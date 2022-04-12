package controladores;

import controladores.helper.Etiquetas;
import controladores.helper.Utilidades;
import datos.entity.BusquedaCliente;
import datos.entity.Cliente;
import datos.entity.Tarifa;
import datos.interfaces.ClienteService;
import datos.interfaces.CrudDao;
import excepciones.MasDeUnClienteEncontrado;

import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class Clientes {

	@Autowired
	private ClienteService clienteService;

	@Autowired
	@Qualifier(value = "tarifasServiceImp")
	private CrudDao tarifasService;

	@GetMapping("")
	public String listar(@RequestParam(required = false, defaultValue = "1", name = "page") Integer paginaActual,
			@RequestParam(required = false, defaultValue = "50", name = "rows") Integer rows,
			@RequestParam(required = false, name = "err") Integer error, Model model) {
		paginaActual = Utilidades.revisarPaginaActual(paginaActual);
		rows = Utilidades.revisarRangoRows(rows, 25);
		model.addAttribute("tituloPagina", ClientesHelper.TITULO_PAGINA);
		model.addAttribute("titulo", ClientesHelper.ENCABEZADO);
		model.addAttribute("tablaTitulo", ClientesHelper.TITULO_PAGINA);

		ClientesHelper.mensaje = (ClientesHelper.mensaje == null) ? ClientesHelper.INSTRUCCION_LISTAR
				: ClientesHelper.mensaje;
		model.addAttribute("mensaje", ClientesHelper.mensaje);

		List<Cliente> clientes = clienteService.listar(rows, paginaActual - 1);
		int ultimaPagina = this.clienteService.contarPaginacion(rows);
		int registrosMostrados = rows * paginaActual;
		if (clientes.isEmpty()) {
			System.out.println("No hay mas elementos por mostrar");
			clientes = this.clienteService.listar(rows, ultimaPagina - 1);
			registrosMostrados = this.clienteService.contarRegistros();
		}
		model.addAttribute("clientes", clientes);
		model.addAttribute("registrosMostrados", registrosMostrados);
		model.addAttribute("totalRegistros", this.clienteService.contarRegistros());
		model.addAttribute("paginaActual", paginaActual);
		model.addAttribute("ultimaPagina", ultimaPagina);
		model.addAttribute("controller", "clientes");
		model.addAttribute("rows", rows);
		if (!model.containsAttribute("busquedaCliente")) {
			model.addAttribute("busquedaCliente", new BusquedaCliente());
		}
		ClientesHelper.reiniciarVariables();
		return "cliente/clientes2";
	}

	@GetMapping("/formulario")
	public String formulario(Model model) {
		model.addAttribute("tituloPagina", ClientesHelper.TITULO_PAGINA);
		model.addAttribute("titulo", ClientesHelper.ENCABEZADO);

		ClientesHelper.ETIQUETA_TABLA_TITULO = (ClientesHelper.cliente == null) ? "Registrando" : "Editando ";
		model.addAttribute("tablaTitulo", ClientesHelper.ETIQUETA_TABLA_TITULO);

		ClientesHelper.mensaje = (ClientesHelper.mensaje == null) ? ClientesHelper.INSTRUCCION_FORMULARIO
				: ClientesHelper.mensaje;
		model.addAttribute("mensaje", ClientesHelper.mensaje);

		ClientesHelper.cliente = (ClientesHelper.cliente == null) ? new Cliente() : ClientesHelper.cliente;
		model.addAttribute("cliente", ClientesHelper.cliente);

		model.addAttribute("showDelete", ClientesHelper.SHOW_DELETE);

		List<Tarifa> tarifas = this.tarifasService.listar();
		model.addAttribute("tarifas", tarifas);

		ClientesHelper.reiniciarVariables();
		return "cliente/clienteForm";
	}

	@PostMapping("/guardar")
	public String guardar(@ModelAttribute("cliente") Cliente cliente, Model model) {
		try {
			if (ClientesHelper.validarCliente(cliente)) {
				clienteService.guardar(cliente);
				ClientesHelper.cliente = null;
				return "redirect:/clientes";
			} else {
				ClientesHelper.cliente = cliente;
				return "redirect:/clientes/formulario";
			}
		} catch (ConstraintViolationException e) {
			switch (e.getErrorCode()) {
			case 1062:
				ClientesHelper.mensaje = "No ha sido posible guardar el registro, el cups <Strong>" + cliente.getCups()
						+ "</Strong> ya ha sido registrado.";
				break;
			default:
				e.printStackTrace(System.out);
				break;
			}
			return "redirect:/clientes/formulario";
		} catch (Exception e) {
			System.out.println("Algo salio mal al guardar el registro");
			e.printStackTrace(System.out);
			return "redirect:/clientes/formulario";
		}
	}

	@GetMapping("/editar")
	public String editar(@RequestParam("idCliente") long idCliente) {
		ClientesHelper.cliente = this.clienteService.encontrarId(idCliente);
		ClientesHelper.SHOW_DELETE = "y";
		return "redirect:/clientes/formulario";
	}

	@GetMapping("/eliminar")
	public String eliminar(@RequestParam("idCliente") long idCliente, @RequestParam("cups") String cups) {
		this.clienteService.eliminar(idCliente);
		ClientesHelper.mensaje = ClientesHelper.FORMULARIO_EXITO_ELIMINAR + cups;
		return "redirect:/clientes";
	}

	@GetMapping("/detalles")
	public String detalles2(@ModelAttribute("busquedaCliente") @Valid final BusquedaCliente busquedaCliente, RedirectAttributes redirectAttributes,
			@RequestParam(required = false, name = "valor") final String valor,
			@RequestParam(required = false, name = "filtro") final String filtro, Model model) {
		redirectAttributes.addFlashAttribute("busquedaCliente", busquedaCliente);
		Cliente cliente = null;
		if (valor != null) {
			busquedaCliente.setValor(valor);
			busquedaCliente.setFiltro(filtro);
		}
		try {
			switch (busquedaCliente.getFiltro()) {
			case "id":
				cliente = this.clienteService.encontrarId(Integer.valueOf(busquedaCliente.getValor()));
				break;
			case "cups":
				cliente = this.clienteService.encontrarCups(busquedaCliente.getValor());
				break;
			}
			if (cliente != null) {
				model.addAttribute("tituloPagina", "Detalles cliente");
				model.addAttribute("titulo", "Detalles Cliente");
				model.addAttribute("mensaje", "Estos son los datos que se encontraron");
				model.addAttribute("cliente", cliente);
				model.addAttribute("busquedaCliente", busquedaCliente);
				return "cliente/detalle_cliente";
			}
			return "redirect:/clientes?sinregistro&v=" + busquedaCliente.getValor() + "&f="
					+ busquedaCliente.getFiltro();
		} catch (MasDeUnClienteEncontrado e) {
			return "redirect:/clientes?cupsiguales";
		} catch (NumberFormatException e) {
			return "redirect:/clientes?idnan";
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return "redirect:/clientes?unknown";
		}
	}

	@PostMapping("/busqueda")
	public final String busqueda(@ModelAttribute("busquedaCliente") @Valid final BusquedaCliente busquedaCliente,
			final BindingResult binding, RedirectAttributes redirectAttributes, Model model) {
		redirectAttributes.addFlashAttribute("busquedaCliente", busquedaCliente);
		if (binding.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.busquedaCliente",
					binding);
			return "redirect:/clientes";
		}
		List<Cliente> clientes = null;
		try {
			switch (busquedaCliente.getFiltro()) {
			case "id":
				return "redirect:/clientes/detalles";
			case "cups":
				if (busquedaCliente.getValor().length() < 20) {
					clientes = this.clienteService.encontrarCupsParcial(busquedaCliente.getValor());
					if (clientes.size() == 0) {
						ClientesHelper.mensaje = "No se encontro algun registro con el valor de <Strong>" + busquedaCliente.getValor() + "</Strong> y el filtro de <Strong>CUPS</Strong>";
						return "redirect:/clientes";
					} else if (clientes.size() == 1) {
						busquedaCliente.setValor(clientes.get(0).getCups());
						return "redirect:/clientes/detalles";
					}
					clientes.stream().forEach(c -> System.out.println(c));
				} else {
					return "redirect:/clientes/detalles";
				}
				break;
			case "nombre":
				clientes = this.clienteService.encontrarByNombre(busquedaCliente.getValor());
				break;
			}

			model.addAttribute("tituloPagina", "Busqueda de Clientes");
			model.addAttribute("titulo", "Busqueda de Clientes");
			model.addAttribute("tablaTitulo", "Clientes");
			model.addAttribute("registrosMostrados", "--");
			model.addAttribute("totalRegistros", clientes.size());
			model.addAttribute("mensaje", "Estos son los datos que se encontraron con el valor " + busquedaCliente.getValor() + " y el filtro de " + busquedaCliente.getFiltro());
			model.addAttribute("ultimaBusqueda", busquedaCliente.getValor());
			model.addAttribute("busquedaCliente", busquedaCliente);
			model.addAttribute("desactivarPaginacion", true);
			model.addAttribute("clientes", clientes);
			return "cliente/clientes2";
		} catch (NumberFormatException e) {
			System.out.println("AAA");
			e.printStackTrace(System.out);
			redirectAttributes.addAttribute("mensaje",
					"El filtro de <Strong>Cliente</Strong> solo acepta valores enteros, revisar el valor ingresado <Strong>"
							+ busquedaCliente.getValor() + "</Strong>");
			return "redirect:/clientes";
		} catch (Exception e) {
			System.out.println("BBB");
			e.printStackTrace(System.out);
			ClientesHelper.mensaje = "Algo ha salido mal :( reporte el bug o revise la consola para saber más";
			return "redirect:/clientes";
		}
	}

}
