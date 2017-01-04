package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PC1 on 26/9/2016.
 */
public class FamilyMember extends Account {

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("contact_number")
    private String contactNumber;

    public FamilyMember(int accountId, String name, String address, String contactNumber) {
        super(accountId, Role.FAMILY_MEMBER);
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public FamilyMember setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public FamilyMember setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public FamilyMember setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
        return this;
    }

}
