package dev.tkspring;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private Map<String, Object> model;
    private String view;

    public ModelAndView() {
        this.model = new HashMap<>();
    }

    public ModelAndView(String view) {
        this();
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public Object get(String attr) {
        return model.get(attr);
    }

    public void set(String attr, Object value) {
        model.put(attr, value);
    }
}
