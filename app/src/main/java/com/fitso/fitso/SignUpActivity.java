package com.fitso.fitso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

	EditText nameEditText;
	EditText emailEditText;
	EditText passwordEditText;
	EditText confirmPasswordEditText;
	EditText ageEditText;
	RadioGroup genderRadioGroup;
	Button confirmButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		nameEditText = (EditText)findViewById(R.id.nameEditText);
		emailEditText = (EditText)findViewById(R.id.emailEditText);
		passwordEditText = (EditText)findViewById(R.id.passwordEditText);
		confirmPasswordEditText = (EditText)findViewById(R.id.confirmPasswordEditText);
		ageEditText = (EditText)findViewById(R.id.ageEditText);
		genderRadioGroup = (RadioGroup)findViewById(R.id.genderRadioGroup);
		confirmButton = (Button)findViewById(R.id.confirmButton);
	}

	public void signUpConfirmButtonClicked(View view) {
		if(!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
			Toast.makeText(this, "Password and confirm password don't match!", Toast.LENGTH_LONG).show();
			return;
		}
		final String name = nameEditText.getText().toString();
		final String email = emailEditText.getText().toString();
		final String age = ageEditText.getText().toString();
		String gender = "m";
		switch(genderRadioGroup.getCheckedRadioButtonId()) {
			case R.id.maleRadioButton:
				gender = "m";
				break;
			case R.id.femaleRadioButton:
				gender = "f";
				break;
			case R.id.otherRadioButton:
				gender = "o";
				break;
		}
		new AsyncTask<String, Void, Void>() {
			protected Void doInBackground(final String... fields) {
				try {
					URL url = new URL("http://128.61.19.249:3000/user/new?name=" + fields[0] + "&email=" + fields[1] + "&age=" + fields[2] + "&gender=" + fields[3]);
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					String str = in.readLine();
					in.close();
					JSONObject json = new JSONObject(str);
//					if(json.getString("err") != null) {
						getSharedPreferences(Constants.sharedPreferencesFile, MODE_PRIVATE).edit().putString("Email", email);
						Intent intent = new Intent(SignUpActivity.this, ActivitiesSelectionActivity.class);
						intent.putExtra("Name", name);
						intent.putExtra("Email", email);
						startActivity(intent);
//					} else {
//						Toast.makeText(SignUpActivity.this, "Fields invalid!", Toast.LENGTH_LONG).show();
//					}
				} catch (Exception e) {}
				return null;
			}
		}.execute(name, email, age, gender);
	}
}
