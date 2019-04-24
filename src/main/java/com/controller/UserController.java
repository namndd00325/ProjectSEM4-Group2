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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class UserController {


    @RequestMapping(value = "user", method = RequestMethod.POST, produces = "application/json")
    String eventDetail(Model model, @RequestBody() String content) throws ExecutionException, InterruptedException {

        System.out.println(content);
        Firestore db = FirestoreClient.getFirestore(FirestoreConfig.app);
        JSONObject object = new JSONObject(content);


        ApiFuture<QuerySnapshot> futureSession = db.collection("session").whereEqualTo("jsession", object.getString("session")).get();
        List<Session> sessions = futureSession.get().toObjects(Session.class);
        System.out.println(sessions.size());
        if (sessions.size() > 0 && ((sessions.get(0).getCreateTime() + 30 * 60 * 1000) > System.currentTimeMillis())) {
            ApiFuture<DocumentSnapshot> futureUser = db.collection("user").document(sessions.get(0).getPhone()).get();
            User user = futureUser.get().toObject(User.class);
            if (user != null) {
                ApiFuture<QuerySnapshot> futureTicket = db.collection("ticket").whereEqualTo("userId", user.getPhone()).get();
                List<Ticket> tickets = futureTicket.get().toObjects(Ticket.class);

                List<Ticket> orders = new ArrayList<>();

                for (Ticket ticket:tickets) {
                    if (ticket.getStatus()==0){
                        orders.add(ticket);
                    }
                }
                tickets.removeIf(t -> (t.getStatus()==0));
                System.out.println(RESTUtil.parJson(user));
                model.addAttribute("orders", orders);
                model.addAttribute("tickets", tickets);
                model.addAttribute("user", user);
                return "information";
            } else return "login";

        } else {
            return "login";
        }
    }

    @RequestMapping(value = "register", method = RequestMethod.GET)
    String getRegister(Model model) throws ExecutionException, InterruptedException {
        return "register";
    }


    @RequestMapping(value = "register", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    String postRegister(Model model, @RequestBody() String content) throws ExecutionException, InterruptedException {
        System.out.println(content);
        Firestore db = FirestoreClient.getFirestore(FirestoreConfig.app);
        JSONObject object = new JSONObject(content);

        ApiFuture<DocumentSnapshot> futureUser = db.collection("user").document(object.getString("phone")).get();
        User u = futureUser.get().toObject(User.class);
        Map<String, Object> map = new HashMap<>();
        if (u == null) {
            User user = new User(object.getString("phone"));
            user.setLastName(object.getString("lastName"));
            user.setFirstName(object.getString("firstName"));
            user.setAddress(object.getString("address"));
            user.setPhone(object.getString("password"));

            db.collection("user").document(user.getPhone()).set(user);

            map.put("status", 1);
            map.put("message", "Successful registration");
        } else {
            map.put("status", 0);
            map.put("message", "Account already exists");
        }
        return RESTUtil.parJson(map);
    }
}
