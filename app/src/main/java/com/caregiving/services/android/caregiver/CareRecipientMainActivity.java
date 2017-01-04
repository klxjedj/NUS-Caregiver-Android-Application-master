package com.caregiving.services.android.caregiver;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.caregiving.services.android.caregiver.fragments.carerecipient.RequestServiceFragment;
import com.caregiving.services.android.caregiver.fragments.carerecipient.CareRecipientMainFragment;

import butterknife.BindView;

public class CareRecipientMainActivity extends BaseActivity implements
    CareRecipientMainFragment.OnFragmentInteractionListener,
        RequestServiceFragment.OnFragmentInteractionListener {

    private static final String HOME_TITLE = "Home";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_recipient);

        setSupportActionBar(mToolbar);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cra_main_content_container, new CareRecipientMainFragment(), "Home")
                .commit();
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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackButtonPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackButtonPressed();
    }

    private void onBackButtonPressed() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cra_main_content_container, new CareRecipientMainFragment(), HOME_TITLE)
                .commit();
        showHomeNavButton(false);
        getSupportActionBar().setTitle(HOME_TITLE);

        int color = 0;
        Drawable background = mToolbar.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        animateAppAndStatusBar(color, R.color.colorPrimary);
    }

    public void showHomeNavButton(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(show);
            actionBar.setDisplayShowHomeEnabled(show);
        }
    }

    public void animateAppAndStatusBar(int fromColor, final int toColor) {
        Animator animator = ViewAnimationUtils.createCircularReveal(
                appBarLayout,
                appBarLayout.getWidth() / 2,
                appBarLayout.getHeight() / 2, 0,
                appBarLayout.getWidth() / 2);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mToolbar.setBackgroundColor(getResources().getColor(toColor));
            }
        });

        appBarLayout.setBackgroundColor(getResources().getColor(toColor));
        animator.setDuration(450);
        animator.start();
        mToolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}
}
