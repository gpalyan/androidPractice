package com.bignerdranch.androdi.criminialintent;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import static java.util.UUID.randomUUID;

@Getter
@Setter
public class Crime implements Serializable {

    private UUID uuid;
    private Date date;
    private String title;
    private boolean isSolved;
    private String suspect;

    public Crime() {
        this.uuid = randomUUID();
        this.date = new Date();
    }

    public Crime(final UUID uuid) {
        this.uuid = uuid;
    }

    public Crime(final String title, final boolean isSolved) {
        this.uuid = randomUUID();
        this.date = new Date();
        this.title = title;
        this.isSolved = isSolved;
    }

    public String getPhotoFilename() {
        return "IMG_" + getUuid().toString() + ".jpg";
    }
}
