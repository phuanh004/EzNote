package net.phuanh004.eznote.Models;

import android.net.Uri;

/**
 * Created by Huu on 15/10/2016.
 */

public class User {

    private String name;
    private String email;
    private String phone;
    private String avatar;

    public User() {
    }

    public User(String name, String email, String phone, String avatar) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
    }

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