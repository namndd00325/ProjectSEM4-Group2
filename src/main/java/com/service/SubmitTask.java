package com.service;

import com.config.Pojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SubmitTask {
    public static class Request {

        public String clientKey =  Pojo.ANTI_CAPTCHA_KEY;
        public Task task = new Task();
    }

    public static class Response {

        public String errorId;
        public long taskId;

    }

    public static class Task {

        public String type = "ImageToTextTask";

        public String body;

        public boolean phrase;
        @SerializedName("case")
        @Expose
        public boolean _case = false;
        public int numeric = 0;
        public int math = 0;
        public int minLength = 5;

        public int maxLength = 7;

    }

}
