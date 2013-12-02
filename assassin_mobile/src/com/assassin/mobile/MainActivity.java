package com.assassin.mobile;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.widget.ProgressBar;

import com.facebook.Session;

public class MainActivity extends FragmentActivity {
	private MainFragment mainFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    CookieSyncManager.createInstance(this);
	    
	    if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	        mainFragment = new MainFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, mainFragment)
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	        mainFragment = (MainFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
	    }
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    
    public void startTraining(View view) {
        Intent intent = new Intent(MainActivity.this, TrainingActivity.class);
        
		try {
			String trainersCompleted = null;
			String trainersRequired = null;
			
			Intent myIntent = getIntent();
			if (myIntent.hasExtra(TrainingActivity.TRAINERS_COMPLETED) &&
				myIntent.hasExtra(TrainingActivity.TRAINERS_REQUIRED)) {
				trainersCompleted = myIntent.getStringExtra(TrainingActivity.TRAINERS_COMPLETED);
				trainersRequired = myIntent.getStringExtra(TrainingActivity.TRAINERS_REQUIRED);
				
			} else {	
				trainersCompleted = Integer.toString((Integer)mainFragment.response.get("trainers_completed"));
				trainersRequired = Integer.toString((Integer)mainFragment.response.get("trainers_required"));
			}
	        
	        intent.putExtra(TrainingActivity.TRAINERS_COMPLETED, trainersCompleted);
	        intent.putExtra(TrainingActivity.TRAINERS_REQUIRED, trainersRequired);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        startActivity(intent);
    }
    
    public void showLeaderboard(View view) {
    	Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
    	startActivity(intent);
    }
    
    public void makeAttempt(View view) {
    	Intent intent = new Intent(MainActivity.this, AttemptActivity.class);
    	startActivity(intent);
    }
}
