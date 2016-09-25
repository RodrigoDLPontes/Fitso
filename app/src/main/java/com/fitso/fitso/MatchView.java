package com.fitso.fitso;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MatchView {

	private LinearLayout linearLayout;

	public MatchView(final Context context, ImageView firstIcon, ImageView secondIcon, ImageView thirdIcon, ImageView fourthIcon,
	                 ImageView fithIcon, String sport, double distance, String[] users, final String id) {
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		linearLayout = (LinearLayout)layoutInflater.inflate(R.layout.match_view, null);
		if(firstIcon != null) ((ImageView)linearLayout.findViewById(R.id.firstIcon)).setImageDrawable(firstIcon.getDrawable());
		if(secondIcon != null) ((ImageView)linearLayout.findViewById(R.id.secondIcon)).setImageDrawable(secondIcon.getDrawable());
		if(thirdIcon != null) ((ImageView)linearLayout.findViewById(R.id.thirdIcon)).setImageDrawable(thirdIcon.getDrawable());
		if(fourthIcon != null) ((ImageView)linearLayout.findViewById(R.id.fourthIcon)).setImageDrawable(fourthIcon.getDrawable());
		if(fithIcon != null) ((ImageView)linearLayout.findViewById(R.id.fifthIcon)).setImageDrawable(fithIcon.getDrawable());
		switch(sport) {
			case "basketball":
				((ImageView)linearLayout.findViewById(R.id.activityIcon)).setImageResource(R.drawable.ic_basketball);
				break;
			case "biking":
				((ImageView)linearLayout.findViewById(R.id.activityIcon)).setImageResource(R.drawable.ic_bike);
				break;
			case "bodyweight":
				((ImageView)linearLayout.findViewById(R.id.activityIcon)).setImageResource(R.drawable.ic_body_weight);
				break;
			case "crossfit":
				((ImageView)linearLayout.findViewById(R.id.activityIcon)).setImageResource(R.drawable.ic_crossfit);
				break;
			case "soccer":
				((ImageView)linearLayout.findViewById(R.id.activityIcon)).setImageResource(R.drawable.ic_soccer);
				break;
			case "ultimate":
				((ImageView)linearLayout.findViewById(R.id.activityIcon)).setImageResource(R.drawable.ic_frisbee);
				break;
			case "running":
				((ImageView)linearLayout.findViewById(R.id.activityIcon)).setImageResource(R.drawable.ic_run);
				break;
			case "swimming":
				((ImageView)linearLayout.findViewById(R.id.activityIcon)).setImageResource(R.drawable.ic_swimming);
				break;
			case "volleyball":
				((ImageView)linearLayout.findViewById(R.id.activityIcon)).setImageResource(R.drawable.ic_volleyball);
				break;
			case "weightlifting":
				((ImageView)linearLayout.findViewById(R.id.activityIcon)).setImageResource(R.drawable.ic_weights);
				break;
		}
		((TextView)linearLayout.findViewById(R.id.matchDistanceTextView)).setText(String.format("%.2f mi", distance));
		StringBuilder titleBuilder = new StringBuilder();
		int counter = users.length;
		for(String user : users) {
			if(user == null) break;
			if((titleBuilder.toString() + user + ", ").length() < 20) {
				titleBuilder.append(user).append(", ");
				counter--;
			} else {
				break;
			}
		}
		((TextView)linearLayout.findViewById(R.id.matchTitleTextView))
				.setText(titleBuilder.substring(0, titleBuilder.length() - 2) +
						(counter != 0 ? " +" + counter : ""));
		linearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(final Void... voids) {
						try {
							String email = context.getSharedPreferences(Constants.sharedPreferencesFile, Context.MODE_PRIVATE).getString("Email", null);
							URL url = new URL("http://128.61.19.249:3000/match/join?email=" + email + "&matchid=" + id);
							BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
							String str = in.readLine();
							in.close();
							JSONObject json = new JSONObject(str);
						} catch(IOException e) {} catch(JSONException e) {}
						return null;
					}
				}.execute();
			}
		});
	}

	public LinearLayout getLinearLayout() {
		return linearLayout;
	}

}
