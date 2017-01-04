package com.caregiving.services.android.caregiver.views.adapters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caregiving.services.android.caregiver.R;

import java.util.HashMap;

public class DemographicAdapter extends ListBaseAdapter {

    private Context mContext;
    private HashMap<String, String> mDemographicsItemValues;
    private String[] mDemographicsItemTitles;
    private int[] mDemographicsItemIcons;
    private LayoutInflater mLayoutInflater;
    private boolean mShowIcon = true;
    private int mIconColorId;
    private int mRecordId;
    private String mRecipientName;

    public DemographicAdapter(Context context, HashMap<String, String> demographicsItemValues, String[] demographicsItemTitles, int[] demographicsItemIcons, int iconColorId, int recordId, String recipientName) {
        super(context, demographicsItemValues, demographicsItemTitles);
        mDemographicsItemValues = demographicsItemValues;
        mContext = context;
        mDemographicsItemTitles = demographicsItemTitles;
        mDemographicsItemIcons = demographicsItemIcons;
        mIconColorId = iconColorId;
        mRecordId = recordId;
        mRecipientName = recipientName;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public DemographicAdapter(Context context, HashMap<String, String> demographicsItemValues, String[] demographicsItemTitles, int[] demographicsItemIcons, int iconColorId) {
        super(context, demographicsItemValues, demographicsItemTitles);
        mDemographicsItemValues = demographicsItemValues;
        mContext = context;
        mDemographicsItemTitles = demographicsItemTitles;
        mDemographicsItemIcons = demographicsItemIcons;
        mIconColorId = iconColorId;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;

        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item_demographic_item, null);
            mViewHolder.mItemValue = (TextView) convertView.findViewById(R.id.demographic_item_value);
            mViewHolder.mItemTitle = (TextView) convertView.findViewById(R.id.demographic_item_title);
            mViewHolder.mItemIcon = (ImageView) convertView.findViewById(R.id.demographic_item_icon);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.mItemValue.setText(mDemographicsItemValues.get(mDemographicsItemTitles[position]));
        mViewHolder.mItemTitle.setText(mDemographicsItemTitles[position]);

        if (mShowIcon) {
            mViewHolder.mItemIcon.setImageResource(mDemographicsItemIcons[position]);
            mViewHolder.mItemIcon.setColorFilter(convertView.getContext().getResources().getColor(mIconColorId));
        }

        if (position == mDemographicsItemValues.size()) {
            View divider = convertView.findViewById(R.id.divider);
            divider.setVisibility(View.GONE);
        }

        return convertView;
    }

    public static class ViewHolder {

        private ImageView mItemIcon;
        private TextView mItemValue;
        private TextView mItemTitle;

    }

}
