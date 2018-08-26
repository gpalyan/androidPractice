package com.bignerdranch.androdi.criminialintent;

import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    private static final String CRIME_DATE = "CRIME_DATE";

    public static final String EXTRA_DATE =
            "com.bignerdranch.android.criminalintent.date";

    public static DatePickerFragment newInstance(final Date crimedDate) {
        final DatePickerFragment datePickerFragment = new DatePickerFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(CRIME_DATE, crimedDate);

        datePickerFragment.setArguments(bundle);
        return datePickerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Date crimeDate = (Date) getArguments().getSerializable(CRIME_DATE);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(crimeDate);

        final View datePickerDialog = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        final DatePicker datePicker = datePickerDialog.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE),
                null
        );

        return new AlertDialog.Builder(getActivity())
                .setView(datePicker)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }

    private void sendResult(final int resultCode, final Date date) {
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
