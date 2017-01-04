package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by aloys on 20/10/2016.
 */
public class EmploymentHistory {

    @SerializedName("employment_history_id")
    private int employmentHistoryId;

    @SerializedName("institution")
    private String institution;

    @SerializedName("position")
    private String position;

    @SerializedName("start_date")
    private DateTime startDate;

    @SerializedName("end_date")
    private DateTime endDate;

    @SerializedName("profession_info_id")
    private int professionInfoId;

    /**
     * Default Constructor
     */
    public EmploymentHistory() {}

    public EmploymentHistory(int employmentHistoryId, String institution, String position, DateTime startDate, DateTime endDate, int professionInfoId) {
        this.employmentHistoryId = employmentHistoryId;
        this.institution = institution;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.professionInfoId = professionInfoId;
    }

    public int getEmploymentHistoryId() {
        return employmentHistoryId;
    }

    public EmploymentHistory setEmploymentHistoryId(int employmentHistoryId) {
        this.employmentHistoryId = employmentHistoryId;
        return this;
    }

    public String getInstitution() {
        return institution;
    }

    public EmploymentHistory setInstitution(String institution) {
        this.institution = institution;
        return this;
    }

    public String getPosition() {
        return position;
    }

    public EmploymentHistory setPosition(String position) {
        this.position = position;
        return this;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public EmploymentHistory setStartDate(DateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public EmploymentHistory setEndDate(DateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public int getProfessionInfoId() {
        return professionInfoId;
    }

    public EmploymentHistory setProfessionInfoId(int professionInfoId) {
        this.professionInfoId = professionInfoId;
        return this;
    }
}
