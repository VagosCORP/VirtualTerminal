package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Device_List extends Activity {

	ListView LD;
	String[] LDev;
	ArrayAdapter<String> Adapter;
	OnItemClickListener IS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent SD = getIntent();
		LDev = SD.getStringArrayExtra(PrincipalActivity.LD);
		setContentView(R.layout.device_list);
		LD = (ListView) findViewById(R.id.LD);
		Adapter = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, LDev);
		IS = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent SelDev = new Intent(PrincipalActivity.RESULT_ACTION);
				SelDev.putExtra(PrincipalActivity.SDev, position);
				setResult(Activity.RESULT_OK, SelDev);
				finish();
			}

		};
		LD.setAdapter(Adapter);
		LD.setOnItemClickListener(IS);
	}
}