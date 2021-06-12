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
    public static final int defInitByte = 13;
    public static final int defEndByte = 10;

    private final int SHOW_INSTRUCTIONS = 16;
    public static final String SIoS = "SIoS";

    private boolean darkTheme = true;
    private boolean clearTXAS = false;
    private int numCommStat = defNumCommStat;
    private int numCommScroll = defNumCommScroll;
    private int charInit = defInitByte;
    private int charEnd = defEndByte;
    private int charPkgEnd = defEndByte;
    private boolean sameEndByte = true;
    private boolean littleEndian = false;

    CheckBox enDarkTheme;
    CheckBox enClearTX_AS;
    LinearLayout quantCommStat_Layout;
    EditText quantCommStat;
    LinearLayout quantCommScroll_Layout;
    EditText quantCommScroll;
    LinearLayout rcvStartByte_Layout;
    EditText rcvStartByte;
    LinearLayout rcvEndByte_Layout;
    EditText rcvEndByte;
    CheckBox sameAsEndByte;
    LinearLayout pkgEndByte_Layout;
    EditText pkgEndByte;
    RadioGroup endianRBGroup;
    RadioButton bigEndianRB;
    RadioButton littleEndianRB;

    SharedPreferences shapre;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        shapre = getPreferences(MODE_PRIVATE);
        shapre = getSharedPreferences(getString(R.string.SHARPREF),MODE_PRIVATE);
        editor = shapre.edit();editor.commit();
        darkTheme = shapre.getBoolean(getString(R.string.DARK_THEME), true);
        boolean pro = shapre.getBoolean(getString(R.string.isPRO), false);
        clearTXAS = shapre.getBoolean(getString(R.string.CLEAR_TX_AFTER_SEND), false);
        if(pro) {
            numCommStat = shapre.getInt(getString(R.string.NUM_COMM_STAT), defNumCommStat);
            numCommScroll = shapre.getInt(getString(R.string.NUM_COMM_SCROLL), defNumCommScroll);
//            charInit = shapre.getInt(getString(R.string.START_BYTE), defInitByte);
//            charEnd = shapre.getInt(getString(R.string.END_BYTE), defEndByte);
//            sameEndByte = shapre.getBoolean(getString(R.string.SAME_END_BYTE), true);
//            charPkgEnd = shapre.getInt(getString(R.string.PKG_END_BYTE), defEndByte);
            littleEndian = shapre.getBoolean(getString(R.string.LITTLE_ENDIAN), false);
        }
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
        quantCommStat_Layout = findViewById(R.id.quantCommStat_Layout);
        quantCommStat = findViewById(R.id.quantCommStat);
        String temp = "" + numCommStat;
        quantCommStat.setText(temp);
        quantCommScroll_Layout = findViewById(R.id.quantCommScroll_Layout);
        quantCommScroll = findViewById(R.id.quantCommScroll);
        temp = "" + numCommScroll;
        quantCommScroll.setText(temp);
        rcvStartByte_Layout = findViewById(R.id.rcvInitByte_Layout);
        rcvStartByte = findViewById(R.id.rcvInitByte);
        temp = "" + charInit;
        rcvStartByte.setText(temp);
        rcvEndByte_Layout = findViewById(R.id.rcvEndByte_Layout);
        rcvEndByte = findViewById(R.id.rcvEndByte);
        temp = "" + charEnd;
        rcvEndByte.setText(temp);
        sameAsEndByte = findViewById(R.id.sameAsEndByte);
        sameAsEndByte.setChecked(sameEndByte);
        sameAsEndByte.setOnCheckedChangeListener(this);
        pkgEndByte_Layout = findViewById(R.id.pkgEndByte_Layout);
        pkgEndByte = findViewById(R.id.pkgEndByte);
        temp = "" + charPkgEnd;
        pkgEndByte.setText(temp);
        endianRBGroup = findViewById(R.id.endianGlobal);
        bigEndianRB = findViewById(R.id.bigEndian);
        bigEndianRB.setChecked(!littleEndian);
        littleEndianRB = findViewById(R.id.littleEndian);
        littleEndianRB.setChecked(littleEndian);
        quantCommStat.setEnabled(pro);
        quantCommScroll.setEnabled(pro);
