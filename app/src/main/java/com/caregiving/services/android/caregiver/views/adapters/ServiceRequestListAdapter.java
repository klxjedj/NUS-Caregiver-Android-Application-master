package com.caregiving.services.android.caregiver.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.caregiving.services.android.caregiver.RecordActivity;
import com.caregiving.services.android.caregiver.R;
import com.caregiving.services.android.caregiver.fragments.BaseFragment;
import com.caregiving.services.android.caregiver.fragments.ServiceRequestListFragment;
import com.caregiving.services.android.caregiver.models.CareRecord;
import com.caregiving.services.android.caregiver.models.actionMaps.CaregiverActionMap;
import com.caregiving.services.android.caregiver.utils.DateUtils;
import com.caregiving.services.android.caregiver.utils.HttpUtils;
import com.caregiving.services.android.caregiver.utils.PrefUtils;
import com.daimajia.swipe.SwipeLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * {@link RecyclerView.Adapter} that can display a list of {@link CareRecord} and makes a call to the
 * specified {@link ServiceRequestListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ServiceRequestListAdapter extends RecyclerView.Adapter<ServiceRequestListAdapter.ViewHolder> {

    private static final String TAG = "ServiceRequestListAdapter";
    private final List<CareRecord> mCareRecords;
    private final ServiceRequestListFragment.OnListFragmentInteractionListener mListener;

    private Context mContext;
    private BaseFragment mFragment;
    public TextView mEmptyListView;

    private boolean isSwipeContentOpening;
    private boolean neverShowAgain = false;
    private int cancellationsLeft;
    private static final DisplayMetrics metrics = new DisplayMetrics();

    @BindView(R.id.request_item_swipe_layout) SwipeLayout swipeLayout;
    @BindView(R.id.accept_request_feedback) LinearLayout acceptServiceRequest;
    @BindView(R.id.reject_request_feedback) LinearLayout rejectServiceRequest;

    public ServiceRequestListAdapter(List<CareRecord> items, ServiceRequestListFragment.OnListFragmentInteractionListener listener, Context context, BaseFragment fragment) {
        mCareRecords = items;
        mListener = listener;
        mContext = context;
        mFragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_service_request, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        ButterKnife.bind(this, view);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);

        //add left drag edge to accept service request feedback view
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, acceptServiceRequest);

        //add right drag edge to reject service request feedback view
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, rejectServiceRequest);

        return new ViewHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mEmptyListView = (TextView) ((LinearLayout) recyclerView.getParent())
                .findViewById(R.id.alt_text_service_request_list);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mCareRecords.get(position);
        holder.mRecipientNameView.setText(holder.mItem.getRecipientName());

        Date appointmentDate = holder.mItem.getAppointmentTime();
        holder.mDateView.setText(DateUtils.formatTime(appointmentDate));
        holder.mTimeView.setText(DateUtils.formatDate(appointmentDate));

        holder.mServiceRequestItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecordActivity.class);
                intent.putExtra("carerecipient_id", holder.mItem.getCareRecipientId())
                        .putExtra("carerecipient_name", holder.mItem.getRecipientName())
                        .putExtra("record_id", holder.mItem.getRecordId())
                        .putExtra("hasActedOnRequest", true)
                        .putExtra("showTrackingButton", true);
                Toast.makeText(mContext,holder.mItem.getRecipientName(),Toast.LENGTH_LONG).show();
                mContext.startActivity(intent);
            }
        });
        ((SwipeLayout) holder.mView).addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout swipeLayout) {
                isSwipeContentOpening = true;
            }

            @Override
            public void onOpen(SwipeLayout swipeLayout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout swipeLayout) {
                isSwipeContentOpening = false;
            }

            @Override
            public void onClose(SwipeLayout swipeLayout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(final SwipeLayout swipeLayout, int leftOffset, int topOffset) {
                //you are swiping.
                if (leftOffset == -metrics.widthPixels && isSwipeContentOpening) {
                    // If Reject care service request text view is completely visible
                    String dialogMessage = mContext.getResources().getString(R.string.confirm_reject_service_request);
                    MaterialDialog confirmCancelDialog = new MaterialDialog.Builder(mContext)
                            .title(R.string.confirm_reject_dialog_title)
                            .content(dialogMessage)
                            .positiveText(R.string.confirm_cancel_dialog_confirm)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    final int requestId = holder.mItem.getRecordId();
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("action", CaregiverActionMap.REJECT_REQUEST.getAction())
                                                .put("user_id", PrefUtils.getUserId(mContext))
                                                .put("record_id", requestId);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    HttpUtils.jsonPost(mContext, jsonObject, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String responseString = response.body().string();
                                            final int cancellationsLeft = Integer.valueOf(responseString);
                                            mCareRecords.remove(position);
                                            mFragment.getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (mCareRecords.isEmpty()) {
                                                        mEmptyListView.setText(mFragment.getString(R.string.empty_service_request_list));
                                                        mEmptyListView.setVisibility(View.VISIBLE);
                                                    }
                                                    Toast.makeText(mContext, cancellationsLeft +
                                                            " cancellations left", Toast.LENGTH_SHORT).show();
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    });
                                    swipeLayout.close(true);
                                    dialog.dismiss();
                                }
                            })
                            .negativeText(R.string.confirm_cancel_dialog_cancel)
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    swipeLayout.close(true);
                                    dialog.dismiss();
                                }
                            })
                            .build();

                    confirmCancelDialog.show();
                } else if (leftOffset == metrics.widthPixels && isSwipeContentOpening) {
                    // If Accept care service request text view is completely visible
                    Toast.makeText(mContext, "This feature is not supported yet.", Toast.LENGTH_SHORT);
                    new MaterialDialog.Builder(mContext)
                            .content(R.string.confirm_accept_service_request)
                            .positiveText(R.string.action_accept)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("action", CaregiverActionMap.ACCEPT_REQUEST.getAction())
                                                .put("user_id", PrefUtils.getUserId(mContext))
                                                .put("record_id", mCareRecords.get(position).getRecordId());
                                        HttpUtils.jsonPost(mContext, jsonObject, new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                e.printStackTrace();
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                final String responseString = response.body().string();
                                                if (responseString.toLowerCase().equals("request accepted")) {
                                                    mCareRecords.remove(position);
                                                    mFragment.getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (mCareRecords.isEmpty()) {
                                                                mEmptyListView.setText(mFragment.getString(R.string.empty_service_request_list));
                                                                mEmptyListView.setVisibility(View.VISIBLE);
                                                            }
                                                            Toast.makeText(mContext, responseString, Toast.LENGTH_SHORT).show();
                                                            notifyDataSetChanged();
                                                        }
                                                    });
                                                } else {
                                                    Log.wtf(TAG, responseString);
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    swipeLayout.close(true);
                                    dialog.dismiss();
                                }
                            })
                            .negativeText(R.string.confirm_cancel_dialog_cancel)
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    swipeLayout.close();
                                    dialog.dismiss();
                                }
                            })
                            .build()
                            .show();
                }
            }

            @Override
            public void onHandRelease(SwipeLayout swipeLayout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(mCareRecords);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCareRecords != null) {
            return mCareRecords.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return mCareRecords.get(position).getRecordId();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mRecipientNameView;
        public final TextView mTimeView;
        public final TextView mDateView;
        public final LinearLayout mServiceRequestItem;
        public CareRecord mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mServiceRequestItem = (LinearLayout) view.findViewById(R.id.service_request_item);
            mRecipientNameView = (TextView) view.findViewById(R.id.text_recipient_name);
            mTimeView = (TextView) view.findViewById(R.id.text_time);
            mDateView = (TextView) view.findViewById(R.id.text_date);
        }
    }
}
