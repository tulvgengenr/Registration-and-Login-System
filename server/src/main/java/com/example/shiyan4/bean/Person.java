package com.example.shiyan4.bean;

public class Person {
    private String username;
    private String name;
    private String teleno;
    private int age;
    {
        teleno = "";
        age = -1;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeleno() {
        return teleno;
    }

    public void setTeleno(String teleno) {
        this.teleno = teleno;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person(String username, String name,int age,String teleno) {
        this.username = username;
        this.name = name;
        this.teleno = teleno;
        this.age = age;
    }
}
