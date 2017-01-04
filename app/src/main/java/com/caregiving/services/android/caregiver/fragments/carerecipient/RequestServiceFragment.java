package com.caregiving.services.android.caregiver.fragments.carerecipient;

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
import android.widget.TextView;

import com.caregiving.services.android.caregiver.CareRecipientMainActivity;
import com.caregiving.services.android.caregiver.R;
import com.caregiving.services.android.caregiver.fragments.BaseFragment;
import com.caregiving.services.android.caregiver.models.CareGiver;
import com.caregiving.services.android.caregiver.models.actionMaps.CarerecipientActionMap;
import com.caregiving.services.android.caregiver.utils.GsonUtils;
import com.caregiving.services.android.caregiver.utils.HttpUtils;
import com.caregiving.services.android.caregiver.utils.PrefUtils;
import com.caregiving.services.android.caregiver.utils.ViewUtils;
import com.caregiving.services.android.caregiver.views.adapters.CaregiverListAdapter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RequestServiceFragment extends BaseFragment {

    private int recipientId;
    private OnFragmentInteractionListener mListener;
    private CaregiverListAdapter mCaregiverListAdapter;
    private ArrayList<CareGiver> mCareGivers = new ArrayList<>();
    private static final String RECIPIENT_ID = "RECIPIENT_ID";

    @BindView(R.id.request_service_list)
    ListView requestServiceList;
    @BindView(R.id.empty_request_list_text)
    TextView emptyRequestListText;
    @BindView(R.id.list_loading_error)
    TextView errorLoadingListText;

    public RequestServiceFragment() {}

    public static RequestServiceFragment newInstance(int recipientId) {
        RequestServiceFragment fragment = new RequestServiceFragment();
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

        ((CareRecipientMainActivity) getActivity()).showHomeNavButton(true);
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        ButterKnife.bind(this, view);

        FrameLayout rootView = (FrameLayout) view.findViewById(R.id.root_view);
        setContentContainer((LinearLayout) rootView.getChildAt(0));
        setLoadDataSpinner((ProgressBar) rootView.getChildAt(1));

        mCaregiverListAdapter = new CaregiverListAdapter(getContext(), mCareGivers);
        requestServiceList.setAdapter(mCaregiverListAdapter);
        requestServiceList.setEmptyView(emptyRequestListText);

        new RequestServiceTask().execute();

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class RequestServiceTask extends AsyncTask<Void, Boolean, Boolean> {

        public RequestServiceTask() {}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("action", CarerecipientActionMap.VIEW_AVAILABLE_CARE_GIVERS.getAction())
                        .put("user_id", PrefUtils.getUserId(getContext()));
                HttpUtils.jsonPost(getContext(), jsonObject, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        cancel(true);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                errorLoadingListText.setVisibility(View.VISIBLE);
                                emptyRequestListText.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            CareGiver[] careGivers = GsonUtils.fromJson(response.body().charStream(), CareGiver[].class);
                            mCareGivers.clear();
                            mCareGivers.addAll(Arrays.asList(careGivers));
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mCaregiverListAdapter.notifyDataSetChanged();
                                    ViewUtils.setListViewHeightBasedOnChildren(requestServiceList);

                                    if (isAdded()) {
                                        showProgress(false);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    if (errorLoadingListText.getVisibility() == View.GONE) {
                                        errorLoadingListText.setVisibility(View.VISIBLE);
                                        emptyRequestListText.setVisibility(View.GONE);
                                    }
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
