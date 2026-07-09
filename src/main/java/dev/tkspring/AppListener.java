package dev.tkspring;

import java.util.HashMap;
import java.util.Map;

import dev.tkspring.annotation.Controller;
import dev.tkspring.annotation.Url;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);

        ServletContext context = sce.getServletContext();
        String basePackages = context.getInitParameter("base-package");

        Map<UrlInfo, UrlMapping> actions = new HashMap<>();
        try {
            Utils.findMethodsByAnnotation(basePackages.split(";"), Controller.class, Url.class, (method) -> {
                UrlInfo url = new UrlInfo((Url) method.getAnnotation(Url.class));
                if (actions.containsKey(url)) {
                    throw new RuntimeException("L’URL (" + url.getMethod() + " " + url.getUrl() + ") est assigné plusieurs fois");
                }
                actions.put(url, new UrlMapping(url, method));
            });
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        context.setAttribute("actions", actions);
    }
}
