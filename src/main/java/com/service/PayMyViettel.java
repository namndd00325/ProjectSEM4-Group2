package com.service;


import com.entity.RespPay;
import com.util.RESTUtil;
import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PayMyViettel {

    public static void main(String[] args) {
        RespPay pay = payViettel("0964014282", "linhlol", "8541315135153");

        System.out.println(RESTUtil.gson.toJson(pay));
    }

    static Logger logger = Logger.getLogger(PayMyViettel.class.getName());

    public static RespPay payViettel(String userName, String password, String code) {
        RespPay rpc = new RespPay();

        String token="";
        Map<String, String> header = new HashMap<>();
        Map<String, String> cookie = new HashMap<>();
        header.put("Host", "vietteltelecom.vn");
        header.put("Cache-Control", "max-age=0");
        header.put("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8");

        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");

        header.put("Upgrade-Insecure-Requests", "1");
        header.put("Referer", "https://vietteltelecom.vn/dang-nhap");
        header.put("Connection", "keep-alive");

        for (int i = 0; i < 3; i++) {
            try {
                Connection.Response respIndex = Jsoup.connect("https://vietteltelecom.vn/dang-nhap".trim())
                        .method(Connection.Method.GET)
                        .headers(header)
                        .execute();

                cookie.putAll(respIndex.cookies());
                header.put("cookie", "vtp_session=" + cookie.get("vtp_session") + "; path=/; HttpOnly");
                token = respIndex.parse().getElementById("vt_signin__csrf_token").attr("value");
            } catch (IOException e) {
                logger.warning("error web");
                rpc.setMessage("Server error");
//            return "serverError";
            }
            logger.info("=== tken ===");
            logger.info(token);

            if (i==2 && token.length()==0){
                rpc.setMessage("Server error");
                return rpc;
            }
            if (token.length() > 0) break;

        }

        System.out.println("size: " + token.length() + " token: " + token);

        header.put("Origin", "https://vietteltelecom.vn");
        header.put("Referer", "https://vietteltelecom.vn/dang-nhap");
        header.put("Content-Type", "application/x-www-form-urlencoded");

        for (int i = 0; i < 3; i++) {
            try {
                Connection.Response respLogin = Jsoup.connect("https://vietteltelecom.vn/dang-nhap")
                        .method(Connection.Method.POST)
                        .headers(header)
                        .ignoreContentType(true)
                        .data("vt_signin[_csrf_token]", token)
                        .data("vt_signin[username]", userName)
                        .data("vt_signin[password]", password)
                        .data("vt_signin[category]", "mob")
                        .execute();


                cookie.putAll(respLogin.cookies());

            } catch (IOException e) {
//            System.out.println("error login");
                logger.warning("error login");
                rpc.setMessage("Server error");
//            return "loginError";
            }
            if (i==2 && cookie.get("vtp_session").length()==0){
                rpc.setMessage("Server error");
                return rpc;
            }
            if (cookie.get("vtp_session").length()>0)break;
        }



        header.put("cookie", "vtp_session=" + cookie.get("vtp_session"));
        header.put("Referer", "https://vietteltelecom.vn/hoadondientu");


        String sid="", csrf_token="", captcha;


        for (int i = 0; i < 3; i++) {
            try {
                Connection.Response hoadondientu = Jsoup.connect(" https://vietteltelecom.vn/my-viettel/quan-ly-cuoc-thanh-toan/nap-the")
                        .method(Connection.Method.GET)
                        .headers(header)
                        .ignoreContentType(true)
                        .execute();

                Document document = hoadondientu.parse();

                String src = document.select("#fee_payment > div:nth-child(7) > div:nth-child(2) > a > img").attr("src");
//            System.out.println(src.length());
                sid = src.substring(13, 45);
                logger.info("size: " + sid.length() + " sid:" + sid);
//            System.out.println("size: " + sid.length() + " sid:" + sid);
                csrf_token = document.getElementById("pay__csrf_token").attr("value");
//            System.out.println("size: " + csrf_token.length() + " csrf_token:" + csrf_token);
                logger.info("size: " + csrf_token.length() + " csrf_token:" + csrf_token);
            } catch (IOException e) {
                logger.warning("quan-ly-cuoc-thanh-toan/nap-the error");
                rpc.setMessage("Server error");
//            return "serverError";
            } catch (StringIndexOutOfBoundsException ex) {
                rpc.setMessage("Server error");
//            return "loginError";
            }

            if (i==2 && (sid.length()==0 || csrf_token.length()==0)){
                rpc.setMessage("Server error");
                return rpc;
            }
            if (sid.length()>0 && csrf_token.length()>0)break;

        }

        cookie.put("Referer", "https://vietteltelecom.vn/my-viettel/quan-ly-cuoc-thanh-toan/nap-the");


        for (int i = 0; i < 3; i++) {
            captcha = getCaptcha(header, sid, "pay");
            if (captcha.length() > 5) {
                try {
                    Connection.Response respPayCard;
                    Map<String, String> datas = new HashMap<>();
                    datas.put("type", "payment");
                    datas.put("pay[service_type]", "1");
                    datas.put("pay[code]", code);
                    datas.put("pay[captcha]", captcha);
                    datas.put("pay[_csrf_token]", csrf_token);
                    datas.put("pay[isdn]", "");

                    respPayCard = Jsoup.connect("https://vietteltelecom.vn/my-viettel/quan-ly-cuoc-thanh-toan/nap-the")
                            .method(Connection.Method.POST)
                            .headers(header)
                            .ignoreContentType(true)
                            .data(datas)
                            .execute();
                    Document document = respPayCard.parse();

//                    System.out.println("TIME: " + Utility.getTime());


                    String message = document.select("#fee_payment > div:nth-child(7) > div:nth-child(2) > p").text();
//                    System.out.println(" " + message.length() + " message: " + message);
                    logger.info("length: " + message.length() + " message: " + message);
                    if (message.length() == 0) {
                        String content = document.select("#fee_payment > div:nth-child(8) > span").text();

//                        String content = "Bạn đã nạp thẻ mệnh giá 10.000 VNĐ. Hệ thống đang xử lý yêu cầu. Vui lòng giữ thẻ chờ tin nhắn thông báo thanh toán thành công về máy điện thoại của Quý khách. Xin cảm ơn!";
                        System.out.println("content: " + content);

                        if (content.startsWith("not message") || content.startsWith("Nạp thẻ không thành công") || content.length() == 0) {
//                            return ("serverError");
                            rpc.setMessage("serverError");
                        }
                        if (content.startsWith("Bạn đã nạp thẻ mệnh giá")) {
                            long money = getCardMoney(content);
//                            return "Bạn đã nạp thẻ mệnh giá " + money + " VNĐ";
                            rpc.setMessage("Bạn đã nạp thẻ mệnh giá " + money + " VNĐ");
                            rpc.setMoney(money);
                            rpc.setStatus(1);
                        }
                        if (content.startsWith("Quý khách đã nhập sai quá 5 lần")) {
//                            return ("Quý khách đã nhập sai quá 5 lần");
                            rpc.setMessage("Quý khách đã nhập sai quá 5 lần");
                        }
                        if (content.startsWith("Quá trình thực hiện có lỗi")) {
//                            return ("Quá trình thực hiện có lỗi");
                            rpc.setMessage("Quá trình thực hiện có lỗi");
                        }
                        if (content.startsWith("Thẻ cào không hợp lệ")) {
//                            return ("Thẻ cào không hợp lệ");
                            rpc.setMessage("Thẻ cào không hợp lệ");
                        }
                        break;
                    }

                } catch (IOException e) {
//                    System.out.println("pay card error");
                    logger.warning("pay card error");
                }
            }

            if (i==2 && rpc.getMessage().length()==0)rpc.setMessage("Server error");
        }
        return rpc;

    }

    private static String getCaptcha(Map<String, String> header, String sid, String key) {
        try {
            Connection.Response respCaptcha = Jsoup.connect("https://vietteltelecom.vn/captcha?sid=" + sid + "?r=" + Math.random() + "&reload=1&namespace=" + key)
                    .method(Connection.Method.GET)
                    .headers(header)
                    .ignoreContentType(true)
                    .execute();

            byte[] byteCaptcha = IOUtils.toByteArray(respCaptcha.bodyStream());
            return GetAntiCapcha.getCaptchaBase64(byteCaptcha);
        } catch (IOException e) {
//            System.out.println("error get captcha");
            logger.warning("error get captcha");
            return "";
        }
    }


    private static long getCardMoney(String message) {
        String[] split = message.substring(24).split("VNĐ");
        String strMoney = split[0].replaceAll("\\.", "").trim();
        System.out.println("money: " + strMoney);
        return Long.parseLong(strMoney);
    }
}
