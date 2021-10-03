package controladores;

import controladores.helper.Etiquetas;
import datos.entity.OtraFactura;
import datos.interfaces.ClienteService;
import datos.interfaces.DocumentoXmlService;
import excepciones.MasDeUnClienteEncontrado;
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
@RequestMapping("/otrasfacturas")
public class OtrasFacturas {
    
    @Autowired
    @Qualifier(value = "otrasFacturasServiceImp")
    private DocumentoXmlService documentoXmlService;
    
    @Autowired
    ClienteService clienteService;
    
    @GetMapping("")
    public String listar(Model model){
        model.addAttribute("tituloPagina", Etiquetas.OTRAS_FACTURAS_TITULO_PAGINA);
        model.addAttribute("titulo", Etiquetas.OTRAS_FACTURAS_ENCABEZADO);

        Etiquetas.OTRAS_FACTURAS_ETIQUETA_TABLA_TITULO = (Etiquetas.OTRAS_FACTURAS_ETIQUETA_TABLA_TITULO == null) ? "Registros" : Etiquetas.OTRAS_FACTURAS_ETIQUETA_TABLA_TITULO;
        model.addAttribute("tablaTitulo", Etiquetas.OTRAS_FACTURAS_ETIQUETA_TABLA_TITULO);

        Etiquetas.OTRAS_FACTURAS_MENSAJE = (Etiquetas.OTRAS_FACTURAS_MENSAJE == null) ? Etiquetas.OTRAS_FACTURAS_INSTRUCCION_LISTAR : Etiquetas.OTRAS_FACTURAS_MENSAJE;
        model.addAttribute("mensaje", Etiquetas.OTRAS_FACTURAS_MENSAJE);

        List<OtrasFacturas> facturas = this.documentoXmlService.listar();
        model.addAttribute("documentos", facturas);
        model.addAttribute("totalRegistros", facturas.size());
        model.addAttribute("controller", Etiquetas.OTRAS_FACTURAS_CONTROLLER);

        this.reiniciarVariables();
        return "xml/lista_otras_facturas";
    }
    
    @GetMapping("/detalles")
    public String verDetalles(@RequestParam("codFisFac") String codFisFac, Model model) {
        try {
            OtraFactura factura = (OtraFactura) this.documentoXmlService.buscarByCodFiscal(codFisFac);
            if (factura == null) {
                Etiquetas.OTRAS_FACTURAS_MENSAJE = "No se encontro registro con el cod factura <Strong>" + codFisFac + "</Strong>";
                return "redirect:/otrasfacturas";
            }
            model.addAttribute("tituloPagina", Etiquetas.OTRAS_FACTURAS_DETALLES_TITULO_PAGINA);
            model.addAttribute("titulo", Etiquetas.OTRAS_FACTURAS_DETALLES_ENCABEZADO);
            model.addAttribute("documento", factura);
            model.addAttribute("cliente", this.clienteService.encontrarCups(factura.getCups()));
            model.addAttribute("mensaje", "Se muestra el registro con el cod factura <Strong>" + codFisFac + "</Strong>");
            model.addAttribute("controller", Etiquetas.OTRAS_FACTURAS_CONTROLLER);
            this.reiniciarVariables();
            return "xml/detalle_otras_facturas";
        } catch (Exception e) {
            System.out.println("(otrasfacturas) " + e.getMessage());
        }
        return "redirect:/otrasfacturas";
    }
    
    @PostMapping("/busqueda")
    public String buscarRegistros(@RequestParam("filtro") String filtro, @RequestParam("valor") String valor, Model model) throws MasDeUnClienteEncontrado {
        if (valor.isEmpty()) {
            Etiquetas.OTRAS_FACTURAS_MENSAJE = "Debe ingresar un valor para buscar.";
            return "redirect:/otrasfacturas";
        }

        List<OtraFactura> facturas = null;
        try {
            switch (filtro) {
                case "cliente":
                    facturas = this.documentoXmlService.buscarByIdCliente(valor);
                    break;
                case "remesa":
                    facturas = this.documentoXmlService.buscarByRemesa(valor);
                    break;
                default:
                    Etiquetas.OTRAS_FACTURAS_MENSAJE = "El filtro <Strong>" + filtro + "</Strong> no es válido";
                    break;
            }
            if (facturas.isEmpty()) {
                Etiquetas.OTRAS_FACTURAS_MENSAJE = "No se encontro coincidencia con el filtro de <Strong>" + filtro + "</Strong> y el valor de <Strong>" + valor + "</Strong>";
                return "redirect:/otrasfacturas";
            }
        } catch (Exception e) {
            System.out.println("(otrasfacturas)" + e.getMessage());
            Etiquetas.OTRAS_FACTURAS_MENSAJE = "No se encontro coincidencia con el filtro de <Strong>" + filtro + "</Strong> y el valor de <Strong>" + valor + "</Strong>";
            this.reiniciarVariables();
            return "redirect:/otrasfacturas";
        }

        model.addAttribute("tituloPagina", Etiquetas.OTRAS_FACTURAS_TITULO_PAGINA);
        model.addAttribute("titulo", Etiquetas.OTRAS_FACTURAS_ENCABEZADO);
        model.addAttribute("tablaTitulo", "Resultados");
        model.addAttribute("mensaje", "Estos son los resultados que se encontraron con el valor de " + valor + " y el filtro de " + filtro);
        model.addAttribute("documentos", facturas);
        //model.addAttribute("documentoResumen", this.resumen(facturas));
        model.addAttribute("totalRegistros", facturas.size());
        model.addAttribute("ultimaBusqueda", valor);
        model.addAttribute("controller", Etiquetas.OTRAS_FACTURAS_CONTROLLER);
        this.reiniciarVariables();
        return "xml/lista";
    }
    
    private OtraFactura resumen(List<OtraFactura> facturas) throws MasDeUnClienteEncontrado {
        if (facturas.isEmpty()) {
            return null;
        }
        OtraFactura factura = new OtraFactura();
        double impPot = 0.0;
        double impEneAct = 0.0;
        double impFac = 0.0;

        for (OtraFactura p : facturas) {
            //impPot += p.getPotImpTot();
            //impEneAct += p.getEaImpTot();
            //impFac += p.getRfImpTot();
        }

        factura.setIdCliente((int) this.clienteService.encontrarCups(facturas.get(0).getCups()).getIdCliente());
        //factura.setPotImpTot(impPot);
        //factura.setEaImpTot(impEneAct);
        //factura.setRfImpTot(impFac);

        return factura;
    }
    
    private void reiniciarVariables(){
        Etiquetas.OTRAS_FACTURAS_MENSAJE = null;
        Etiquetas.OTRAS_FACTURAS_CONTENIDO_VISIBLE = null;
        Etiquetas.OTRAS_FACTURAS_ETIQUETA_TABLA_TITULO = null;
    }
    
}
