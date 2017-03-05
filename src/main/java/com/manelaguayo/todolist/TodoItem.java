package com.manelaguayo.todolist;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.widget.ImageView;

import com.google.firebase.database.Exclude;

/**
 * Created by tarda on 25/01/17.
 */

public class TodoItem {


    public String id;
    public String title;
    public String url;
    public String uri;
    public boolean borrat;



    TodoItem(){

    }

    public TodoItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public TodoItem(String title, String url, String uri) {
        this.title = title;
        this.url = url;
        this.uri=uri;
    }

    @Exclude
    public boolean isBorrat() {
        return borrat;
    }

    @Exclude
    public void setBorrat(boolean borrat) {
        this.borrat = borrat;
    }


    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id=id;
    }

    @Exclude
    public String getUri() {
        return uri;
    }
    @Exclude
    public String getUrl() {
        return url;
    }
    @Exclude
    public String getTitle(){
        return title;
    }


}


