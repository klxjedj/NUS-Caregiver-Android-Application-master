package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.sql.Date;

/**
 * Created by aloys on 17/9/2016.
 */
public class CareRecord {

    @SerializedName("record_id")
    private int recordId;

    @SerializedName("caregiver_id")
    private int caregiverId;

    @SerializedName("caregiver_name")
    private String caregiverName;

    @SerializedName("carerecipient_id")
    private int careRecipientId;

    @SerializedName("carerecipient_name")
    private String careRecipientName;

    @SerializedName("appointment_time")
    private Date appointmentTime;

    @SerializedName("end_datetime")
    private Date endDateTime;

    @SerializedName("special_need")
    private String specialNeeds;

    @SerializedName("record_status")
    private String recordStatus;

    @SerializedName("review")
    private String review;

    @SerializedName("rating")
    private float rating;

    public String getRecipientName() {
        return careRecipientName;
    }

    public CareRecord setRecipientName(String recipientName) {
        this.careRecipientName = recipientName;
        return this;
    }

    /**
     * Default Constructor
     */
    public CareRecord() {}

    public CareRecord(int recordId,int caregiverId, int careRecipientId, Date appointmentTime, Date endDateTime, String specialNeeds, String recordStatus, String review, float rating) {
        this.recordId = recordId;
        this.caregiverId = caregiverId;
        this.careRecipientId = careRecipientId;
        this.appointmentTime = appointmentTime;
        this.endDateTime = endDateTime;
        this.specialNeeds = specialNeeds;
        this.recordStatus = recordStatus;
        this.review = review;
        this.rating = rating;
    }

    public int getRecordId() {
        return recordId;
    }

    public CareRecord setRecordId(int recordId) {
        this.recordId = recordId;
        return this;
    }

    public int getCaregiverId() {
        return caregiverId;
    }

    public CareRecord setCaregiverId(int caregiverId) {
        this.caregiverId = caregiverId;
        return this;
    }

    public int getCareRecipientId() {
        return careRecipientId;
    }

    public CareRecord setCareRecipientId(int careRecipientId) {
        this.careRecipientId = careRecipientId;
        return this;
    }

    public Date getAppointmentTime() {
        return appointmentTime;
    }

    public CareRecord setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
        return this;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public CareRecord setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
        return this;
    }

    public String getSpecialNeeds() {
        return specialNeeds;
    }

    public CareRecord setSpecialNeeds(String specialNeeds) {
        this.specialNeeds = specialNeeds;
        return this;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public CareRecord setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
        return this;
    }

    public String getReview() {
        return review;
    }

    public CareRecord setReview(String review) {
        this.review = review;
        return this;
    }

    public float getRating() {
        return rating;
    }

    public CareRecord setRating(float rating) {
        this.rating = rating;
        return this;
    }

}

