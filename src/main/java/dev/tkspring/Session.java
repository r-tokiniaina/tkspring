package dev.tkspring;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private Map<String, Object> data;

    protected Session() {
        this.data = new HashMap<>();
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object get(String attr) {
        return data.get(attr);
    }

    public void set(String attr, Object value) {
        data.put(attr, value);
    }

    public void remove(String attr) {
        data.put(attr, null);
    }
}
