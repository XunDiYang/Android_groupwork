package com.yff.myapplication;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class VideoDetail extends GSYBaseActivityDetail<StandardGSYVideoPlayer> {
    StandardGSYVideoPlayer detailPlayer;

    private String video_url;
    private String image_url;
    private String username;
    private String student_id;
    TextView textView1;
    TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_detail);
        Bundle bundle=this.getIntent().getExtras();//获取MainAcivity的Extras
        video_url =bundle.getString("video_url");
        image_url=bundle.getString("image_url");
        username=bundle.getString("username");
        student_id=bundle.getString("student_id");
        //name.setText(bundle.getString("message"));
        textView1=findViewById(R.id.user_id);
        textView1.setText("作者ID："+student_id);
        textView2=findViewById(R.id.username);
        textView2.setText("作者姓名："+username);
        detailPlayer = (StandardGSYVideoPlayer) findViewById(R.id.detail_player);
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);

        initVideoBuilderMode();

    }

    @Override
    public StandardGSYVideoPlayer getGSYVideoPlayer() {
        return detailPlayer;
    }

    @Override
    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        ImageView imageView = new ImageView(this);
        loadCover(imageView,image_url);
        return new GSYVideoOptionBuilder()
                .setThumbImageView(imageView)
                .setUrl(video_url)
                .setCacheWithPlay(true)
                .setVideoTitle(" ")
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setSeekRatio(1);
    }

    @Override
    public void clickForFullScreen() {

    }
    //启动横屏
    @Override
    public boolean getDetailOrientationRotateAuto() {
        return true;
    }

    private void loadCover(ImageView imageView, String url) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(image_url).into(imageView);
    }

}
