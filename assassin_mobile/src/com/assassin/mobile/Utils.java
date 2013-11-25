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

import org.json.JSONException;
import org.json.JSONObject;

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
	
	
	public static HttpsURLConnection getServerConnection(String URI, boolean authed, String reqType) {
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
	
	
    public static JSONObject callServer(String URI, boolean authed, String reqType) {
       	HttpsURLConnection conn = getServerConnection(URI, authed, reqType);
       	
       	JSONObject result = null;
       	if (conn != null) {
            String response = null;
			try {
				response = readStream(conn.getInputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            try {
				result = new JSONObject(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            conn.disconnect();
        }
        return result;
    }
    
    
    public static String readStream(InputStream in) {
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
    
    public static boolean isTrained(JSONObject obj) {
    	try {
			return obj.getInt("trainers_required") <= obj.getInt("trainers_completed");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
}
