package com.yff.myapplication;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.yff.myapplication.utils.Utils.MEDIA_TYPE_IMAGE;
import static com.yff.myapplication.utils.Utils.getOutputMediaFile;

public class VideoActivity extends AppCompatActivity {

    private static String TAG = "自定义相机";
    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;
    private static final int GRANT_PERMISSION = 3;
    private static final int PICK_VIDEO = 2;

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private Camera.Parameters params;
    private Chronometer chronometer;

    private ImageView light;
    private ImageView upload;

    private boolean isRecording = false;
    private boolean islighting = false;
    private boolean isSelected = false;

    private int cameraPosition = 1;  //0:后置    1：前置
    private int rotationDegree = 0;

    private String localPath = null;
    public Uri mSelectedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        //todo 摄像头数据实时显示
        mCamera = getCamera(0);
        mSurfaceView = findViewById(R.id.img);
        final SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        params=mCamera.getParameters();

        //todo 给SurfaceHolder添加Callback
        surfaceHolder.addCallback(new VideoActivity.SurfaceCallback());


        //todo 切换前后摄像头
        findViewById(R.id.btn_facing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cameraCount = Camera.getNumberOfCameras();
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                for (int i = 0; i < cameraCount; i++) {
                    Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                    if (cameraPosition == 1) {
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            mCamera.stopPreview();//停掉原来摄像头的预览
                            mCamera = VideoActivity.this.getCamera(1);
                            try {
                                mCamera.setPreviewDisplay(surfaceHolder);
                                mCamera.startPreview();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mCamera.startPreview();//开始预览
                            cameraPosition = 0;
                            break;
                        }
                    } else {
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            mCamera.stopPreview();//停掉原来摄像头的预览
                            mCamera = VideoActivity.this.getCamera(0);
                            try {
                                mCamera.setPreviewDisplay(surfaceHolder);
//                            mCamera.setDisplayOrientation(getCameraDisplayOrientation(cameraPosition));
                                mCamera.startPreview();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            mCamera.startPreview();//开始预览
                            cameraPosition = 1;
                            break;
                        }
                    }
                }
            }
        });

        //todo 调焦
        final SeekBar zoom = findViewById(R.id.zoom);;

        zoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try
                {
                    final int MAX = params.getMaxZoom();
                    zoom.setMax(MAX);
                    int zoomValue = params.getZoom();
                    zoomValue = progress;
                    params.setZoom(zoomValue);
                    mCamera.setParameters(params);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //todo 闪光灯
        light = findViewById(R.id.light);
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(islighting){
                    light.setImageResource(R.mipmap.light);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(params);
                    islighting = false;
                }
                else{
                    light.setImageResource(R.mipmap.no_light);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(params);
                    islighting = true;
                }
            }
        });

        //todo 点击屏幕 自动对焦
        findViewById(R.id.img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null){
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if(success){
//                                Toast.makeText(VideoActivity.this,"对焦成功",Toast.LENGTH_SHORT).show();
                            }else{

                            }
                        }
                    });
                }
            }
        });

        chronometer = findViewById(R.id.chronometer);
        chronometer.setVisibility(View.INVISIBLE);

        //todo 录制
        FloatingActionButton recordBtn = findViewById(R.id.btn_record);
        recordBtn.setOnClickListener(v -> {
            //todo 录制，第一次点击是start，第二次点击是stop
            if (isRecording && isSelected == false) {
                //todo 停止录制
                releaseMediaRecorder();
                recordBtn.setImageDrawable(getResources().getDrawable(R.mipmap.record2));
                isRecording = false;
                chronometer.stop();
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("是否已选好视频？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        mSelectedVideo = Uri.fromFile(new File(localPath));
                                        Intent intent2 = new Intent();
                                        intent2.setData(mSelectedVideo);
                                        intent2.setClass(VideoActivity.this, CameraActivity.class);
                                        startActivity(intent2);
                                        isSelected = true;
                                        finish();
                                    }
                                })
                        .setNegativeButton("关闭",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        finish();
                                    }
                                })
                        .setCancelable(false)
                        .show();

            } else {
                //todo 录制
                if(mMediaRecorder == null){
                    if(!prepareVideoRecorder()){

                    }
                }
                mMediaRecorder.start();
                isRecording = true;
                recordBtn.setImageDrawable(getResources().getDrawable(R.mipmap.recording));
                chronometer.setVisibility(View.VISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            }
        });

        //todo 选择文件中的video
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestReadExternalStoragePermission("select a video")) {
                    startActivity(new Intent(VideoActivity.this, UploadActivity.class));
                    finish();
                }
            }
        });

    }

    //todo 摄像头设置，属性
    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        Camera cam = Camera.open(position);

        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等
        cam.setDisplayOrientation(getCameraDisplayOrientation(cameraPosition));
        return cam;
    }

    //todo Sufurse回调
    private class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.setDisplayOrientation(getCameraDisplayOrientation(cameraPosition));
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    //todo 获取旋转角度
    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }

    //todo 释放camera资源
    private void releaseCameraAndPreview() {
        mCamera.release();
//        mCamera.stopPreview();
        mCamera = null;
    }

    //todo 开始预览
    private void startPreview(SurfaceHolder holder) {

    }


    //todo 准备MediaRecorder
    private MediaRecorder mMediaRecorder;

    private boolean prepareVideoRecorder() {
        localPath =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + String.valueOf(System.currentTimeMillis()) + ".mp4";
        if(mMediaRecorder != null)
            releaseMediaRecorder();
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile((CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)));
//        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);
        mMediaRecorder.setOutputFile(localPath);


        try{
            mMediaRecorder.prepare();
        }catch (Exception e){
            releaseMediaRecorder();
            return false;
        }

        return true;
    }

    //todo 释放MediaRecorder
    private void releaseMediaRecorder() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
    }

    //todo 回调Camera.PictureCallback
    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }
        Toast toast = Toast.makeText(this, "拍照成功", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 150);
        mCamera.startPreview();
    };


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    //todo 获取访问文件的权限
    private boolean requestReadExternalStoragePermission(String explanation) {
        if (ActivityCompat.checkSelfPermission(VideoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(VideoActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "You should grant external storage permission to continue " + explanation, Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(VideoActivity.this, new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, GRANT_PERMISSION);
            }
            return false;
        } else {
            return true;
        }
    }



}
