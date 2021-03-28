
package web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/inicio")
public class Inicio extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        HttpSession sesion = request.getSession();
        String icono = "<i class='fas fa-home'></i>";
        String titulo = "Sistema de gestión";
        sesion.setAttribute("tituloPagina", titulo);
        sesion.setAttribute("titulo", icono + " " + titulo);
        request.getRequestDispatcher("inicio.jsp").forward(request, response);
    }
}
