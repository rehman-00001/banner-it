package com.codemonk_labs.bannerit.network;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class NetworkHandler {
    private static NetworkHandler instance;
    private RequestQueue requestQueue;
    private static Context context;
    private static final Gson gson = new Gson();

    private NetworkHandler(Context context) {
        NetworkHandler.context = context.getApplicationContext();
        this.requestQueue = getRequestQueue();
    }

    public static synchronized NetworkHandler instance(Context context) {
        if (instance == null) {
            instance = new NetworkHandler(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir());
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return NetworkHandler.gson.toJson(src, typeOfSrc);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
