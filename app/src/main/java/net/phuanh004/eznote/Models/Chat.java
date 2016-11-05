package net.phuanh004.eznote.Models;

/**
 * Created by anhpham on 11/4/16.
 */

public class Chat {
    String name;
    String text;
    String uid;

    public Chat() {
    }

    public Chat(String name, String uid, String message) {
        this.name = name;
        this.text = message;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }
}
