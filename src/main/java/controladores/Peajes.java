package controladores;

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
        model.addAttribute("tituloPagina", PeajesHelper.TITULO_PAGINA);
        model.addAttribute("titulo", PeajesHelper.ENCABEZADO);

        PeajesHelper.ETIQUETA_TABLA_TITULO = (PeajesHelper.ETIQUETA_TABLA_TITULO == null) ? "Registros" : PeajesHelper.ETIQUETA_TABLA_TITULO;
        model.addAttribute("tablaTitulo", ClientesHelper.ETIQUETA_TABLA_TITULO);

        PeajesHelper.MENSAJE = (PeajesHelper.MENSAJE == null) ? PeajesHelper.INSTRUCCION_LISTAR : PeajesHelper.MENSAJE;
        model.addAttribute("mensaje", PeajesHelper.MENSAJE);

        List<Peaje> peajes = this.documentoXmlService.listar();
        model.addAttribute("documentos", peajes);
        model.addAttribute("totalRegistros", peajes.size());

        PeajesHelper.reiniciarVariables();
        return "xml/lista";
    }

    @GetMapping("/detalles")
    public String verDetalles(@RequestParam("codFisFac") String codFisFac, Model model) {
        try {
            Peaje peaje = (Peaje) this.documentoXmlService.buscarByCodFiscal(codFisFac);
            if (peaje == null) {
                PeajesHelper.MENSAJE = "No se encontro registro con el cod factura <Strong>" + codFisFac + "</Strong>";
                return "redirect:/peajes";
            }
            model.addAttribute("tituloPagina", PeajesHelper.DETALLES_TITULO_PAGINA);
            model.addAttribute("titulo", PeajesHelper.DETALLES_ENCABEZADO);
            model.addAttribute("documento", peaje);
            model.addAttribute("cliente", this.clienteService.encontrarCups(peaje.getCups()));
            model.addAttribute("mensaje", "Se muestra el registro con el cod factura <Strong>" + codFisFac + "</Strong>");
            PeajesHelper.reiniciarVariables();
            return "xml/detalle";
        } catch (Exception e) {
            System.out.println("(peajes) " + e.getMessage());
        }
        return "redirect:/peajes";
    }

    @PostMapping("/busqueda")
    public String buscarRegistros(@RequestParam("filtro") String filtro, @RequestParam("valor") String valor, Model model) {
        if (valor.isEmpty()) {
            PeajesHelper.MENSAJE = "Debe ingresar un valor para buscar.";
            return "redirect:/peajes";
        }

        List<Peaje> peajes = null;
        try {
            switch (filtro) {
                case "cliente":
                    peajes = this.documentoXmlService.buscarByIdCliente(valor);
                    break;
                case "remesa":
                    peajes = this.documentoXmlService.buscarByRemesa(valor);
                    break;
                default:
                    PeajesHelper.MENSAJE = "El filtro <Strong>" + filtro + "</Strong> no es válido";
                    break;
            }
            if (peajes.isEmpty()) {
                return "redirect:/peajes";
            }
        } catch (Exception e) {
            System.out.println("(Peajes)" + e.getMessage());
            PeajesHelper.MENSAJE = "No se encontro coincidencia con el filtro de <Strong>" + filtro + "</Strong> y el valor de <Strong>" + valor + "</Strong>";
            PeajesHelper.reiniciarVariables();
            return "redirect:/peajes";
        }

        model.addAttribute("tablaTitulo", "Resultados");
        model.addAttribute("mensaje", "Estos son los resultados que se encontraron con el valor de " + valor + " y el filtro de " + filtro);
        model.addAttribute("documentos", peajes);
        model.addAttribute("documentoResumen", this.resumen(peajes));
        model.addAttribute("totalRegistros", peajes.size());
        model.addAttribute("ultimaBusqueda", valor);
        PeajesHelper.reiniciarVariables();
        return "xml/lista";
    }

    private Peaje resumen(List<Peaje> peajes) {
        if (peajes.isEmpty()) {
            return null;
        }
        Peaje peaje = new Peaje();
        double impPot = 0.0;
        double impEneAct = 0.0;
        double impFac = 0.0;

        for (Peaje p : peajes) {
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
}
