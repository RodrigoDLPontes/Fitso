package com.fitso.fitso;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

	LinearLayout topMatchesLinearLayout;

	private GoogleApiClient mGoogleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		topMatchesLinearLayout = (LinearLayout)findViewById(R.id.topMatchesLinearLayout);
		mGoogleApiClient = new GoogleApiClient
				.Builder(this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.enableAutoManage(this, this)
				.build();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				final Place place = PlacePicker.getPlace(data, this);
				new AsyncTask<Place, Void, Void>() {
					protected Void doInBackground(final Place... places) {
						try {
							String email = getSharedPreferences(Constants.sharedPreferencesFile, MODE_PRIVATE).getString("Email", null);
							if(email == null) return null;
							URL url = new URL("http://128.61.19.249:3000/match/new?lat=" + place.getLatLng().latitude + "&lon=" + place.getLatLng().longitude);
							BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
							String str = in.readLine();
							in.close();
							JSONObject json = new JSONObject(str);
						} catch (Exception e) {}
						return null;
					}
				}.execute(place);
			}
		}
	}

	@Override
	public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {}

	public void createMatchButtonClicked(View view) {
		try {
			PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
			startActivityForResult(builder.build(this), 1);
		} catch(GooglePlayServicesRepairableException e) {
			e.printStackTrace();
		} catch(GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
	}
}
