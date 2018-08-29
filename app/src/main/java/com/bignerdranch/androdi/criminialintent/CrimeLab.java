package com.bignerdranch.androdi.criminialintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.androdi.criminialintent.database.CrimeBaseHelper;
import com.bignerdranch.androdi.criminialintent.database.CrimeCursorWrapper;
import com.bignerdranch.androdi.criminialintent.database.CrimeDbSchema;
import com.bignerdranch.androdi.criminialintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
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

    private Context context;
    private final List<Crime> crimes =  new ArrayList<>();

    private SQLiteDatabase database;

    // Singleton pattern
    public static CrimeLab get(@NonNull final Context context) {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = new CrimeLab(context);
        return INSTANCE;
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;
    }

    public File getPhotoFile(Crime crime) {
        final File filesDir = context.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());
    }

    public void addCrime(@NonNull final Crime crime) {
        database.insert(CrimeTable.NAME, null, getContentValues(crime));
    }

    public void updateCrime(Crime crime) {
        final String uuidString = crime.getUuid().toString();
        final ContentValues values = getContentValues(crime);

        database.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private ContentValues getContentValues(final Crime crime) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID, crime.getUuid().toString());
        contentValues.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        contentValues.put(CrimeTable.Cols.TITLE, crime.getTitle());
        contentValues.put(CrimeTable.Cols.SOLVED, crime.isSolved());
        contentValues.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return contentValues;
    }

    private CrimeLab(final Context context) {
        this.context = context.getApplicationContext();
        database = new CrimeBaseHelper(context)
                .getWritableDatabase();

        this.context = context;
    }

    public Optional<Crime> getCrime(@NonNull final UUID uuid) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { uuid.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return Optional.empty();
            }

            cursor.moveToFirst();
            return Optional.of(cursor.getCrime());
        } finally {
            cursor.close();
        }
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                CrimeTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new CrimeCursorWrapper(cursor);
    }
}
