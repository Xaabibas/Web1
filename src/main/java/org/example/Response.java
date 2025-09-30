package org.example;

public class Response {
    private boolean hit;
    private String json;
    private long time;

    public String getJson() {
        return json;
    }

    public long getTime() {
        return time;
    }

    public boolean isHit() {
        return hit;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
}
