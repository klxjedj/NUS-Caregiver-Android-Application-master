package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by aloys on 20/10/2016.
 */
public class CareServiceCategory {

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("category_name")
    private String categoryName;
    /**
     * Default Constructor
     */
    public CareServiceCategory() {}

    public CareServiceCategory(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public CareServiceCategory setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public CareServiceCategory setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

}
