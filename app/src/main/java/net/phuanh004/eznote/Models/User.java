package net.phuanh004.eznote.Models;

import android.net.Uri;

/**
 * Created by Huu on 15/10/2016.
 */

public class User {

    private String username;
    private String email;
    private String pass;
    private String phone;
    private String avatar;

    public User() {
    }

    public User(String username, String email, String pass, String phone, String avatar) {
        this.username = username;
        this.email = email;
        this.pass = pass;
        this.phone = phone;
        this.avatar = avatar;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
