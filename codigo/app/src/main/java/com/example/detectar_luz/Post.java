package com.example.detectar_luz;

public class Post {

    private String state;

    private String msg;

    private String env;

    private String name;

    private String lastname;

    private int dni;

    private String email;

    private String password;

    private int commission;

    private int group;

    private String token;

    public Post(String env, String name, String lastname, int dni, String email, String password, int commission, int group) {
        this.env = env;
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.commission = commission;
        this.group = group;
    }

    public String getToken() {
        return token;
    }

    public String getMsg() {
        return msg;
    }

    public String getState() {
        return state;
    }

    public String getEnv() {
        return env;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public int getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getCommission() {
        return commission;
    }

    public int getGroup() {
        return group;
    }
}

