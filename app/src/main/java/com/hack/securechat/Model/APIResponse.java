package com.hack.securechat.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class APIResponse extends ResponseBody {

    String url,error,message;

    public APIResponse(String url, String error, String message) {
        this.url = url;
        this.error = error;
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public long contentLength() {
        return 0;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return null;
    }

    @NonNull
    @Override
    public BufferedSource source() {
        return null;
    }
}
