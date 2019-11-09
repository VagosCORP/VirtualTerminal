package com.vagoscorp.virtualterminal;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
    TextView insGP;

    TextView textViewBasics;
    TextView textViewLayout;
    TextView textViewFastSend;
    TextView textViewXtring;
    TextView textViewAdvRcv;
    TextView textViewPackRcv;

    LinearLayout slayout;
    CheckBox checkBox;
    Button getPRO;
    ActionBar actionBar;
    boolean checked = false;
    boolean pro = false;

    int defversion = 20150000;
    int versionCode = defversion;
    String versionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pro = getIntent().getBooleanExtra(getString(R.string.Extra_LVL), false);
        boolean darkTheme = getIntent().getBooleanExtra(PrincipalActivity.theme, true);
        if(darkTheme)
            this.setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_instructions);
        SharedPreferences shapre = getPreferences(MODE_PRIVATE);
        checked = shapre.getBoolean(PrincipalActivity.SIoS, false);
        slayout = findViewById(R.id.slayout);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && darkTheme)
            slayout.setBackgroundColor(Color.parseColor("#ff303030"));
        getPRO = findViewById(R.id.getPRO);
        insGP = findViewById(R.id.insGP);
        InspVer = findViewById(R.id.InspVer);
        textViewBasics = findViewById(R.id.textViewBasics);
        textViewLayout = findViewById(R.id.textViewLayout);
        textViewFastSend = findViewById(R.id.textViewFastSend);
        textViewXtring = findViewById(R.id.textViewXtring);
        textViewAdvRcv = findViewById(R.id.textViewAdvRcv);
        textViewPackRcv = findViewById(R.id.textViewPackRcv);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String tempString = "v" + versionName + " b" + versionCode;
        InspVer.setText(tempString);
        if(pro) {
            getPRO.setVisibility(View.GONE);
            insGP.setVisibility(View.GONE);
        }
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

    void hideInst() {
        textViewBasics.setVisibility(View.GONE);
        textViewLayout.setVisibility(View.GONE);
        textViewFastSend.setVisibility(View.GONE);
        textViewXtring.setVisibility(View.GONE);
        textViewAdvRcv.setVisibility(View.GONE);
        textViewPackRcv.setVisibility(View.GONE);
    }

    public void instBasics(View view) {
        boolean sas = textViewBasics.getVisibility() != View.VISIBLE;
        hideInst();
        if(sas)
            textViewBasics.setVisibility(View.VISIBLE);
    }

    public void instLayout(View view) {
        boolean sas = textViewLayout.getVisibility() != View.VISIBLE;
        hideInst();
        if(sas)
            textViewLayout.setVisibility(View.VISIBLE);
    }

    public void instFastSend(View view) {
        boolean sas = textViewFastSend.getVisibility() != View.VISIBLE;
        hideInst();
        if(sas)
            textViewFastSend.setVisibility(View.VISIBLE);
    }

    public void instXtring(View view) {
        boolean sas = textViewXtring.getVisibility() != View.VISIBLE;
        hideInst();
        if(sas)
            textViewXtring.setVisibility(View.VISIBLE);
    }

    public void instAdvRcv(View view) {
        boolean sas = textViewAdvRcv.getVisibility() != View.VISIBLE;
        hideInst();
        if(sas)
            textViewAdvRcv.setVisibility(View.VISIBLE);
    }

    public void instPackRcv(View view) {
        boolean sas = textViewPackRcv.getVisibility() != View.VISIBLE;
        hideInst();
        if(sas)
            textViewPackRcv.setVisibility(View.VISIBLE);

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