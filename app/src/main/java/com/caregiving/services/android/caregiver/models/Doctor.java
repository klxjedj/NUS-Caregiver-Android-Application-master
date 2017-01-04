package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PC1 on 26/9/2016.
 */
public class Doctor extends Account {

    @SerializedName("name")
    private String name;

    @SerializedName("clinic")
    private String clinic;

    @SerializedName("contact")
    private String contactNumber;

    public Doctor(int accountId, String name, String clinic, String contactNumber) {
        super(accountId, Role.DOCTOR);
        this.name = name;
        this.clinic = clinic;
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public Doctor setName(String name) {
        this.name = name;
        return this;
    }

    public String getClinic() {
        return clinic;
    }

    public Doctor setClinic(String clinic) {
        this.clinic = clinic;
        return this;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public Doctor setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
        return this;
    }
}
