package com.caregiving.services.android.caregiver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caregiving.services.android.caregiver.fragments.AcceptedRequestFragment;
import com.caregiving.services.android.caregiver.fragments.CalendarFragment;
import com.caregiving.services.android.caregiver.fragments.CareGiverDemographicsFragment;
import com.caregiving.services.android.caregiver.fragments.EmptyFragment;
import com.caregiving.services.android.caregiver.fragments.ServiceRequestListFragment;
import com.caregiving.services.android.caregiver.models.CareRecord;
import com.caregiving.services.android.caregiver.utils.DateUtils;
import com.caregiving.services.android.caregiver.utils.PrefUtils;

import java.util.List;

import butterknife.BindView;

public class CareGiverMainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CalendarFragment.OnFragmentInteractionListener,
        CareGiverDemographicsFragment.OnFragmentInteractionListener,
        ServiceRequestListFragment.OnListFragmentInteractionListener,
        AcceptedRequestFragment.OnFragmentInteractionListener {

    private static final String[] CALENDAR_TABS_TITLE = {"Today", "Week", "Month"};
    private static final String[] PROFILE_TABS_TITLE = {"Demographics", "Professional Info"};
    private Menu mActionBarMenu;
    private CalendarTabsPagerAdapter calendarTabsPagerAdapter;
    private ProfileTabsPagerAdapter profileTabsPagerAdapter;

    @BindView(R.id.drawer_layout) protected DrawerLayout drawer;
    @BindView(R.id.toolbar) protected Toolbar toolbar;
    @BindView(R.id.layout_profile_pic_container) protected LinearLayout profilePictureContainer;
    @BindView(R.id.nav_view) protected NavigationView navigationView;
    @BindView(R.id.viewPager_calendar) protected ViewPager calendarViewPager;
    @BindView(R.id.viewPager_profile) protected ViewPager profileViewPager;
    @BindView(R.id.tabs_calendar) protected TabLayout calendarTabs;
    @BindView(R.id.tabs_profile) protected TabLayout profileTabs;
    @BindView(R.id.today_container) protected LinearLayout todayContainer;
    @BindView(R.id.toolbar_caregiver_name) protected TextView toolbarCaregiverNameText;
    @BindView(R.id.caregiver_rating) protected AppCompatRatingBar caregiverRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView navCaregiverName = (TextView) headerView.findViewById(R.id.nav_header_caregiver_name);
        final String caregiverName = PrefUtils.getStringFromPrefs(getApplicationContext(), "caregiver_name", "caregiver_name");
        navCaregiverName.setText(caregiverName);
    }

    private void onTodayTabSelected(boolean visible) {
        if (visible) {
            todayContainer.setVisibility(View.VISIBLE);
            TextView dateTextView = (TextView) todayContainer.getChildAt(0);
            TextView monthTextView = (TextView) todayContainer.getChildAt(1);
            dateTextView.setText(DateUtils.getCurrentDateNumber());
            monthTextView.setText(DateUtils.getCurrentMonthName());
        } else {
            todayContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu mActionBarMenu) {

        getMenuInflater().inflate(R.menu.menu_main, mActionBarMenu);
        this.mActionBarMenu = mActionBarMenu;
        mActionBarMenu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case R.id.action_edit_profile:
                return true;

            case R.id.action_settings:
                return true;

            case R.id.action_logout:
                logout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        String toolbarTtile = "Home";
        FragmentManager fragmentManager = getSupportFragmentManager();

        mActionBarMenu.getItem(0).setVisible(false);

        if (calendarTabs.getVisibility() == View.VISIBLE) {
            calendarViewPager.setVisibility(View.GONE);
            calendarTabs.setVisibility(View.GONE);
        }

        if (profilePictureContainer.getVisibility() == View.VISIBLE) {
            profilePictureContainer.setVisibility(View.GONE);
            profileViewPager.setVisibility(View.GONE);
            profileTabs.setVisibility(View.GONE);
        }

        switch(id) {
            case R.id.nav_home:
                toolbarTtile = getResources().getString(R.string.nav_home);
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content_main_container, new EmptyFragment(), toolbarTtile)
                        .commit();
                onTodayTabSelected(false);
                break;

            case R.id.nav_existing:
                toolbarTtile = getResources().getString(R.string.fragment_request_title);
                if (fragmentManager.findFragmentByTag(toolbarTtile) == null) {
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.content_main_container, new ServiceRequestListFragment(), toolbarTtile)
                            .commit();
                }
                onTodayTabSelected(false);
                break;

            case R.id.nav_outstanding:
                toolbarTtile = getResources().getString(R.string.fragment_existing_title);
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content_main_container, new EmptyFragment(), toolbarTtile)
                        .commit();

                if (calendarTabsPagerAdapter == null) {

                    calendarTabsPagerAdapter = new CalendarTabsPagerAdapter(getSupportFragmentManager());
                    calendarViewPager.setOffscreenPageLimit(CALENDAR_TABS_TITLE.length);
                    calendarViewPager.setAdapter(calendarTabsPagerAdapter);
                    calendarTabs.setupWithViewPager(calendarViewPager);
                } else {
                    calendarTabsPagerAdapter.notifyDataSetChanged();
                }

                calendarViewPager.setVisibility(View.VISIBLE);
                calendarTabs.setVisibility(View.VISIBLE);
                onTodayTabSelected(true);
                break;

            case R.id.nav_profile:
                mActionBarMenu.getItem(0).setVisible(true);
                toolbarTtile = getResources().getString(R.string.nav_profile);
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content_main_container, new EmptyFragment(), toolbarTtile)
                        .commit();

                if (profileTabsPagerAdapter == null) {
                    toolbarCaregiverNameText.setText(PrefUtils
                            .getStringFromPrefs(getApplicationContext(), "caregiver_name", "Name not found"));

                    profileTabsPagerAdapter = new ProfileTabsPagerAdapter(getSupportFragmentManager());
                    profileViewPager.setOffscreenPageLimit(9);
                    profileViewPager.setAdapter(profileTabsPagerAdapter);
                    profileTabs.setupWithViewPager(profileViewPager);
                } else {
                    profileTabsPagerAdapter.notifyDataSetChanged();
                }
                profilePictureContainer.setVisibility(View.VISIBLE);
                profileTabs.setVisibility(View.VISIBLE);
                profileViewPager.setVisibility(View.VISIBLE);
                onTodayTabSelected(false);
                break;

            case R.id.nav_completed:
                toolbarTtile = getResources().getString(R.string.nav_completed);
                onTodayTabSelected(false);
                Toast.makeText(getApplicationContext(), "Feature not supported yet", Toast.LENGTH_SHORT)
                    .show();
                break;

            case R.id.nav_settings:
                toolbarTtile = getResources().getString(R.string.nav_settings);
                onTodayTabSelected(false);
                Toast.makeText(getApplicationContext(), "Feature not supported yet", Toast.LENGTH_SHORT)
                    .show();
                break;

            case R.id.nav_logout:
                onTodayTabSelected(false);
                logout();
                break;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(toolbarTtile);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
       try {
           PrefUtils.resetLoginPrefs(getApplicationContext());
           Intent intent = new Intent(CareGiverMainActivity.this, LoginActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
           startActivity(intent);
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    @Override
    public void onListFragmentInteraction(List<CareRecord> careRecords) {}

    public class CalendarTabsPagerAdapter extends FragmentStatePagerAdapter {

        public CalendarTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int caregiverId = PrefUtils.getUserId(getApplicationContext());
            switch (position) {
                case 0:
                    onTodayTabSelected(true);
                    return AcceptedRequestFragment.newInstance(caregiverId);

                case 1:
                    onTodayTabSelected(false);
                    return EmptyFragment.newInstance();

                case 2:
                    onTodayTabSelected(false);
                    return EmptyFragment.newInstance();

                default:
                    onTodayTabSelected(true);
                    return AcceptedRequestFragment.newInstance(caregiverId);
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return CALENDAR_TABS_TITLE.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return CALENDAR_TABS_TITLE[0];
                case 1:
                    return CALENDAR_TABS_TITLE[1];
                case 2:
                    return CALENDAR_TABS_TITLE[2];
            }
            return null;
        }
    }

    public class ProfileTabsPagerAdapter extends FragmentStatePagerAdapter {

        public ProfileTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return CareGiverDemographicsFragment.newInstance(PrefUtils.getUserId(getApplicationContext()), R.color.colorPrimary);

                case 1:
                    return EmptyFragment.newInstance();

                default:
                    return CareGiverDemographicsFragment.newInstance(PrefUtils.getUserId(getApplicationContext()), R.color.colorPrimary);
            }
        }

        @Override
        public int getCount() {
            return PROFILE_TABS_TITLE.length;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return PROFILE_TABS_TITLE[0];
                case 1:
                    return PROFILE_TABS_TITLE[1];
            }
            return null;
        }
    }
}
