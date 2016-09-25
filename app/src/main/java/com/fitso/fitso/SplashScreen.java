package com.fitso.fitso;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		if(getSharedPreferences(Constants.sharedPreferencesFile, MODE_PRIVATE).getBoolean("HasBeenUsed", false)) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(SplashScreen.this, SignUpSelectionActivity.class);
					startActivity(intent);
				}
			}, 2000);
		}
	}
}
