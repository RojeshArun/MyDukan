package org.app.mydukan.data;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by Alessandro on 04/03/2017.
 */

public class Feed implements Serializable {

    String photoAvatar;
    String name;
    String time;
    String text;
    String photoFeed;
    String idUser;
    String idFeed;
    String link;

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setLikeCount(int likeCount) {

        this.likeCount = likeCount;
    }

    @Exclude
    int likeCount;
    @Exclude
    boolean liked;

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isLiked() {
        return liked;
    }

    public Feed() {
    }

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdFeed() {
        return idFeed;
    }

    public void setIdFeed(String idFeed) {
        this.idFeed = idFeed;
    }

    public String getPhotoAvatar() {
        return photoAvatar;
    }

    public void setPhotoAvatar(String photoAvatar) {
        this.photoAvatar = photoAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhotoFeed() {
        return photoFeed;
    }

    public void setPhotoFeed(String photoFeed) {
        this.photoFeed = photoFeed;
    }
}

