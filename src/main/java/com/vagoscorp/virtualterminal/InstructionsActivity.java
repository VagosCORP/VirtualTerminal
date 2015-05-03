package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;


public class InstructionsActivity extends Activity {

    LinearLayout slayout;
    CheckBox checkBox;
    boolean checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        SharedPreferences shapre = getPreferences(MODE_PRIVATE);
        checked = shapre.getBoolean(Principal.SIoS, false);
        slayout = (LinearLayout)findViewById(R.id.slayout);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            slayout.setBackgroundColor(Color.parseColor("#ff303030"));
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                Intent result = new Intent(Principal.RESULT_ACTION);
                result.putExtra(Principal.SIoS, checked);
                setResult(Activity.RESULT_OK, result);
                SharedPreferences shapre = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = shapre.edit();
                editor.putBoolean(Principal.SIoS, isChecked);
                editor.commit();
            }
        });
        checkBox.setChecked(checked);
    }

    public void closeIns(View view) {
        finish();
    }
}