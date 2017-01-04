package com.caregiving.services.android.caregiver.models.actionMaps;

/**
 * Created by aloys on 25/10/2016.
 */
public enum CommonActionMap {

    VIEW_CARE_GIVER_SERVICE_REVIEWS("view_care_giver_service_reviews"),
    VIEW_ALL_CARE_SERVICE_CATEGORY("view_all_care_service_category"),
    UPDATE_CARE_REQUEST_APPOINTMENT_DATE("update_care_request_appointment_date"),
    UPDATE_CARE_REQEST_END_DATETIME("update_care_request_end_datetime");

    private String action;

    public String getAction() {
        return action;
    }

    CommonActionMap(String action) {
        this.action = action;
    }

}
