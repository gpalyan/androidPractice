package com.bignerdranch.androdi.criminialintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import lombok.NonNull;

public class CrimeFragment extends Fragment {

    private static final int DATE_REQUEST_CODE = 0;
    private static final int REQUEST_SUSPECT = 1;

    private static final String CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private CheckBox isSolvedCheckBox;
    private Button crimeReportButton;
    private Button crimeSuspectButton;

    public static CrimeFragment newInstance(@NonNull final UUID uuid) {
        final Bundle crime = new Bundle();
        crime.putSerializable(CRIME_ID, uuid);

        final CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(crime);
        return crimeFragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID);
        this.crime = CrimeLab.get(getActivity()).getCrime(crimeId).orElse(null);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(crime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        titleField = v.findViewById(R.id.crime_title);
        titleField.setText(crime.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                crime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dateButton = v.findViewById(R.id.crime_date);
        dateButton.setEnabled(true);
        dateButton.setText(crime.getDate().toString());
        dateButton.setOnClickListener(v1 -> {
            final FragmentManager manager = getFragmentManager();
            final DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
            dialog.setTargetFragment(this, DATE_REQUEST_CODE);
            dialog.show(manager, DIALOG_DATE);
        });

        isSolvedCheckBox = v.findViewById(R.id.crime_solved);
        isSolvedCheckBox.setChecked(crime.isSolved());
        isSolvedCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> crime.setSolved(isChecked));

        crimeReportButton = v.findViewById(R.id.crime_report);
        crimeReportButton.setOnClickListener(v12 -> {
            final Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
            i.putExtra(Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject));
            startActivity(Intent.createChooser(i, getString(R.string.send_report)));
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        crimeSuspectButton = v.findViewById(R.id.crime_suspect);
        crimeSuspectButton.setOnClickListener(view -> {
            startActivityForResult(pickContact, REQUEST_SUSPECT);
        });

        if (crime.getSuspect() != null) {
            crimeSuspectButton.setText(crime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            crimeSuspectButton.setEnabled(false);
        }

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == DATE_REQUEST_CODE) {
            final Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            dateButton.setText(crime.getDate().toString());
        } else if (requestCode == REQUEST_SUSPECT && data != null) {
            final Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for
            if (contactUri == null) {
                return;
            }

            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            final Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that you actually got results
                if (c == null || c.getCount() == 0) {
                    return;
                }

                c.moveToFirst();

                final String suspect = c.getString(0);
                crime.setSuspect(suspect);
                crimeSuspectButton.setText(suspect);

                // Pull out the first column of the first row of data -
                // that is your suspect's name
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    }

    private String getCrimeReport() {
        final String solvedString;
        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,
                crime.getDate()).toString();

        String suspect = crime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        return getString(R.string.crime_report,
                crime.getTitle(), dateString, solvedString, suspect);
    }
}
