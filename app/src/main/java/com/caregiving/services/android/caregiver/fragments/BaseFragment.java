package com.caregiving.services.android.caregiver.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public abstract class BaseFragment extends Fragment {

    private ProgressBar mLoadDataSpinner;
    private LinearLayout mContentContainer;

    public void setLoadDataSpinner(ProgressBar mLoadDataSpinner) {
        this.mLoadDataSpinner = mLoadDataSpinner;
    }

    public void setContentContainer(LinearLayout mContentContainer) {
        this.mContentContainer = mContentContainer;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mContentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            mContentContainer.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mContentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mLoadDataSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoadDataSpinner.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoadDataSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mLoadDataSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
            mContentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
