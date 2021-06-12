package com.vagoscorp.virtualterminal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class IOc extends Activity {

    Button TX_Menu;
    Button RX_Menu;
    Button AcceptIO;
    TextView endianMode;

    RadioGroup typesRG;
    RadioGroup intFormRG;

    CheckBox packageMode;

    int actualTXtype = 0;
    int actualTXform = 0;
    int actualRXtype = 0;
    int actualRXform = 0;
    int actualConfig = 0;
    int itemPos = 0;
    boolean external = true;

    SharedPreferences shapre;
    SharedPreferences.Editor editor;

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_BYTE = 1;
    public static final int TYPE_SHORT = 2;
    public static final int TYPE_INT = 3;
    public static final int TYPE_LONG = 4;
    public static final int TYPE_FLOAT = 5;
    public static final int TYPE_DOUBLE = 6;
    public static final int TYPE_DUAL = 7;
    public static final int TYPE_PACKAGE = 8;

    public static final int INT_FORM_DEC = 0;
    public static final int INT_FORM_HEX = 1;
    public static final int INT_FORM_BIN = 2;

    public static final int TX_CONFIG = 1;
    public static final int RX_CONFIG = 2;
    public static final int XTRING_CONFIG = 3;

    public static final String TX_TYPE = "TX_TYPE";
    public static final String TX_FORM = "TX_FORM";
    public static final String RX_TYPE = "RX_TYPE";
    public static final String RX_FORM = "RX_FORM";
    public static final String CONFIG_ACT = "CONFIG_ACT";
    public static final String PACKMOD_EN = "PACKMOD_EN";

    boolean pro = false;
    boolean littleEndian = false;
    boolean vtSendProtocol = false;
    boolean packModeEn = false;

    int[] types = {R.id.SelText, R.id.SelByte, R.id.SelShort, R.id.SelInt, R.id.SelLong,
            R.id.SelFloat, R.id.SelDouble, R.id.SelDual, R.id.SelPackage};
    int[] intForms = {R.id.SelDec, R.id.SelHex, R.id.SelBin};
    RadioButton[] typeRadB = new RadioButton[9];
    RadioButton[] intFormRadB = new RadioButton[3];

    public static int[] typeStrings = {R.string.typeString, R.string.size8, R.string.size16,
            R.string.size32, R.string.size64, R.string.typeFloat, R.string.typeDouble};
    public static int[] formStrings = {R.string.formDecimal, R.string.formHexadecimal, R.string.formBinary};
    public static int[] radixTX = {10, 16, 2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        actualTXtype = data.getIntExtra(TX_TYPE, TYPE_TEXT);
        actualTXform = data.getIntExtra(TX_FORM, INT_FORM_DEC);
        actualRXtype = data.getIntExtra(RX_TYPE, TYPE_TEXT);
        actualRXform = data.getIntExtra(RX_FORM, INT_FORM_DEC);
        actualConfig = data.getIntExtra(CONFIG_ACT, TX_CONFIG);
        packModeEn = data.getBooleanExtra(PACKMOD_EN, false);
        itemPos = data.getIntExtra(XtringActivity.POS, 1);
        external = data.getBooleanExtra(XtringActivity.EXTERNAL, true);
        shapre = getSharedPreferences(getString(R.string.SHARPREF),MODE_PRIVATE);
        editor = shapre.edit();editor.commit();
        pro = shapre.getBoolean(getString(R.string.isPRO), false);
        if(pro) {
            littleEndian = shapre.getBoolean(getString(R.string.LITTLE_ENDIAN), false);
            vtSendProtocol = shapre.getBoolean(getString(R.string.SEND_VT_PROTOCOL), false);
        }
        setContentView(R.layout.activity_ioconfig);
        TX_Menu = findViewById(R.id.TX_Menu);
        RX_Menu = findViewById(R.id.RX_Menu);
        AcceptIO = findViewById(R.id.AcceptIO);
        typesRG = findViewById(R.id.typesRG);
        intFormRG = findViewById(R.id.intFormRG);
        endianMode = findViewById(R.id.endianMode);
        packageMode = findViewById(R.id.packageMode);
        for(int i = 0; i < 9; i++)
            typeRadB[i] = findViewById(types[i]);
        for(int i = 0; i < 3; i++)
            intFormRadB[i] = findViewById(intForms[i]);
        packageMode.setChecked(packModeEn);
        packageMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                packModeEn = isChecked;
            }
        });
        TX_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualConfig = TX_CONFIG;
                updateConfigMode();
            }
        });
        RX_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualConfig = RX_CONFIG;
                updateConfigMode();
            }
        });
        AcceptIO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateResult();
                finish();
                overridePendingTransition(R.animator.slide_in_bottom,
                        R.animator.slide_out_top);
            }
        });
        updateConfigMode();
        typeRadB[TYPE_LONG].setEnabled(pro);
        typeRadB[TYPE_DOUBLE].setEnabled(pro);
        typesRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for(int i = 0; i < 9; i++) {
                    if(types[i] == checkedId) {
                        if(actualConfig == TX_CONFIG || actualConfig == XTRING_CONFIG)
                            actualTXtype = i;
                        else if(actualConfig == RX_CONFIG)
                            actualRXtype = i;
                        break;
                    }
                }
                isAnInt();
            }
        });
        intFormRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for(int i = 0; i < 3; i++) {
                    if(intForms[i] == checkedId) {
                        if(actualConfig == TX_CONFIG || actualConfig == XTRING_CONFIG)
                            actualTXform = i;
                        else if(actualConfig == RX_CONFIG)
                            actualRXform = i;
                        break;
                    }
                }
            }
        });
        //String endian = "▶⛬⚠";
        String endian = "⚠ ";
        if(littleEndian)
            endian += getString(R.string.LittleEndian);//endianMode.setText(R.string.LittleEndian);
        else
            endian += getString(R.string.BigEndian);//endianMode.setText(R.string.BigEndian);
        endianMode.setText(endian);
        isAnInt();
        //touchOut(false);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void touchOut(boolean tOut) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setFinishOnTouchOutside(tOut);
        }
    }

    public void updateConfigMode() {
        if(actualConfig == TX_CONFIG) {
            typeRadB[actualTXtype].setChecked(true);
            intFormRadB[actualTXform].setChecked(true);
            TX_Menu.setVisibility(View.GONE);//TX_Menu.setEnabled(false);
            RX_Menu.setVisibility(View.VISIBLE);//RX_Menu.setEnabled(true);
            setTitle(R.string.TXConfig);
            typeRadB[TYPE_DUAL].setVisibility(View.GONE);
            //typeRadB[TYPE_PACKAGE].setVisibility(View.GONE);
            packageMode.setVisibility(View.GONE);
        }else if(actualConfig == XTRING_CONFIG) {
            typeRadB[actualTXtype].setChecked(true);
            intFormRadB[actualTXform].setChecked(true);
            TX_Menu.setVisibility(View.GONE);
            RX_Menu.setVisibility(View.GONE);
            String temp = getString(R.string.XtringConfig) + " ( " + itemPos + " )";
            setTitle(temp);
            typeRadB[TYPE_DUAL].setVisibility(View.GONE);
            //typeRadB[TYPE_PACKAGE].setVisibility(View.GONE);
            packageMode.setVisibility(View.GONE);
        }else if(actualConfig == RX_CONFIG) {
            typeRadB[actualRXtype].setChecked(true);
            intFormRadB[actualRXform].setChecked(true);
            TX_Menu.setVisibility(View.VISIBLE);//TX_Menu.setEnabled(true);
            RX_Menu.setVisibility(View.GONE);//RX_Menu.setEnabled(false);
            setTitle(R.string.RXConfig);
            typeRadB[TYPE_DUAL].setVisibility(View.VISIBLE);
            //typeRadB[TYPE_PACKAGE].setVisibility(View.VISIBLE);
            packageMode.setVisibility(View.VISIBLE);
        }
    }

    public void isAnInt() {
        boolean isInt = false;
        int aType = 0;
        boolean txIsInt = actualTXtype > 0 && actualTXtype < 5;
        boolean rxIsInt = actualRXtype > 0 && actualRXtype < 5 || actualRXtype > 6;
        if (actualConfig == TX_CONFIG || actualConfig == XTRING_CONFIG) {
            isInt = txIsInt;
            aType = actualTXtype;
        } else if (actualConfig == RX_CONFIG) {
            isInt = rxIsInt;
            aType = actualRXtype;
        }
        for(int i = 0; i < 3; i++)
            intFormRadB[i].setEnabled(isInt);
        if(aType > 1 && aType < 7) {
            endianMode.setVisibility(View.VISIBLE);
            packageMode.setEnabled(true);
        }else {
            endianMode.setVisibility(View.INVISIBLE);
            packageMode.setEnabled(aType == 0);
        }
    }

    public void updateResult() {
        Intent result = new Intent(PrincipalActivity.RESULT_ACTION);
        result.putExtra(TX_TYPE, actualTXtype);
        result.putExtra(TX_FORM, actualTXform);
        result.putExtra(RX_TYPE, actualRXtype);
        result.putExtra(RX_FORM, actualRXform);
        result.putExtra(PACKMOD_EN, packModeEn);
        result.putExtra(XtringActivity.POS, itemPos);
        result.putExtra(XtringActivity.EXTERNAL, external);
        setResult(Activity.RESULT_OK, result);
    }

}
