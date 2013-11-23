package com.assassin.mobile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.os.AsyncTask;

class CallServerTask extends AsyncTask<String, Void, InputStreamReader> {
    @Override
    protected InputStreamReader doInBackground(String... params) {
    	boolean authed = false;
    	if (params[1].equals("true")) {
    		authed = true;
    	}

        return Utils.callServer(params[0], authed, params[2]);
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(InputStreamReader result) {
        ;
   }
}