package com.controller;


import com.config.FirestoreConfig;
import com.entity.Event;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import com.google.cloud.firestore.QuerySnapshot;

import com.google.firebase.cloud.FirestoreClient;
import com.util.RESTUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.thymeleaf.util.StringUtils.startsWith;


@Controller
@SpringBootApplication
public class HomeController {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(HomeController.class, args);
    }

    @RequestMapping(value = "home", method = RequestMethod.GET)
    String index(Model model) throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore(FirestoreConfig.app);
        long time = System.currentTimeMillis();
//        ApiFuture<QuerySnapshot> futureEvent = db.collection("Account").whereGreaterThanOrEqualTo("start_at", time).get();
        ApiFuture<QuerySnapshot> futureEvent = db.collection("event").limit(12).get();
        List<Event> events = futureEvent.get().toObjects(Event.class);
        model.addAttribute("events", events);
        return "index";
    }

    @RequestMapping(value = "search{eve_name}{eve_type}{eve_place}{eve_time}", method = RequestMethod.GET)
    String searchEvent(Model model, @RequestParam("eve_name") String eve_name, @RequestParam("eve_type") String eve_type, @RequestParam("eve_place") String eve_place, @RequestParam("eve_time") String eve_time) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore(FirestoreConfig.app);
        ApiFuture<QuerySnapshot> futureEvent;

        if (!eve_type.equals("0") && !eve_place.equals("0")) {
            futureEvent = db.collection("event").whereEqualTo("type", eve_type).whereEqualTo("place", eve_place).limit(50).get();
        } else if (!eve_type.equals("0") && eve_place.equals("0")) {
            futureEvent = db.collection("event").whereEqualTo("type", eve_type).limit(50).get();
        } else if (!eve_place.equals("0") && eve_type.equals("0")) {
            futureEvent = db.collection("event").whereEqualTo("place", eve_place).limit(50).get();
        } else futureEvent = db.collection("event").limit(50).get();

        List<Event> events = futureEvent.get().toObjects(Event.class);
        System.out.println(events.size());
        if (!eve_time.equals("0")) {
            events.removeIf(e -> (!RESTUtil.checkTime(e.getStart_at(), eve_time)));
        }
        List<Event> eventList = new ArrayList<>();
        for (Event event : events) {
            String[] splitName = event.getTitle().split(" ");
            for (String text : splitName) {

                System.out.println(RESTUtil.checkText(eve_name, event.getTitle()) + " " + RESTUtil.startsWith(text, eve_name));
                if (RESTUtil.checkText(eve_name, event.getTitle()) || RESTUtil.startsWith(text, eve_name)) {
                    eventList.add(event);
                    break;
                }
            }
        }
        System.out.println(eventList.size());
        model.addAttribute("events", eventList);
        return "index";
    }

    @RequestMapping(value = "event{id}", method = RequestMethod.GET)
    String eventDetail(Model model, @RequestParam("id") String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore(FirestoreConfig.app);
        ApiFuture<DocumentSnapshot> futureEvent = db.collection("event").document(id).get();
        Event event = futureEvent.get().toObject(Event.class);
        model.addAttribute("event", event);
        return "detail";
    }


}
