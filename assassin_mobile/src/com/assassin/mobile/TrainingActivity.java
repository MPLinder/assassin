package com.assassin.mobile;

import java.util.HashMap;

import com.facebook.Session;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class TrainingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);
		
		// TODO: this is just a proof of concept, don't actually do this
		String imageLocation = "/storage/sdcard0/DCIM/browser-photos/1384022398651.jpg";
		String URI = "attempt";
		Session session = Session.getActiveSession();
		String accessToken = session.getAccessToken();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("to_user", "1");
		new ImageUploadTask().execute(imageLocation, URI, accessToken, params);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.training, menu);
		return true;
	}

}