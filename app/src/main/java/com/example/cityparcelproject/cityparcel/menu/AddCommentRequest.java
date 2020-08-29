package com.example.cityparcelproject.cityparcel.menu;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddCommentRequest extends StringRequest {
        final static private String URL = "http://thecityparcel.com/AddComment.php";
        private Map<String, String> parameters;

        public AddCommentRequest(String parcelIndex, String member, String comment, Response.Listener<String> listener) {
            super(Method.POST, URL, listener, null);
            parameters = new HashMap<>();
            parameters.put("parcelIndex", parcelIndex);
            parameters.put("member", member);
            parameters.put("comment", comment);
        }

        @Override
        public Map<String, String> getParams() {
            return parameters;
        }
}


