package com.assassin.mobile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.facebook.Session;

public class AttemptActivity extends Activity {
	final Context context = this;
	private JSONArray friends;
	private ArrayList<String> friendPics;
	private Bitmap attempt;
	private String attemptUri;
	private Integer which;
	private String name;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attempt);
		progressBar = (ProgressBar) findViewById(R.id.progress_bar); 
		
		getFriends();
		
		File tempDir = getExternalFilesDir(null);
		tempDir.mkdirs();
		
		this.attemptUri = tempDir.toString() + "/" + "attempt.jpg";
		
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		File imageFile = new File(this.attemptUri);
		Uri uriSavedImage = Uri.fromFile(imageFile);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
		startActivityForResult(cameraIntent, Constants.CAMERA_PIC_REQUEST);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.attempt, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == Constants.CAMERA_PIC_REQUEST) {  
	    	if (resultCode == RESULT_OK) {
	    		this.attempt = Utils.getBitmap(this.attemptUri);
	    		
	    		while (this.friendPics == null || this.friendPics.size() < this.friends.length()) {
	    			progressBar.setVisibility(View.VISIBLE);
	    			;
	    		}
	    		progressBar.setVisibility(View.GONE);
	    		
	    		
		    	ImageView image = (ImageView) findViewById(R.id.imageResult); 
		    	image.setImageBitmap(this.attempt);
		 
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
	 
				// set title
				alertDialogBuilder.setTitle(R.string.whoIsThis);
				
				alertDialogBuilder
					.setSingleChoiceItems(getVictimAdapter(), -1, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// The 'which' argument contains the index position
							// of the selected item
							AttemptActivity.this.which = which;
							try {
								AttemptActivity.this.name = (String) AttemptActivity.this.friends.getJSONObject(which).get("name");
							} catch (JSONException e) {
								e.printStackTrace();
								AttemptActivity.this.name = "";
							}
						}
					})
					.setCancelable(false)
					.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							if (AttemptActivity.this.which != null) {

						    	ImageView image = (ImageView) findViewById(R.id.imageResult); 
						    	
						    	image.setVisibility(View.GONE);
						    	progressBar.setVisibility(View.VISIBLE);
								
								sendAttempt();
							} else {
								Toast.makeText(context, "No target selected. Assassiation attempt aborted.", Toast.LENGTH_LONG).show();
								finish();
							}
						}
					  })
					.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, just close
							// the dialog box and do nothing
							dialog.cancel();
							finish();
						}
					});

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
	    	} else {
	    		finish();
	    	}
		};
	 
	}  
	
	private ListAdapter getVictimAdapter(){
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (int i = 0; i < this.friends.length(); i++) {
			  try {
				  HashMap<String, String> data = new HashMap<String, String>();
				  String name = (String) this.friends.getJSONObject(i).get("name");
				  data.put("name", name);
				  String id = (String) this.friends.getJSONObject(i).get("id");
				  data.put("id", id);

				  data.put("picture", friendPics.get(i));
				  
				  list.add(data);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String[] from = {"name", "id", "picture"};
		int[] to = {R.id.name, R.id.user_id, R.id.picture};
		ListAdapter dataAdapter = new SimpleAdapter(this, list, R.layout.friend_list, from, to);
		return dataAdapter;
	}
			
	private void friendsCallback(JSONObject friends) {
		try {
			this.friends = friends.getJSONArray("friends");
			this.friendPics = new ArrayList<String>();
			
			for (int i = 0; i < this.friends.length(); i++) {
				String picture = (String) this.friends.getJSONObject(i).get("picture");
				  
				String bitmap = Utils.downloadBitmap(picture, getCacheDir().toString());

				if (bitmap != null) {
					this.friendPics.add(String.valueOf(bitmap));
				} else {
					this.friendPics.add(String.valueOf(R.drawable.assassin_launcher));
				}
				
			}
			
			// For testing only. Increases number of entries in AlertDialog
//			for (int i=0; i<20; i++) {
//				this.friends.put(this.friends.get(0));
//				this.friendPics.add(this.friendPics.get(0));
//			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void getFriends() {
		new Thread(new Runnable() {
			public void run() {
				JSONObject response = null;
				try {
					response = new CallServerTask().execute("friends/", "true", "GET").get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				friendsCallback(response);
			}
		}).start();
	}
	
    public void sendAttempt() {    		
		new Thread(new Runnable() {
			public void run() {
				String URI = "attempt/";
				Session session = Session.getActiveSession();
				
				if(session==null){                      
				    // try to restore from cache
				    session = Session.openActiveSessionFromCache(AttemptActivity.this);
				}
				
				String accessToken = session.getAccessToken();
				HashMap<String, String> params = new HashMap<String, String>();
				try {
					String to_user = (String)friends.getJSONObject(which).get("id");
					params.put("to_user", to_user);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					// TODO: actually throw error here
					e1.printStackTrace();
				}
				
				JSONObject output = null;
				try {
					String outputStr = (String) new ImageUploadTask().execute(attemptUri, URI, accessToken, params).get();
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
				}
				
				// TODO text if output is null?
				if (output == null) {
					AttemptActivity.this.runOnUiThread(new Runnable() {
					    public void run() {
					        Toast.makeText(AttemptActivity.this, R.string.errorTryAgain, Toast.LENGTH_LONG).show();
					    }
					});
					
					new File(attemptUri).delete();
					attempt.recycle();
					finish();
				} else {
					attemptOutputCallback(output);
				}
			}
		}).start();
	}
    
    public void attemptOutputCallback(JSONObject output) {
    	
    	String confidenceLevel = "";
    	Boolean success = null;
    	Integer successPoints = 0;
    	try {
			confidenceLevel = (String)output.get("confidence_level");
			success = (Boolean)output.get("success");
			successPoints = (Integer)output.get("success_points");
		} catch (JSONException e) {
			e.printStackTrace();
			
			Toast.makeText(context, R.string.attemptFailed, Toast.LENGTH_LONG).show();
			new File(attemptUri).delete();
			this.attempt.recycle();
			finish();
		}
    	
    	Intent intent = new Intent(this, ResultActivity.class);
    	intent.putExtra(ResultActivity.ATTEMPT_URI, this.attemptUri);
    	intent.putExtra(ResultActivity.TO_USER, this.name);
    	intent.putExtra(ResultActivity.CONFIDENCE_LEVEL, confidenceLevel);
    	intent.putExtra(ResultActivity.SUCCESSFUL_ATTEMPT, success);
    	intent.putExtra(ResultActivity.SUCCESS_POINTS, Integer.toString(successPoints));
    	    	
    	startActivity(intent);
    }
}
