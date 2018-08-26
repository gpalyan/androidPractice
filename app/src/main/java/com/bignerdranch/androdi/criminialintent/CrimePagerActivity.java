package com.bignerdranch.androdi.criminialintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    private ViewPager viewPager;
    private List<Crime> crimes;

    public static Intent newIntent(final Context context, final UUID crimeId) {
        final Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crime_pager);

        viewPager = findViewById(R.id.crime_view_pager);

        crimes = CrimeLab.get(this).getCrimes();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Crime crime = crimes.get(position);
                return CrimeFragment.newInstance(crime.getUuid());
            }

            @Override
            public int getCount() {
                return crimes.size();
            }
        });

        final UUID chosenCrimeId = (UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        for (int i = 0; i < crimes.size(); i++) {
            final Crime crime = crimes.get(i);
            if (Objects.equals(chosenCrimeId, crime.getUuid())) {
                viewPager.setCurrentItem(i);
            }
        }
    }
}
