package org.example.modules;

import java.util.HashMap;

public class Request {
    private String method;
    private HashMap<String, String> params;

    public HashMap<String, String> getParams() {
        return params;
    }

    public String getMethod() {
        return method;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
