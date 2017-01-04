package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PC1 on 26/9/2016.
 */
public enum Role {

    @SerializedName("a")
    ADMINISTRATOR("a"),

    @SerializedName("g")
    CARE_GIVER("g"),

    @SerializedName("r")
    CARE_RECIPIENT("r"),

    @SerializedName("m")
    FAMILY_MEMBER("m"),

    @SerializedName("d")
    DOCTOR("d");

    private String acronym;

    Role(String acronym) {
        this.acronym = acronym;
    }

    public String getAcronym() {
        return acronym;
    }
}
