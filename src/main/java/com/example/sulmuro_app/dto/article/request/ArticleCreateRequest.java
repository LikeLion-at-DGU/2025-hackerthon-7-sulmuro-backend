package com.example.sulmuro_app.dto.article.request;

import com.example.sulmuro_app.domain.bin.Location;
import com.example.sulmuro_app.domain.bin.Theme;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArticleCreateRequest {

    @NotBlank
    private String title;

    private String subTitle;

    @NotNull
    private Theme theme;

    @NotNull
    private Location location;

    // getter/setter
    public String getTitle() { return title; }
    public String getSubTitle() { return subTitle; }
    public Theme getTheme() { return theme; }
    public Location getLocation() { return location; }

    public void setTitle(String title) { this.title = title; }
    public void setSubTitle(String subTitle) { this.subTitle = subTitle; }
    public void setTheme(Theme theme) { this.theme = theme; }
    public void setLocation(Location location) { this.location = location; }
}
