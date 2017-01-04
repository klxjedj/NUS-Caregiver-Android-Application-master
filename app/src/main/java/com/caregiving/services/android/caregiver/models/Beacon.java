package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aloys on 10/10/2016.
 */
public class Beacon {

    @SerializedName("UUID")
    private String UUID;

    @SerializedName("minor")
    private String minor;

    @SerializedName("major")
    private String major;

    @SerializedName("carerecipient_id")
    private int carerecipientId;

    public Beacon(String UUID, String minor, String major, int carerecipientId) {
        this.UUID = UUID;
        this.minor = minor;
        this.major = major;
        this.carerecipientId = carerecipientId;
    }

    public String getUUID() {
        return UUID;
    }

    public Beacon setUUID(String UUID) {
        this.UUID = UUID;
        return this;
    }

    public String getMinor() {
        return minor;
    }

    public Beacon setMinor(String minor) {
        this.minor = minor;
        return this;
    }

    public String getMajor() {
        return major;
    }

    public Beacon setMajor(String major) {
        this.major = major;
        return this;
    }

    public int getCarerecipientId() {
        return carerecipientId;
    }

    public Beacon setCarerecipientId(int carerecipientId) {
        this.carerecipientId = carerecipientId;
        return this;
    }
}
