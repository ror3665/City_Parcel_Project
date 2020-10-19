package com.example.cityparcelproject.cityparcel.profile;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public class CommentModel {

    public String username;
    public String message;
    public Object timestamp;
    public String uid;

    public CommentModel() {}

    public CommentModel(String username, String message, Object timestamp, String uid) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("message", message);
        result.put("timestamp", timestamp);
        result.put("uid", uid);
        return result;
    }
}
