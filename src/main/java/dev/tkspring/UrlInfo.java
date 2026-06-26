package dev.tkspring;

import java.util.Objects;

import dev.tkspring.annotation.Url;
import dev.tkspring.constant.HttpMethod;

public class UrlInfo {

    private HttpMethod method;
    private String url;

    public UrlInfo(Url url) {
        this.method = url.method();
        this.url = url.value();
    }

    public UrlInfo(HttpMethod method, String url) {
        this.method = method;
        this.url = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, url);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UrlInfo urlInfo) {
            return urlInfo.getMethod().equals(this.method) && urlInfo.getUrl().equals(this.url);
        }
        return false;
    }
}
