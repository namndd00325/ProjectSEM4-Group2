package com.service;

import com.config.Pojo;
import com.google.gson.Gson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Base64;

public class GetAntiCapcha {

    public static String getCaptchaBase64(byte[] byteCaptcha) {

        Gson gson = new Gson();
        SubmitTask.Request r = new SubmitTask.Request();
        r.task.body = Base64.getEncoder().encodeToString(byteCaptcha);
        String jsonCaptchar = null;
        try {
            jsonCaptchar = Jsoup.connect(Pojo.URLcreateCaptcha)
                    .method(Connection.Method.POST)
                    .requestBody(gson.toJson(r))
                    .ignoreContentType(true)
                    .execute()
                    .body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SubmitTask.Response submittask = gson.fromJson(jsonCaptchar, SubmitTask.Response.class);
        GetTaskResult.Request getTask = new GetTaskResult.Request();
        getTask.taskId = submittask.taskId;
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        GetTaskResult.Response getTaskResponse;
        while (true) {
            String getTaskBody = null;
            try {
                getTaskBody = Jsoup.connect(Pojo.URLgetcaptcha)
                        .method(Connection.Method.POST)
                        .requestBody(gson.toJson(getTask))
                        .ignoreContentType(true)
                        .execute()
                        .body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            getTaskResponse = gson.fromJson(getTaskBody, GetTaskResult.Response.class);

//                System.out.println(Utility.gson.toJson(getTaskResponse));
            if ("processing".equals(getTaskResponse.status)) {
                System.out.println("Get resp captcha");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
//                System.out.println(getTaskResponse.status);
            break;
        }
        return getTaskResponse.solution.text;
    }



    public static String getStrCaptcha(String byteCaptcha) {

        Gson gson = new Gson();
        SubmitTask.Request r = new SubmitTask.Request();
        r.task.body = byteCaptcha;
        String jsonCaptchar = null;
        try {
            jsonCaptchar = Jsoup.connect(Pojo.URLcreateCaptcha)
                    .method(Connection.Method.POST)
                    .requestBody(gson.toJson(r))
                    .ignoreContentType(true)
                    .execute()
                    .body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SubmitTask.Response submittask = gson.fromJson(jsonCaptchar, SubmitTask.Response.class);
        GetTaskResult.Request getTask = new GetTaskResult.Request();
        getTask.taskId = submittask.taskId;
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        GetTaskResult.Response getTaskResponse;
        while (true) {
            String getTaskBody = null;
            try {
                getTaskBody = Jsoup.connect(Pojo.URLgetcaptcha)
                        .method(Connection.Method.POST)
                        .requestBody(gson.toJson(getTask))
                        .ignoreContentType(true)
                        .execute()
                        .body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            getTaskResponse = gson.fromJson(getTaskBody, GetTaskResult.Response.class);

//                System.out.println(Utility.gson.toJson(getTaskResponse));
            if ("processing".equals(getTaskResponse.status)) {
                System.out.println("Get resp captcha");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
//                System.out.println(getTaskResponse.status);
            break;
        }
        return getTaskResponse.solution.text;
    }

}
