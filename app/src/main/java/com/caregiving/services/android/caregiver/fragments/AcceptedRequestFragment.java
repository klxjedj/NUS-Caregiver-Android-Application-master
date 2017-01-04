package com.caregiving.services.android.caregiver.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.caregiving.services.android.caregiver.R;
import com.caregiving.services.android.caregiver.models.CareRecord;
import com.caregiving.services.android.caregiver.models.actionMaps.CaregiverActionMap;
import com.caregiving.services.android.caregiver.utils.GsonUtils;
import com.caregiving.services.android.caregiver.utils.HttpUtils;
import com.caregiving.services.android.caregiver.utils.ViewUtils;
import com.caregiving.services.android.caregiver.views.adapters.AcceptedCareRequestListAdapter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AcceptedRequestFragment extends BaseFragment {

    private static final String TAG = "AcceptedRequestFragment";
    private static final String CAREGIVER_ID = "CAREGIVER_ID";
    private int mCaregiverId;
    private AcceptedCareRequestListAdapter mCareRequestAdapter;
    private List<CareRecord> mCareRecords = new ArrayList<>();
    private int mSelectedRecipientId;
    private int mSelectedRequestId;
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.existing_request_list) protected ListView existingRequestList;
    @BindView(R.id.empty_list_text) protected TextView emptyListText;

    public AcceptedRequestFragment() {}

    public static AcceptedRequestFragment newInstance(int caregiverId) {
        AcceptedRequestFragment fragment = new AcceptedRequestFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CAREGIVER_ID, caregiverId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCaregiverId = getArguments().getInt(CAREGIVER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_existing_request, container, false);
        ButterKnife.bind(this, view);

        FrameLayout rootView = (FrameLayout) view.findViewById(R.id.root_view);
        setContentContainer((LinearLayout) rootView.getChildAt(0));
        setLoadDataSpinner((ProgressBar) rootView.getChildAt(1));

        mCareRequestAdapter = new AcceptedCareRequestListAdapter(getContext(), mCareRecords);
        existingRequestList.setAdapter(mCareRequestAdapter);

        registerForContextMenu(view.findViewById(R.id.existing_request_list));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            new ExistingRequestTask().execute();
        }
    }

    @Optional
    @OnClick(R.id.open_context_button)
    public void onExistingRequestContextMenu(View v) {
        View parentRow = (View) v.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        CareRecord careRecord = (CareRecord) mCareRequestAdapter.getItem(position);
        mSelectedRecipientId = careRecord.getCareRecipientId();
        mSelectedRequestId = careRecord.getRecordId();
        ((Activity) getContext()).openContextMenu(v);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }
        CareRecord careRecord = (CareRecord) mCareRequestAdapter.getItem(info.position);
        if (careRecord == null) {
            return;
        }

        menu.setHeaderTitle(careRecord.getRecipientName());
        menu.setHeaderIcon(R.drawable.ic_person_24dp);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_existing_request, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_track) {
            Toast.makeText(getContext(), "mSelectedRecipientId: " + mCareRequestAdapter.getSelectedRecipientId() +
                    " mSelectedRequestId: " + mCareRequestAdapter.getSelectedRequestId(), Toast.LENGTH_LONG).show();
        } else {
            return false;
        }
        return true;
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

    private class ExistingRequestTask extends AsyncTask<Void, Boolean, Boolean> {

        public ExistingRequestTask() {}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", CaregiverActionMap.VIEW_SERVICE_TO_PERFORM_TODAY.getAction())
                        .put("user_id", mCaregiverId);

                HttpUtils.jsonPost(getContext(), jsonObject, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        CareRecord[] careRecords = GsonUtils.fromJson(response.body().charStream(), CareRecord[].class);
                        mCareRecords.clear();
                        mCareRecords.addAll(Arrays.asList(careRecords));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCareRequestAdapter.notifyDataSetChanged();
                                if (existingRequestList.getEmptyView() == null) {
                                    existingRequestList.setEmptyView(emptyListText);
                                }
                                ViewUtils.setListViewHeightBasedOnChildren(existingRequestList);
                            }
                        });
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
            onCancelled();
        }
    }
}
