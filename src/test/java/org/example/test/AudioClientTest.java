package org.example.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpResponse;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.example.model.Artist;


public class AudioClientTest {
    
    private static final int TOTAL_CLIENTS = 100;
    private static final int GET_REQUEST_RATIO = 10; // 10:1
    
    @Test
    public void testConcurrentRequests() throws Exception {
        
        // Create a list of client threads
        List<Thread> clients = new ArrayList<>();
        
        // Create and start the client threads
        for (int i = 0; i < TOTAL_CLIENTS; i++) {
            Thread clientThread = new Thread(new AudioClientRunnable());
            clientThread.start();
            clients.add(clientThread);
        }
        
        // Wait for all client threads to finish
        for (Thread clientThread : clients) {
            clientThread.join();
        }
    }
    
    private class AudioClientRunnable implements Runnable {
        
        private static final String BASE_URL = "http://localhost:8080/audio?track_title=";
        private final String[] TRACK_TITLES = {"You Belong With Me", "I Love Me"};
        
        @Override
        public void run() {
            
            try {
                // Send GET or POST request based on ratio
                if (Math.random() * GET_REQUEST_RATIO <= 1) {
                    // Send GET request
                    String trackTitle = TRACK_TITLES[(int) (Math.random() * TRACK_TITLES.length)];
                    URL url = new URL(BASE_URL + trackTitle);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    connection.disconnect();
                    System.out.println("GET response from " + Thread.currentThread().getName() + ": " + response.toString());
                } else {
                    // Send POST request
                    // TODO: Implement POST request simulation
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}



