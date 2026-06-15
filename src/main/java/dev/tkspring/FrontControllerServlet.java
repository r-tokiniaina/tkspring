package dev.tkspring;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class FrontControllerServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>TKSpring</title></head>");
        out.println("<body>");
        out.println("<h1>Ça marche!</h1>");
        out.println("<p><strong>Méthode:</strong> " + request.getMethod() + "</p>");
        out.println("<p><strong>Route:</strong> " + request.getServletPath() + "</p>");
        out.println("</body>");
        out.println("</html>");
    }
}
