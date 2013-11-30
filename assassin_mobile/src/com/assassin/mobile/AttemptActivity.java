package com.assassin.mobile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.facebook.Session;

public class AttemptActivity extends Activity {
	final Context context = this;
	private Button button;
	private JSONArray friends;
	private ArrayList<String> friendPics;
	private Bitmap attempt;
	private String attemptUri;
	private Integer which;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.attemptUri = getCacheDir().toString() + "/" + "attempt.jpg";
		getFriends();
		
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
		setContentView(R.layout.activity_attempt);
	    if (requestCode == Constants.CAMERA_PIC_REQUEST) {  
	    	if (resultCode == RESULT_OK) {
	    		this.attempt = (Bitmap) data.getExtras().get("data");

				try {
					FileOutputStream out = new FileOutputStream(this.attemptUri);
	                this.attempt.compress(Bitmap.CompressFormat.PNG, 100, out);
	                out.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println("****error saving file");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("****error saving file");
					e.printStackTrace();
				}

		    	ImageView image = (ImageView) findViewById(R.id.imageResult); 
		    	image.setImageBitmap(this.attempt);
		    	
		    	button = (Button) findViewById(R.id.buttonAlert);
		    	
		    	// add button listener
				button.setOnClickListener(new OnClickListener() {
		 
				@Override
				public void onClick(View arg0) {
		 
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
		 
					// set title
					alertDialogBuilder.setTitle(R.string.whoIsThis);
					
					alertDialogBuilder
						.setSingleChoiceItems(getVictimAdapter(), -1, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// The 'which' argument contains the index position
								// of the selected item
								AttemptActivity.this.which = which;
								System.out.println("****List item selected: " + which);
							}
						})
						.setCancelable(false)
						.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								if (AttemptActivity.this.which != null) {
									Button sendAttempt = (Button) findViewById(R.id.sendAttempt);
									sendAttempt.setVisibility(View.VISIBLE);
								}
							}
						  })
						.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});

						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();
		 
						// show it
						alertDialog.show();
					}
				});
			} else {
				finish();
			}
	    }	 
	}  
	
	private ListAdapter getVictimAdapter(){
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (int i = 0; i < this.friends.length(); i++) {
			  try {
				  HashMap<String, String> data = new HashMap<String, String>();
				  String name = (String) this.friends.getJSONObject(i).get("name");
				  data.put("name", name);
				  Integer id = (Integer) this.friends.getJSONObject(i).get("id");
				  data.put("id", Integer.toString(id));

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
	
    public void sendAttempt(View view) {
    	System.out.println("******which: " + this.which);
    	System.out.println("****attempt: " + this.attempt);
    	setContentView(R.layout.progressbar_activity);
    	
		String URI = "attempt/";
		Session session = Session.getActiveSession();
		String accessToken = session.getAccessToken();
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			Integer to_user = (Integer)this.friends.getJSONObject(this.which).get("id");
			System.out.println("****to user: " + Integer.toString(to_user));
			params.put("to_user", Integer.toString(to_user));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			// TODO: actually throw error here
			e1.printStackTrace();
		}
		
		try {
			System.out.println("****attemptUri: " + this.attemptUri);
			System.out.println("****upload uri: " + URI);
			String output = (String) new ImageUploadTask().execute(this.attemptUri, URI, accessToken, params).get();
			System.out.println("****Image Upload output: " + output);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
