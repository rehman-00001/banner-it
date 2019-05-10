package com.codemonk_labs.bannerit.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {
    private Class<T> clazz;
    private Response.Listener<T> listener;
    private Map<String, String> headers, params;
    private JSONObject body;

    /**
     * Make a GET|POST request and return a parsed object from JSON.
     *
     */
    public GsonRequest(int method, String url, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    NetworkHandler.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException | JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return body == null ? super.getBody() : body.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return super.getBody();
        }
    }

    public static class Builder<T> {
        int method;
        String url;
        Class<T> clazz;
        Response.Listener<T> listener;
        Response.ErrorListener errorListener;
        Map<String, String> headers;
        Map<String, String> params;
        JSONObject body;

        public Builder(int method, String url, Response.ErrorListener errorListener) {
            this.method = method;
            this.url = url;
            this.errorListener = errorListener;
        }

        public Builder<T> withResponseType(Class<T> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder<T> withResponseListener(Response.Listener<T> listener) {
            this.listener = listener;
            return this;
        }

        public Builder<T> withHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder<T> withParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder<T> withBody(JSONObject body) {
            this.body = body;
            return this;
        }

        public GsonRequest<T> build() {
            GsonRequest<T> request = new GsonRequest<T>(method, url, errorListener);
            request.clazz = this.clazz;
            request.headers = this.headers;
            request.params = this.params;
            request.listener = this.listener;
            request.body = this.body;
            return request;
        }
    }
}