package com.controller;


import com.config.FirestoreConfig;
import com.entity.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.service.PayMyViettel;
import com.util.RESTUtil;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class PaymentController {

    @RequestMapping(value = "card", method = RequestMethod.GET)
    String getLogin(Model model) throws ExecutionException, InterruptedException {
        return "recharge";
    }


    @RequestMapping(value = "pay", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    String eventDetail(Model model, @RequestBody() String content) throws ExecutionException, InterruptedException {
        Map<String, Object> map = new HashMap<>();

        System.out.println(content);
        Firestore db = FirestoreClient.getFirestore(FirestoreConfig.app);
        JSONObject object = new JSONObject(content);


        ApiFuture<QuerySnapshot> futureSession = db.collection("session").whereEqualTo("jsession", object.getString("session")).get();
        List<Session> sessions = futureSession.get().toObjects(Session.class);

        if (sessions.size() > 0 && ((sessions.get(0).getCreateTime()+ 30*60*1000) > System.currentTimeMillis())) {
            ApiFuture<DocumentSnapshot> futureUser = db.collection("user").document(sessions.get(0).getPhone()).get();
            User user = futureUser.get().toObject(User.class);

            ApiFuture<QuerySnapshot> futureMyViettel = db.collection("myviettel").whereEqualTo("status", true).get();
            List<MyViettel> list = futureMyViettel.get().toObjects(MyViettel.class);
            if (list.size() > 0 && user != null) {
                RespPay pay = PayMyViettel.payViettel(list.get(0).getPhone(), list.get(0).getPassword(), object.getString("code"));
                if (pay.getStatus() == 1) {
                    user.setWallet(user.getWallet() + pay.getMoney());
                    db.collection("user").document(user.getPhone()).set(user);
                    map.put("status", 1);
                    map.put("message", pay.getMessage());
                } else {
                    map.put("status", 0);
                    map.put("message", pay.getMessage());
                }
            } else {
                map.put("status", 0);
                map.put("message", "book ticket failed");
            }

        } else {
            map.put("status", 0);
            map.put("message", "user not found");
        }
        return RESTUtil.parJson(map);
    }


}
