package net.phuanh004.eznote.Models;

import java.util.Date;

/**
 * Created by anhpham on 10/13/16.
 */

public class Note {
    private String noteId;
    private String title;
    private String content;
    private long SavedTime;

    public Note() {
    }

    public Note(String noteId, String title, String content, long savedTime) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        SavedTime = savedTime;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSavedTime() {
        return new Date(SavedTime*1000L);
    }

    public void setSavedTime(long savedTime) {
        SavedTime = savedTime;
    }
}
