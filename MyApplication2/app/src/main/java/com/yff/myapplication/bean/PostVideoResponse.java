package com.yff.myapplication.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Xavier.S
 * @date 2019.01.18 17:53
 */
public class PostVideoResponse {

    // TODO-C2 (3) Implement your PostVideoResponse Bean here according to the response json
    @SerializedName("success")
    boolean isSuccess;
    @SerializedName("item")
    Feed item;
    @SerializedName("url")
    String url;

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Feed getItem() {
        return item;
    }

    public void setItem(Feed item) {
        this.item = item;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PostVideoResponse{" +
                "success=" + isSuccess + '\'' +
                ", item=" + item +
                ", url='" + url + '\'' +
                '}';
    }
}
