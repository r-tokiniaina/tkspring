package dev.tkspring;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

import com.google.gson.Gson;
import dev.tkspring.ModelAndView;
import dev.tkspring.UrlInfo;
import dev.tkspring.UrlMapping;
import dev.tkspring.Utils;
import dev.tkspring.annotation.AsJson;
import dev.tkspring.annotation.RequireAuth;
import dev.tkspring.annotation.RequireRoles;
import dev.tkspring.constant.HttpMethod;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@MultipartConfig
public class FrontControllerServlet extends HttpServlet {

    private String viewFormat;
    private String authAttr;
    private String roleAttr;
    private Map<UrlInfo, UrlMapping> actions;

    @Override
    public void init() throws ServletException {
        String viewPrefix = this.getInitParameter("view-prefix");
        String viewSuffix = this.getInitParameter("view-suffix");
        viewFormat = viewPrefix + "%s" + viewSuffix;

        authAttr = this.getInitParameter("auth-attr");
        roleAttr = this.getInitParameter("role-attr");

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

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UrlInfo urlInfo = new UrlInfo(HttpMethod.valueOf(request.getMethod()), request.getServletPath());

        if (actions.containsKey(urlInfo)) {
            UrlMapping mapping = actions.get(urlInfo);

            try {
                Session session = null;

                if (mapping.getMethod().isAnnotationPresent(RequireAuth.class) || mapping.getMethod().isAnnotationPresent(RequireRoles.class)) {
                    if (authAttr == null) {
                        System.out.println("WARNING: Il faut définir 'auth-attr' dans web.xml afin d’utiliser @RequireAuth et @RequireRole");
                    }
                    else {
                        session = Utils.getSessionOrPut(request);
                        Object authValue = session.get(authAttr);
                        if (authValue == null) {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Accès non autorisé");
                            return;
                        }
                        if (mapping.getMethod().isAnnotationPresent(RequireRoles.class)) {
                            if (roleAttr == null) {
                                System.out.println("WARNING: Il faut définir 'role-attr' dans web.xml afin d’utiliser @RequireRole");
                            }
                            else {
                                Object roleValue = session.get(roleAttr);
                                RequireRoles requireRoles = mapping.getMethod().getAnnotation(RequireRoles.class);
                                if (! Arrays.asList(requireRoles.value()).contains(roleValue)) {
                                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé");
                                    return;
                                }
                            }
                        }
                    }
                }

                Map<String, Object> requestParameterTree = Utils.getRequestParameterTree(request);
                Parameter[] methodParameters = mapping.getMethod().getParameters();

                Object[] mArgs = new Object[methodParameters.length];
                for (int i = 0; i < methodParameters.length; i++) {
                    String mpName = methodParameters[i].getName();
                    Class<?> mpType = methodParameters[i].getType();
                    if (mpType.equals(Session.class)) {
                        if (session == null) {
                            session = Utils.getSessionOrPut(request);
                        }
                        mArgs[i] = session;
                    }
                    else {
                        Object rpValuesObj = requestParameterTree.get(mpName);
                        mArgs[i] = Utils.parse(rpValuesObj, mpType);
                    }
                }

                Object controller = Utils.getControllerInstanceOrPut(request, mapping.getController());
                Object returnValue = mapping.getMethod().invoke(controller, mArgs);

                if (mapping.getMethod().isAnnotationPresent(AsJson.class)) {
                    AsJson asJson = mapping.getMethod().getAnnotation(AsJson.class);

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
                    else {
                        PrintWriter out = response.getWriter();
                        out.print(returnValue);
                    }
                }

                if (session != null) {
                    HttpSession httpSession = request.getSession();
                    for (Map.Entry<String, Object> entry : session.getData().entrySet()) {
                        httpSession.setAttribute(entry.getKey(), entry.getValue());
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
