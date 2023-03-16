package distributed_system_ski_resort_client;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;



import org.example.model.Skier;

import com.google.gson.Gson;

public class SkiResortClient {	
	public static void main(String[] args) throws IOException, URISyntaxException
	{
		
	}
	
	//Testing GET request (Only for testing) 
	public static void get(String uri) throws Exception 
	{
	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).GET().build();

	    HttpResponse<String> response =
	          client.send(request, BodyHandlers.ofString());

	    System.out.println("GET REQUEST RESPONSE CODE FROM SERVER : "+response.statusCode());
	}
	
	
	public static int post(int resortID,String seasonID,String dayID,int skierID,int liftID,int time) throws Exception 
	{
	    HttpClient client = HttpClient.newHttpClient();
	    
	    Map<String, Object> requestMap = new HashMap<String, Object>();
	    Map<String, Object> skierMap = new HashMap<String, Object>();
	    
	    skierMap.put("resortID", resortID);
	    skierMap.put("seasonID",seasonID);
	    skierMap.put("dayID", dayID);
	    skierMap.put("skierID", skierID);
	    skierMap.put("liftID", liftID);
	    skierMap.put("time",time);
	    
	    String skierIDString = Integer.toString(skierID);
	    
	    requestMap.put(skierIDString, skierMap);
	    
	    Gson gson = new Gson();
	    String requestBody = gson.toJson(requestMap);
	    String url_test = "http://155.248.234.61:8080/ski"; // CHANGE IT
	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url_test))
	            .header("Content-Type","application/json")
	            .POST(BodyPublishers.ofString(requestBody))
	            .build();

	    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
	    System.out.println("POST REQUEST RESPONSE BODY : "+response.body());
	    System.out.println("POST REQUEST RESPONSE CODE"+response.statusCode());
	    return response.statusCode();
	}
	
	public static void test() throws Exception
	{
		//***** FOR TESTING CONNECTIVITY *****
		
		//Calling a simple GET method
		
		String url_get = "http://155.248.234.61:8080/ski?skierID=1070";
		get(url_get);
		System.out.println("CONNECTED TO SERVER");
	}
	
	public static int[] randomGenerator() 
	{
		// Random Generator for Data Generation 
		
		Random random = new Random();
		int resortID = random.nextInt(10)+1;
		int seasonID = 2022;
		int dayID = 1;
		int skierID = random.nextInt(100000)+1;
		int liftID = random.nextInt(40)+1;
		int time = random.nextInt(360)+1;
		int[] result = {resortID,seasonID,dayID,skierID,liftID,time} ;
		return result;
	}
	
	
}
