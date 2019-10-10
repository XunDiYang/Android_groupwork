package com.yff.myapplication.net;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface IMiniDouyinService {
    String HOST = "https://douyin.fkynjyq.com/api/";

    @Multipart
    @POST("/video/")
    Call<PostVideoResponse> createVideo(
            @Query("student_id") String studentId,
            @Query("user_name") String userName,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part video);

    @GET()
    Call<FeedResponse> getVideo(@Url String url);
}

