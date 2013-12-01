package com.assassin.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TrainingActivity extends Activity {
	public static String TRAINERS_REQUIRED = "trainersRequired";
	public static String TRAINERS_COMPLETED = "trainersCompleted";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);
		
        Intent intent = getIntent();
        
        String trainersRequired = "";
        String trainersCompleted = "";
        if (intent.hasExtra(TRAINERS_REQUIRED) && 
        	intent.hasExtra(TRAINERS_COMPLETED)) {
        	
            trainersRequired = intent.getStringExtra(TRAINERS_REQUIRED);
        	trainersCompleted = intent.getStringExtra(TRAINERS_COMPLETED);
        } else {
			Toast.makeText(this, R.string.attemptFailed, Toast.LENGTH_LONG).show();
			finish();
        }
		
        String instructions = "Assassin! requires at least " + trainersRequired + " training images " +
        "in order to recognize you. Training images should be close up images of your face in good light. " +
        "So far you have completed " + trainersCompleted + " training session(s). Train yourself up now!";
        
        TextView trainingInstructions = (TextView) findViewById(R.id.trainingInstructions);
        trainingInstructions.setText(instructions);

		
		
		
		
		
		
		
		
//		// TODO: this is just a proof of concept, don't actually do this
//		String imageLocation = "/storage/sdcard0/DCIM/browser-photos/1384022398651.jpg";
//		String URI = "attempt/";
//		Session session = Session.getActiveSession();
//		String accessToken = session.getAccessToken();
//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("to_user", "1");
//		try {
//			String output = (String) new ImageUploadTask().execute(imageLocation, URI, accessToken, params).get();
//			System.out.println("****Image Upload output: " + output);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.training, menu);
		return true;
	}

	
    public void menu(View view) {
    	// TODO: pass training numbers back
    	Intent intent = new Intent(TrainingActivity.this, MainActivity.class);
    	startActivity(intent);
    }
    
    public void train (View view) {
    	;
    }
}