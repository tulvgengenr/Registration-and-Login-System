package com.example.shiyan4.bean;

public class Users {
    private String username;
    private String pass;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Users(String username, String pass) {
        this.username = username;
        this.pass = pass;
    }
}
