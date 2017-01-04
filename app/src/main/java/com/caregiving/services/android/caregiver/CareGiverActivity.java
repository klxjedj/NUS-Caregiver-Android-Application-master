package com.caregiving.services.android.caregiver;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.caregiving.services.android.caregiver.fragments.CareGiverDemographicsFragment;
import com.caregiving.services.android.caregiver.fragments.EmptyFragment;
import com.caregiving.services.android.caregiver.utils.PrefUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class CareGiverActivity extends BaseActivity
    implements CareGiverDemographicsFragment.OnFragmentInteractionListener {

    private static final String[] CAREGIVER_PROFILE_TABS_TITLE = {"Details", "Professional Info"};
    private static int caregiverId = 0;
    private static String caregiverName = "";
    private CaregiverProfileTabsPagerAdapter caregiverProfileTabsPagerAdapter;

    @BindView(R.id.caregiver_name) TextView caregiverNameText;
    @BindView(R.id.caregiver_tabs) TabLayout caregiverTabs;
    @BindView(R.id.caregiver_toolbar) Toolbar toolbar;
    @BindView(R.id.viewPager_caregiver_profile) ViewPager caregiverViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_giver);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        caregiverId = extras.getInt("caregiverId");
        caregiverName = extras.getString("caregiverName");
        caregiverNameText.setText(caregiverName);

        caregiverProfileTabsPagerAdapter = new CaregiverProfileTabsPagerAdapter(getSupportFragmentManager());
        caregiverViewPager.setOffscreenPageLimit(caregiverProfileTabsPagerAdapter.getCount());
        caregiverViewPager.setAdapter(caregiverProfileTabsPagerAdapter);
        caregiverTabs.setupWithViewPager(caregiverViewPager);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.request_service_button)
    public void onRequestService() {
        Intent requestServiceForm = new Intent(getApplicationContext(), CareRecipientRequestServiceFormActivity.class);
        requestServiceForm
                .putExtra("caregiver_id", caregiverId)
                .putExtra("caregiver_name", caregiverName)
                .putExtra("user_id", PrefUtils.getUserId(getApplicationContext()));
        startActivity(requestServiceForm);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    public class CaregiverProfileTabsPagerAdapter extends FragmentPagerAdapter {

        public CaregiverProfileTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return CareGiverDemographicsFragment.newInstance(caregiverId, R.color.colorRequestService);
                case 1:
                    return EmptyFragment.newInstance();

                default:
                    return CareGiverDemographicsFragment.newInstance(caregiverId, R.color.colorRequestService);
            }
        }

        @Override
        public int getCount() {
            return CAREGIVER_PROFILE_TABS_TITLE.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return CAREGIVER_PROFILE_TABS_TITLE[0];

                case 1:
                    return CAREGIVER_PROFILE_TABS_TITLE[1];

                default:
                    return CAREGIVER_PROFILE_TABS_TITLE[0];
            }
        }
    }
}
