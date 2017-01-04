package com.caregiving.services.android.caregiver.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {

    public static final String IP_ADDRESS = "222.29.240.170";
    public static final String REQUEST_URL = "http://" + IP_ADDRESS + ":80/api";
    public static final String LOGIN_REQUEST_URL = "http://" + IP_ADDRESS + ":80/api_login";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static Call jsonPost(Context context, JSONObject json, Callback callback) {
        Log.d("HTTP jsonPost Request", json.toString());
        return performPost(context, REQUEST_URL, json, callback);
    }

    public static Call jsonLoginPost(Context context, JSONObject json, Callback callback) {
        Log.d("HTTP Login Request", json.toString());
        return performPost(context, LOGIN_REQUEST_URL, json, callback);
    }

    public static Call formPost(Context context, String url, RequestBody requestBody, Callback callback) {
        if (isNetworkAvailable(context)) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(callback);
            return call;
        } else {
            return null;
        }
    }

    private static Call performPost(Context context, String url, JSONObject json, Callback callback) {
        if (isNetworkAvailable(context)) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(callback);
            return call;
        } else {
            return null;
        }
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
