package com.yff.myapplication;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.yff.myapplication.message.Message;
import com.yff.myapplication.message.PullParser;
import com.yff.myapplication.message.Message;
import com.yff.myapplication.net.FeedResponse;
import com.yff.myapplication.net.IMiniDouyinService;
import com.yff.myapplication.net.RetrofitManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements MyAdapter.DetailInformation,MessageAdapter.MyItemClickListener{
    private RecyclerView mNumbersListView;
    private MyAdapter myAdapter;
    private LinearLayoutManager layoutManager;//承担了一个View(当然指的是RecyclerView)的布局、测量、子View 创建 复用 回收 缓存 滚动...
    private List<Feed>feeds;
    private StandardGSYVideoPlayer standardGSYVideoPlayer;
    private RecyclerView myNumbersListView;
    private MessageAdapter messageAdapter;//消息界面用
    private List<Message> messages;

    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_EXTERNAL_CAMERA = 101;
    private static final int GRANT_PERMISSION = 3;
    private String[] mPermissionsArrays = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final String TAG = "YYY";


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener//底边栏监听
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {//底边栏 不同的选择是不同的界面
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setMainScreeen();//自定义函数 获取视频流
                    return true;
                case R.id.navigation_dashboard:
                    setContentView(R.layout.activity_people);
                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
                    navigation.getMenu().getItem(1).setChecked(true);
                    Resources resource=(Resources)getBaseContext().getResources();
                    @SuppressLint("ResourceType") ColorStateList csl=(ColorStateList)resource.getColorStateList(R.drawable.navigation_menu_item_color);
                    navigation.setItemTextColor(csl);
                    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

                    return true;

                case R.id.navigation_notifications:
                    setMessageScreen();//信息列表
                    return true;
            }
            return false;
        }
    };

    public void fetchFeed() {
        RetrofitManager.get(IMiniDouyinService.HOST).create(IMiniDouyinService.class).fetchFeed().enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                Log.d(TAG, "onResponse() called with: call = [" + call + "], response = [" + response.body() + "]");
                if (response.isSuccessful()) {
                    feeds.clear();
                    feeds.addAll(response.body().getFeeds());
                    mNumbersListView.getAdapter().notifyDataSetChanged();
                } else {

                    Toast.makeText(MainActivity.this, "fetch feed failure!", Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onFailure(Call<FeedResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }





    public void setfeeds(){
//        feeds=new LinkedList<>();
        fetchFeed();
    }
    public void setMainScreeen()
    {
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);//底边栏选择监听方式
        Resources resource=(Resources)getBaseContext().getResources();
        @SuppressLint("ResourceType") ColorStateList csl=(ColorStateList)resource.getColorStateList(R.drawable.navigation_menu_item_color);
        navigation.setItemTextColor(csl);
        //获取信息列表
        setfeeds();

        //设置recyclerview
        mNumbersListView = findViewById(R.id.my_list);//recyclelist的名字
        myAdapter=new MyAdapter(feeds,this);//新建adapter
        layoutManager = new LinearLayoutManager(this);//承担了一个View(当然指的是RecyclerView)的布局、测量、子View 创建 复用 回收 缓存 滚动等等操作。
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//竖屏
        mNumbersListView.setLayoutManager(layoutManager);
        mNumbersListView.setHasFixedSize(true);
        mNumbersListView.setAdapter(myAdapter);
        //自动播放
        mNumbersListView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem, lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem   = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if(dy>=0) {//下滑
                    StandardGSYVideoPlayer mStandardGSYVideoPlayer = (StandardGSYVideoPlayer) recyclerView.getChildAt(lastVisibleItem - firstVisibleItem).findViewById(R.id.detail_player);//在item里定义的播放器 这是两个position相减 比如说 1-0 就getChild1 第二个 有第2个了 就第二个 第一个停（或者只是让第二个可以播
                    int[] screenPosition = new int[2];
                    mStandardGSYVideoPlayer.getLocationOnScreen(screenPosition);//获取屏幕位置
                    //大概第二个视频居中时而且没在播时底下视频开始播放
                    if (screenPosition[1] <= 500 && !mStandardGSYVideoPlayer.isInPlayingState()) {
                        mStandardGSYVideoPlayer.startPlayLogic();
                        standardGSYVideoPlayer=mStandardGSYVideoPlayer;
                    }
                }else{//上划
                    if(lastVisibleItem==firstVisibleItem){
                        //上划刷新
                        setMainScreeen();
                    }else{
                        StandardGSYVideoPlayer mStandardGSYVideoPlayer = (StandardGSYVideoPlayer) recyclerView.getChildAt(lastVisibleItem - firstVisibleItem-1).findViewById(R.id.detail_player);
                        int[] screenPosition = new int[2];
                        mStandardGSYVideoPlayer.getLocationOnScreen(screenPosition);
                        if (screenPosition[1] >= -500 && !mStandardGSYVideoPlayer.isInPlayingState())
                            mStandardGSYVideoPlayer.startPlayLogic();
                        standardGSYVideoPlayer=mStandardGSYVideoPlayer;
                    }
                }
            }
        });
        LottieAnimationView fab = findViewById(R.id.fab);//悬浮按钮录像;
        LottieAnimationView fab2 = findViewById(R.id.fab2);//悬浮按钮拍照;

        fab.playAnimation();
        ObjectAnimator alphaAnimationView = ObjectAnimator.ofFloat( fab,
                "alpha",1.0f,0.0f
        );

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这里申请权限

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },0);
                //开启拍照模式
               // startActivity(new Intent(MainActivity.this,TakeCamera.class));
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, mPermissionsArrays, GRANT_PERMISSION);
                } else {
                    startActivity(new Intent(MainActivity.this, VideoActivity.class));
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这里申请权限
                mNumbersListView.scrollToPosition(0);
                feeds=new LinkedList<>();
                fetchFeed();
                fab2.playAnimation();
                ObjectAnimator alphaAnimationView = ObjectAnimator.ofFloat( fab,
                        "alpha",1.0f,0.0f
                );
                //开启拍照模式
                // startActivity(new Intent(MainActivity.this,TakeCamera.class));
            }
        });
    }
    public void setMessageScreen()
    {

        setContentView(R.layout.activity_messages);
        myNumbersListView = findViewById(R.id.rv_list);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);//底部菜单栏
        navigation.getMenu().getItem(2).setChecked(true);
        Resources resource=(Resources)getBaseContext().getResources();
        @SuppressLint("ResourceType") ColorStateList csl=(ColorStateList)resource.getColorStateList(R.drawable.navigation_menu_item_color);
        navigation.setItemTextColor(csl);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        try {
            InputStream assetInput = getAssets().open("data.xml");
            messages = PullParser.pull2xml(assetInput);//将xml文件转化
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        messageAdapter =new MessageAdapter(messages,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myNumbersListView.setLayoutManager(layoutManager);
        myNumbersListView.setHasFixedSize(true);
        myNumbersListView.setAdapter(messageAdapter);

    }
    //@Override
    public void onListItemClick(int position)//重写MA中的方法
    {
        Intent intent=new Intent(this,Chatroom.class);
        intent.putExtra("message",messages.get(position).getTitle());
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMainScreeen();

    }


    @Override
    public void onStop()
    {super.onStop();
        if(standardGSYVideoPlayer!=null)
            standardGSYVideoPlayer.release();
    }
    @Override
    public void openDetailInformation(Feed feed) {//MyAdapter中的接口所调用的
        Intent intent=new Intent(this,VideoDetail.class);//DetailVideoAcivity是一个新类
        intent.putExtra("video_url",feed.getVideoUrl());
        intent.putExtra("image_url",feed.getImageUrl());
        intent.putExtra("username",feed.getUserName());
        intent.putExtra("student_id",feed.getStudentId());
        startActivity(intent);
    }




}


