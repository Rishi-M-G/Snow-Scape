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
	static int counter;
	
    private static final int NUM_THREADS = 32;
    private static final AtomicInteger requestCount = new AtomicInteger(0);
    private static final int MAX_NUM_REQUESTS = 10000;
    private static final int NUM_REQUESTS_PER_THREAD = 1000;

    static String csvFilePath = "C:\\Users\\Admin\\Desktop\\latency.csv";
    File csvWriter = new File(csvFilePath);
    

    


	
	public static void main(String[] args) throws IOException, URISyntaxException
	{
		
		try
		{
			// Testing Connectivity
			test();
			
			//Creating a Thread pool with 32 Threads
			ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
			
			// Creating a Blocking Queue that will hold the lift ride events
			final BlockingQueue<int[]> queue = new LinkedBlockingQueue<int[]>();

			// Create a single dedicated thread that will generate lift ride events
			Thread liftRideEventGenerator = new Thread(() -> {
				//Generating lift ride events and them to queue
				
				while(true)
				{
					int[] ski_event = randomGenerator();
					try {
						queue.put(ski_event);
					}
					catch (InterruptedException e)
					{
						Thread.currentThread().interrupt();
			            break;
					}
				}
			});
			
			//Starting lift ride event generator thread
			long startTime = System.currentTimeMillis();
			liftRideEventGenerator.start();
			
			//send post requests from the thread Pool
			for(int i=1;i<=2000;i++)
			{
				executorService.submit(() -> {
					try {
							int[] ski_event = queue.take();
							int resortID = ski_event[0];
							int seasonIDint = ski_event[1];
							String seasonID = Integer.toString(seasonIDint);
							int dayIDint = ski_event[2];
							String dayID = Integer.toString(dayIDint);
							int skierID = ski_event[3];
							int liftID = ski_event[4];
							int time = ski_event[5];
							
							// ***** HANDLING ERRORS *****
							int retryCount = 0;
							boolean success = false;
							while(!success && retryCount <5)
							{
								try {
									
									long st = System.currentTimeMillis();
									
									
									int responseCode = post(resortID,seasonID,dayID,skierID,liftID,time);
									long et = System.currentTimeMillis();
									long latency = et-st;
									
									String[] metrics = {String.valueOf(st), "POST", String.valueOf(latency), String.valueOf(responseCode)};
				                    FileWriter csvWriter = new FileWriter(csvFilePath, true);
				                    csvWriter.append(String.join(",", metrics));
				                    csvWriter.append("\n");
				                    csvWriter.close();
									
									if (responseCode == 201)
									{
										success = true;
										int numRequestsSent = requestCount.incrementAndGet();
										if(numRequestsSent >= 2000 )
										{
											int unsuccessful = 2000-numRequestsSent;
											executorService.shutdown();
											liftRideEventGenerator.interrupt();
											
											// ***** On Completion *****
											long endTime = System.currentTimeMillis();
											long totalTime = endTime - startTime;
						                    System.out.println(" Number of Successful requests sent : " + 10000);
						                    System.out.println(" Number of Unuccessful requests sent : " + unsuccessful);
						                    System.out.println(" Total Run Time : " + totalTime);
						                    double throughput = numRequestsSent/(totalTime/1000);
						                    
						                    System.out.println(" Total throughput in requests per second : " + throughput);
						                    
						                   
										}
									}
									else
									{
										retryCount++;
							            Thread.sleep(1000);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						}
							
					catch (InterruptedException e)
					{
			            e.printStackTrace();
					}	
				});
				
				
			}
			
			
			
			//***** For Profiling Performance *****
			
//			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//			Timestamp timestamp_bfr = new Timestamp(System.currentTimeMillis());
//			System.out.println("Time Stamp Before Sending POST Request : "+sdf.format(timestamp_bfr));
//			
			
			//test values
			
//			int resortID = randomGenerator()[0];
//			
//			
//			int seasonIDint = randomGenerator()[1];
//			String seasonID = Integer.toString(seasonIDint);
//			
//			
//			int dayIDint = randomGenerator()[2];
//			String dayID = Integer.toString(dayIDint);
//			
//			
//			int skierID = randomGenerator()[3];
//			
//			
//			int liftID = randomGenerator()[4];
//			
//			
//			int time = randomGenerator()[5];
//			
//			
//			
//			post(resortID,seasonID,dayID,skierID,liftID,time);
//			
//			Timestamp timestamp_aft = new Timestamp(System.currentTimeMillis());
//			System.out.println("Time Stamp After Recieving POST response : "+sdf.format(timestamp_aft));
		}
		catch(Exception e)
		{
            System.err.println("Error: " + e.getMessage());
		}
		
		
	}
	
	//Testing GET request (Only for testing) - NOT included in project deliverable
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
