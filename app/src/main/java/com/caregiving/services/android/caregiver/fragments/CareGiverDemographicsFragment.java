package com.caregiving.services.android.caregiver.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.caregiving.services.android.caregiver.R;
import com.caregiving.services.android.caregiver.models.actionMaps.CarerecipientActionMap;
import com.caregiving.services.android.caregiver.utils.DateUtils;
import com.caregiving.services.android.caregiver.utils.HttpUtils;
import com.caregiving.services.android.caregiver.utils.PrefUtils;
import com.caregiving.services.android.caregiver.views.adapters.DemographicAdapter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CareGiverDemographicsFragment extends BaseFragment {

    private static final String CAREGIVER_ID = "CAREGIVER_ID";
    private int caregiverId;
    private static int mIconColorId;
    private static final String[] DEMOGRAPHIC_TITLES = {"Gender", "Email","Phone Number", "Date of Birth", "Highest Education Level"};
    private static final int[] DEMOGRAPHICS_ICONS ={R.drawable.ic_people_24dp, R.drawable.ic_email_24dp,R.drawable.ic_smartphone_24dp, R.drawable.ic_date_range_24dp, R.drawable.ic_school_24dp};

    private OnFragmentInteractionListener mListener;
    private DemographicAdapter mDemographicsListAdapter;
    private HashMap<String, String> mDemographicsItemValues = new HashMap<>();
    private AppCompatRatingBar mCaregiverRatingBar;

    @BindView(R.id.list_demographic) protected ListView demographicsList;

    public CareGiverDemographicsFragment() {}

    public static CareGiverDemographicsFragment newInstance(int caregiverId, int iconColorId) {
        CareGiverDemographicsFragment fragment = new CareGiverDemographicsFragment();
        Bundle bundle= new Bundle();
        bundle.putInt(CAREGIVER_ID, caregiverId);
        fragment.setArguments(bundle);
        mIconColorId = iconColorId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            caregiverId = getArguments().getInt(CAREGIVER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_demographics, container, false);
        ButterKnife.bind(this, view);

        mCaregiverRatingBar = (AppCompatRatingBar) getActivity().findViewById(R.id.caregiver_rating);

        FrameLayout rootView = (FrameLayout) view.findViewById(R.id.root_view);
        setContentContainer((LinearLayout) rootView.getChildAt(0));
        setLoadDataSpinner((ProgressBar) rootView.getChildAt(1));

        mDemographicsListAdapter = new DemographicAdapter(getContext(),mDemographicsItemValues, DEMOGRAPHIC_TITLES, DEMOGRAPHICS_ICONS, mIconColorId);
        demographicsList.setAdapter(mDemographicsListAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            new DemographicsTask().execute();
        }
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

    private class DemographicsTask extends AsyncTask<Void, Boolean, Boolean> {

        public DemographicsTask() {}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("action", CarerecipientActionMap.VIEW_CARE_GIVER_BY_ID.getAction())
                        .put("user_id", PrefUtils.getUserId(getContext()))
                        .put("caregiver_id", caregiverId);
                HttpUtils.jsonPost(getContext(), jsonObject, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseString = response.body().string();
                        try {
                            JSONObject responseJson = new JSONObject(responseString);
                            mDemographicsItemValues.put(DEMOGRAPHIC_TITLES[0],responseJson.getString("gender"));
                            mDemographicsItemValues.put(DEMOGRAPHIC_TITLES[1],responseJson.getString("email"));
                            mDemographicsItemValues.put(DEMOGRAPHIC_TITLES[2],responseJson.getString("contact"));
                            mDemographicsItemValues.put(DEMOGRAPHIC_TITLES[3],DateUtils.formatDateString(responseJson.getString("date_of_birth")));
                            mDemographicsItemValues.put(DEMOGRAPHIC_TITLES[4], responseJson.getString("highest_education_level"));

                            final String rating = responseJson.getString("average_rating");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Float ratingValue = (!rating.equals("null")) ? Float.valueOf(rating) : null;
                                    mCaregiverRatingBar.setRating(ratingValue != null ? ratingValue : 0);
                                    mDemographicsListAdapter.notifyDataSetChanged();

                                    View caregiverRatingBar = getActivity().findViewById(R.id.caregiver_rating);
                                    if (caregiverRatingBar.getVisibility() == View.GONE
                                            && ratingValue != null
                                            && ratingValue != 0) {
                                        caregiverRatingBar.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
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
