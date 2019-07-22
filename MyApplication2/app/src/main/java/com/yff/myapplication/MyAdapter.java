//RecycleView里面需要用Adapter适配器 Adapter适配器里声明是通过ViewHolder来在滚动时快速设置值以提升性能的
package com.yff.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.List;

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        MainActivity activity;
        ImageView imageView;
        OrientationUtils orientationUtils;
        List<Feed> feeds;//Feed的列表
        public MyAdapter(List<Feed>feeds,MainActivity activity) {//构建
            this.feeds=feeds;
            this.activity=activity;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            int layoutIdForListItem = R.layout.item;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;
            View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);//应该是格式适应
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {//onBindViewHolder()方法，负责将每个子项holder绑定数据。俩参数分别是RecyclerView.ViewHolder holder, int position；position应该系统会给
            holder.init(position);
        }

        @Override
        public int getItemCount() {
            return feeds.size();
        }//有多少个就多少次

        public class MyViewHolder extends RecyclerView.ViewHolder {//ViewHolder通常出现在适配器里，为的是listview滚动的时候快速设置值，而不必每次都重新创建很多对象，从而提升性能。 自定义的ViewHolder 继承原本ViewHolder
            private StandardGSYVideoPlayer videoPlayer;
            private FloatingActionButton button;
            private LottieAnimationView animationView;
            public int time = 0;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                videoPlayer=itemView.findViewById(R.id.detail_player);
                button=itemView.findViewById(R.id.button2);
                animationView=itemView.findViewById(R.id.animation_view);

            }
            private void init(final int position) {//可能有问题
                animationView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {//可能有问题

                        animationView.playAnimation();
                    }
                });
                int position2 = position;

                animationView.setProgress(0 );
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DetailInformation detailInformation=activity;
                        detailInformation.openDetailInformation(feeds.get(position));
                    }
                });
                videoPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        time = time + 1;
                        if (time == 2)
                        {
                            animationView.playAnimation();
                            time = 0;
                        }
                    }
                });

                String source1 = feeds.get(position).getVideoUrl();
                imageView=new ImageView(activity);
                Glide.with(activity).load(feeds.get(position).getImageUrl()).into(imageView);
                videoPlayer.setUp(source1, true, "test");
                videoPlayer.setThumbImageView(imageView);
                videoPlayer.getTitleTextView().setVisibility(View.INVISIBLE);
                videoPlayer.getBackButton().setVisibility(View.INVISIBLE);
                orientationUtils = new OrientationUtils(activity, videoPlayer);
                videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orientationUtils.resolveByClick();
                    }
                });
                videoPlayer.setIsTouchWiget(true);
                videoPlayer.startPlayLogic();
            }


        }

        interface DetailInformation{
            public void openDetailInformation(Feed feed);//在Main里要写
        }
}
