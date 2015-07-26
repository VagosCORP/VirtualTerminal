package com.vagoscorp.virtualterminal;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class InstructionsActivity extends Activity {

    TextView InspVer;
    LinearLayout slayout;
    CheckBox checkBox;
    Button getPRO;
    ActionBar actionBar;
    boolean checked = false;
    boolean pro = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pro = getIntent().getBooleanExtra(getString(R.string.Extra_LVL), false);
        setContentView(R.layout.activity_instructions);
        SharedPreferences shapre = getPreferences(MODE_PRIVATE);
        checked = shapre.getBoolean(PrincipalActivity.SIoS, false);
        slayout = (LinearLayout)findViewById(R.id.slayout);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            slayout.setBackgroundColor(Color.parseColor("#ff303030"));
        getPRO = (Button)findViewById(R.id.getPRO);
        if(pro)
            getPRO.setVisibility(View.GONE);
        InspVer = (TextView)findViewById(R.id.InspVer);
        InspVer.setText(getString(R.string.Version) + getString(R.string.versionnum));
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                Intent result = new Intent(PrincipalActivity.RESULT_ACTION);
                result.putExtra(PrincipalActivity.SIoS, checked);
                setResult(Activity.RESULT_OK, result);
                SharedPreferences shapre = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = shapre.edit();
                editor.putBoolean(PrincipalActivity.SIoS, isChecked);
                editor.commit();
            }
        });
        checkBox.setChecked(checked);
        setupActionBar();
    }

    public void closeIns(View view) {
        finish();
    }

    public void getpro(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.vagoscorp.virtualterminal.prokey"));
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            actionBar = getActionBar();
            if(actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.action_settings: {

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}