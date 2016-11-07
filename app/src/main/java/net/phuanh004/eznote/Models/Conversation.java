package net.phuanh004.eznote.Models;

/**
 * Created by anhpham on 11/7/16.
 */

public class Conversation {
    private String sender;
    private String senderId;
    private String avatar;
    private String lastMessage;
    private String room;
    private String timeZone;
    private long sendedTime;

    public Conversation() {
    }

    public Conversation(String sender, String senderId, String avatar, String lastMessage, String room, String timeZone, long sendedTime) {
        this.sender = sender;
        this.senderId = senderId;
        this.avatar = avatar;
        this.lastMessage = lastMessage;
        this.room = room;
        this.timeZone = timeZone;
        this.sendedTime = sendedTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public long getSendedTime() {
        return sendedTime;
    }

    public void setSendedTime(long sendedTime) {
        this.sendedTime = sendedTime;
    }
}
