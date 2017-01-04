package com.caregiving.services.android.caregiver.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caregiving.services.android.caregiver.CareGiverActivity;
import com.caregiving.services.android.caregiver.R;
import com.caregiving.services.android.caregiver.models.CareGiver;

import java.util.ArrayList;

public class CaregiverListAdapter extends BaseAdapter {

    private ArrayList<CareGiver> mCaregivers;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public CaregiverListAdapter(Context context, ArrayList<CareGiver> careGivers) {
        mContext = context;
        mCaregivers = careGivers;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mCaregivers.size();
    }

    @Override
    public Object getItem(int position) {
        return mCaregivers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mCaregivers.get(position).getAccountId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item_care_giver, null);

            viewHolder.mCaregiverName = (TextView) convertView.findViewById(R.id.caregiver_name);
            viewHolder.mServiceCount = (TextView) convertView.findViewById(R.id.caregiver_service_count);
            viewHolder.mActionContainer = (LinearLayout) convertView.findViewById(R.id.action_container);
            viewHolder.mToggleActionBar = (ImageView) convertView.findViewById(R.id.toggle_action_button);
            viewHolder.mOverallRating = (AppCompatRatingBar) convertView.findViewById(R.id.caregiver_overall_rating);
            viewHolder.mOverallRatingValue = (TextView) convertView.findViewById(R.id.caregiver_overall_rating_value);
            viewHolder.mRatingBarContainer = (LinearLayout) convertView.findViewById(R.id.rating_bar_container);
            viewHolder.mCaregeiverInfo = (LinearLayout) convertView.findViewById(R.id.caregiver_info);
            viewHolder.mCaregeiverInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CareGiver careGiver = (CareGiver) getItem(position);
                    Intent caregiverIntent = new Intent(mContext, CareGiverActivity.class);
                    caregiverIntent
                            .putExtra("caregiverId", careGiver.getAccountId())
                            .putExtra("caregiverName", careGiver.getName());
                    mContext.startActivity(caregiverIntent);
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CareGiver careGiver = mCaregivers.get(position);
        viewHolder.mCaregiverName.setText(careGiver.getName());
        viewHolder.mServiceCount.setText(String.valueOf(careGiver.getNoOfServiceGiven()));

        Float rating = careGiver.getAverageRating();
        viewHolder.mOverallRating.setRating(rating);
        viewHolder.mOverallRatingValue.setText(String.valueOf(rating));
        viewHolder.mRatingBarContainer.setVisibility(View.VISIBLE);


        return convertView;
    }

    public static class ViewHolder {
        private TextView mCaregiverName;
        private TextView mServiceCount;
        private TextView mOverallRatingValue;
        private ImageView mToggleActionBar;
        private LinearLayout mActionContainer;
        private LinearLayout mRatingBarContainer;
        private LinearLayout mCaregeiverInfo;
        private AppCompatRatingBar mOverallRating;
    }
}
