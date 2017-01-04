package com.caregiving.services.android.caregiver.views.adapters;

import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caregiving.services.android.caregiver.R;
import com.caregiving.services.android.caregiver.fragments.RecipientCareServiceHistoryFragment.OnCareServiceHistoryListFragmentListener;
import com.caregiving.services.android.caregiver.models.CareRecord;
import com.caregiving.services.android.caregiver.utils.DateUtils;

import java.sql.Date;
import java.util.List;


public class MyCareServiceHistoryRecyclerViewAdapter extends RecyclerView.Adapter<MyCareServiceHistoryRecyclerViewAdapter.ViewHolder> {

    private final List<CareRecord> mCareRecords;
    private final OnCareServiceHistoryListFragmentListener mListener;

    public MyCareServiceHistoryRecyclerViewAdapter(List<CareRecord> careRecords, OnCareServiceHistoryListFragmentListener listener) {
        mCareRecords = careRecords;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_careservicehistory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mCareRecord = mCareRecords.get(position);

        Date appointmentDate = holder.mCareRecord.getAppointmentTime();
        holder.mServiceHistoryAppointmentDate.setText(DateUtils.formatDate(appointmentDate));
        holder.mServiceHistoryAppointmentTime.setText(DateUtils.formatDateString(appointmentDate, "h:m a"));

        String specialNeeds = holder.mCareRecord.getSpecialNeeds();
        holder.mSpecialNeedsText.setText(specialNeeds);
        holder.mSpecialNeedsText.setVisibility((!specialNeeds.isEmpty()) ? View.VISIBLE : View.GONE);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.OnCareServiceHistoryListFragmentListener(holder.mCareRecord);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCareRecords.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mServiceHistoryAppointmentDate;
        public final TextView mServiceHistoryAppointmentTime;
        public final TextView mSpecialNeedsText;
        public CareRecord mCareRecord;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mServiceHistoryAppointmentDate = (TextView) mView.findViewById(R.id.service_history_appointment_date);
            mServiceHistoryAppointmentTime = (TextView) mView.findViewById(R.id.service_history_appointment_time);
            mSpecialNeedsText = (TextView) mView.findViewById(R.id.recipient_special_needs);
        }
    }
}
