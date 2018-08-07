package com.bignerdranch.androdi.criminialintent;

import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import static java.util.UUID.randomUUID;

@Getter
public class Crime {

    private final UUID uuid;
    private final Date date;
    @Setter private String title;
    @Setter private boolean isSolved;

    public Crime(final String title, final boolean isSolved) {
        this.uuid = randomUUID();
        this.date = new Date();
        this.title = title;
        this.isSolved = isSolved;
    }
}
