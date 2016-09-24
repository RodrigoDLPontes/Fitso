package com.fitso.fitso;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MatchView {

	private LinearLayout linearLayout;

	public MatchView(Context context, ImageView firstIcon, ImageView secondIcon, ImageView thirdIcon, ImageView fourthIcon,
	                 ImageView fithIcon, String sport, double distance, String... users) {
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		linearLayout = (LinearLayout)layoutInflater.inflate(R.layout.match_view, null);
		((ImageView)linearLayout.findViewById(R.id.firstIcon)).setImageDrawable(firstIcon.getDrawable());
		((ImageView)linearLayout.findViewById(R.id.secondIcon)).setImageDrawable(secondIcon.getDrawable());
		((ImageView)linearLayout.findViewById(R.id.thirdIcon)).setImageDrawable(thirdIcon.getDrawable());
		((ImageView)linearLayout.findViewById(R.id.fourthIcon)).setImageDrawable(fourthIcon.getDrawable());
		((ImageView)linearLayout.findViewById(R.id.fifthIcon)).setImageDrawable(fithIcon.getDrawable());
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
			case "football":
				((ImageView)linearLayout.findViewById(R.id.activityIcon)).setImageResource(R.drawable.ic_football);
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
	}

}
