package com.assassin.mobile;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;

public class TrainingActivity extends Activity {
	public static String TRAINERS_REQUIRED = "trainersRequired";
	public static String TRAINERS_COMPLETED = "trainersCompleted";

	private String trainerUri;
	private ProgressBar progressBar;
	private TextView trainingInstructions;
	private Boolean isTrained;
	private String trainersRequired;
	private String trainersCompleted;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);
		progressBar = (ProgressBar) findViewById(R.id.training_progress_bar);
		isTrained = false;
		
        Intent intent = getIntent();
        
        trainersRequired = "";
        trainersCompleted = "";
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
        
        trainingInstructions = (TextView) findViewById(R.id.trainingInstructions);
        trainingInstructions.setText(instructions);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.training, menu);
		return true;
	}
	
	final Handler trainingViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	
        	String instructions = "Congratulations, you completed a training session! ";
        	
        	if (isTrained == true) {
        		instructions += "You're training is now complete! Start assassinating now!";
        		
        		Button assassinateButton = (Button) findViewById(R.id.trainAssassinate);
        		assassinateButton.setVisibility(View.VISIBLE);
        		
        		Button trainButton = (Button) findViewById(R.id.train);
        		trainButton.setVisibility(View.GONE);
        	} else {
        		instructions += "You have now completed " + trainersCompleted + " of the " + 
        	    trainersRequired + " required training sessions! You're almost there! Keep going!";	
        	}
        	
        	trainingInstructions.setText(instructions);
    		progressBar.setVisibility(View.GONE);
        }
    };

    public void menu(View view) {
    	Intent intent = new Intent(TrainingActivity.this, MainActivity.class);
    	intent.putExtra(TRAINERS_REQUIRED, this.trainersRequired);
    	intent.putExtra(TRAINERS_COMPLETED, this.trainersCompleted);
    	
    	startActivity(intent);
    }
    
    public void train (View view) {
		File tempDir = getExternalFilesDir(null);
		tempDir.mkdirs();
		
		this.trainerUri = tempDir.toString() + "/" + "trainer.jpg";
		
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		File imageFile = new File(this.trainerUri);
		Uri uriSavedImage = Uri.fromFile(imageFile);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
		startActivityForResult(cameraIntent, Constants.CAMERA_PIC_REQUEST);
    }
    
    public void makeAttempt(View view) {
    	Intent intent = new Intent(TrainingActivity.this, AttemptActivity.class);
    	startActivity(intent);
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		super.onActivityResult(requestCode, resultCode, data);
	    
		if (requestCode == Constants.CAMERA_PIC_REQUEST) {  
	    	if (resultCode == RESULT_OK) {
	    		progressBar.setVisibility(View.VISIBLE);
	    		sendTrainer();
	    	}
		}
	}
	
    public void sendTrainer() {    		
		new Thread(new Runnable() {
			public void run() {
				String URI = "train/";
				Session session = Session.getActiveSession();
				
				if(session==null){                      
				    // try to restore from cache
					System.out.println("******retreiving session from cache");
				    session = Session.openActiveSessionFromCache(TrainingActivity.this);
				}

				
				String accessToken = session.getAccessToken();
				System.out.println("******access token inside training: " + accessToken);
				
				JSONObject output = null;
				try {
					String outputStr = (String) new ImageUploadTask().execute(trainerUri, URI, accessToken, null).get();
					output = new JSONObject(outputStr);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(TrainingActivity.this, R.string.errorTryAgain, Toast.LENGTH_LONG).show();
					finish();
				}
				
				if (output == null) {
					Toast.makeText(TrainingActivity.this, R.string.errorTryAgain, Toast.LENGTH_LONG).show();					
					progressBar.setVisibility(View.GONE);
				} else {					
					try {
						trainersRequired = Integer.toString((Integer)output.get("trainers_required"));
						trainersCompleted = Integer.toString((Integer)output.get("trainers_completed"));
						isTrained = Utils.isTrained(output);
						trainingViewHandler.sendEmptyMessage(0);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(TrainingActivity.this, R.string.errorTryAgain, Toast.LENGTH_LONG).show();
					}
				}
			}
		}).start();
	}
}