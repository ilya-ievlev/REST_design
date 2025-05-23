package com.epam.test_task.dto;

public class Link {
    private String href;
    private String method;

    public Link() {
    }

    public Link(String href, String method) {
        this.href = href;
        this.method = method;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
