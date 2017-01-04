package com.caregiving.services.android.caregiver.models.actionMaps;

/**
 * Created by aloys on 1/10/2016.
 */
public enum CarerecipientActionMap {
    VIEW_AVAILABLE_CARE_GIVERS("view_available_care_givers"),
    VIEW_CARE_GIVER_BY_ID("view_care_giver_by_id"),
    CREATE_CARE_REQUEST("create_care_request"),;

    private String action;

    public String getAction() {
        return action;
    }

    CarerecipientActionMap(String action) {
        this.action = action;
    }
}
