package com.caregiving.services.android.caregiver;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.caregiving.services.android.caregiver.models.CareGiver;
import com.caregiving.services.android.caregiver.models.actionMaps.CaregiverActionMap;
import com.caregiving.services.android.caregiver.models.Role;
import com.caregiving.services.android.caregiver.utils.AndroidBug5497Workaround;
import com.caregiving.services.android.caregiver.utils.GsonUtils;
import com.caregiving.services.android.caregiver.utils.HttpUtils;
import com.caregiving.services.android.caregiver.utils.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private static final String APP_LOGO_TEXT = "Carebeacon";
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String TAG = "LoginActivity";

    @BindView(R.id.input_password) protected TextInputEditText mPasswordField;
    @BindView(R.id.login_progress) protected ProgressBar mProgressView;
    @BindView(R.id.login_form) protected LinearLayout mLoginFormView;
    @BindView(R.id.text_app_logo) protected TextView mTextAppLogo;
    @BindView(R.id.input_username) protected TextInputEditText mUsernameField;
    @BindView(R.id.login_button) protected Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AndroidBug5497Workaround.assistActivity(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        SpannableString appLogoText = new SpannableString(APP_LOGO_TEXT.toUpperCase());
        appLogoText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 4, APP_LOGO_TEXT.length(), 0);
        mTextAppLogo.setText(appLogoText, TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.login_button)
    public void onClick(View view) {
        attemptLogin();
    }

    @OnEditorAction(R.id.input_password)
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.content_wrapper || id == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }

    public void attemptLogin() {

        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();

        mUsernameField.setError(null);
        mPasswordField.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordField.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordField;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameField.setError(getString(R.string.error_field_required));
            focusView = mUsernameField;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameField.setError(getString(R.string.error_invalid_user_id));
            focusView = mUsernameField;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            new UserLoginTask(username, password, this).execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 0;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private Activity activity;
        private String mUsername;
        private String mPassword;
        private Intent mIntent;

        UserLoginTask(String username, String password, Activity activity) {
            this.mUsername = username;
            this.mPassword = password;
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", mUsername)
                        .put("password", mPassword);
                HttpUtils.jsonLoginPost(getApplicationContext(), jsonObject, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String responseBodyString = response.body().string();
                            try {
                                Log.d("HTTP Login Response", responseBodyString);
                                JSONObject responseJson = new JSONObject(responseBodyString);
                                mUsername = responseJson.getString("username");
                                if (response.isSuccessful()) {
                                    String role = responseJson.getString("role");
                                    int userId = responseJson.getInt("account_id");

                                    PrefUtils.saveStringToPrefs(
                                            getApplicationContext(),
                                            PrefUtils.PREFS_LOGIN_USERNAME_KEY,
                                            mUsername);

                                    PrefUtils.saveStringToPrefs(
                                            getApplicationContext(),
                                            PrefUtils.PREFS_LOGIN_PASSWORD_KEY,
                                            responseJson.getString("password"));

                                    PrefUtils.saveStringToPrefs(
                                            getApplicationContext(),
                                            PrefUtils.PREFS_ROLE_KEY,
                                            role);

                                    PrefUtils.saveIntToPrefs(
                                            getApplicationContext(),
                                            PrefUtils.PREFS_USER_ID,
                                            userId);

                                    onPostLoginExecute(true, role, userId);
                                }
                            } catch (Exception e) {
                                if (!(e instanceof JSONException)) {
                                    e.printStackTrace();
                                }
                                final String info=e.toString();

                                if (responseBodyString.length() > 0) {
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            new MaterialDialog.Builder(LoginActivity.this)
                                                    .title(responseBodyString)
                                                    .content(info)
                                                    .positiveText(android.R.string.ok)
                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            dialog.dismiss();
                                                            showProgress(false);
                                                        }
                                                    })
                                                    .build()
                                                    .show();
                                        }
                                    });
                                }
                            }
                        } else {

                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        private void onPostLoginExecute(boolean mIsLoginRequestSuccessful, String role, final int userId) {
            if (mIsLoginRequestSuccessful) {
                final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                final String usernamePrefKey = mUsername + "Login";
                if (prefs.getBoolean(usernamePrefKey, true)) {
                    if (role.equals(Role.CARE_GIVER.getAcronym())) {
                        final MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(activity)
                                .title(R.string.ftd_title)
                                .content(R.string.ftd_content)
                                .positiveText(R.string.ftd_confirm_password)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                        try {
                                            JSONObject jsonObject = new JSONObject();
                                            final String newPassword = dialog.getInputEditText().getText().toString();
                                            jsonObject.put("action", CaregiverActionMap.CHANGE_PASSWORD.getAction())
                                                    .put("user_id", userId)
                                                    .put("old_password", mPassword)
                                                    .put("new_password", newPassword);
                                            HttpUtils.jsonPost(getApplicationContext(), jsonObject, new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    e.printStackTrace();
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    String reponseString = response.body().string();
                                                    if (reponseString.equals("Password Changed")) {
                                                        dialog.dismiss();
                                                        Log.d("HTTP jsonPost Response", reponseString);

                                                        prefs.edit().putBoolean(usernamePrefKey, false).apply();

                                                        PrefUtils.saveStringToPrefs(
                                                                getApplicationContext(),
                                                                PrefUtils.PREFS_LOGIN_PASSWORD_KEY,
                                                                newPassword);

                                                        storeSummarizedProfile();
                                                        mIntent = new Intent(LoginActivity.this, CareGiverMainActivity.class);
                                                        startActivity(mIntent);
                                                        Log.d(TAG, "Navigating user to CareGiverMainActivity");
                                                        finish();
                                                    } else {
                                                        dialog.getInputEditText().setError(getString(R.string.error_incorrect_password));
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .dismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        showProgress(false);
                                    }
                                })
                                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                .input(R.string.ftd_new_password, 0, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        if (input.length() > 0) {
                                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                        } else {
                                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                        }
                                    }
                                })
                                .autoDismiss(true)
                                .alwaysCallInputCallback();

                        LoginActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                MaterialDialog firstTimeLoginDialog = dialogBuilder.build();
                                firstTimeLoginDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                firstTimeLoginDialog.show();
                            }
                        });
                    } else if (role.equals(Role.CARE_RECIPIENT.getAcronym())
                            || role.equals(Role.FAMILY_MEMBER.getAcronym())) {
                        mIntent = new Intent(LoginActivity.this, CareRecipientMainActivity.class);
                        startActivity(mIntent);
                        Log.d(TAG, "Navigating user to CareRecipientMainActivity");
                        finish();
                    }
                } else {
                    storeSummarizedProfile();
                    mIntent = new Intent(LoginActivity.this, CareGiverMainActivity.class);
                    startActivity(mIntent);
                    Log.d(TAG, "Navigating user to CareGiverMainActivity");
                    finish();
                }
            } else {
                showProgress(false);
            }
        }

        private boolean storeSummarizedProfile() {
            boolean success = false;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("action", CaregiverActionMap.GET_SUMMARIZED_PROFILE.getAction())
                    .put("user_id", PrefUtils.getUserId(getApplicationContext()));
                HttpUtils.jsonPost(getApplicationContext(), jsonObject, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        CareGiver careGiver = GsonUtils.fromJson(response.body().charStream(), CareGiver.class);
                        try {
                            PrefUtils.saveStringToPrefs(getApplicationContext(),
                                    "caregiver_name", careGiver.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}

