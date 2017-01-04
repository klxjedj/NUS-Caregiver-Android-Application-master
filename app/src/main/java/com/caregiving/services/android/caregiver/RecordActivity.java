package com.caregiving.services.android.caregiver;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.caregiving.services.android.caregiver.fragments.EmptyFragment;
import com.caregiving.services.android.caregiver.fragments.RecipientDemographicFragment;
import com.caregiving.services.android.caregiver.models.CareRecipient;
import com.caregiving.services.android.caregiver.models.CareRecord;
import com.caregiving.services.android.caregiver.models.actionMaps.CaregiverActionMap;

import com.caregiving.services.android.caregiver.utils.GsonUtils;
import com.caregiving.services.android.caregiver.utils.HttpUtils;
import com.caregiving.services.android.caregiver.utils.PrefUtils;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecordActivity extends BaseActivity
        implements RecipientDemographicFragment.OnFragmentInteractionListener {

    private boolean mHasActOnRequest = false;
    private boolean showActivityAcceptDialog;
    private int recipientId;
    private int recordId;
    private String recipientName;
    private static final String TAG = "RecipientRecordActivity";
    public CareRecipient mCareRecipient;
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mFragmentTitle=new ArrayList<>();
    private Bundle mBundle;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @BindView(R.id.crr_viewPager)
    protected ViewPager viewPager;
    @BindView(R.id.crr_toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.crr_tabs)
    protected TabLayout tabLayout;
    @BindView(R.id.fab_track_recipient)
    protected FloatingActionButton trackRecipientButton;
    @BindView(R.id.crr_bottom_bar)
    protected BottomBar bottomBar;
    @BindView(R.id.carerecipient_name)
    protected TextView carerecipientNameText;
    @BindView(R.id.crr_progress_bar)
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_recipient_record);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mBundle = getIntent().getExtras();
        recipientId = mBundle.getInt("carerecipient_id");
        recordId = mBundle.getInt("record_id");
        recipientName = mBundle.getString("carerecipient_name");
        boolean hasActedOnRequest = mBundle.getBoolean("hasActedOnRequest");
        boolean showTrackingButton = mBundle.getBoolean("showTrackingButton");
        carerecipientNameText.setText(recipientName);

        if (hasActedOnRequest) {
            bottomBar.setVisibility(View.VISIBLE);
            trackRecipientButton.setVisibility(View.GONE);
            bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
                @Override
                public void onTabReSelected(@IdRes int tabId) {
                    onSelectTabListener(tabId);
                }
            });
            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    onSelectTabListener(tabId);
                }
            });
        }

        if (!showTrackingButton) {
            trackRecipientButton.setVisibility(View.GONE);
        }
        mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager(),mFragments,mFragmentTitle);



        viewPager.setOffscreenPageLimit(mFragments.size());
        viewPager.setAdapter(mSectionsPagerAdapter);

        mFragmentTitle.add("Blank");
        mFragments.add(new EmptyFragment());

        mFragmentTitle.add("Blank 2");
        mFragments.add(new EmptyFragment());

        mSectionsPagerAdapter.notifyDataSetChanged();


    }

    private void acceptRequest(MaterialDialog dialog) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", CaregiverActionMap.ACCEPT_REQUEST.getAction())
                    .put("user_id", PrefUtils.getUserId(getApplicationContext()))
                    .put("record_id", recordId);
            HttpUtils.jsonPost(getApplicationContext(), jsonObject, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseString = response.body().string();
                    if (responseString.toLowerCase().equals("request accepted")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bottomBar.setVisibility(View.GONE);
                                trackRecipientButton.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), recipientName + "'s request accepted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.wtf(TAG, responseString);
                    }
                }
            });
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSelectTabListener(int tabId) {
        if (tabId == R.id.tab_accept) {
            if (mHasActOnRequest) {
                final boolean innerShowActivityAcceptDialog = PrefUtils.getBooleanFromPrefs(getApplicationContext(), "showActivityAcceptDialog", true);
                if (innerShowActivityAcceptDialog) {
                    MaterialDialog confirmCancelDialog = new MaterialDialog.Builder(RecordActivity.this)
                            .content(R.string.confirm_accept_service_request)
                            .positiveText(R.string.confirm_cancel_dialog_confirm)
                            .checkBoxPromptRes(R.string.action_never_show_again, false, new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    showActivityAcceptDialog = isChecked;
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (!innerShowActivityAcceptDialog) {
                                        PrefUtils.saveBooleanToPrefs(getApplicationContext(), "showActivityAcceptDialog", showActivityAcceptDialog);
                                    }
                                    acceptRequest(dialog);
                                }
                            })
                            .negativeText(R.string.confirm_cancel_dialog_cancel)
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).build();

                    confirmCancelDialog.show();
                } else {
                    MaterialDialog confirmCancelDialog = new MaterialDialog.Builder(RecordActivity.this)
                            .content(R.string.confirm_accept_service_request)
                            .positiveText(R.string.confirm_cancel_dialog_confirm)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    acceptRequest(dialog);
                                }
                            })
                            .negativeText(R.string.confirm_cancel_dialog_cancel)
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).build();

                    confirmCancelDialog.show();
                }
            }
            mHasActOnRequest = true;
        } else if (tabId == R.id.tab_reject) {
            if (mHasActOnRequest) {
                String dialogMessage = "";
                int cancellationsLeft = PrefUtils.getIntFromPrefs(getApplicationContext(), "cancellationsLeft", 3);
                Context context = RecordActivity.this;

                if (cancellationsLeft < 3) {
                    String dialogMessageSecondParameter = (cancellationsLeft == 1) ? "time" : "times";
                    dialogMessage = context.getResources().getString(R.string.confirm_reject_dialog_content,
                            cancellationsLeft, dialogMessageSecondParameter);
                } else {
                    dialogMessage = context.getResources().getString(R.string.confirm_reject_dialog_content_alert);
                }

                MaterialDialog confirmCancelDialog = new MaterialDialog.Builder(context)
                        .title(R.string.confirm_reject_dialog_title)
                        .content(dialogMessage)
                        .positiveText(R.string.confirm_cancel_dialog_confirm)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("action", CaregiverActionMap.REJECT_REQUEST.getAction())
                                            .put("user_id", PrefUtils.getUserId(getApplicationContext()))
                                            .put("record_id", recordId);
                                    HttpUtils.jsonPost(getApplicationContext(), jsonObject, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            final String responseString = response.body().string();
                                            if (Integer.parseInt(responseString) > 0) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bottomBar.setVisibility(View.GONE);
                                                        Toast.makeText(getApplicationContext(), recipientName +
                                                                "'s request rejected", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Log.wtf(TAG, responseString);
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .negativeText(R.string.confirm_cancel_dialog_cancel)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).build();

                confirmCancelDialog.show();
            } else {
                mHasActOnRequest = true;
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @OnClick(R.id.fab_track_recipient)
    public void onTrackRecipient() {
        final MaterialDialog confirmScanDialog = new MaterialDialog.Builder(this)
                .title("Confirm")
                .content("Start care service with " + recipientName)
                .positiveText(android.R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    }
                })
                .negativeText(android.R.string.no)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build();

        confirmScanDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mAdapterFragments;
        private List<String> mFragmentTitles;

        public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> fragmentTitles) {
            super(fm);
            mAdapterFragments = fragments;
            mFragmentTitles = fragmentTitles;
        }

        @Override
        public Fragment getItem(int position) {

            return (mAdapterFragments.get(position)) != null ? mAdapterFragments.get(position) : EmptyFragment.newInstance();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


}

