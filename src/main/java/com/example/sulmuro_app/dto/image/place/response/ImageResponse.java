package com.example.sulmuro_app.dto.image.place.response;

public class ImageResponse {
    private String url;
    private String caption;
    private String alt;
    private int position;
    private boolean is_cover;

    public ImageResponse(String url, String caption, String alt, int position, boolean is_cover) {
        this.url = url;
        this.caption = caption;
        this.alt = alt;
        this.position = position;
        this.is_cover = is_cover;
    }
}
