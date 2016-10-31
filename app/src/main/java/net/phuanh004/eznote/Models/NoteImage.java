package net.phuanh004.eznote.Models;

import java.util.HashMap;

/**
 * Created by anhpham on 10/28/16.
 */

public class NoteImage {
    private HashMap<String, String> images;

    public NoteImage() {
    }

    public NoteImage(HashMap<String, String> images) {
        this.images = images;
    }

    public HashMap<String, String> getImages() {
        return images;
    }

    public void setImages(HashMap<String, String> images) {
        this.images = images;
    }
}
