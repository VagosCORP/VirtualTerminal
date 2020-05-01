package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Configuration extends Activity implements OnCheckedChangeListener{

    /*public static final String DARK_THEME = "DARK_THEME";
    public static final String NUM_COMM_STAT = "NUM_COMM_STAT";
    public static final String NUM_COMM_SCROLL = "NUM_COMM_SCROLL";
    public static final String CLEAR_TX_AFTER_SEND = "CLEAR_TX_AFTER_SEND";
    public static final String LITTLE_ENDIAN4SEND = "LITTLE_ENDIAN4SEND";
    public static final String LITTLE_ENDIAN4RECEIVE = "LITTLE_ENDIAN4RECEIVE";*/

    boolean doubleBackToExitPressedOnce = false;

    public static final int defNumCommStat = 4;
    public static final int defNumCommScroll = 4;

    private final int SHOW_INSTRUCTIONS = 16;
    public static final String SIoS = "SIoS";

    private boolean darkTheme = true;
    private boolean pro = false;
    private boolean clearTXAS = false;
    private int numCommStat = defNumCommStat;
    private int numCommScroll = defNumCommScroll;
    private boolean littleEndianGlobal = false;
    //private boolean littleEndianForReceive = false;

    CheckBox enDarkTheme;
    CheckBox enClearTX_AS;
    LinearLayout cantCommStat_Layout;
    EditText cantCommStat;
    LinearLayout cantCommScroll_Layout;
    EditText cantCommScroll;
    RadioGroup endianGlobal;
    RadioButton bigEndian;
    RadioButton littleEndian;

    //RadioGroup endian4Receive;
    //RadioButton bigEndian4Receive;
    //RadioButton littleEndian4Receive;

    SharedPreferences shapre;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //shapre = getPreferences(MODE_PRIVATE);
        shapre = getSharedPreferences(getString(R.string.SHARPREF),MODE_PRIVATE);
        editor = shapre.edit();editor.commit();
        darkTheme = shapre.getBoolean(getString(R.string.DARK_THEME), true);
        pro = shapre.getBoolean(getString(R.string.isPRO), false);
        clearTXAS = shapre.getBoolean(getString(R.string.CLEAR_TX_AFTER_SEND), false);
        numCommStat = shapre.getInt(getString(R.string.NUM_COMM_STAT), defNumCommStat);
        numCommScroll = shapre.getInt(getString(R.string.NUM_COMM_SCROLL), defNumCommScroll);
        littleEndianGlobal = shapre.getBoolean(getString(R.string.LITTLE_ENDIAN), false);
        //littleEndianForReceive = shapre.getBoolean(getString(R.string.LITTLE_ENDIAN4RECEIVE), false);
        if(darkTheme)
            this.setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_configuration);
        LinearLayout layoutSettings = findViewById(R.id.layout_settings);;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && darkTheme)
            layoutSettings.setBackgroundColor(Color.parseColor(getString(R.string.DT_Color)));
        //layoutSettings.setBackgroundColor(Color.parseColor("#ff303030"));
        enDarkTheme = findViewById(R.id.enDarkTheme);
        enDarkTheme.setChecked(darkTheme);
        enDarkTheme.setOnCheckedChangeListener(this);
        enClearTX_AS = findViewById(R.id.enClearTX_AS);
        enClearTX_AS.setChecked(clearTXAS);
        cantCommStat_Layout = findViewById(R.id.cantCommStat_Layout);
        cantCommStat = findViewById(R.id.cantCommStat);
        String temp = "" + numCommStat;
        cantCommStat.setText(temp);
        cantCommScroll_Layout = findViewById(R.id.cantCommScroll_Layout);
        cantCommScroll = findViewById(R.id.cantCommScroll);
        temp = "" + numCommScroll;
        cantCommScroll.setText(temp);
        endianGlobal = findViewById(R.id.endianGlobal);
        bigEndian = findViewById(R.id.bigEndian);
        bigEndian.setChecked(!littleEndianGlobal);
        littleEndian = findViewById(R.id.littleEndian);
        littleEndian.setChecked(littleEndianGlobal);
        //endian4Receive = findViewById(R.id.endian4Receive);
        //bigEndian4Receive = findViewById(R.id.bigEndian4Receive);
        //bigEndian4Receive.setChecked(!littleEndianForReceive);
        //littleEndian4Receive = findViewById(R.id.littleEndian4Receive);
        //littleEndian4Receive.setChecked(littleEndianForReceive);
        if(!pro) {
            cantCommStat.setEnabled(pro);
            cantCommScroll.setEnabled(pro);
            cantCommStat.setText("");
            cantCommScroll.setText("");
            cantCommStat.setHint(R.string.NEED_PRO);
            cantCommScroll.setHint(R.string.NEED_PRO);
            bigEndian.setEnabled(pro);
            littleEndian.setEnabled(pro);
            //bigEndian4Receive.setEnabled(pro);
            numCommStat = defNumCommStat;
            numCommScroll = defNumCommScroll;
            editor.putInt(getString(R.string.NUM_COMM_STAT), numCommStat);
            editor.putInt(getString(R.string.NUM_COMM_SCROLL), numCommScroll);
            editor.commit();
        }
        //littleEndian4Receive.setEnabled(false);
    }

    public void applyConfig(View view) {
        darkTheme = enDarkTheme.isChecked();
        clearTXAS = enClearTX_AS.isChecked();
        if(!cantCommStat.getText().toString().equals(""))
            numCommStat = Integer.parseInt(cantCommStat.getText().toString());
        if(!cantCommScroll.getText().toString().equals(""))
            numCommScroll = Integer.parseInt(cantCommScroll.getText().toString());
        littleEndianGlobal = littleEndian.isChecked();
        //littleEndianForReceive = littleEndian4Receive.isChecked();
        editor.putBoolean(getString(R.string.DARK_THEME), darkTheme);
        editor.putBoolean(getString(R.string.CLEAR_TX_AFTER_SEND), clearTXAS);
        editor.putInt(getString(R.string.NUM_COMM_STAT), numCommStat);
        editor.putInt(getString(R.string.NUM_COMM_SCROLL), numCommScroll);
        editor.putBoolean(getString(R.string.LITTLE_ENDIAN), littleEndianGlobal);
        //editor.putBoolean(getString(R.string.LITTLE_ENDIAN4RECEIVE), littleEndianForReceive);
        editor.commit();
        Intent resIntent = new Intent(PrincipalActivity.RESULT_ACTION);
        setResult(Activity.RESULT_OK, resIntent);
        exitConfig(view);
    }

    public void exitConfig(View view) {
        finish();
        overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, R.string.cThemeToast, Toast.LENGTH_SHORT).show();
    }

    public void showInstructions(View view) {
        Intent enableIntent = new Intent(this, InstructionsActivity.class);
        startActivityForResult(enableIntent, SHOW_INSTRUCTIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SHOW_INSTRUCTIONS:
                if (resultCode == Activity.RESULT_OK) {
                    boolean checked = data.getBooleanExtra(SIoS, false);
                    editor.putBoolean(PrincipalActivity.SIoS, checked);
                    editor.commit();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(this, R.string.ConfigNotSaved, Toast.LENGTH_SHORT).show();
        if (doubleBackToExitPressedOnce) {
            exitConfig(null);
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.ConfigNotSaved, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
