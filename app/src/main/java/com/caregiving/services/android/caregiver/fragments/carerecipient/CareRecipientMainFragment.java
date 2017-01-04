package com.caregiving.services.android.caregiver.fragments.carerecipient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.caregiving.services.android.caregiver.CareRecipientMainActivity;
import com.caregiving.services.android.caregiver.LoginActivity;
import com.caregiving.services.android.caregiver.R;
import com.caregiving.services.android.caregiver.fragments.BaseFragment;
import com.caregiving.services.android.caregiver.utils.PrefUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CareRecipientMainFragment extends BaseFragment implements
    RequestServiceFragment.OnFragmentInteractionListener {

    private int recipientId;
    private OnFragmentInteractionListener mListener;
    private CareRecipientMainTask mCareRecipientMainTask;
    private AppBarLayout mAppBarLayout;
    private static final String RECIPIENT_ID = "RECIPIENT_ID";
    private static final String REQUEST_SERVICE = "Request Serivce";
    private static final String APPOINTMENTS_TITLE = "Appointments";
    private static final String TENTATIVE_SERVICE_REQUESTS = "Tentative Service Requests";

    public CareRecipientMainFragment() {}

    public static CareRecipientMainFragment newInstance(int recipientId) {
        CareRecipientMainFragment fragment = new CareRecipientMainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(RECIPIENT_ID, recipientId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipientId = getArguments().getInt(RECIPIENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_care_recipient_main, container, false);
        ButterKnife.bind(this, view);

        mAppBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);

        FrameLayout rootView = (FrameLayout) view.findViewById(R.id.root_view);
        setContentContainer((LinearLayout) rootView.getChildAt(0));
        setLoadDataSpinner((ProgressBar) rootView.getChildAt(1));

        showProgress(true);
        mCareRecipientMainTask = new CareRecipientMainTask();
        mCareRecipientMainTask.execute();

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    @OnClick(R.id.request_service_card)
    public void onRequestService() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cra_main_content_container, new RequestServiceFragment(), REQUEST_SERVICE)
                .commit();

        onNavigateToChildFragments(REQUEST_SERVICE, R.color.colorRequestService);
    }

    @OnClick(R.id.view_appointments_card)
    public void onViewAppointments() {
        onNavigateToChildFragments(APPOINTMENTS_TITLE, R.color.colorViewAppointments);
    }

    @OnClick(R.id.view_tentative_request_card)
    public void onViewTentativeRequests() {
        onNavigateToChildFragments(TENTATIVE_SERVICE_REQUESTS, R.color.colorViewTentativeRequest);
    }

    private void onNavigateToChildFragments(String toolbarTitle, int toolbarColor) {
        CareRecipientMainActivity parentActivity = ((CareRecipientMainActivity) getActivity());
        parentActivity.animateAppAndStatusBar(R.color.colorPrimary, toolbarColor);
        parentActivity.getSupportActionBar().setTitle(toolbarTitle);
    }

    @OnClick(R.id.logout_card)
    public void onLogout() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .content(R.string.confirm_logout_dialog_title)
                .positiveText(R.string.action_logout)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        PrefUtils.resetLoginPrefs(getContext());
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .negativeText(android.R.string.no)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        builder.build()
                .show();
    }

    private class CareRecipientMainTask extends AsyncTask<Void, Boolean, Boolean> {

        public CareRecipientMainTask() {}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return false;
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (isAdded()) {
                showProgress(success);
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            onCancelled();
        }
    }
}
