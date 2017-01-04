package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

import java.sql.Date;

/**
 * Created by PC1 on 26/9/2016.
 */
public class CareGiver extends Account {

    @SerializedName("name")
    private String name;

    @SerializedName("gender")
    private String gender;

    @SerializedName("email")
    private String email;

    @SerializedName("contact")
    private String phoneNumber;

    @SerializedName("date_of_birth")
    private Date dateOfBirth;

    @SerializedName("highest_education_level")
    private String highestEducationLevel;

    @SerializedName("no_of_service_given")
    private Integer noOfServiceGiven;

    @SerializedName("block_status")
    private Integer blockStatus;

    @SerializedName("average_rating")
    private float averageRating;
    /**
     * Default Constructor
     */
    public CareGiver() {}

    public CareGiver(int accountId) {
        super(accountId, Role.CARE_GIVER);
    }

    public CareGiver(int accountId, String name, Integer noOfServiceGiven, Float averageRating) {
        super(accountId, Role.CARE_GIVER);
        this.name = name;
        this.noOfServiceGiven = noOfServiceGiven;
    }

    public String getName() {
        return name;
    }

    public CareGiver setName(String name) {
        this.name = name;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public CareGiver setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CareGiver setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public CareGiver setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public CareGiver setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public String getHighestEducationLevel() {
        return highestEducationLevel;
    }

    public CareGiver setHighestEducationLevel(String highestEducationLevel) {
        this.highestEducationLevel = highestEducationLevel;
        return this;
    }

    public Integer getNoOfServiceGiven() {
        return noOfServiceGiven;
    }

    public CareGiver setNoOfServiceGiven(Integer noOfServiceGiven) {
        this.noOfServiceGiven = noOfServiceGiven;
        return this;
    }

    public Integer getBlockStatus() {
        return blockStatus;
    }

    public CareGiver setBlockStatus(Integer blockStatus) {
        this.blockStatus = blockStatus;
        return this;
    }

    public CareGiver setAverageRating(float averageRating){
        this.averageRating=averageRating;
        return this;
    }

    public float getAverageRating(){
        return averageRating;
    }
}
