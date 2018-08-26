package com.bignerdranch.androdi.criminialintent;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import static java.util.UUID.randomUUID;

@Getter
public class Crime implements Serializable {

    private final UUID uuid;
    @Setter private Date date;
    @Setter private String title;
    @Setter private boolean isSolved;

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
}
