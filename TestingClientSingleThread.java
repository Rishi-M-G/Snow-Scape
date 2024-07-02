import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import org.apache.catalina.connector.Response;



public class TestingClientSingleThread
{
     static int success=0;
     static int unSuccess=0;
     static long total=0;
     static int totalReq=0;
     static int NumThreads = 1;
     static String BaseAddr = "http://204.216.110.184:8080/Skiers"; 
     static int RequestPerThread = 10000;
     static int TotalRequests = 10000;
     static File file = new File("./myStats.csv");
     static List<String[]> data=new ArrayList<String[]>();
     static int RT[]= new int[Math.max(TotalRequests,NumThreads*RequestPerThread)];
     static int mean,per99,minRes,maxRes;
     static double median,throughPut;

    public static void main(String[] args) throws Exception 
    {
        HttpClient client = HttpClient.newHttpClient();
        List<Thread> threads = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < NumThreads; i++) 
        {
            Thread thread = new Thread(new RequestSender(client));
            thread.start();
            threads.add(thread);
        }
        
        for(Thread t : threads)
        {
            t.join();
        }
        threads.clear();
        int numRemainingRequests = TotalRequests - NumThreads * RequestPerThread;
        int numThreads = (int)Math.ceil((double)numRemainingRequests / RequestPerThread);
        //System.out.println(numThreads);
        for (int i = 0; i < numThreads; i++) 
        {
            Thread thread = new Thread(new RequestSender(client));
            thread.start();
            threads.add(thread);
        }
        for(Thread t : threads)
        {
            t.join();
        }
        long end = System.currentTimeMillis();
        long timeElapsed = end - start;
        double InSeconds = timeElapsed/1000F;
        System.out.println("Successfull count : "+success);
        System.out.println("UnSuccessfull count : "+unSuccess);
        totalReq=success+unSuccess;
        System.out.println("Total Time Taken : "+InSeconds);
        System.out.println("###############################################################################");
        System.out.println("Total milli seconds taken by all http post requests calculated indivually : "+total );
        double AvgTimeTaken=(double)total/totalReq;
        System.out.println("Average time taken by single request  : "+(double)total/totalReq);
        System.out.println("Actual rate of requests : "+(double)(totalReq)/((double)total/1000F));
        System.out.println("Little's Law predicted Throughput per second  : "+((double)(totalReq)/(((double)AvgTimeTaken/1000F)*totalReq)));
        System.out.println("###############################################################################");
        FileWriter outputfile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outputfile);
        writer.writeAll(data);
        System.out.println("Statistics Written to the file in format (startTime,RequestType,Latency(ResponseTime),StatusCode)..!!");
        writer.close();
        int i=0,totRes=0;
        
        for (String[] s: data)
        {
            RT[i]=Integer.parseInt(s[2]);
            totRes=totRes+RT[i];
            i++;
        }
        Arrays.sort(RT);
        if (RT.length % 2 != 0)
        {
           median=RT[RT.length / 2];
        }
        else
        {
           median= (double) (RT[(RT.length - 1) / 2] + RT[RT.length / 2]) / 2.0;
        }
        mean=totRes/RT.length;
        throughPut=(double)RT.length/((double)totRes/1000);
        per99=RT[(int)(RT.length*0.99)];
        minRes=RT[0];
        maxRes=RT[RT.length-1];
        System.out.println("Mean :"+mean);
        System.out.println("Median :"+median);
        System.out.println("throughPut per second  :"+throughPut);
        System.out.println("P99 :"+per99);
        System.out.println("Max Response Time :"+maxRes);
        System.out.println("Min Response Time :"+minRes);
    }
    
    private static List<String> generateLiftRideEvents(int numEvents) 
    {
        Random rand = new Random();
        List<String> events = new ArrayList<>(numEvents);
        for (int i = 0; i < numEvents; i++) {
            int skierID = rand.nextInt(100000) + 1;
            int resortID = rand.nextInt(10) + 1;
            int liftID = rand.nextInt(40) + 1;
            int seasonID = 2022;
            int dayID = 1;
            int time = rand.nextInt(360) + 1;
            String event = String.format(
                "{ \"skierID\": %d, \"resortID\": %d, \"liftID\": %d, \"seasonID\": %d, \"dayID\": %d, \"time\": %d }",
                skierID, resortID, liftID, seasonID, dayID, time);
            events.add(event);
        }
        return events;
    }
    static class RequestSender implements Runnable 
    {
        HttpClient client;
        public RequestSender( HttpClient client) 
        {
            this.client= client;
        }

        @Override
        public void run() 
        {
            try
            {
                Random rand = new Random();
                URL url = new URL(BaseAddr + "/skiers");
                List<String> events = generateLiftRideEvents(RequestPerThread);
                HttpResponse<String> response = null;
                for (String event : events) 
                {
                    synchronized(client)
                    {
                    //System.out.println(Thread.currentThread().getName());
                    System.out.println(event);
                    HttpRequest request = HttpRequest.newBuilder()
                             .uri(URI.create(url+""))
                             .POST(HttpRequest.BodyPublishers.ofString(event.toString()))
                             .build();
                    int statusCode=0;
                    long start,end;
                    try  
                    {
                      start = System.currentTimeMillis();
                      response = client.send(request, HttpResponse.BodyHandlers.ofString());
                      end = System.currentTimeMillis();
                      statusCode=response.statusCode();
                      total=total+(end-start);
                      data.add(new String[]{start+"","POST",(end-start)+"",statusCode+""});
                      //System.out.println(end-start);
                    }
                    catch(Exception e)
                    {
                        
                    }
                    if (response == null || statusCode < 200 || statusCode >= 300) 
                    {
                            if(response!=null)
                            {
                                System.err.println("Request failed 1 times : "+response.body());
                            }
                            else
                            {
                            System.err.println("Request failed 1 times ");
                            }
                            for (int i = 0; i < 4; i++) 
                            {
                                try 
                                {
                                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                                    statusCode = response.statusCode();

                                } 
                                catch (Exception ex) 
                                {
                                    //ex.printStackTrace();
                                }
                                if (response == null || statusCode < 200 || statusCode >= 300) 
                                {
                                    if(response!=null)
                                    {
                                    System.err.println("Request failed "+(i+2)+" times : "+response.body());
                                    }
                                    else
                                    {
                                    System.err.println("Request failed "+(i+2)+" times ");
                                    }
                                } 
                            }
                            if (response == null || statusCode < 200 || statusCode >= 300) 
                            {
                                unSuccess = unSuccess + 1;
                            }
                            else
                            {
                               System.out.println(response.body());
                               success=success+1; 
                            }
                            
                    }
                    else
                    {
                        System.out.println(response.body());
                        success=success+1;
                    }
                    }
                }
                
            
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            } 
            finally 
            {
                
            }
        }
    }
}
