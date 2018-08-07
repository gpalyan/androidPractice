package com.bignerdranch.androdi.criminialintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class CrimeLab {

    private static CrimeLab INSTANCE;

    private final Context context;
    private final List<Crime> crimes;

    // Singleton pattern
    public static CrimeLab get(@NonNull final Context context) {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = new CrimeLab(context);
        return INSTANCE;
    }

    private CrimeLab(final Context context) {
        this.context = context;

        crimes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            crimes.add(new Crime("Crime #" + i, i %2 == 0));
        }
    }

    public Optional<Crime> getCrime(@NonNull final UUID uuid) {
        return crimes.stream()
                .filter(crime -> Objects.equals(uuid, crime.getUuid()))
                .findAny();
    }
}
