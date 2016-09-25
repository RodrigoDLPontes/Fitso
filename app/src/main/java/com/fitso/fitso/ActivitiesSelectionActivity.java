package com.fitso.fitso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ActivitiesSelectionActivity extends Activity {

	TextView greetingsTextView;
	ImageView bodyweightTile;
	ImageView weightLiftingTile;
	ImageView crossfitTile;
	ImageView runningTile;
	ImageView swimmingTile;
	ImageView bikingTile;
	ImageView basketballTile;
	ImageView soccerTile;
	ImageView ultimateTile;
	ImageView volleyballTile;

	Boolean[] selectedActivities;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activities_selection);
		greetingsTextView = (TextView)findViewById(R.id.greetingsTextView);
		bodyweightTile = (ImageView)findViewById(R.id.bodyweightTile);
		weightLiftingTile = (ImageView)findViewById(R.id.weightLiftingTile);
		crossfitTile = (ImageView)findViewById(R.id.crossfitTile);
		runningTile = (ImageView)findViewById(R.id.runningTile);
		swimmingTile = (ImageView)findViewById(R.id.swimmingTile);
		bikingTile = (ImageView)findViewById(R.id.bikingTile);
		basketballTile = (ImageView)findViewById(R.id.basketballTile);
		soccerTile = (ImageView)findViewById(R.id.soccerTile);
		ultimateTile = (ImageView)findViewById(R.id.ultimateTile);
		volleyballTile = (ImageView)findViewById(R.id.volleyballTile);
		selectedActivities = new Boolean[20];
		for(int i = 0; i < 20; i++) {
			selectedActivities[i] = false;
		}
		String name = getIntent().getStringExtra("Name");
		greetingsTextView.setText("Hi, " + name + "!\nWhat do you want to do?");
//		Display display = getWindowManager().getDefaultDisplay();
//		Point size = new Point();
//		display.getSize(size);
//		int width = size.x;
//		int height = size.y;
//		bodyweightTile.measure(width, height);
//		weightLiftingTile.measure(width, height);
//		crossfitTile.measure(width, height);
//		runningTile.measure(width, height);
//		swimmingTile.measure(width, height);
//		bikingTile.measure(width, height);
//		basketballTile.measure(width, height);
//		soccerTile.measure(width, height);
//		footballTile.measure(width, height);
//		volleyballTile.measure(width, height);
//		((LinearLayout.LayoutParams)bodyweightTile.getLayoutParams()).height = bodyweightTile.getMeasuredWidth();
//		((LinearLayout.LayoutParams)weightLiftingTile.getLayoutParams()).height = weightLiftingTile.getMeasuredWidth();
//		((LinearLayout.LayoutParams)crossfitTile.getLayoutParams()).height = crossfitTile.getMeasuredWidth();
//		((LinearLayout.LayoutParams)runningTile.getLayoutParams()).height = runningTile.getMeasuredWidth();
//		((LinearLayout.LayoutParams)swimmingTile.getLayoutParams()).height = swimmingTile.getMeasuredWidth();
//		((LinearLayout.LayoutParams)bikingTile.getLayoutParams()).height = bikingTile.getMeasuredWidth();
//		((LinearLayout.LayoutParams)basketballTile.getLayoutParams()).height = basketballTile.getMeasuredWidth();
//		((LinearLayout.LayoutParams)soccerTile.getLayoutParams()).height = soccerTile.getMeasuredWidth();
//		((LinearLayout.LayoutParams)footballTile.getLayoutParams()).height = footballTile.getMeasuredWidth();
//		((LinearLayout.LayoutParams)volleyballTile.getLayoutParams()).height = volleyballTile.getMeasuredWidth();
	}

	public void activitySelected(View view) {
		if(view.getAlpha() == 1.0f) {
			view.setAlpha(0.5f);
		} else {
			view.setAlpha(1.0f);
		}
		switch(view.getId()) {
			case R.id.bodyweightTile:
				selectedActivities[0] = !selectedActivities[0];
				break;
			case R.id.weightLiftingTile:
				selectedActivities[1] = !selectedActivities[1];
				break;
			case R.id.crossfitTile:
				selectedActivities[2] = !selectedActivities[2];
				break;
			case R.id.runningTile:
				selectedActivities[3] = !selectedActivities[3];
				break;
			case R.id.swimmingTile:
				selectedActivities[4] = !selectedActivities[4];
				break;
			case R.id.bikingTile:
				selectedActivities[5] = !selectedActivities[5];
				break;
			case R.id.basketballTile:
				selectedActivities[6] = !selectedActivities[6];
				break;
			case R.id.soccerTile:
				selectedActivities[7] = !selectedActivities[7];
				break;
			case R.id.ultimateTile:
				selectedActivities[8] = !selectedActivities[8];
				break;
			case R.id.volleyballTile:
				selectedActivities[9] = !selectedActivities[9];
				break;
		}
	}

	public void activitiesSelectionConfirmButtonClicked(View view) {
		boolean activitySelected = false;
		for(Boolean value : selectedActivities) {
			if(value) activitySelected = true;
		}
		if(!activitySelected) {
			Toast.makeText(this, "Please select an activity first!", Toast.LENGTH_LONG).show();
			return;
		}
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(final Void... voids) {
				try {
					String urlString = "http://128.61.19.249:3000/user/groups?email=" + getIntent().getStringExtra("Email");
					if(selectedActivities[0] || selectedActivities[1] || selectedActivities[2]) {
						urlString += "&g=";
						if(selectedActivities[0]) urlString += "bodyweight,";
						if(selectedActivities[1]) urlString += "weightlifting,";
						if(selectedActivities[2]) urlString += "crossfit,";
						urlString = urlString.substring(0, urlString.length() - 1);
					} else if(selectedActivities[3] || selectedActivities[4] || selectedActivities[5]) {
						urlString += "&c=";
						if(selectedActivities[3]) urlString += "running,";
						if(selectedActivities[4]) urlString += "swimming,";
						if(selectedActivities[5]) urlString += "biking,";
						urlString = urlString.substring(0, urlString.length() - 1);
					} else if(selectedActivities[6] || selectedActivities[7] || selectedActivities[8] || selectedActivities[9]) {
						urlString += "&s=";
						if(selectedActivities[6]) urlString += "basketball,";
						if(selectedActivities[7]) urlString += "soccer,";
						if(selectedActivities[8]) urlString += "ultimate,";
						if(selectedActivities[9]) urlString += "volleyball,";
						urlString = urlString.substring(0, urlString.length() - 1);
					}
					URL url = new URL(urlString);
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					String str = in.readLine();
					in.close();
					JSONObject json = new JSONObject(str);
					if(json.getString("err") != null) {
						Intent intent = new Intent(ActivitiesSelectionActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
					}
				} catch (Exception e) {}
				return null;
			}
		}.execute();
	}
}
