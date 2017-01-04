package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

/**
 * Created by PC1 on 26/9/2016.
 */
public class DoctorReferral {

    private int referalId;
    private int doctorId;
    private int recipientId;
    private String referralReason;
    private LocalDate referralDate;

    public DoctorReferral() {}

    public DoctorReferral(int referalId,int doctorId, int recipientId, String referralReason, LocalDate referralDate) {
        this.referalId=referalId;
        this.doctorId = doctorId;
        this.recipientId = recipientId;
        this.referralReason = referralReason;
        this.referralDate = referralDate;
    }

    public int getReferalId() {
        return referalId;
    }

    public DoctorReferral setReferalId(int referalId) {
        this.referalId = referalId;
        return this;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public DoctorReferral setDoctorId(int doctorId) {
        this.doctorId = doctorId;
        return this;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public DoctorReferral setRecipientId(int recipientId) {
        this.recipientId = recipientId;
        return this;
    }

    public String getReferralReason() {
        return referralReason;
    }

    public DoctorReferral setReferralReason(String referralReason) {
        this.referralReason = referralReason;
        return this;
    }

    public LocalDate getReferralDate() {
        return referralDate;
    }

    public DoctorReferral setReferralDate(LocalDate referralDate) {
        this.referralDate = referralDate;
        return this;
    }
}
