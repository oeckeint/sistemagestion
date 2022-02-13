package controladores;

import controladores.helper.Etiquetas;
import datos.entity.Factura;
import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.MasDeUnClienteEncontrado;
import excepciones.NoEsUnNumeroException;
import excepciones.RegistroVacioException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
@Controller
@RequestMapping("/facturas")
public class Facturas {

    @Autowired
    @Qualifier(value = "facturasServiceImp")
    private DocumentoXmlService documentoXmlService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping("")
    public String listar(Model model) {
        model.addAttribute("tituloPagina", Etiquetas.FACTURAS_TITULO_PAGINA);
        model.addAttribute("titulo", Etiquetas.FACTURAS_ENCABEZADO);

        Etiquetas.FACTURAS_ETIQUETA_TABLA_TITULO = (Etiquetas.FACTURAS_ETIQUETA_TABLA_TITULO == null) ? "Registros" : Etiquetas.FACTURAS_ETIQUETA_TABLA_TITULO;
        model.addAttribute("tablaTitulo", Etiquetas.FACTURAS_ETIQUETA_TABLA_TITULO);

        Etiquetas.FACTURAS_MENSAJE = (Etiquetas.FACTURAS_MENSAJE == null) ? Etiquetas.FACTURAS_INSTRUCCION_LISTAR : Etiquetas.FACTURAS_MENSAJE;
        model.addAttribute("mensaje", Etiquetas.FACTURAS_MENSAJE);

        List<Factura> facturas = this.documentoXmlService.listar();
        model.addAttribute("documentos", facturas);
        model.addAttribute("totalRegistros", facturas.size());
        model.addAttribute("controller", Etiquetas.FACTURAS_CONTROLLER);

        this.reiniciarVariables();
        return "xml/lista";
    }

    @GetMapping("/detalles")
    public String verDetalles(@RequestParam("codFisFac") String codFisFac, Model model) {
        try {
            Factura factura = (Factura) this.documentoXmlService.buscarByCodFiscal(codFisFac);
            if (factura == null) {
                Etiquetas.FACTURAS_MENSAJE = "No se encontro registro con el cod factura <Strong>" + codFisFac + "</Strong>";
                return "redirect:/facturas";
            }
            model.addAttribute("tituloPagina", Etiquetas.FACTURAS_DETALLES_TITULO_PAGINA);
            model.addAttribute("titulo", Etiquetas.FACTURAS_DETALLES_ENCABEZADO);
            model.addAttribute("documento", factura);
            model.addAttribute("cliente", this.clienteService.encontrarCups(factura.getCups()));
            model.addAttribute("mensaje", "Se muestra el registro con el cod factura <Strong>" + codFisFac + "</Strong>");
            model.addAttribute("controller", "facturas");
            model.addAttribute("ultimaBusqueda", codFisFac);
            this.reiniciarVariables();
            return "xml/detalle";
        } catch (Exception e) {
            System.out.println("(facturas) " + e.getMessage());
        }
        return "redirect:/facturas";
    }

