package controladores.reclamaciones;

import controladores.Operaciones;
import controladores.helper.Utilidades;
import datos.entity.reclamaciones.Reclamacion;
import datos.interfaces.CrudDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/clientes/reclamaciones")
public class Reclamaciones extends Operaciones{
    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    @Qualifier(value = "reclamacionServiceImp")
    private CrudDao<Reclamacion> reclamacionService;

    @GetMapping("")
    @Override
    public ModelAndView listar() {
        return null;
    }
    @GetMapping("/listar")
    @Override
    public ModelAndView listar(@RequestParam(required = false, defaultValue = "1", name = "page") Integer paginaActual,
                               @RequestParam(required = false,defaultValue = "50", name = "rows") Integer rows) {
        //List<Reclamacion> = reclamacionService.listar();
        super.mv = new ModelAndView("reclamaciones/lista");
        super.mv.addObject("titulo", "Reclamaciones");

        //Paginacion
        paginaActual = Utilidades.revisarPaginaActual(paginaActual);
        int ultimaPagina = this.reclamacionService.contarPaginacion(rows);
        rows = Utilidades.revisarRangoRows(rows, 25);
        List<Reclamacion> reclamaciones = this.reclamacionService.listar();
        int registrosMostrados = rows * paginaActual;
        super.mv.addObject("reclamaciones", reclamaciones);
        super.mv.addObject("totalRegistros", this.reclamacionService.contarRegistros());
        super.mv.addObject("paginaActual", paginaActual);
        super.mv.addObject("ultimaPagina", ultimaPagina);
        super.mv.addObject("rows", rows);
        super.mv.addObject("registrosMostrados", registrosMostrados);
        return super.mv;
    }
    @GetMapping("/listarFiltro")
    @Override
    public ModelAndView listarFiltro() {
        System.out.println("Dentro del método listarFiltro");
        return null;
    }
    @GetMapping("/detalles")
    @Override
    public ModelAndView detalle(@RequestParam("valor") String valor,@RequestParam("filtro") String filtro) {
        Reclamacion reclamacion = null;
        List<Reclamacion> reclamaciones = null;
        super.mv = new ModelAndView("reclamaciones/reclamacion_detalle");
        switch (filtro){
            case "reclamacion":
                reclamacion = this.reclamacionService.buscarId(Integer.parseInt(valor));
                super.mv.addObject("reclamacion", reclamacion);
                if(reclamacion == null){
                    this.listar(1, 50);
                    super.mv.addObject("error", "sinregistro");
                }
                break;
            case "CUPS":
                System.out.println("Dentro del case CUPS");
                break;
            case "Cliente":
                System.out.println("Dentro del case Cliente");
                break;
            case "ATR":
                System.out.println("Dentro del case ATR");
                break;
        }
        super.mv.addObject("titulo", "Reclamaciones");
        return super.mv;
    }

    @GetMapping("/agregar")
    @Override
    public ModelAndView agregar() {
        System.out.println();
        return null;
    }
}
