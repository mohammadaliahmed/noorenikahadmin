package com.appsinventiv.noorenikahadmin.Models;

public class PromotionBanner {
    String url,imgUrl,placement;

    public PromotionBanner(String url, String imgUrl, String placement) {
        this.url = url;
        this.imgUrl = imgUrl;
        this.placement = placement;
    }

    public PromotionBanner() {
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