//        rcvStartByte.setEnabled(pro);
//        rcvEndByte.setEnabled(pro);
//        sameAsEndByte.setEnabled(pro);
//        if(pro && !sameEndByte)
//            pkgEndByte_Layout.setVisibility(View.VISIBLE);
        bigEndianRB.setEnabled(pro);
        littleEndianRB.setEnabled(pro);
        if(!pro) {
//            cantCommStat.setText("");
//            cantCommScroll.setText("");
//            cantCommStat.setHint("" + defNumCommStat);
//            cantCommScroll.setHint("" + defNumCommScroll);
//            rcvInitByte.setText("");
//            rcvEndByte.setText("");
//            rcvInitByte.setHint("" + defInitByte);
//            rcvEndByte.setHint("" + defEndByte);
            editor.putInt(getString(R.string.NUM_COMM_STAT), numCommStat);
            editor.putInt(getString(R.string.NUM_COMM_SCROLL), numCommScroll);
//            editor.putInt(getString(R.string.START_BYTE), charInit);
//            editor.putInt(getString(R.string.END_BYTE), charEnd);
//            editor.putBoolean(getString(R.string.SAME_END_BYTE), sameEndByte);
//            editor.putInt(getString(R.string.PKG_END_BYTE), charPkgEnd);
            editor.commit();
        }
    }

    public void applyConfig(View view) {
        darkTheme = enDarkTheme.isChecked();
        clearTXAS = enClearTX_AS.isChecked();
        if(!quantCommStat.getText().toString().equals(""))
            numCommStat = Integer.parseInt(quantCommStat.getText().toString());
        if(!quantCommScroll.getText().toString().equals(""))
            numCommScroll = Integer.parseInt(quantCommScroll.getText().toString());
//        if(!rcvStartByte.getText().toString().equals(""))
//            charInit = Integer.parseInt(rcvStartByte.getText().toString());
//        if(!rcvEndByte.getText().toString().equals(""))
//            charEnd = Integer.parseInt(rcvEndByte.getText().toString());
//        if(!pkgEndByte.getText().toString().equals(""))
//            charPkgEnd = Integer.parseInt(pkgEndByte.getText().toString());
        littleEndian = littleEndianRB.isChecked();
        editor.putBoolean(getString(R.string.DARK_THEME), darkTheme);
        editor.putBoolean(getString(R.string.CLEAR_TX_AFTER_SEND), clearTXAS);
        editor.putInt(getString(R.string.NUM_COMM_STAT), numCommStat);
        editor.putInt(getString(R.string.NUM_COMM_SCROLL), numCommScroll);
//        editor.putInt(getString(R.string.START_BYTE), charInit);
//        editor.putInt(getString(R.string.END_BYTE), charEnd);
//        editor.putBoolean(getString(R.string.SAME_END_BYTE), sameEndByte);
//        if(sameEndByte)
//            charPkgEnd = charEnd;
//        editor.putInt(getString(R.string.PKG_END_BYTE), charPkgEnd);
        editor.putBoolean(getString(R.string.LITTLE_ENDIAN), littleEndian);
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
        if(buttonView == enDarkTheme)
            Toast.makeText(this, R.string.cThemeToast, Toast.LENGTH_SHORT).show();
        else if(buttonView == sameAsEndByte) {
            sameEndByte = isChecked;
            if(isChecked)
                pkgEndByte_Layout.setVisibility(View.GONE);
            else
                pkgEndByte_Layout.setVisibility(View.VISIBLE);
        }
    }

    public void showInstructions(View view) {
        Intent enableIntent = new Intent(this, InstructionsActivity.class);
        startActivityForResult(enableIntent, SHOW_INSTRUCTIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case SHOW_INSTRUCTIONS:
            if(requestCode == SHOW_INSTRUCTIONS) {
                if (resultCode == Activity.RESULT_OK) {
                    boolean checked = data.getBooleanExtra(SIoS, false);
                    editor.putBoolean(PrincipalActivity.SIoS, checked);
                    editor.commit();
                }
//                break;
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
