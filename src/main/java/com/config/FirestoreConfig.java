package com.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.IOException;
import java.io.InputStream;

public class FirestoreConfig {
    //    final public static String JSONFILE = "src/main/resources/booking-ticker-firebase-adminsdk-dtbbs-7eb8e1ef6e.json";
    final private static String JSONFILE = "booking-ticker-firebase-adminsdk-dtbbs-7eb8e1ef6e.json";


    public static FirebaseApp app = getApp();

    public static FirebaseApp getApp() {
        try {
            InputStream serviceAccount = ClassLoader.getSystemResourceAsStream(FirestoreConfig.JSONFILE);
            FirebaseOptions
                    firestoreOptions = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId("booking-ticker")
                    .build();
            return FirebaseApp.initializeApp(firestoreOptions, "booking-ticker");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

