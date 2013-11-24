package com.assassin.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.facebook.Session;

public class Utils {
	String accessToken;
	
	public String getAccessToken() {
		return this.accessToken;
	}
	
	public void setAccessToken(String token) {
		this.accessToken = token;
	}
	
	
	public static HostnameVerifier getHostnameVerifier() {
    	HostnameVerifier hostnameVerifier = new HostnameVerifier() {
    	    @Override
    	    public boolean verify(String hostname, SSLSession session) {
    	        if (hostname.equals(Constants.BASE_DOMAIN)) {
    	        	return true;
    	        }
    	        return false;
    	    }
    	};
    	
    	return hostnameVerifier;
	}
	
	
    public static HttpsURLConnection callServer(String URI, boolean authed, String reqType) {
    	HostnameVerifier hostnameVerifier = getHostnameVerifier();
    	
    	HttpsURLConnection conn = null;
        try {
        	if (authed) {
            	if (!URI.contains("?")) {
            		URI += "?";
            	}
            	Session session = Session.getActiveSession();
                URI = URI + Constants.ACCESS_TOKEN_KEY + "=" + session.getAccessToken() + "&type=json";
            }
        	
            URL url = new URL(Constants.BASE_URL + URI);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod(reqType);
            conn.setHostnameVerifier(hostnameVerifier);
            conn.setRequestProperty("Content-length", "0");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.connect();
        } catch (MalformedURLException ex) {
        	ex.printStackTrace();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        return conn;
    }
}
