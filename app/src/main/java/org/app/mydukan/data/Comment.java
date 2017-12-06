package org.app.mydukan.data;

/**
 * Created by Harshit Agarwal on 04-10-2017.
 */

public class Comment {
    UserInfo userInfo;
    String text;
    String time;
    String id;

    public Comment() {
    }

    public Comment(UserInfo userInfo, String text, String time, String id) {
        this.userInfo = userInfo;
        this.text = text;
        this.time = time;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static class UserInfo {
        String uId;
        String name;
        String photoUrl;

        public UserInfo(String uId, String name, String photoUrl) {
            this.uId = uId;
            this.name = name;
            this.photoUrl = photoUrl;
        }

        public UserInfo() {
        }

        public String getuId() {
            return uId;
        }

        public void setuId(String uId) {
            this.uId = uId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }


    }
}

