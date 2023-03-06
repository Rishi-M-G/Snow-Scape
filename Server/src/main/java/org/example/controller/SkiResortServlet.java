package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.example.model.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


@WebServlet(name = "ski", value = "ski")
public class SkiResortServlet extends HttpServlet {
	
	//Concurrent Hashmap 
	ConcurrentHashMap<String,Skier> skierDB = new ConcurrentHashMap<>();

	
	@Override
	 public void init() throws ServletException {
		Skier skier_obj1 = new Skier();
		skier_obj1.setResortID(2);
		skier_obj1.setSeasonID("4");
		skier_obj1.setDayID("20");
		skier_obj1.setSkierID(1070);
		skier_obj1.setLiftID(30);
		skier_obj1.setTime(350);
		
		skierDB.put("1070", skier_obj1);
	}
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		
		String skierIDString = request.getParameter("skierID");
		int skierID = Integer.parseInt(skierIDString);
		Skier skier_obj = skierDB.get(skierIDString);
		int resortID = skier_obj.getResortID();
		String seasonID = skier_obj.getSeasonID();
		int liftID = skier_obj.getLiftID();
		int time = skier_obj.getTime();
		String dayID = skier_obj.getDayID();
		
		
		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(skierDB);
		
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		
		out.println("RESPONSE IN JSON - Single Element");
		out.println("Resort ID: " + gson.toJson(resortID));
		out.println("Season ID: " + gson.toJson(seasonID));
		out.println("Day ID : "+ gson.toJson(dayID));
		out.println("Skier ID : "+ gson.toJson(skierID));
		out.println("Lift ID : "+gson.toJson(liftID));
		out.println("Time : "+gson.toJson(time));
		
		out.println("GET All Elements"+element.toString());
		out.flush();
		
	}
	
	@Override
	protected void doPost (HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
	{
		//***** GET PARAMETERS *****
		//***** READ REQUEST BODY *****
	    BufferedReader reader = request.getReader();
	    Gson gson = new Gson();
	    
	    JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
	    String skierIDString  = jsonObject.keySet().iterator().next();
	    int skierID = Integer.parseInt(skierIDString);
	    
	    
	    JsonObject skierObject = jsonObject.getAsJsonObject(skierIDString); 
	    int resortID = skierObject.get("resortID").getAsInt();
	    String seasonID = skierObject.get("seasonID").getAsString();
	    int liftID = skierObject.get("liftID").getAsInt();
	    int time = skierObject.get("time").getAsInt();
	    String dayID = skierObject.get("dayID").getAsString();
	    
	    // BASIC PARAMETER VALIDATION
	    
	    if (jsonObject == null || jsonObject.isEmpty()) {
	        throw new IllegalArgumentException("The JSON object is null or empty.");
	    }
	    
	    
	    if (resortID <= 0) {
	        throw new IllegalArgumentException("The resortID must be a positive integer.");
	    }

	    if (liftID <= 0) {
	        throw new IllegalArgumentException("The liftID must be a positive integer.");
	    }

	    if (time <= 0) {
	        throw new IllegalArgumentException("The time must be a positive integer.");
	    }
	    if (seasonID == null || seasonID.isEmpty()) {
	        throw new IllegalArgumentException("The seasonID is null or empty.");
	    }

	    if (dayID == null || dayID.isEmpty()) {
	        throw new IllegalArgumentException("The dayID is null or empty.");
	    }

	    
	    Skier skier = new Skier();
	    skier.setResortID(resortID);
	    skier.setSeasonID(seasonID);
	    skier.setDayID(dayID);
	    skier.setSkierID(skierID);
	    skier.setLiftID(liftID);
	    skier.setTime(time);
	    //skierDB.put(skierIDString, skier); // line changed 

	    //***** SEND RESPONSE : POST METHOD *****
	    response.setStatus(HttpServletResponse.SC_CREATED);
	    response.getOutputStream().println("POST RESPONSE: Skier " + skierIDString + " is added to the database.");
	    }
}
