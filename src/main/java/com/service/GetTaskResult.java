package com.service;

import com.config.Pojo;


import java.util.logging.Logger;

public class GetTaskResult {
    static Logger log = Logger.getLogger(GetTaskResult.class.getName());
    static
    {
        log.warning("Log in com.anticaptcha");
    }
    public static class Request {
        public String clientKey = Pojo.ANTI_CAPTCHA_KEY;
        public long taskId;
    }

    public class Solution {

        public String text;

        public String url;

    }

    public static class Response {

        public int errorId;

        public String status;

        public Solution solution;

        public String cost;

        public String ip;

        public int createTime;

        public int endTime;

        public String solveCount;

    }

}
