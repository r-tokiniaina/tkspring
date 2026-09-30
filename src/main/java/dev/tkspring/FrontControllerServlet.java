package dev.tkspring;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import dev.tkspring.UrlMapping;
import dev.tkspring.ModelAndView;
import dev.tkspring.UrlInfo;
import dev.tkspring.Utils;
import dev.tkspring.annotation.AsJson;
import dev.tkspring.annotation.Controller;
import dev.tkspring.annotation.Url;
import dev.tkspring.constant.HttpMethod;

public class FrontControllerServlet extends HttpServlet {

    private String viewFormat;
    private Map<UrlInfo, UrlMapping> actions;

    @Override
    public void init() throws ServletException {
        String viewPrefix = this.getInitParameter("view-prefix");
        String viewSuffix = this.getInitParameter("view-suffix");
        viewFormat = viewPrefix + "%s" + viewSuffix;

        ServletContext context = this.getServletContext();
        actions = (Map<UrlInfo, UrlMapping>) context.getAttribute("actions");
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
        UrlInfo urlInfo = new UrlInfo(HttpMethod.valueOf(request.getMethod()), request.getServletPath());

        if (actions.containsKey(urlInfo)) {
            UrlMapping mapping = actions.get(urlInfo);

            try {
                Object controller = mapping.getController().getDeclaredConstructor().newInstance();

                Object[] args = new Object[mapping.getMethod().getParameterCount()];
                Class<?>[] argsTypes = mapping.getMethod().getParameterTypes();
                for (int i = 0; i < args.length; i++) {
                    Class<?> type = argsTypes[i];
                    if (type.equals(ServletContext.class)) {
                        args[i] = getServletContext();
                    }
                }

                Object returnValue = mapping.getMethod().invoke(controller, args);

                if (mapping.getMethod().isAnnotationPresent(AsJson.class)) {
                    AsJson asJson = (AsJson) mapping.getMethod().getAnnotation(AsJson.class);

                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    if (asJson.raw()) {
                        out.print(returnValue);
                    }
                    else {
                        Gson gson = new Gson();
                        out.print(gson.toJson(returnValue));
                    }
                }
                else {
                    if (returnValue instanceof ModelAndView) {
                        ModelAndView mav = (ModelAndView) returnValue;

                        for (Map.Entry<String, Object> entry : mav.getModel().entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
                        }

                        RequestDispatcher dispatcher = request.getRequestDispatcher(
                            String.format(viewFormat, mav.getView())
                        );
                        dispatcher.forward(request, response);
                    }
                }
            }
            catch (Exception e) {
                throw new ServletException(e);
            }
        }
        else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<head><title>TKSpring</title></head>");
            out.println("<body>");
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
            out.println("</body>");
            out.println("</html>");
        }
    }
}
