package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
		skierDB.put("20", skier_obj1);
	}
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
//		String skierIDString = request.getParameter("skierID");
//		int skierID = Integer.parseInt(skierIDString);
//		Skier skier_obj = skierDB.get(skierID);
//		int resortID = skier_obj.getResortID();
//		String dayID = skier_obj.getDayID();
//		String seasonID = skier_obj.getSeasonID();
		
		String dayID = request.getParameter("dayID");
		Skier skier_obj = skierDB.get(dayID);
		int resortID = skier_obj.getResortID();
		String seasonID = skier_obj.getSeasonID();
		int skierID = skier_obj.getSkierID();
		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(skierDB);
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		out.println("RESPONSE IN JSON - Single Element");
		out.println("Resort ID: " + gson.toJson(resortID));
		out.println("Season ID: " + gson.toJson(seasonID));
		out.println("Day ID : "+ gson.toJson(dayID));
		out.println("Skier ID: "+ gson.toJson(skierID));
		out.println("GET All Elements"+element.toString());
		out.flush();
	}
	
	@Override
	protected void doPost (HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
	{
		String resortIDString = request.getParameter("resortID");
		int resortID = Integer.parseInt(resortIDString);
		
		String seasonID = request.getParameter("seasonID");
		String dayID = request.getParameter("dayID");
		
		String skierIDString = request.getParameter("skierID");
		int skierID = Integer.parseInt(skierIDString);
		
		Skier skier_object = new Skier();
		skier_object.setResortID(resortID);
		skier_object.setSeasonID(seasonID);
		skier_object.setDayID(dayID);
		skier_object.setSkierID(skierID);
		
		skierDB.put(dayID, skier_object);
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getOutputStream().println("POST RESPONSE: Skier " + skierIDString + " is added to the database.");
	}
}
