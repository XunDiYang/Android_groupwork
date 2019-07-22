package com.yff.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yff.myapplication.bean.PostVideoResponse;
import com.yff.myapplication.newtork.IMiniDouyinService;
import com.yff.myapplication.newtork.RetrofitManager;

import java.io.File;
import java.security.Policy;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {

    private Uri mSelectedVideo;
    private Uri mSelectedImage;

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final String TAG = "UploadActivity";
    private static final int IMAGE_MODE=1;
    private static final int VIDEO_MODE=2;

    private FloatingActionButton selectVideo ;
    private FloatingActionButton selectImg;
    private FloatingActionButton upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        selectVideo = findViewById(R.id.selectVideo);
        selectImg  = findViewById(R.id.selectImg);
        upload = findViewById(R.id.upload);

        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();
            }
        });

        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedVideo != null && mSelectedImage != null) {
                    postVideo();
                } else {
//                    throw new IllegalArgumentException("error data uri, mSelectedVideo = " + mSelectedVideo + ", mSelectedImage = " + mSelectedImage);
                }
            }
        });

    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        Log.d(TAG,"image的uri："+String.valueOf(mSelectedImage));
    }


    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
        Log.d(TAG,"video的uri："+String.valueOf(mSelectedVideo));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                selectImg.setImageDrawable(getResources().getDrawable(R.mipmap.yes));
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                selectVideo.setImageDrawable(getResources().getDrawable(R.mipmap.yes));;
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(UploadActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {

        selectVideo.setEnabled(false);
        selectImg.setEnabled(false);

        MultipartBody.Part coverImg = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part video = getMultipartFromUri("video", mSelectedVideo);
        // TODO-C2 (6) Send Request to post a video with its cover image
        // if success, make a text Toast and show
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        Call<PostVideoResponse> call = retrofit.create(IMiniDouyinService.class).createVideo(
                "19992dw", "yoyo",
                coverImg,
                video
        );

        call.enqueue(new Callback<PostVideoResponse>() {

            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {

                PostVideoResponse postVideoResponse = response.body();
                Log.d(TAG,"post格式："+ postVideoResponse.toString());
                if (postVideoResponse.getIsSuccess()) {
                    Log.d(TAG, "post is OK ");
                    Log.d(TAG, "response is" + postVideoResponse);
                    Toast.makeText(UploadActivity.this,"POST SUCCESS!",Toast.LENGTH_SHORT).show();
                    selectImg.setImageDrawable(getResources().getDrawable(R.mipmap.video));
                    selectImg.setEnabled(true);
                    selectVideo.setImageDrawable(getResources().getDrawable(R.mipmap.locate));
                    selectVideo.setEnabled(true);
                }

            }

            @Override
            public void onFailure(Call<PostVideoResponse> arg0, Throwable arg1) {
                //Log.d(TAG, "onFailure() called with: arg0 = [" + arg0 + "], arg1 = [" + arg1 + "]");
                Toast.makeText(UploadActivity.this,"POST WRONG!",Toast.LENGTH_SHORT).show();
                selectImg.setImageDrawable(getResources().getDrawable(R.mipmap.video));
                selectImg.setEnabled(true);
                selectVideo.setImageDrawable(getResources().getDrawable(R.mipmap.locate));
                selectVideo.setEnabled(true);
            }
        });
    }

}
