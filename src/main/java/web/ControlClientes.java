package web;

import datos.ClienteDao;
import dominio.Cliente;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ControlClientes")
public class ControlClientes extends HttpServlet {
    String icono = "<i class='fas fa-users'></i>";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession();
        sesion.setAttribute("tituloPagina", "Control de Clientes");
        sesion.setAttribute("titulo", icono + " Control de Clientes");
        String accion = request.getParameter("accion");
        System.out.println("Validando " + accion);
        if (accion != null) {
            switch (accion) {
                case "editar":
                    sesion.setAttribute("mensajeRegistro", "Usted esta editando un registro");
                    this.editarCliente(request, response);
                    break;
                case "eliminar":
                    sesion.setAttribute("mensajeRegistro", "Registro archivado, ya no será mostrado pero aún se encuentra en la base de datos.");
                    this.eliminarCliente(request, response);
                    break;
                case "cancelar":
                    sesion.setAttribute("mensajeRegistro", "No se actualizó ningún registro");
                    this.accionDefault(request, response);
                    break;
                default:
                    sesion.setAttribute("mensajeRegistro", "!Vaya!, Algo no ha salido bien :(");
                    this.accionDefault(request, response);
                    break;
            }
        } else {
            sesion.setAttribute("mensajeRegistro", "Use la herramienta de búsqueda para encontrar fácilmente un registro.");
            this.accionDefault(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession();
        sesion.setAttribute("titulo", "Control de Clientes");
        String accion = request.getParameter("accion");
        System.out.println("Validando " + accion);
        if (accion != null) {
            switch (accion) {
                case "insertar":
                    sesion.setAttribute("mensajeRegistro", "Registro exitoso");
                    this.insertarCliente(request, response);
                    break;
                case "actualizar":
                    sesion.setAttribute("mensajeRegistro", "Actualización exitosa");
                    this.actualizarCliente(request, response);
                    break;
                default:
                    sesion.setAttribute("mensajeRegistro", "!Vaya!, Algo no ha salido bien :(");
                    System.out.println("Default");
                    this.accionDefault(request, response);
                    break;
            }
        } else {
            sesion.setAttribute("mensajeRegistro", "");
            this.accionDefault(request, response);
        }
    }

    private void accionDefault(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Cliente> clientes = new ClienteDao().listar();
        HttpSession sesion = request.getSession();
        sesion.setAttribute("clientes", clientes);
        sesion.setAttribute("totalClientes", clientes.size());

        //request.getRequestDispatcher("clientes.jsp").forward(request, response);
        response.sendRedirect("clientes.jsp");

    }

    private void insertarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cups = request.getParameter("cups");
        String nombre = request.getParameter("nombre");
        String tarifa = request.getParameter("tarifa");

        Cliente cliente = new Cliente(cups, nombre, tarifa);

        int registrosModificados = new ClienteDao().insertar(cliente);
        System.out.println("registrosModificados = " + registrosModificados);

        this.accionDefault(request, response);
    }

    private void editarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cliente cliente = new ClienteDao().encontrar(new Cliente(Integer.parseInt(request.getParameter("idCliente"))));
        request.setAttribute("cliente", cliente);
        String jspEditar = "/WEB-INF/paginas/cliente/editarCliente.jsp";
        request.getRequestDispatcher(jspEditar).forward(request, response);
    }

    private void actualizarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idCliente = request.getParameter("idCliente");
        String cups = request.getParameter("cups");
        String nombre = request.getParameter("nombre");
        String tarifa = request.getParameter("tarifa");

        int registrosModificados = new ClienteDao().actualizar(new Cliente(Integer.parseInt(idCliente), cups, nombre, tarifa));
        System.out.println("registrosModificados = " + registrosModificados);

        this.accionDefault(request, response);
    }

    private void eliminarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int registrosModificados = new ClienteDao().eliminar(new Cliente(Integer.parseInt(request.getParameter("idCliente"))));
        System.out.println("registrosModificados = " + registrosModificados);

        this.accionDefault(request, response);
    }
}
