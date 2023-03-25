package controladores.reclamaciones;

import controladores.Operaciones;
import controladores.helper.Utilidades;
import datos.entity.reclamaciones.BusquedaReclamacion;
import datos.entity.reclamaciones.Reclamacion;
import datos.interfaces.CrudDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/clientes/reclamaciones")
public class Reclamaciones extends Operaciones<BusquedaReclamacion>{
    private Logger logger = Logger.getLogger(getClass().getName());

    private final CrudDao<Reclamacion> reclamacionService;
    private final BusquedaReclamacion busquedaReclamacion;

    private boolean confirmacionArchivado;
    private boolean confirmacionDesarchivado;
    private boolean confirmacionNuevoComentario;

    public Reclamaciones(@Qualifier(value = "reclamacionServiceImp") CrudDao<Reclamacion> reclamacionService, BusquedaReclamacion busquedaReclamacion) {
        this.reclamacionService = reclamacionService;
        this.busquedaReclamacion = busquedaReclamacion;
    }

    @GetMapping("")
    @Override
    public ModelAndView listar(@RequestParam(required = false, defaultValue = "1", name = "page") Integer paginaActual,
                               @RequestParam(required = false,defaultValue = "50", name = "rows") Integer rows) {
        super.mv = new ModelAndView("reclamaciones/lista");

        rows = Utilidades.revisarRangoRows(rows, 25);
        paginaActual = Utilidades.revisarPaginaActual(paginaActual);
        List<Reclamacion> reclamaciones = this.reclamacionService.listar(rows, paginaActual - 1);

        super.mv.addObject("titulo", "<i class=\"fa fa-exclamation-circle\" aria-hidden=\"true\"></i> Reclamaciones");
        super.mv.addObject("registrosMostrados", (rows * paginaActual));
        super.mv.addObject("totalRegistros", this.reclamacionService.contarRegistros());
        super.mv.addObject("rows", rows);
        super.mv.addObject("paginaActual", paginaActual);
        super.mv.addObject("ultimaPagina", this.reclamacionService.contarPaginacion(rows));
        super.mv.addObject("busqueda", this.busquedaReclamacion);
        super.mv.addObject("controller", "clientes/reclamaciones");
        super.mv.addObject("reclamaciones", reclamaciones);

        return super.mv;
    }

    @GetMapping("/detalles")
    @Override
    public ModelAndView detalle(@RequestParam("valor") String valor,@RequestParam("filtro") String filtro) {
        super.mv = new ModelAndView("reclamaciones/reclamacion_detalle");
        Reclamacion reclamacion = null;
        List<Reclamacion> reclamaciones = null;
        String mensaje = "sinregistro";

        switch (filtro){
            case "reclamacion":
                reclamacion = this.reclamacionService.buscarId(Integer.parseInt(valor));
                if (reclamacion != null){
                    super.mv.addObject("reclamacion", reclamacion);
                    mensaje = "registroEncontrado";
                } else {
                    this.listar(1, 50);
                }
                break;
            case "cliente":
                reclamaciones = this.reclamacionService.buscarFiltro(valor, filtro);

                if (reclamaciones != null){
                    if (reclamaciones.size() == 1){
                        super.mv.addObject("reclamacion", reclamaciones.get(0));
                        mensaje = "registroEncontrado";
                    } else {
                        this.createViewForReclamacionesFilter(reclamaciones);
                        mensaje = "registrosEncontrados";
                    }
                } else {
                    super.mv = this.listar(1, 50);
                }
                break;
        }

        mensaje = confirmacionArchivado ? "registroarchivado" : mensaje;
        mensaje = confirmacionDesarchivado ? "registrodesarchivado" : mensaje;
        mensaje = confirmacionNuevoComentario ? "nuevoComentario" : mensaje;
        this.reiniciarVariablesBooleanas();

        busquedaReclamacion.setValorActual(valor);
        busquedaReclamacion.setFiltroActual(filtro);
        super.mv.addObject("mensaje", mensaje);
        super.mv.addObject("busqueda", this.busquedaReclamacion);
        super.mv.addObject("titulo", "<i class=\"fa fa-exclamation-circle\" aria-hidden=\"true\"></i> Reclamaciones");
        return super.mv;
    }

    @Override
    public ModelAndView listarFiltro() {
        return null;
    }

    @GetMapping("/agregar")
    @Override
    public ModelAndView agregar() {
        return null;
    }

    @PostMapping("/busqueda")
    @Override
    public ModelAndView busqueda(@ModelAttribute("busqueda") @Valid final BusquedaReclamacion busqueda, final BindingResult bindingResult) {
        if (!bindingResult.hasErrors()){
            return this.detalle(busqueda.getValorActual(), busqueda.getFiltroActual());
        }
        super.mv.addObject("busqueda", busqueda);
        return super.mv;
    }

    @GetMapping("/archivar")
    private ModelAndView archivar(@RequestParam(required = true, name = "id") final String id){
        Reclamacion r = reclamacionService.buscarId(Long.parseLong(id));
        if (r != null){
            if (r.getIsDeleted() == 0){
                r.setIsDeleted(1);
                this.confirmacionArchivado = true;
            } else if (r.getIsDeleted() == 1){
                r.setIsDeleted(0);
                this.confirmacionDesarchivado = true;
            }
            this.reclamacionService.actualizar(r);
            super.mv = new ModelAndView("redirect:/clientes/reclamaciones/detalles?valor=" + r.getIdReclamacion() + "&filtro=reclamacion");
        } else {
            super.mv = this.listar(1, 50);
        }
        return super.mv;
    }

    @PostMapping("/comentar")
    private ModelAndView comentar(@RequestParam("comentario") String comentario, @RequestParam("id") String id){
        try {
            Reclamacion r = this.reclamacionService.buscarId(Long.parseLong(id));
            if (r != null){
                r.setComentarios(comentario);
                this.reclamacionService.actualizar(r);
                this.confirmacionNuevoComentario = true;
            }
            super.mv = new ModelAndView("redirect:/clientes/reclamaciones/detalles?valor=" + r.getIdReclamacion() + "&filtro=reclamacion");
        } catch (Exception e){
            e.printStackTrace(System.out);
        }
        return super.mv;
    }



    @Override
    public ModelAndView listar() {
        return null;
    }

    private ModelAndView createViewForReclamacionesFilter(List<Reclamacion> reclamaciones) {
        super.mv = new ModelAndView("reclamaciones/lista");
        super.mv.addObject("reclamaciones", reclamaciones);
        super.mv.addObject("titulo", "Reclamaciones");
        super.mv.addObject("totalRegistros", reclamaciones.size());
        super.mv.addObject("desactivarPaginacion", true);
        return super.mv;
    }

    private void reiniciarVariablesBooleanas(){
        this.confirmacionDesarchivado = false;
        this.confirmacionArchivado = false;
        this.confirmacionNuevoComentario = false;
    }


}