    @PostMapping("/busqueda")
    public String buscarRegistros(@RequestParam("filtro") String filtro, @RequestParam("valor") String valor, Model model) throws MasDeUnClienteEncontrado {
        if (valor.isEmpty()) {
            Etiquetas.FACTURAS_MENSAJE = "Debe ingresar un valor para buscar.";
            return "redirect:/facturas";
        }

        List<Factura> facturas = new ArrayList<>();
        try {
            switch (filtro) {
                case "cliente":
                    facturas = this.documentoXmlService.buscarByIdCliente(valor);
                    break;
                case "remesa":
                    facturas = this.documentoXmlService.buscarByRemesa(valor);
                    break;
                case "codFisFac":
                    return "redirect:/facturas/detalles?codFisFac=" + valor;
                default:
                    Etiquetas.FACTURAS_MENSAJE = "El filtro <Strong>" + filtro + "</Strong> no es válido";
                    break;
            }
            
            if (facturas.isEmpty() || facturas == null) {
                Etiquetas.FACTURAS_MENSAJE = "No se encontró coincidencia con el filtro de <Strong>" + filtro + "</Strong> y el valor de <Strong>" + valor + "</Strong>.";
                model.addAttribute("ultimaBusqueda", valor);
                return "redirect:/facturas";
            }

            model.addAttribute("tituloPagina", Etiquetas.FACTURAS_TITULO_PAGINA);
            model.addAttribute("titulo", Etiquetas.FACTURAS_ENCABEZADO);
            model.addAttribute("tablaTitulo", "Resultados");
            model.addAttribute("mensaje", "Estos son los resultados que se encontraron con el valor de <Strong>" + valor + "</Strong> y el filtro de <Strong>" + filtro + "</Strong>.");
            model.addAttribute("documentos", facturas);
            model.addAttribute("documentoResumen", this.resumen(facturas));
            model.addAttribute("totalRegistros", facturas.size());
            model.addAttribute("ultimaBusqueda", valor);
            model.addAttribute("controller", Etiquetas.FACTURAS_CONTROLLER);
            this.reiniciarVariables();
            
        } catch(NoEsUnNumeroException e){
            Etiquetas.FACTURAS_MENSAJE = "El filtro de <Strong>Cliente</Strong> solo acepta valores numericos, revisar el valor ingresado <Strong>" + valor + "</Strong>";
            return "redirect:/facturas";
        } catch(RegistroVacioException e){
            Etiquetas.FACTURAS_MENSAJE = "No se encontró coincidencia con el filtro de <Strong>" + filtro + "</Strong> y el valor de <Strong>" + valor + "</Strong>.";
            return "redirect:/facturas";
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.out.println("(facturas)" + e.getMessage());
            Etiquetas.FACTURAS_MENSAJE = "Algo ah salido mal :( por favor reporte el bug o revise el log.";
            return "redirect:/facturas";
        }
        return "xml/lista";
    }
    
    @PostMapping("/comentar")
    public String comentar(@RequestParam("comentario") String comentario, @RequestParam("codFisFac") String codFisFac){
        Factura factura = (Factura) documentoXmlService.buscarByCodFiscal(codFisFac);
        if (factura != null) {
            factura.setComentarios(comentario);
            documentoXmlService.guardar(factura);
        }
        return "redirect:/facturas/detalles?codFisFac=" + codFisFac;
    }
    
    @GetMapping("/archivar")
    public String archivar(@RequestParam("codFisFac") String codFisFac){
        Factura factura = (Factura) documentoXmlService.buscarByCodFiscal(codFisFac);
        if (factura != null) {
            if (factura.getIsDeleted() == 0) {
                factura.setIsDeleted(1);
            } else {
                factura.setIsDeleted(0);
            }
            documentoXmlService.actualizar(factura);
        }
        return "redirect:/facturas/detalles?codFisFac=" + codFisFac;
    }

    private Factura resumen(List<Factura> facturas) throws MasDeUnClienteEncontrado, RegistroVacioException {
        if (facturas.isEmpty()) {
            return null;
        }
        Factura factura = new Factura();
        double impPot = 0.0;
        double impEneAct = 0.0;
        double impFac = 0.0;

        for (Factura p : facturas) {
            if (p == null) {
                throw new RegistroVacioException();
            }
            impPot += p.getPotImpTot();
            impEneAct += p.getEaImpTot();
            impFac += p.getRfImpTot();
        }

        factura.setIdCliente(this.clienteService.encontrarCups(facturas.get(0).getCups()).getIdCliente());
        factura.setPotImpTot(impPot);
        factura.setEaImpTot(impEneAct);
        factura.setRfImpTot(impFac);

        return factura;
    }

    private void reiniciarVariables() {
        Etiquetas.FACTURAS_MENSAJE = null;
        Etiquetas.FACTURAS_CONTENIDO_VISIBLE = null;
        Etiquetas.FACTURAS_ETIQUETA_TABLA_TITULO = null;
    }

}
