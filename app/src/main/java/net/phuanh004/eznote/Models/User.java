package net.phuanh004.eznote.Models;

/**
 * Created by Huu on 15/10/2016.
 */

public class User {

    private String uid;
    private String username;
    private String email;
    private String pass;
    private String phone;

    public User() {
    }

    public User(String uid, String username, String email, String pass, String phone) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.pass = pass;
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
