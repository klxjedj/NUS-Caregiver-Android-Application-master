package com.caregiving.services.android.caregiver.models.actionMaps;

/**
 * Created by aloys on 30/9/2016.
 */
public enum CaregiverActionMap {
    CHANGE_PASSWORD("change_password"),
    GET_SUMMARIZED_PROFILE("get_summarized_profile"),
    VIEW_ALL_REQUEST("view_all_request"),
    ACCEPT_REQUEST("accept_request"),
    REJECT_REQUEST("reject_request"),
    GET_CANCELLATIONS_LEFT("get_cancellations_left"),
    VIEW_FULL_INFO("view_full_info"),
    VIEW_SERVICE_TO_PERFORM_TODAY("view_service_to_perform_today"),
    VIEW_RECIPIENT_CARE_SERVICE_HISTORY("view_recipient_care_service_history"),
    VIEW_RECIPIENT("view_care_recipient");
    private String action;

    public String getAction() {
        return action;
    }

    CaregiverActionMap(String action) {
        this.action = action;
    }
}
