package com.assassin.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

public class LeaderboardActivity extends Activity {

	private JSONArray leaderboard;
	private JSONObject user_info;
	private ArrayList<String> leaderboardPics;
	private String userPic;
	
	private ListView leaderboardView;
	private ListView userLeaderboardView;
	private ProgressBar progressBarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);
				
		leaderboardView = (ListView) findViewById(R.id.leaderboard);
		userLeaderboardView = (ListView) findViewById(R.id.userPoints);
		progressBarView = (ProgressBar) findViewById(R.id.lb_progress_bar);
		
		leaderboardView.setVisibility(View.GONE);
		userLeaderboardView.setVisibility(View.GONE);
		progressBarView.setVisibility(View.VISIBLE);
		
		getLeaderboard();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.leaderboard, menu);
		return true;
	}
	
	final Handler lbViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
    		leaderboardView.setVisibility(View.VISIBLE);
    		userLeaderboardView.setVisibility(View.VISIBLE);
    		progressBarView.setVisibility(View.GONE);
    		
    		leaderboardView.setAdapter(leaderboardAdapter());
    		userLeaderboardView.setAdapter(userLeaderboardAdapter());
        }
    };
	
	private void getLeaderboard() {
		new Thread(new Runnable() {
			public void run() {
				JSONObject response = null;
				try {
					response = new CallServerTask().execute("leaderboard/", "true", "GET").get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				leaderboardCallback(response);
			}
		}).start();
	}
	
	private void leaderboardCallback(JSONObject leaderboard) {
		try {
			this.leaderboard = leaderboard.getJSONArray("friends");
			this.user_info = leaderboard.getJSONObject("user");
			this.leaderboardPics = new ArrayList<String>();
			
			String picture = (String) this.user_info.get("picture");
			String bitmap = Utils.downloadBitmap(picture, getCacheDir().toString());
			this.userPic = String.valueOf(bitmap);
			
			for (int i = 0; i < this.leaderboard.length(); i++) {
				picture = (String) this.leaderboard.getJSONObject(i).get("picture");
				  
				bitmap = Utils.downloadBitmap(picture, getCacheDir().toString());

				if (bitmap != null) {
					this.leaderboardPics.add(String.valueOf(bitmap));
				} else {
					this.leaderboardPics.add(String.valueOf(R.drawable.assassin_launcher));
				}
			}
			
//			// For testing only. Increases number of entries in AlertDialog
//			for (int i=0; i<20; i++) {
//				this.leaderboard.put(this.leaderboard.get(0));
//				this.leaderboardPics.add(this.leaderboardPics.get(0));
//			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lbViewHandler.sendEmptyMessage(0);
		
	}

	private ListAdapter leaderboardAdapter() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (int i = 0; i < this.leaderboard.length(); i++) {
			  try {
				  HashMap<String, String> data = new HashMap<String, String>();
				  String name = (String) this.leaderboard.getJSONObject(i).get("name");
				  data.put("name", name);
				  String id = (String) this.leaderboard.getJSONObject(i).get("id");
				  data.put("id", id);
				  Integer points = (Integer) this.leaderboard.getJSONObject(i).get("points");
				  data.put("points", Integer.toString(points));

				  data.put("picture", leaderboardPics.get(i));
				  
				  list.add(data);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String[] from = {"name", "id", "picture", "points"};
		int[] to = {R.id.leaderboardName, R.id.leaderboardUser_id, R.id.leaderboardPicture, R.id.leaderboardPoints};
		ListAdapter dataAdapter = new SimpleAdapter(this, list, R.layout.leaderboard_entry, from, to);
		return dataAdapter;
	}
	
	private ListAdapter userLeaderboardAdapter() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

			try {
				  HashMap<String, String> data = new HashMap<String, String>();
				  String name = (String) this.user_info.get("name");
				  data.put("name", name);
				  String id = (String) this.user_info.get("id");
				  data.put("id", id);
				  Integer points = (Integer) this.user_info.get("points");
				  data.put("points", Integer.toString(points));

				  data.put("picture", userPic);
				  
				  list.add(data);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		String[] from = {"name", "id", "picture", "points"};
		int[] to = {R.id.leaderboardName, R.id.leaderboardUser_id, R.id.leaderboardPicture, R.id.leaderboardPoints};
		ListAdapter dataAdapter = new SimpleAdapter(this, list, R.layout.leaderboard_entry, from, to);
		return dataAdapter;
	}
	
    public void makeAttempt(View view) {
    	Intent intent = new Intent(LeaderboardActivity.this, AttemptActivity.class);
    	startActivity(intent);
    }
    
    public void menu(View view) {
    	Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
    	startActivity(intent);
    }
	
}
