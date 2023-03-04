package org.example.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
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


class WebServletTest {

	//@Test
	void testHelloServletGet() throws Exception {
		
		HttpClient client = new HttpClient();
        client.start();

        ContentResponse res = client.GET("http://localhost:9090/coen6317/HelloServlet");
        
        System.out.println(res.getContentAsString());
        
        client.stop();
		
	}
	
	
	//@Test
	void testBlockingServletGet() throws Exception {
		
		HttpClient client = new HttpClient();
        client.start();

        ContentResponse res = client.GET("http://localhost:9090/coen6317/BlockingServlet");
        
        System.out.println(res.getContentAsString());
        
        client.stop();
		
	}
	
	//@Test
	void testAsyncServletGet() throws Exception {
		
		String url = "http://localhost:9090/coen6317/longtask";
		HttpClient client = new HttpClient();
        client.start();

        ContentResponse response = client.GET(url);

		assertThat(response.getStatus(), equalTo(200));
		
		String responseContent = IOUtils.toString(response.getContent());
		
		 System.out.println(responseContent);
		//assertThat(responseContent, equalTo( "This is some heavy resource that will be served in an async way"));
		
	}

	
	@Test
	void testArtistsGet() throws Exception {
		String url = "http://localhost:9090/coen6317/artists";
		HttpClient client = new HttpClient();
        client.start();

        Request request = client.newRequest(url);
        request.param("id","id200");
        ContentResponse response = request.send();
   

		assertThat(response.getStatus(), equalTo(200));
		
		String responseContent = IOUtils.toString(response.getContent());
		
		 System.out.println(responseContent);
		client.stop();
		
	}
	
	@SuppressWarnings("deprecation")
	@Test
	void testArtistsPost() throws Exception {
		
		String url = "http://localhost:9090/coen6317/artists";
		HttpClient client = new HttpClient();
        client.start();
        
        Request request = client.POST(url);
        
        request.param("id","id200");
        request.param("name","artist200");
        
        ContentResponse response = request.send();
		String res = new String(response.getContent());
		System.out.println(res);
		client.stop();
	}
}






