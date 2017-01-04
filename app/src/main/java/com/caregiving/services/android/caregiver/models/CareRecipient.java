package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PC1 on 26/9/2016.
 */
public class CareRecipient extends Account {

    @SerializedName("name")
    private String name;

    @SerializedName("gender")
    private String gender;

    @SerializedName("address")
    private String address;

    @SerializedName("resident_contact")
    private String residentContact;

    @SerializedName("mobile_contact")
    private String mobileContact;

    @SerializedName("contact_person_id")
    private int contactPersonId;

    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("beacon_id")
    private String beaconId;

    public CareRecipient(int accountId, String name,String gender, String address,
                         String residentContact, String mobileContact, int contactPersonId,int doctorId) {
        super(accountId, Role.CARE_RECIPIENT);
        this.name = name;
        this.gender = gender;
        this.address = address;
        this.residentContact = residentContact;
        this.mobileContact = mobileContact;
        this.contactPersonId = contactPersonId;
        this.doctorId=doctorId;
    }

    public String getName() {
        return name;
    }

    public CareRecipient setName(String name) {
        this.name = name;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public CareRecipient setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public CareRecipient setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getResidentContact() {
        return residentContact;
    }

    public CareRecipient setResidentContact(String residentContact) {
        this.residentContact = residentContact;
        return this;
    }

    public String getMobileContact() {
        return mobileContact;
    }

    public CareRecipient setMobileContact(String mobileContact) {
        this.mobileContact = mobileContact;
        return this;
    }

    public int getContactPersonId() {
        return contactPersonId;
    }

    public CareRecipient setContactPersonId(int contactPersonId) {
        this.contactPersonId = contactPersonId;
        return this;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public CareRecipient setDoctorId(int doctorId) {
        this.doctorId = doctorId;
        return this;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public CareRecipient setBeaconId(String beaconId) {
        this.beaconId = beaconId;
        return this;
    }

}
