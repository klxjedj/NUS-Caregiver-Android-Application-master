package com.caregiving.services.android.caregiver.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.caregiving.services.android.caregiver.RecordActivity;
import com.caregiving.services.android.caregiver.R;
import com.caregiving.services.android.caregiver.models.CareRecipient;
import com.caregiving.services.android.caregiver.models.FamilyMember;
import com.caregiving.services.android.caregiver.models.actionMaps.CaregiverActionMap;
import com.caregiving.services.android.caregiver.utils.GsonUtils;
import com.caregiving.services.android.caregiver.utils.HttpUtils;
import com.caregiving.services.android.caregiver.utils.PrefUtils;
import com.caregiving.services.android.caregiver.utils.ViewUtils;
import com.caregiving.services.android.caregiver.views.adapters.DemographicAdapter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecipientDemographicFragment extends BaseFragment {

    private static final String RECIPIENT_ID = "RECIPIENT_ID";
    private static final String RECORD_ID = "RECORD_ID";
    private static final String RECIPIENT_NAME = "RECIPIENT_NAME";
    private int recipientId;
    private int recordId;
    private String recipientName = "";
    private static final String[] RECIPIENT_DEMOGRAPHIC_TITLES = {"Address","Home Phone Number", "Mobile Phone Number"};
    private static final String[] RECIPIENT_EMERGENCY_TITLES = {"Name", "Address", "Mobile Phone Number"};
    private static final int[] RECIPIENT_DEMOGRAPHIC_ICONS = {R.drawable.ic_assignment_ind_24dp,R.drawable.ic_location_on_24dp, R.drawable.ic_phone_24dp, R.drawable.ic_smartphone_24dp};
    private static final int[] RECIPIENT_EMERGENCY_ICON = {R.drawable.ic_person_24dp, R.drawable.ic_location_on_24dp,R.drawable.ic_smartphone_24dp, R.drawable.ic_people_24dp};
    private RecipientDemographicsTask mRecipientDemographicsTask;
    private DemographicAdapter mDemographicsListAdapter;
    private DemographicAdapter mEmergencyListAdapter;
    private HashMap<String, String> mDemographicsItemValues = new HashMap<>();
    private HashMap<String, String> mEmergencyItemValues = new HashMap<>();
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.list_recipient_demographic) protected ListView recipientDemographicList;
    @BindView(R.id.list_recipient_emergency_contact) protected ListView recipientEmergencyList;

    public RecipientDemographicFragment() {}

    public static RecipientDemographicFragment newInstance(int recipientId) {
        RecipientDemographicFragment fragment = new RecipientDemographicFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(RECIPIENT_ID, recipientId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static RecipientDemographicFragment newInstance(int recipientId, int recordId, String recipientName) {
        RecipientDemographicFragment fragment = new RecipientDemographicFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(RECIPIENT_ID, recipientId);
        bundle.putString(RECIPIENT_NAME, recipientName);
        bundle.putInt(RECORD_ID, recordId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipientId = getArguments().getInt(RECIPIENT_ID);
            recordId = getArguments().getInt(RECORD_ID);
            recipientName = getArguments().getString(RECIPIENT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipient_demographic, container, false);
        ButterKnife.bind(this, view);

        FrameLayout rootView = (FrameLayout) view.findViewById(R.id.root_view);
        setContentContainer((LinearLayout) rootView.getChildAt(0));
        setLoadDataSpinner((ProgressBar) rootView.getChildAt(1));

        showProgress(true);

        if (recipientName != null  || !recipientName.isEmpty()) {
            mDemographicsListAdapter = new DemographicAdapter(getContext(),mDemographicsItemValues, RECIPIENT_DEMOGRAPHIC_TITLES, RECIPIENT_DEMOGRAPHIC_ICONS, R.color.colorPrimary, recordId, recipientName);
            recipientDemographicList.setAdapter(mDemographicsListAdapter);
        } else {
            mDemographicsListAdapter = new DemographicAdapter(getContext(),mDemographicsItemValues, RECIPIENT_DEMOGRAPHIC_TITLES, RECIPIENT_DEMOGRAPHIC_ICONS, R.color.colorPrimary);
            recipientDemographicList.setAdapter(mDemographicsListAdapter);
        }

        ViewUtils.setListViewHeightBasedOnChildren(recipientDemographicList);

        mEmergencyListAdapter = new DemographicAdapter(getContext(),mEmergencyItemValues, RECIPIENT_EMERGENCY_TITLES, RECIPIENT_EMERGENCY_ICON, R.color.colorPrimary);
        recipientEmergencyList.setAdapter(mEmergencyListAdapter);
        ViewUtils.setListViewHeightBasedOnChildren(recipientEmergencyList);

        mRecipientDemographicsTask = new RecipientDemographicsTask();
        mRecipientDemographicsTask.execute();

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

    private class RecipientDemographicsTask extends AsyncTask<Void, Boolean, Boolean> {

        public RecipientDemographicsTask() {}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("action", CaregiverActionMap.VIEW_FULL_INFO.getAction())
                        .put("user_id", PrefUtils.getUserId(getContext()))
                        .put("recipient_id", recipientId);
                HttpUtils.jsonPost(getContext(), jsonObject, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        CareRecipient careRecipient = GsonUtils.fromJson(response.body().charStream(), CareRecipient.class);
                        if (getActivity() != null) {
                            ((RecordActivity) getActivity()).mCareRecipient = careRecipient;
                        }

                        try {
                            mDemographicsItemValues.put(RECIPIENT_DEMOGRAPHIC_TITLES[1], careRecipient.getAddress());
                            mDemographicsItemValues.put(RECIPIENT_DEMOGRAPHIC_TITLES[2], careRecipient.getResidentContact());
                            mDemographicsItemValues.put(RECIPIENT_DEMOGRAPHIC_TITLES[3], careRecipient.getMobileContact());

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDemographicsListAdapter.notifyDataSetChanged();
                                        if (isAdded()) {
                                            showProgress(false);
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                jsonObject.put("action", CaregiverActionMap.VIEW_FULL_INFO.getAction())
                        .put("user_id", PrefUtils.getUserId(getContext()))
                        .put("recipient_id", recipientId);
                HttpUtils.jsonPost(getContext(), jsonObject, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        FamilyMember familyMember = GsonUtils.fromJson(response.body().charStream(), FamilyMember.class);

                        try {
                            mEmergencyItemValues.put(RECIPIENT_EMERGENCY_TITLES[0],familyMember.getName());
                            mEmergencyItemValues.put(RECIPIENT_EMERGENCY_TITLES[1],familyMember.getAddress());
                            mEmergencyItemValues.put(RECIPIENT_EMERGENCY_TITLES[2], familyMember.getContactNumber());

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mEmergencyListAdapter.notifyDataSetChanged();
                                        if (isAdded()) {
                                            showProgress(false);
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return true;
            } catch (Exception e) {
                return false;
            }
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
