package com.caregiving.services.android.caregiver;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.caregiving.services.android.caregiver.models.CareServiceCategory;
import com.caregiving.services.android.caregiver.models.actionMaps.CarerecipientActionMap;
import com.caregiving.services.android.caregiver.utils.HttpUtils;
import com.caregiving.services.android.caregiver.utils.PrefUtils;
import com.codetroopers.betterpickers.Utils;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CareRecipientRequestServiceFormActivity extends BaseActivity implements
        CalendarDatePickerDialogFragment.OnDateSetListener,
        RadialTimePickerDialogFragment.OnTimeSetListener{

    private static int userId;
    private static int caregiverId;
    private static String caregiverName;
    private DateTime appointmentDate;
    private DateTime appointmentTime;
    private DateTime appointmentDateTime;
    private String specialNeeds;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";

    @BindView(R.id.request_service_form_toolbar) Toolbar toolbar;
    @BindView(R.id.caregiver_name) TextView caregiverNameText;
    @BindView(R.id.appointment_date_input) TextInputEditText appointmentDateInput;
    @BindView(R.id.appointment_time_input) TextInputEditText appointmentTimeInput;
    @BindView(R.id.special_needs_input) TextInputEditText specialNeedsInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service_form);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        caregiverId = extras.getInt("caregiver_id");
        userId = PrefUtils.getUserId(getApplicationContext());
        caregiverName = extras.getString("caregiver_name");
        caregiverNameText.setText(caregiverName);
        toolbar.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialogFragment != null) {
            calendarDatePickerDialogFragment.setOnDateSetListener(this);
        }

        RadialTimePickerDialogFragment rtpd = (RadialTimePickerDialogFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG_TIME_PICKER);
        if (rtpd != null) {
            rtpd.setOnTimeSetListener(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.appointment_date_input)
    public void onClickAppointmentDate(View view) {
        if (view.isFocusable()) {
            view.setFocusable(false);
        }

        DateTime now = DateTime.now();
        MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(now.getYear(), now.getMonthOfYear() - 2, now.getDayOfMonth());
        MonthAdapter.CalendarDay maxDate = new MonthAdapter.CalendarDay(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());

        SparseArray<MonthAdapter.CalendarDay> disabledDays = new SparseArray<>();
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(minDate.getDateInMillis());
        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(maxDate.getDateInMillis());

        while (startCal.before(endCal)) {
            if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                    || startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                int key = Utils.formatDisabledDayForKey(startCal.get(Calendar.YEAR),
                        startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH));
                disabledDays.put(key, new MonthAdapter.CalendarDay(startCal));
            }
            startCal.add(Calendar.DATE, 1);
        }

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setDateRange(minDate, maxDate)
                .setDisabledDays(disabledDays)
                .setOnDateSetListener(CareRecipientRequestServiceFormActivity.this);

        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        DateTime now = DateTime.now();
        appointmentDate = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0, 0);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("d MMMM");
        String yearString = (now.getYear() == year) ? "" : String.valueOf(year);
        appointmentDateInput.setText(formatter.print(appointmentDate) + " " + yearString);
    }

    @OnClick(R.id.appointment_time_input)
    public void onClickAppointmentTime(View view) {
        if (view.isFocusable()) {
            view.setFocusable(false);
        }

        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                .setOnTimeSetListener(CareRecipientRequestServiceFormActivity.this)
                .setStartTime(9,30)
                .setForced12hFormat();
        rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        appointmentTime = new DateTime(2016, 1, 1, hourOfDay, minute, 0);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("h:mm a");
        appointmentTimeInput.setText(formatter.print(appointmentTime));
    }

    @OnClick(R.id.request_service_button)
    public void onRequestService() {
        boolean cancel = false;
        View focusView = null;

        appointmentDateInput.setError(null);
        appointmentTimeInput.setError(null);

        if (appointmentDate == null) {
            appointmentDateInput.setError(getString(R.string.error_missing_appointment_date));
            focusView = appointmentDateInput;
            cancel = true;
        }

        if (appointmentTime == null) {
            appointmentTimeInput.setError(getString(R.string.error_missing_appointment_time));
            focusView = appointmentTimeInput;
            cancel = true;
        }

        if (cancel) {
            focusView.setFocusable(true);
            focusView.requestFocus();
        } else {
            new RequestServiceTask().execute();
        }
    }


    private class RequestServiceTask extends AsyncTask<Void, Void, Boolean> {

        private MaterialDialog progressDialog;
        private boolean success = false;
        private int selectedServiceCategory;

        @Override
        protected void onPreExecute() {
            progressDialog = new MaterialDialog.Builder(CareRecipientRequestServiceFormActivity.this)
                    .content(R.string.submit_service_request)
                    .progress(true, 0)
                    .build();
            progressDialog.show();
            specialNeeds=specialNeedsInput.getText().toString();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            appointmentDateTime = new DateTime(
                    appointmentDate.getYear(), appointmentDate.getMonthOfYear(), appointmentDate.getDayOfMonth(),
                    appointmentTime.getHourOfDay(), appointmentTime.getMinuteOfHour(), 0
            );
            JSONObject jsonObject = new JSONObject();
            Log.d("userId", String.valueOf(userId));

            try {
                jsonObject.put("action", CarerecipientActionMap.CREATE_CARE_REQUEST.getAction())
                        .put("user_id", userId)
                        .put("caregiver_id", caregiverId)
                        .put("special_needs", specialNeeds)
                        .put("appointment_time", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(appointmentDateTime));
                HttpUtils.jsonPost(getApplicationContext(), jsonObject, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseString = response.body().string();
                        if (responseString.equals("success")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Service request sent to " + caregiverName, Toast.LENGTH_SHORT)
                                        .show();
                                }
                            });
                            success = true;
                            finish();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            progressDialog.dismiss();
            try {
                Thread.sleep(150);
                if (success) {
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ServiceCategoryAdapter extends ArrayAdapter<CareServiceCategory> {

        private Context context;

        private CareServiceCategory[] careServiceCategories;

        public ServiceCategoryAdapter(Context context, int textViewResourceId,
                                      CareServiceCategory[] careServiceCategories) {
            super(context, textViewResourceId, careServiceCategories);
            this.context = context;
            this.careServiceCategories = careServiceCategories;
        }

        @Override
        public CareServiceCategory getItem(int position) {
            return careServiceCategories[position];
        }

        @Override
        public long getItemId(int position) {
            return careServiceCategories[position].getCategoryId();
        }

        @Override
        public int getCount() {
            return careServiceCategories.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(context);
            label.setText(careServiceCategories[position].getCategoryName());

            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(context);
            label.setText(careServiceCategories[position].getCategoryName());

            return label;
        }
    }
}
