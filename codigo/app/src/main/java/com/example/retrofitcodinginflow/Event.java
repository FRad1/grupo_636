package com.example.retrofitcodinginflow;

public class Event {

    private String env;
    private String type_events;
    private String state;
    private String description;

    public Event(String env, String type_events, String state, String description) {
        this.env = env;
        this.type_events = type_events;
        this.state = state;
        this.description = description;
    }

    public String getEnv() {
        return env;
    }

    public String getType_events() {
        return type_events;
    }

    public String getState() {
        return state;
    }

    public String getdescription() {
        return description;
    }
}
