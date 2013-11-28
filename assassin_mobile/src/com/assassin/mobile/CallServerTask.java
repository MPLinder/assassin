package com.assassin.mobile;

import org.json.JSONObject;

import android.os.AsyncTask;

class CallServerTask extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... params) {
    	boolean authed = false;
    	if (params[1].equals("true")) {
    		authed = true;
    	}
    	System.out.println("**** calling server via utils");
        return Utils.callServer(params[0], authed, params[2]);
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(JSONObject result) {
        ;
   }
}