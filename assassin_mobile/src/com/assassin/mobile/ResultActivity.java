package com.assassin.mobile;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {
	public static String ATTEMPT_URI = "attemptUri";
	public static String CONFIDENCE_LEVEL = "confidenceLevel";
	public static String TO_USER = "toUsername";
	public static String SUCCESSFUL_ATTEMPT = "success";
	
	private String attemptUri;
	private String confidenceLevel;
	private String toUsername;
	private Boolean success;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
        Intent intent = getIntent();
        if (intent.hasExtra(ATTEMPT_URI) && 
        	intent.hasExtra(CONFIDENCE_LEVEL) &&
        	intent.hasExtra(TO_USER) &&
        	intent.hasExtra(SUCCESSFUL_ATTEMPT)) {
        	
            attemptUri = intent.getStringExtra(ATTEMPT_URI);
        	confidenceLevel = intent.getStringExtra(CONFIDENCE_LEVEL);
        	toUsername = intent.getStringExtra(TO_USER);
        	success = intent.getExtras().getBoolean(SUCCESSFUL_ATTEMPT);
        } else {
			Toast.makeText(this, R.string.attemptFailed, Toast.LENGTH_LONG).show();
			finish();
        }
        
		Bitmap attempt = BitmapFactory.decodeFile(attemptUri);

    	ImageView imageView = (ImageView) findViewById(R.id.attemptResult); 
    	imageView.setImageBitmap(attempt);
    	
    	TextView toUsernameView = (TextView) findViewById(R.id.toUsername);
    	toUsernameView.setText(toUsername);
    	
    	TextView successView = (TextView) findViewById(R.id.success);
    	successView.setText(Boolean.toString(success));
    	
    	TextView confidenceLevelView = (TextView) findViewById(R.id.confidenceLevel);
    	confidenceLevelView.setText(confidenceLevel);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}

}
