package com.xhy.weibo.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuhaoyang on 16/5/13.
 */
public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Type type;
    private Map<String,String> headers;
    private final Response.Listener<T> listener;
    private final Context context;

    public GsonRequest(int method, String url, Type type, Map<String, String> headers,
                        Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.context = null;
        this.type = type;
        this.headers = headers;
        this.listener = listener;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String json;
            if("gzip".equals(networkResponse.headers.get("Content-Encoding"))) {
                json = VolleyQueueSingleton.GZipDecoderToString(networkResponse.data);
            }else {
                json = new String(networkResponse.data,
                        HttpHeaderParser.parseCharset(networkResponse.headers));
            }
            Log.i("GsonRequest", json);
            return Response.success((T)(gson.fromJson(json, type)),
                    HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T t) {
        if(listener != null) {
            listener.onResponse(t);
        }
    }
}

