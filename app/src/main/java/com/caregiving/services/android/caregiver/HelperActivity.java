package com.caregiving.services.android.caregiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.caregiving.services.android.caregiver.models.Role;
import com.caregiving.services.android.caregiver.utils.PrefUtils;

public class HelperActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(0, 0);

        Context context = getApplicationContext();
        String username = PrefUtils.getStringFromPrefs(context, PrefUtils.PREFS_LOGIN_USERNAME_KEY, "");
        String password = PrefUtils.getStringFromPrefs(context, PrefUtils.PREFS_LOGIN_PASSWORD_KEY, "");
        String role = PrefUtils.getStringFromPrefs(context, PrefUtils.PREFS_ROLE_KEY, "");

        if (username.length() > 0
                && password.length() > 0
                && role.length() > 0) {

            if (role.equals(Role.CARE_GIVER.getAcronym())) {
                startActivity(new Intent(this, CareGiverMainActivity.class));
            } else if (role.equals(Role.CARE_RECIPIENT.getAcronym()) || role.equals(Role.FAMILY_MEMBER.getAcronym())) {
                startActivity(new Intent(this, CareRecipientMainActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }

    @Override
    public void finish() {
        overridePendingTransition(0, 0);
        super.finish();
    }
}
