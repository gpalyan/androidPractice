package com.bignerdranch.androdi.criminialintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
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

    private static final String CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private CheckBox isSolvedCheckBox;

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

        final UUID crimeId = (UUID)getArguments().getSerializable(CRIME_ID);
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

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)   {
            return;
        }

        if (requestCode == DATE_REQUEST_CODE) {
            final Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            dateButton.setText(crime.getDate().toString());
        }
    }
}
