package controladores;

import controladores.helper.Etiquetas;
import datos.entity.Peaje;
import datos.interfaces.ClienteService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import datos.interfaces.DocumentoXmlService;
import excepciones.MasDeUnClienteEncontrado;
import excepciones.NoEsUnNumeroException;
import excepciones.RegistroVacioException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Qualifier;

@Controller
@RequestMapping("/peajes")
public class Peajes {

    @Autowired
    @Qualifier(value = "peajesServiceImp")
    private DocumentoXmlService documentoXmlService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping("")
    public String listar(Model model) {
        model.addAttribute("tituloPagina", Etiquetas.PEAJES_TITULO_PAGINA);
        model.addAttribute("titulo", Etiquetas.PEAJES_ENCABEZADO);

        Etiquetas.PEAJES_ETIQUETA_TABLA_TITULO = (Etiquetas.PEAJES_ETIQUETA_TABLA_TITULO == null) ? "Registros" : Etiquetas.PEAJES_ETIQUETA_TABLA_TITULO;
        model.addAttribute("tablaTitulo", Etiquetas.PEAJES_ETIQUETA_TABLA_TITULO);

        Etiquetas.PEAJES_MENSAJE = (Etiquetas.PEAJES_MENSAJE == null) ? Etiquetas.PEAJES_INSTRUCCION_LISTAR : Etiquetas.PEAJES_MENSAJE;
        model.addAttribute("mensaje", Etiquetas.PEAJES_MENSAJE);

        List<Peaje> peajes = this.documentoXmlService.listar();
        model.addAttribute("documentos", peajes);
        model.addAttribute("totalRegistros", peajes.size());
        model.addAttribute("controller", Etiquetas.PEAJES_CONTROLLER);
        this.reiniciarVariables();
        return "xml/lista";
    }

    @GetMapping("/detalles")
    public String verDetalles(@RequestParam("codFisFac") String codFisFac, Model model) {
        try {
            Peaje peaje = (Peaje) this.documentoXmlService.buscarByCodFiscal(codFisFac);
            if (peaje == null) {
                Etiquetas.PEAJES_MENSAJE = "No se encontro registro con el cod factura <Strong>" + codFisFac + "</Strong>";
                return "redirect:/peajes";
            }
            model.addAttribute("tituloPagina", Etiquetas.PEAJES_DETALLES_TITULO_PAGINA);
            model.addAttribute("titulo", Etiquetas.PEAJES_DETALLES_ENCABEZADO);
            model.addAttribute("documento", peaje);
            model.addAttribute("cliente", this.clienteService.encontrarCups(peaje.getCups()));
            model.addAttribute("mensaje", "Se muestra el registro con el cod factura <Strong>" + codFisFac + "</Strong>");
            model.addAttribute("controller", "peajes");
            model.addAttribute("ultimaBusqueda", codFisFac);
            this.reiniciarVariables();
            return "xml/detalle";
        } catch (Exception e) {
            System.out.println("(peajes) " + e.getMessage());
        }
        return "redirect:/peajes";
    }

    @PostMapping("/busqueda")
    public String buscarRegistros(@RequestParam("filtro") String filtro, @RequestParam("valor") String valor, Model model) throws MasDeUnClienteEncontrado {
        if (valor.isEmpty()) {
            Etiquetas.PEAJES_MENSAJE = "Debe ingresar un valor para buscar.";
            return "redirect:/peajes";
        }

        List<Peaje> peajes = new ArrayList<>();
        try {
            switch (filtro) {
                case "cliente":
                    peajes = this.documentoXmlService.buscarByIdCliente(valor);
                    break;
                case "remesa":
                    peajes = this.documentoXmlService.buscarByRemesa(valor);
                    break;
                case "codFisFac":
                    return "redirect:/peajes/detalles?codFisFac=" + valor;
                default:
                    Etiquetas.PEAJES_MENSAJE = "El filtro <Strong>" + filtro + "</Strong> no es válido";
                    break;
            }

            if (peajes.isEmpty() || peajes == null) {
                Etiquetas.PEAJES_MENSAJE = "No se encontró coincidencia con el filtro de <Strong>" + filtro + "</Strong> y el valor de <Strong>" + valor + "</Strong>.";
                return "redirect:/peajes";
            }

            model.addAttribute("tituloPagina", Etiquetas.PEAJES_TITULO_PAGINA);
            model.addAttribute("titulo", Etiquetas.PEAJES_ENCABEZADO);
            model.addAttribute("tablaTitulo", "Resultados");
            model.addAttribute("mensaje", "Estos son los resultados que se encontraron con el valor de <Strong>" + valor + "</Strong> y el filtro de <Strong>" + filtro + "</Strong>.");
            model.addAttribute("documentos", peajes);
            model.addAttribute("documentoResumen", this.resumen(peajes));
            model.addAttribute("totalRegistros", peajes.size());
            model.addAttribute("ultimaBusqueda", valor);
            model.addAttribute("controller", Etiquetas.PEAJES_CONTROLLER);
            this.reiniciarVariables();

        } catch (NoEsUnNumeroException e) {
            Etiquetas.PEAJES_MENSAJE = "El filtro de <Strong>Cliente</Strong> solo acepta valores enteros, revisar el valor ingresado <Strong>" + valor + "</Strong>";
            return "redirect:/peajes";
        } catch (RegistroVacioException e) {
            Etiquetas.FACTURAS_MENSAJE = "No se encontró coincidencia con el filtro de <Strong>" + filtro + "</Strong> y el valor de <Strong>" + valor + "</Strong>.";
            return "redirect:/peajes";
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.out.println("(Peajes-controlador)" + e.getMessage());
            Etiquetas.PEAJES_MENSAJE = "Algo ah salido mal :( por favor reporte el bug o revise el log.";
            return "redirect:/peajes";
        }

        return "xml/lista";

    }

    private Peaje resumen(List<Peaje> peajes) throws MasDeUnClienteEncontrado, RegistroVacioException {
        if (peajes.isEmpty()) {
            return null;
        }
        Peaje peaje = new Peaje();
        double impPot = 0.0;
        double impEneAct = 0.0;
        double impFac = 0.0;

        for (Peaje p : peajes) {
            if (p == null) {
                throw new RegistroVacioException();
            }
            impPot += p.getPotImpTot();
            impEneAct += p.getEaImpTot();
            impFac += p.getRfImpTot();
        }

        peaje.setIdCliente(this.clienteService.encontrarCups(peajes.get(0).getCups()).getIdCliente());
        peaje.setPotImpTot(impPot);
        peaje.setEaImpTot(impEneAct);
        peaje.setRfImpTot(impFac);

        return peaje;
    }

    private void reiniciarVariables() {
        Etiquetas.PEAJES_MENSAJE = null;
        Etiquetas.PEAJES_CONTENIDO_VISIBLE = null;
        Etiquetas.PEAJES_ETIQUETA_TABLA_TITULO = null;
    }
}
