package com.assassin.mobile;

import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;

class CallServerTask extends AsyncTask<String, Void, HttpsURLConnection> {
    @Override
    protected HttpsURLConnection doInBackground(String... params) {
    	boolean authed = false;
    	if (params[1].equals("true")) {
    		authed = true;
    	}

        return Utils.callServer(params[0], authed, params[2]);
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(HttpsURLConnection result) {
        ;
   }
}