package com.yff.myapplication.bean;

import android.net.Uri;

public class TransportData {
    private Uri video;
    private Uri image;

    public void setVideo(Uri video) {
        this.video = video;
    }

    public Uri getVideo() {
        return video;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public Uri getImage() {
        return image;
    }
}
