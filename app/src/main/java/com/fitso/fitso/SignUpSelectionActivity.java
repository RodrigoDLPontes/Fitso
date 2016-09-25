package com.fitso.fitso;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SignUpSelectionActivity extends Activity {

	ImageButton facebookSignUpButton;
	ImageButton googlePlusSignUpButton;
	ImageButton fitsoSignUpButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_selection);
		facebookSignUpButton = (ImageButton)findViewById(R.id.facebookSignUpButton);
		googlePlusSignUpButton = (ImageButton)findViewById(R.id.googlePlusSignUpButton);
		fitsoSignUpButton = (ImageButton)findViewById(R.id.fitsoSignUpButton);
	}

	public void facebookSignUpButtonClicked(View view) {

	}

	public void googlePlusSignUpButtonClicked(View view) {

	}

	public void fitsoSignUpButtonClicked(View view) {
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
	}

}
