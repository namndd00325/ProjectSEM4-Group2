package com.controller;

import com.common.constProject;
import com.config.FirestoreConfig;
import com.entity.Event;
import com.entity.Session;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class AdminController {
    private final static String registerSuccess = "Successful registration";
    private final static String checkAccount = "Account not admin";
    private final static String checkAccExists = "Account already exists";
    private final static int roleAdmin = 2;

    @RequestMapping(value = "create", method = RequestMethod.GET)
    String getRegister(Model model) throws ExecutionException, InterruptedException {
        return "addev";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    String postRegister(Model model, @RequestBody() String content) throws ExecutionException, InterruptedException {
        System.out.println(content);
        Firestore db = FirestoreClient.getFirestore(FirestoreConfig.app);
        JSONObject object = new JSONObject(content);

        ApiFuture<QuerySnapshot> futureSession = db.collection("session").whereEqualTo("jsession", object.getString("session")).get();
        List<Session> sessions = futureSession.get().toObjects(Session.class);
        Map<String, Object> map = new HashMap<>();
        if (sessions.size() > 0 && ((sessions.get(0).getCreateTime() + 30 * 60 * 1000) > System.currentTimeMillis())) {
            ApiFuture<DocumentSnapshot> futureUser = db.collection("user").document(sessions.get(0).getPhone()).get();
            User user = futureUser.get().toObject(User.class);

            if (user!=null && user.getStatus()== roleAdmin){
                Event event = new Event();
                event.setTitle(object.getString("title"));
                event.setDescription(object.getString("description"));
                event.setAddress(object.getString("address"));

                event.setPlace(object.getString("place"));
                event.setPrice(object.getLong("price"));
                event.setQuantity(object.getLong("quantity"));

                event.setStart_at(RESTUtil.parLongTime(object.getString("start_at")));
                event.setTime(object.getLong("time"));
                event.setType(object.getString("type"));
                event.setImage(object.getString("image"));

                db.collection(constProject.eventStr).document(event.getId()).set(event);

                map.put(constProject.statusStr, 1);
                map.put(constProject.msgStr, registerSuccess);
            }else {
                map.put(constProject.statusStr, 0);
                map.put(constProject.msgStr, checkAccount);
            }

        } else {
            map.put(constProject.statusStr, 0);
            map.put(constProject.msgStr, checkAccExists);
        }

        return RESTUtil.parJson(map);
    }

}
