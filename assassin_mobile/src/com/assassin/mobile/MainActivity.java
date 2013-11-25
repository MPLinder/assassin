package com.assassin.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.Button;

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
        startActivity(intent);
    }
    
    public void showLeaderboard(View view) {
        // TODO: start leaderboard view when I have it
    	;
    }
    
    public void startCamera(View view) {
        // TODO: start camera
    	;
    }
}
