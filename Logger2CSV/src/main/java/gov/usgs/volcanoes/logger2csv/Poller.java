package gov.usgs.volcanoes.logger2csv;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import gov.usgs.util.ConfigFile;

public class Poller implements Runnable {

	private final ConfigFile config;
	
	private final InetAddress address;
	
	public Poller(ConfigFile config) throws UnknownHostException {
		this.config = config;
		address = InetAddress.getByName(config.getString("address"));
	}

	public void httpClient() throws ClientProtocolException, IOException {
	       CloseableHttpClient httpclient = HttpClients.createDefault();
	        try {
	            HttpGet httpget = new HttpGet("http://www.adn.com/");

	            System.out.println("Executing request " + httpget.getRequestLine());

	            // Create a custom response handler
	            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

	                @Override
	                public String handleResponse(
	                        final HttpResponse response) throws ClientProtocolException, IOException {
	                    int status = response.getStatusLine().getStatusCode();
	                    if (status >= 200 && status < 300) {
	                        HttpEntity entity = response.getEntity();
	                        return entity != null ? EntityUtils.toString(entity) : null;
	                    } else {
	                        throw new ClientProtocolException("Unexpected response status: " + status);
	                    }
	                }

	            };
	            String responseBody = httpclient.execute(httpget, responseHandler);
	            System.out.println("----------------------------------------");
	            System.out.println(responseBody);
	        } finally {
	            httpclient.close();
	        }

	}

	@Override
	public void run() {
	}

}
