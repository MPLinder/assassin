package com.assassin.mobile;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

public class AttemptActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, Constants.CAMERA_PIC_REQUEST);
		
		System.out.println("*****AttemptActivity onCreate called.");
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
	    	}
	    	 
	    }  
	}
}
