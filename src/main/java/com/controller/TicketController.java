package com.controller;


import com.config.FirestoreConfig;
import com.entity.Event;
import com.entity.Session;
import com.entity.Ticket;
import com.entity.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import com.util.RESTUtil;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class TicketController {


    @RequestMapping(value = "book", method = RequestMethod.POST, produces = "application/json")
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

            ApiFuture<DocumentSnapshot> futureEvent = db.collection("event").document(object.getString("event")).get();
            Event event = futureEvent.get().toObject(Event.class);
            if (event != null && user!=null ) {

                if (object.getString("pay").equalsIgnoreCase("wallet") && user.getWallet() >= event.getPrice()){
                    Ticket ticket = new Ticket();
                    ticket.setEventId(event.getId());
                    ticket.setUserId(user.getPhone());
                    ticket.setPlace(event.getAddress() + " - " + event.getPlace());
                    ticket.setTime(RESTUtil.timeEvent(event.getStart_at(),event.getTime()));
                    ticket.setStatus(1);
                    db.collection("ticket").document(ticket.getId()).set(ticket);
                    map.put("status", 1);
                    map.put("message", "book ticket completed");
                }
                if (object.getString("pay").matches("visa|transfer|direct")){
                    Ticket ticket = new Ticket();
                    ticket.setEventId(event.getId());
                    ticket.setUserId(user.getPhone());
                    ticket.setPlace(event.getAddress() + " - " + event.getPlace());
                    ticket.setTime(RESTUtil.timeEvent(event.getStart_at(),event.getTime()));
                    ticket.setStatus(0);
                    db.collection("ticket").document(ticket.getId()).set(ticket);
                    map.put("status", 1);
                    map.put("message", "book ticket completed");
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
