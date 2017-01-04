package com.caregiving.services.android.caregiver.utils;


import android.content.Context;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.util.ArrayList;
import java.util.List;

public class BeaconUtils {

    private static BluetoothClient mClient;
    private static List<String>  mAddressList=new ArrayList<>();
    private static Context mContext;
    private static boolean isInitalFinished;

    public static void beaconCheck(Context context, BeaconCheckCallback Callback, final String beaconAddress){
        final BeaconCheckCallback mListener=Callback;
        mContext=context;
        isInitalFinished=false;
        if (mAddressList!=null){
            mAddressList.clear();
        }
        mClient=new BluetoothClient(context);
        SearchRequest mRequest=new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000,3)
                .build();

        mClient.search(mRequest, new SearchResponse() {
            @Override
            public void onSearchStarted() {

            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                String address=device.getAddress().toString();
                if (!mAddressList.contains(address)){
                    mAddressList.add(address);
                }
            }

            @Override
            public void onSearchStopped() {
                if (mAddressList.contains(beaconAddress)) {
                    mListener.onBeaconCheckPassed();
                } else {
                    mListener.onBeaconCheckFailed();
                }

            }

            @Override
            public void onSearchCanceled() {
            }
        });
    }

    public interface BeaconCheckCallback {
        public void onBeaconCheckPassed();
        public void onBeaconCheckFailed();

    }

}
