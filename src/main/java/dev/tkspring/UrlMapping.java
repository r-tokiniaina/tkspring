package dev.tkspring;

import java.lang.reflect.Method;

public class UrlMapping {

    private UrlInfo url;
    private Class<?> controller;
    private Method method;

    public UrlMapping(UrlInfo url, Method method) {
        this.url = url;
        this.controller = method.getDeclaringClass();
        this.method = method;
    }

    public UrlInfo getUrl() {
        return url;
    }

    public Class<?> getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }
}
