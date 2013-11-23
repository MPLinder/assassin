package com.assassin.mobile;

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
	
	
    public static InputStreamReader callServer(String URI, boolean authed, String reqType) {
    	HostnameVerifier hostnameVerifier = new HostnameVerifier() {
    	    @Override
    	    public boolean verify(String hostname, SSLSession session) {
    	        if (hostname.equals(Constants.BASE_DOMAIN)) {
    	        	return true;
    	        }
    	        return false;
    	    }
    	};
    	
        InputStreamReader result = null;
        try {
        	if (authed) {
            	if (!URI.contains("?")) {
            		URI += "?";
            	}
            	Session session = Session.getActiveSession();
                URI = URI + Constants.ACCESS_TOKEN_KEY + "=" + session.getAccessToken();
            }
        	
            URL url = new URL(Constants.BASE_URL + URI);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod(reqType);
            conn.setHostnameVerifier(hostnameVerifier);
            conn.setRequestProperty("Content-length", "0");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.connect();
            result = new InputStreamReader(conn.getInputStream(), "UTF-8");
            InputStream in = conn.getInputStream();
        } catch (MalformedURLException ex) {
        	ex.printStackTrace();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        return result ;        
    }
}
