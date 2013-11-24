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
            
    	String imageLocation = (String) args[0];
    	String URI = (String) args[1];
        String accesToken = (String) args[2];
        HashMap<String, String> params = (HashMap<String, String>) args[3];
        
        // Get csrftoken and sessionid
        HttpsURLConnection response = Utils.callServer(URI, true, "GET");
        
        List<String> cookieList = response.getHeaderFields().get("Set-Cookie");
        HashMap<String, String> cookies = new HashMap<String, String>();
        if (cookieList != null) {
            for (String cookie : cookieList) {
            	String[] token = cookie.split(";");
            	String[] nameAndValue = token[0].split("=");
            	cookies.put(nameAndValue[0], nameAndValue[1]);
            }
        }
             
        // Post image to uri
    	HostnameVerifier hostnameVerifier = Utils.getHostnameVerifier();
    	
    	ContentBody contentBody = new FileBody(new File(imageLocation), ContentType.create("image/jpeg"), "image.jpg");
    	MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    	
    	reqEntity.addPart("image", contentBody);
        for (String param : params.keySet()) {
        	try {
				reqEntity.addPart(param, new StringBody(params.get(param)));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    	
    	HttpsURLConnection conn = null;
        try {
            URL url = new URL(response.getURL().toString());
            
            conn = (HttpsURLConnection) url.openConnection();
            conn.addRequestProperty("X-CSRFToken", cookies.get("csrftoken"));
            System.out.println("****CSRF TOKEN: " + cookies.get("csrftoken"));
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());
            conn.addRequestProperty("Content-length", reqEntity.getContentLength()+"");
            
            conn.setRequestMethod("POST");
            conn.setHostnameVerifier(hostnameVerifier);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            
            OutputStream os = conn.getOutputStream();
            reqEntity.writeTo(conn.getOutputStream());
            os.close();
            
            conn.connect();
            
            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                return readStream(conn.getInputStream());
            }
            
        } catch (MalformedURLException ex) {
        	ex.printStackTrace();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        return conn;
    }

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

}