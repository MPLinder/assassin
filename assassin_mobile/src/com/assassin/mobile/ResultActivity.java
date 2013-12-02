package com.assassin.mobile;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {
	public static String ATTEMPT_URI = "attemptUri";
	public static String CONFIDENCE_LEVEL = "confidenceLevel";
	public static String TO_USER = "toUsername";
	public static String SUCCESSFUL_ATTEMPT = "success";
	public static String SUCCESS_POINTS = "success_points";
	
	private String attemptUri;
	private String confidenceLevel;
	private String toUsername;
	private Boolean success;
	private String successPoints;
	
	private Bitmap attempt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
        Intent intent = getIntent();
        if (intent.hasExtra(ATTEMPT_URI) && 
        	intent.hasExtra(CONFIDENCE_LEVEL) &&
        	intent.hasExtra(TO_USER) &&
        	intent.hasExtra(SUCCESSFUL_ATTEMPT) &&
        	intent.hasExtra(SUCCESS_POINTS)) {
        	
            attemptUri = intent.getStringExtra(ATTEMPT_URI);
        	confidenceLevel = intent.getStringExtra(CONFIDENCE_LEVEL);
        	toUsername = intent.getStringExtra(TO_USER);
        	success = intent.getExtras().getBoolean(SUCCESSFUL_ATTEMPT);
        	successPoints = intent.getStringExtra(SUCCESS_POINTS);
        } else {
			Toast.makeText(this, R.string.attemptFailed, Toast.LENGTH_LONG).show();
			finish();
        }
        
		attempt = Utils.getBitmap(attemptUri);

    	ImageView imageView = (ImageView) findViewById(R.id.attemptResult); 
    	imageView.setImageBitmap(attempt);
    	
    	TextView successView = (TextView) findViewById(R.id.success);
    	TextView resultView = (TextView) findViewById(R.id.result);
    	TextView pointsView = (TextView) findViewById(R.id.points);
    	if (success == true) {
    		successView.setText(R.string.success);
    		resultView.setText("There is a " + confidenceLevel + " chance that you hit your target!");
    		pointsView.setText("You earned " + successPoints + " points!");
    	} else {
    		successView.setText(R.string.failure);
    		resultView.setText("There is only a " + confidenceLevel + " chance that you hit your target.");
    		pointsView.setText("You earned 0 points.");
    	}	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}
	
	@Override
	protected void onDestroy () {
        new File(attemptUri).delete();
        attempt.recycle();
        
		super.onDestroy();
	}
	
    public void showLeaderboard(View view) {
    	Intent intent = new Intent(ResultActivity.this, LeaderboardActivity.class);
    	startActivity(intent);
    }
    
    public void makeAttempt(View view) {
    	Intent intent = new Intent(ResultActivity.this, AttemptActivity.class);
    	startActivity(intent);
    }
    
    public void menu(View view) {
    	Intent intent = new Intent(ResultActivity.this, MainActivity.class);
    	startActivity(intent);
    }

}
