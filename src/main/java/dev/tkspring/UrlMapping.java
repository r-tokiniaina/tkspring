package dev.tkspring;

import java.lang.reflect.Method;

public class UrlMapping {

    private String url;
    private Class<?> controller;
    private Method method;

    public UrlMapping(String url, Method method) {
        this.url = url;
        this.controller = method.getDeclaringClass();
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public Class<?> getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }
}
