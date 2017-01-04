package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aloys on 20/10/2016.
 */
public class Bidding {

    @SerializedName("bidding_id")
    private int biddingId;

    @SerializedName("caregiver_id")
    private int caregiverId;

    @SerializedName("record_id")
    private int recordId;

    @SerializedName("bidding_status")
    private String bidding_status;
    /**
     * Default Constructor
     */
    public Bidding() {}

    public Bidding(int biddingId, int caregiverId, int recordId, String bidding_status) {
        this.biddingId = biddingId;
        this.caregiverId = caregiverId;
        this.recordId = recordId;
        this.bidding_status=bidding_status;
    }

    public int getBiddingId() {
        return biddingId;
    }

    public Bidding setBiddingId(int biddingId) {
        this.biddingId = biddingId;
        return this;
    }

    public int getCaregiverId() {
        return caregiverId;
    }

    public Bidding setCaregiverId(int caregiverId) {
        this.caregiverId = caregiverId;
        return this;
    }

    public int getRecordId() {
        return recordId;
    }

    public Bidding setRecordId(int recordId) {
        this.recordId = recordId;
        return this;
    }

    public String getBidding_status(){
        return bidding_status;
    }

    public Bidding setBiddingStatus(String bidding_status){
        this.bidding_status=bidding_status;
        return this;
    }
}
