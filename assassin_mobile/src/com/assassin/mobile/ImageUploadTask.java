package com.assassin.mobile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.os.AsyncTask;

public class ImageUploadTask extends AsyncTask<Object, Void, Object> {

    @Override
    protected Object doInBackground(Object... args) {
    	
    	String responseContents = new String();
            
    	String imageLocation = (String) args[0];
    	String URI = (String) args[1];
        String accesToken = (String) args[2];
        HashMap<String, String> params = (HashMap<String, String>) args[3];
        
        // Get csrftoken
        HttpsURLConnection response = Utils.getServerConnection(URI, true, "GET");
        
        List<String> cookieList = response.getHeaderFields().get("Set-Cookie");
        HashMap<String, String> cookies = new HashMap<String, String>();
        if (cookieList != null) {
            for (String cookie : cookieList) {
            	String[] token = cookie.split(";");
            	String[] nameAndValue = token[0].split("=");
            	cookies.put(nameAndValue[0], nameAndValue[1]);
            }
        }
             
    	
    	// Get image as POST content
    	File imageFile = new File(imageLocation);
    	ContentBody contentBody = new FileBody(new File(imageLocation), ContentType.create("image/jpeg"), imageFile.getName());
    	MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    	
    	reqEntity.addPart("image", contentBody);
    	
    	// Add parameters onto post request
    	if (params != null) {
	        for (String param : params.keySet()) {
	            try {
	                            reqEntity.addPart(param, new StringBody(params.get(param)));
	                    } catch (UnsupportedEncodingException e) {
	                            // TODO Auto-generated catch block
	                            e.printStackTrace();
	                    }
	        }
    	}
        
    	HostnameVerifier hostnameVerifier = Utils.getHostnameVerifier();
    	HttpsURLConnection conn = null;
        try {
        	// Set up connection
            URL url = new URL(response.getURL().toString());
            
            conn = (HttpsURLConnection) url.openConnection();
            
            // Django requires the csrf token and referer header
            conn.addRequestProperty("Cookie", "csrftoken=" + cookies.get("csrftoken") +
					"; sessionid=" + cookies.get("sessionid"));
            conn.addRequestProperty("X-CSRFToken", cookies.get("csrftoken"));
            conn.addRequestProperty("Referer", Constants.BASE_URL);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());
            conn.addRequestProperty("Content-length", reqEntity.getContentLength()+"");
            
            conn.setRequestMethod("POST");
            conn.setHostnameVerifier(hostnameVerifier);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            
            OutputStream os = conn.getOutputStream();
            reqEntity.writeTo(conn.getOutputStream());
            os.close();
            
            // Make connection
            conn.connect();
            
            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                responseContents =  Utils.readStream(conn.getInputStream());
                System.out.println("*****Image Upload response contents: " + responseContents);
            } else {
                responseContents = Utils.readStream(conn.getErrorStream());
                System.out.println("*****Image Upload response contents: " + responseContents);
            }
            conn.disconnect();
            
        } catch (MalformedURLException ex) {
        	ex.printStackTrace();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        response.disconnect();
        return responseContents;
    }

}