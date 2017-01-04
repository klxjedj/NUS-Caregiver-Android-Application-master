package com.caregiving.services.android.caregiver.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;

/**
 * Created by PC1 on 22/9/2016.
 */
public abstract class ListBaseAdapter extends BaseAdapter {

    private Context mContext;
    private HashMap<String, String> mItemValues;
    private String[] mItemTitles;

    public ListBaseAdapter(Context mContext, HashMap<String, String> mItemValues, String[] mItemTitles) {
        super();
        this.mContext = mContext;
        this.mItemValues = mItemValues;
        this.mItemTitles = mItemTitles;
    }

    @Override
    public int getCount() {
        return mItemTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return mItemValues.get(mItemTitles[position]);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

}
