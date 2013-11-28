package com.assassin.mobile;

import java.util.ArrayList;
import java.util.List;
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
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;

public class AttemptActivity extends Activity {
	final Context context = this;
	private Button button;
	private JSONArray friends;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
	    		Bitmap bitmap = (Bitmap) data.getExtras().get("data");
		    	ImageView image = (ImageView) findViewById(R.id.imageResult); 
		    	image.setImageBitmap(bitmap);
		    	
		    	button = (Button) findViewById(R.id.buttonAlert);
		    	
		    	
		    	// add button listener
				button.setOnClickListener(new OnClickListener() {
		 
				@Override
				public void onClick(View arg0) {
		 
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
		 
					// set title
					alertDialogBuilder.setTitle("Who is this?");
		 
					
					
					// set dialog message
					alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, close
								// current activity
								AttemptActivity.this.finish();
							}
						  })
						.setNegativeButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						})
						.setAdapter(getVictimAdapter(), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
					               // The 'which' argument contains the index position
					               // of the selected item
					        }
						});
						
		 
						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();
		 
						// show it
						alertDialog.show();
					}
				});
			}
//		    	
//		    	addVictims();
//		    	addListenerOnButton();
	    	}
	    	 
	    }  
	
	private ListAdapter getVictimAdapter(){
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < this.friends.length(); i++) {
			  try {
				list.add((String) this.friends.getJSONObject(i).get("name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ListAdapter dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
//		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return dataAdapter;
	}
	
//	public void addListenerOnButton() {
//		  
//			victimSelector = (Spinner) findViewById(R.id.victimSelector);
//			btnSubmit = (Button) findViewById(R.id.btnSubmit);
//		 
//			btnSubmit.setOnClickListener(new OnClickListener() {
//		 
//			  @Override
//			  public void onClick(View v) {
//		 
//			    Toast.makeText(AttemptActivity.this,
//				"OnClickListener : " + 
//		                "\nSpinner 1 : "+ String.valueOf(victimSelector.getSelectedItem()) + 
//					Toast.LENGTH_SHORT, 0).show();
//			  }
//		 
//			});
//	}
		
	
	private void friendsCallback(JSONObject friends) {
		JSONArray friendsArray = new JSONArray();
		try {
			this.friends = friends.getJSONArray("friends");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(this.friends);
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
}
