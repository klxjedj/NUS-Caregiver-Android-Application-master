package com.caregiving.services.android.caregiver.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caregiving.services.android.caregiver.R;
import com.caregiving.services.android.caregiver.models.CareRecord;
import com.caregiving.services.android.caregiver.models.actionMaps.CaregiverActionMap;
import com.caregiving.services.android.caregiver.utils.GsonUtils;
import com.caregiving.services.android.caregiver.utils.HttpUtils;
import com.caregiving.services.android.caregiver.utils.PrefUtils;
import com.caregiving.services.android.caregiver.views.adapters.MyCareServiceHistoryRecyclerViewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecipientCareServiceHistoryFragment extends BaseFragment {

    private static final String RECIPIENT_ID = "RECIPIENT_ID";
    private int recipientId;
    private OnCareServiceHistoryListFragmentListener mListener;
    private List<CareRecord> mCareRecords = new ArrayList<>();
    private MyCareServiceHistoryRecyclerViewAdapter careServiceHistoryRecyclerViewAdapter;

    @BindView(R.id.care_service_history_list) RecyclerView careServiceHistoryList;
    @BindView(R.id.empty_care_service_history_list_text) TextView mEmptyListView;

    public RecipientCareServiceHistoryFragment() {}

    public static RecipientCareServiceHistoryFragment newInstance(int recipientId) {
        RecipientCareServiceHistoryFragment fragment = new RecipientCareServiceHistoryFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipient_careservicehistory_list, container, false);
        ButterKnife.bind(this, view);

        FrameLayout rootView = (FrameLayout) view.findViewById(R.id.root_view);
        setContentContainer((LinearLayout) rootView.getChildAt(0));
        setLoadDataSpinner((ProgressBar) rootView.getChildAt(1));

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.care_service_history_list);
        careServiceHistoryRecyclerViewAdapter = new MyCareServiceHistoryRecyclerViewAdapter(mCareRecords, mListener);
        recyclerView.setAdapter(careServiceHistoryRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCareServiceHistoryListFragmentListener) {
            mListener = (OnCareServiceHistoryListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCareServiceHistoryListFragmentListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            new CareServiceHistoryTask().execute();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class CareServiceHistoryTask extends AsyncTask<Void, Boolean, Boolean> {

        public CareServiceHistoryTask() {}

        @Override
        protected void onPreExecute() {
            showProgress(true);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject payload = new JSONObject();
                payload.put("action", CaregiverActionMap.VIEW_RECIPIENT_CARE_SERVICE_HISTORY.getAction())
                        .put("user_id", PrefUtils.getUserId(getContext()))
                        .put("carerecipient_id", recipientId);
                HttpUtils.jsonPost(getContext(), payload, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        CareRecord[] careRecords = GsonUtils.fromJson(response.body().charStream(), CareRecord[].class);
                        mCareRecords.clear();
                        mCareRecords.addAll(Arrays.asList(careRecords));
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showEmptyMessage(mCareRecords.isEmpty());
                                    careServiceHistoryRecyclerViewAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (isAdded()) {
                showProgress(success);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private boolean showEmptyMessage(boolean show) {
        mEmptyListView.setVisibility(show ? View.VISIBLE : View.GONE);
        return show;
    }

    public interface OnCareServiceHistoryListFragmentListener {
        void OnCareServiceHistoryListFragmentListener(CareRecord careRecord);
    }
}
