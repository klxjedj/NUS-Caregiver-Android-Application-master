package com.caregiving.services.android.caregiver.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.caregiving.services.android.caregiver.utils.SimpleDividerItemDecoration;
import com.caregiving.services.android.caregiver.views.adapters.ServiceRequestListAdapter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ServiceRequestListFragment extends BaseFragment {

    private int mColumnCount = 1;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private OnListFragmentInteractionListener mListener;
    private List<CareRecord> mCareRecords = new ArrayList<>();
    private ServiceRequestListAdapter mServiceRequestAdapter;

    @BindView(R.id.service_request_list) RecyclerView serviceRequestList;
    @BindView(R.id.alt_text_service_request_list) TextView mEmptyListView;

    public ServiceRequestListFragment() {}

    public static ServiceRequestListFragment newInstance(int columnCount) {
        ServiceRequestListFragment fragment = new ServiceRequestListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_request_list, container, false);
        ButterKnife.bind(this, view);

        FrameLayout rootView = (FrameLayout) view;
        setContentContainer((LinearLayout) rootView.getChildAt(0));
        setLoadDataSpinner((ProgressBar) rootView.getChildAt(1));

        Context context = view.getContext();
        mServiceRequestAdapter = new ServiceRequestListAdapter(mCareRecords, mListener, context, ServiceRequestListFragment.this);

        if (mColumnCount <= 1) {
            serviceRequestList.setLayoutManager(new LinearLayoutManager(context));
        } else {
            serviceRequestList.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        serviceRequestList.setItemAnimator(new DefaultItemAnimator());
        serviceRequestList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        serviceRequestList.setAdapter(mServiceRequestAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {

        void onListFragmentInteraction(List<CareRecord> careRecords);
    }

    private class ServiceRequestTask extends AsyncTask<Void, Boolean, Boolean> {

        public ServiceRequestTask() {}

        @Override
        protected void onPreExecute() {
            showProgress(true);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject json = new JSONObject();
                try {
                    json.put("action", CaregiverActionMap.GET_CANCELLATIONS_LEFT.getAction())
                            .put("user_id", PrefUtils.getUserId(getContext()));
                    HttpUtils.jsonPost(getContext(), json, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseString = response.body().string();
                            PrefUtils.saveIntToPrefs(getContext(), "cancellationsLeft", Integer.valueOf(responseString));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", CaregiverActionMap.VIEW_ALL_REQUEST.getAction())
                        .put("user_id", PrefUtils.getUserId(getContext()));
                HttpUtils.jsonPost(getContext(), jsonObject, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        showListViewErrorMessage();
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            mCareRecords.addAll(Arrays.asList(GsonUtils.fromJson(response.body().charStream(), CareRecord[].class)));
                            if (!mCareRecords.isEmpty()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mServiceRequestAdapter.notifyDataSetChanged();
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mEmptyListView.setText(getString(R.string.empty_service_request_list));
                                        mEmptyListView.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            showListViewErrorMessage();
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
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
        }

        private void showListViewErrorMessage() {
            if (isAdded()) {
                mEmptyListView.setText(getString(R.string.error_service_request_list));
                mEmptyListView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            mCareRecords.clear();
            mEmptyListView.setVisibility(View.GONE);
            mServiceRequestAdapter.notifyDataSetChanged();
            new ServiceRequestTask().execute();
        }
    }
}
