package com.fitso.fitso;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

	LinearLayout topMatchesLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		topMatchesLinearLayout = (LinearLayout)findViewById(R.id.topMatchesLinearLayout);
	}
}
