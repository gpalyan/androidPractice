package com.bignerdranch.androdi.criminialintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.androdi.criminialintent.Crime;
import com.bignerdranch.androdi.criminialintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        final String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        final String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        final long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        final int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        final String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        final Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setSolved(isSolved != 0);
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSuspect(suspect);

        return crime;
    }
}
