package dev.tkspring;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import dev.tkspring.Utils;
import dev.tkspring.annotation.Controller;

public class FrontControllerServlet extends HttpServlet {

    private List<Class<?>> controllers;

    @Override
    public void init() throws ServletException {
        String basePackages = this.getInitParameter("base-package");
        try {
            controllers = Utils.findClassesByAnnotation(Controller.class, basePackages.split(";"));
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
    }

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
        out.println("<p>");
        out.println("<strong>Controlleurs:</strong>");
        out.println("<ul>");
        for (Class<?> controller : controllers) {
            out.println("<li>" + controller.getName() + "</li>");
        }
        out.println("</ul>");
        out.println("</p>");
        out.println("</body>");
        out.println("</html>");
    }
}
