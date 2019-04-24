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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class SessionController {

    @RequestMapping(value = "login", method = RequestMethod.GET)
    String getLogin(Model model) throws ExecutionException, InterruptedException {
        return "login";
    }


    @RequestMapping(value = "login", method = RequestMethod.POST)
    public @ResponseBody
    String eventDetail(Model model, @RequestBody() String content, HttpServletResponse resp) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore(FirestoreConfig.app);
        JSONObject object = new JSONObject(content);
        System.out.println(object);
        ApiFuture<DocumentSnapshot> futureUser = db.collection("user").document(object.getString("phone")).get();
        User user = futureUser.get().toObject(User.class);

        if (user != null && user.getPassword().equals(object.getString("password"))) {
            System.out.println(user.getPassword().equals(object.getString("password")));
            Session session = new Session(user.getPhone());
            db.collection("session").document(session.getPhone()).set(session);
            resp.addCookie(new Cookie("JSESSION", session.getJsession()));
            return "{\"status\":1,\"message\":\"login success\"}";
        } else {
            return "{\"status\":0,\"message\":\"login false\"}";
        }
    }
}
