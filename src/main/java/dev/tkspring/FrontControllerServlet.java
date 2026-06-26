package dev.tkspring;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import dev.tkspring.UrlMapping;
import dev.tkspring.UrlInfo;
import dev.tkspring.Utils;
import dev.tkspring.annotation.Controller;
import dev.tkspring.annotation.Url;
import dev.tkspring.constant.HttpMethod;

public class FrontControllerServlet extends HttpServlet {

    private Map<UrlInfo, UrlMapping> actions;

    @Override
    public void init() throws ServletException {
        actions = new HashMap<>();
        String basePackages = this.getInitParameter("base-package");

        try {
            Utils.findMethodsByAnnotation(basePackages.split(";"), Controller.class, Url.class, (method) -> {
                UrlInfo url = new UrlInfo((Url) method.getAnnotation(Url.class));
                actions.put(url, new UrlMapping(url, method));
            });
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

        UrlInfo urlInfo = new UrlInfo(HttpMethod.valueOf(request.getMethod()), request.getServletPath());

        if (actions.containsKey(urlInfo)) {
            UrlMapping mapping = actions.get(request.getServletPath());
            out.println("<h1>Ça marche!</h1>");
            out.println("<p><strong>Méthode:</strong> " + request.getMethod() + "</p>");
            out.println("<p><strong>Route:</strong> " + request.getServletPath() + "</p>");
            out.println("<p><strong>Action:</strong> " + mapping.getController().getName() + "::" + mapping.getMethod().getName() + "</p>");
        }
        else {
            out.println("<h1>Route inconnue!</h1>");
            out.println("<p>");
            out.println("<strong>Actions connues:</strong>");
            out.println("<ul>");
            for (UrlInfo url : actions.keySet()) {
                UrlMapping mapping = actions.get(url);
                out.println("<li>" + url.getUrl() + " → " + mapping.getController().getName() + "::" + mapping.getMethod().getName() + "</li>");
            }
            out.println("</ul>");
            out.println("</p>");
        }

        out.println("</body>");
        out.println("</html>");
    }
}
