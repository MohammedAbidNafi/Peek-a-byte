package com.hack.securechat;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Service {

    @Multipart
    @POST("/encodeText")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("message") RequestBody message,@Part("password") RequestBody password,@Part("filename") RequestBody filename);

    @Multipart
    @POST("/encodeImage")
    Call<ResponseBody> postImageWithImage(@Part MultipartBody.Part image,@Part MultipartBody.Part imageToEncode, @Part ("password") RequestBody password,@Part("filename") RequestBody filename);

    @Multipart
    @POST("/encodeVideoWithText")
    Call<ResponseBody> postVideoWithText(@Part MultipartBody.Part video, @Part("message") RequestBody message, @Part("password") RequestBody password,@Part("filename") RequestBody filename);

    @Multipart
    @POST("/encodeVideoWithImage")
    Call<ResponseBody> postVideoWithImage(@Part MultipartBody.Part video,@Part MultipartBody.Part imageToEncode,@Part ("password") RequestBody password,@Part("filename") RequestBody filename);


    @POST("/decode")
    Call<ResponseBody> DecodeImage(@Body JsonObject object);

    @Multipart
    @POST("/decodeImage")
    Call<ResponseBody>DecodeImageImage(@Part MultipartBody.Part image,@Part("password") RequestBody password);




}
