package com.caregiving.services.android.caregiver.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.caregiving.services.android.caregiver.RecordActivity;
import com.caregiving.services.android.caregiver.R;
import com.caregiving.services.android.caregiver.models.CareRecord;
import com.caregiving.services.android.caregiver.utils.DateUtils;

import java.sql.Date;
import java.util.List;

/**
 * Created by PC1 on 24/9/2016.
 */
public class AcceptedCareRequestListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<CareRecord> mCareRecords;

    private int mSelectedRecipientId;
    private int mSelectedRequestId;

    public AcceptedCareRequestListAdapter(Context context, List<CareRecord> careRecords) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCareRecords = careRecords;
    }

    public int getSelectedRecipientId() {
        return mSelectedRecipientId;
    }

    public int getSelectedRequestId() {
        return mSelectedRequestId;
    }

    @Override
    public int getCount() {
        return mCareRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return mCareRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mCareRecords.get(position).getCareRecipientId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        final CareRecord careRecord = mCareRecords.get(position);

        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item_care_request, null);
            mViewHolder.mExistingRequestItem = (LinearLayout) convertView.findViewById(R.id.existing_request_item);
            mViewHolder.mExistingRequestItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainActivityIntent = new Intent(mContext, RecordActivity.class);
                    mainActivityIntent.putExtra("carerecipient_id", careRecord.getCareRecipientId())
                            .putExtra("record_id", careRecord.getRecordId())
                            .putExtra("carerecipient_name", careRecord.getRecipientName())
                            .putExtra("showTrackingButton", true);
                    mContext.startActivity(mainActivityIntent);
                }
            });
            mViewHolder.mRecipientName = (TextView) convertView.findViewById(R.id.text_recipient_name);
            mViewHolder.mAppointmentTime = (TextView) convertView.findViewById(R.id.text_time);
            mViewHolder.mAppointmentDate = (TextView) convertView.findViewById(R.id.text_date);
            mViewHolder.mOpenContextButton = (ImageView) convertView.findViewById(R.id.open_context_button);
            mViewHolder.mOpenContextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent().getParent();
                    final int position = listView.getPositionForView(parentRow);
                    CareRecord careRecord = (CareRecord) getItem(position);
                    mSelectedRecipientId = careRecord.getCareRecipientId();
                    mSelectedRequestId = careRecord.getRecordId();
                    listView.showContextMenuForChild(v);
                }
            });
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.mRecipientName.setText(careRecord.getRecipientName());
        Date appointmentDate = careRecord.getAppointmentTime();
        mViewHolder.mAppointmentTime.setText(DateUtils.formatTime(appointmentDate));
        mViewHolder.mAppointmentDate.setText(DateUtils.formatDate(appointmentDate));

        return convertView;
    }

    public static class ViewHolder {

        private LinearLayout mExistingRequestItem;
        private ImageView mRecipientPicture;
        private TextView mRecipientName;
        private TextView mAppointmentTime;
        private TextView mAppointmentDate;
        private ImageView mOpenContextButton;

    }
}
