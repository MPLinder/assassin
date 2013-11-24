package com.assassin.mobile;

import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;

public class ImageUploadTask extends AsyncTask<Object, Void, Object> {

    @Override
    protected Object doInBackground(Object... args) {
            
    	String imageLocation = (String) args[0];
    	String URI = (String) args[1];
        String accesToken = (String) args[2];
        HashMap<String, String> params = (HashMap<String, String>) args[3];
        
        // TODO: utils.callserver to /attempt and parse out csrf token
        HttpsURLConnection response = Utils.callServer(URI, true, "GET");
        String csrfToken = response.getHeaderField("csrftoken");
        System.out.println("********CSRFTOKEN: " + csrfToken);
        
             
        // TODO: make form post w/ token and other stuff
        
        return new Object();
    }

}