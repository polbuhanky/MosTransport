package dev.artem.mostransport.models;

import java.util.ArrayList;

public class User {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User(String name, String email, String pass, String phone, String post) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.phone = phone;
        this.post = post;
    }

    private String name, email, pass, phone, post;

    public User() {
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }
}
