package com.codemonk_labs.bannerit.network;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

import com.crashlytics.android.Crashlytics;

public class PostRequest extends StringRequest {

    private final String mRequestBody;

    public PostRequest(String url,
                Response.Listener<String> listener, Response.ErrorListener errorListener, String requestBody) {
        super(Method.POST, url, listener, errorListener);
        this.mRequestBody = requestBody;
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            Crashlytics.log(Log.ERROR, Crashlytics.TAG,
                    "Unsupported Encoding while trying to get the bytes of "+mRequestBody+" using utf-8" );
            return null;
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String responseString = "";
        if (response != null) {
            responseString = String.valueOf(response.statusCode);
        }
        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
    }
}
