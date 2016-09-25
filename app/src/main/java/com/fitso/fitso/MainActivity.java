package com.fitso.fitso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

	LinearLayout topMatchesLinearLayout;
	LinearLayout nearYouLinearLayout;

	private GoogleApiClient mGoogleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		topMatchesLinearLayout = (LinearLayout)findViewById(R.id.topMatchesLinearLayout);
		nearYouLinearLayout = (LinearLayout)findViewById(R.id.nearYouLinearLayout);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		myToolbar.inflateMenu(R.menu.main_activity_menu);
		AccountHeader headerResult = new AccountHeaderBuilder()
				.withActivity(this)
				.withHeaderBackground(R.drawable.header)
				.addProfiles(
						new ProfileDrawerItem().withName("Level 5").withEmail("FitScore: 1123").withIcon(getResources().getDrawable(R.drawable.ic_testbubble))
				)
				.build();
		new DrawerBuilder()
				.withActivity(this)
				.withToolbar(myToolbar)
				.withAccountHeader(headerResult)
				.addDrawerItems(
						new PrimaryDrawerItem().withIdentifier(1).withName("History"),
						new PrimaryDrawerItem().withIdentifier(2).withName("Shop"),
						new PrimaryDrawerItem().withIdentifier(3).withName("Settings")
				)
				.build();
		mGoogleApiClient = new GoogleApiClient
				.Builder(this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.enableAutoManage(this, this)
				.build();
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location location = null;
		if(ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{"android.permission.ACCESS_FINE_LOCATION"},
					1);
		} else {
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(location == null) {
				location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if(location == null) {
					location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
				}
			}
			if(location != null) {
				final double latitude = location.getLatitude();
				final double longitude = location.getLongitude();
				new AsyncTask<Void, Object, Void>() {
					protected Void doInBackground(final Void... voids) {
						try {
							String email = getSharedPreferences(Constants.sharedPreferencesFile, MODE_PRIVATE).getString("Email", null);
							URL url = new URL("http://128.61.19.249:3000/match/browse?email=" + email + "&lat=" + latitude + "&lon=" + longitude);
							BufferedReader in = null;
							in = new BufferedReader(new InputStreamReader(url.openStream()));
							String str = in.readLine();
							in.close();
							Log.d("Debug", str);
							JSONObject json = new JSONObject(str);
							if(json.getString("err").equals("null")) {
								JSONArray scoreMatches = json.getJSONObject("res").getJSONArray("match");
								int limit = Math.min(5, scoreMatches.length());
								for(int i = 0; i < limit ; i++) {
									JSONObject match = scoreMatches.getJSONObject(i);
									JSONArray users = match.getJSONArray("people");
									int limit2 = Math.min(5, users.length());
									String[] names = new String[limit2];
									for(int j = 0; j < limit2 ; j++) {
										names[j] = users.getJSONObject(j).getString("name");
									}
									String sport = match.getString("sport");
									double distance = match.getDouble("dist");
									publishProgress(true, new MatchView(MainActivity.this, null, null, null, null, null, sport, distance, names));
								}
								JSONArray distanceMatches = json.getJSONObject("res").getJSONArray("dist");
								int limit3 = Math.min(5, distanceMatches.length());
								for(int i = 0; i < limit3 ; i++) {
									JSONObject match = distanceMatches.getJSONObject(i);
									JSONArray users = match.getJSONArray("people");
									int limit4 = Math.min(5, users.length());
									String[] names = new String[limit4];
									for(int j = 0; j < limit4 ; j++) {
										names[j] = users.getJSONObject(j).getString("name");
									}
									String sport = match.getString("sport");
									double distance = match.getDouble("dist");
									publishProgress(false, new MatchView(MainActivity.this, null, null, null, null, null, sport, distance, names));
								}
							}
						} catch (Exception e) {}
						return null;
					}

					protected void onProgressUpdate(final Object... objects) {
						if((Boolean)objects[0]) {
							topMatchesLinearLayout.addView(((MatchView)objects[1]).getLinearLayout());
						} else {
							nearYouLinearLayout.addView(((MatchView)objects[1]).getLinearLayout());
						}
					}
				}.execute();
			}
//			for(int i = 0 ; i < 5 ; i++) {
//				String[] names = {"Akarsh", "Rodrigo"};
//				topMatchesLinearLayout.addView(new MatchView(MainActivity.this, null, null, null, null, null, "soccer", 0.4, names).getLinearLayout());
//			}
//			for(int i = 0 ; i < 5 ; i++) {
//				String[] names = {"Akarsh", "Rodrigo"};
//				nearYouLinearLayout.addView(new MatchView(MainActivity.this, null, null, null, null, null, "soccer", 0.4, names).getLinearLayout());
//			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_menu, menu);
		return true;
	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 || requestCode == 2 || requestCode == 3 || requestCode == 4) {
			if (resultCode == RESULT_OK) {
				final Place place = PlacePicker.getPlace(data, this);
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(final Void... voids) {
						try {
							String email = getSharedPreferences(Constants.sharedPreferencesFile, MODE_PRIVATE).getString("Email", null);
							if(email == null) return null;
							String stringUrl = "http://128.61.19.249:3000/match/new?email=" + email + "&lat=" + place.getLatLng().latitude + "&lon=" + place.getLatLng().longitude;
							switch(requestCode) {
								case 1:
									stringUrl += "&sport=soccer";
									break;
								case 2:
									stringUrl += "&sport=basketball";
									break;
								case 3:
									stringUrl += "&sport=volleyball";
									break;
								case 4:
									stringUrl += "&sport=ultimate";
									break;
							}
							URL url = new URL(stringUrl);
							BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
							String str = in.readLine();
							in.close();
							JSONObject json = new JSONObject(str);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
				}.execute();
			}
		}
	}

	@Override
	public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {}

	public void createMatchButtonClicked(View view) {
		try {
			PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
			switch(view.getId()) {
				case R.id.soccerFloatingActionButton:
					startActivityForResult(builder.build(this), 1);
					break;
				case R.id.basketballFoatingActionButton:
					startActivityForResult(builder.build(this), 2);
					break;
				case R.id.volleyballFloatingActionButton:
					startActivityForResult(builder.build(this), 3);
					break;
				case R.id.ultimateFrisbeeFloatingActionButton:
					startActivityForResult(builder.build(this), 4);
					break;
			}
		} catch(GooglePlayServicesRepairableException e) {
			e.printStackTrace();
		} catch(GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
	}
}
