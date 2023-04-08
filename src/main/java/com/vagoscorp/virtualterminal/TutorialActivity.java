package com.vagoscorp.virtualterminal;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TutorialActivity extends Activity {

    ActionBar actionBar;
    TextView endianMode;
    TextView RX;// Received Data
    TextView RXn;// Received Data
    TextView sepLab;// Received Data
    Button Conect;
    Button Chan_Ser;
    Button Send;
    ScrollView scro;
    ScrollView scron;
    LinearLayout commBase;
    LinearLayout commStaticL;
    LinearLayout commScrollableL;
    LinearLayout byteRCV;
    LinearLayout layNAct;
    EditText editNAct;
    CheckBox UpdN;
    CheckBox aCRpLF;
    TextView typeTXB;
    Button DelTX;
    Button DelRX;
    Button nextTut;
    Button prevTut;

    Button[] commX = new Button[8];
    EditText[] TXs = new EditText[5];
    View[] tutOrder;
    int[] stringResTut = {R.string.tutChanSer, R.string.tutConnect, R.string.tutSendType,
            R.string.tutACRpLF, R.string.tutEndianLab, R.string.tutTXinputText,
            R.string.tutTXinputNum, R.string.tutTXinputHex, R.string.tutTXinputBin,
            R.string.tutTXinputFloat, R.string.tutSend, R.string.tutFastSend,
            R.string.tutDelTRX, R.string.tutRXt, R.string.tutlayNAct, R.string.tutRXn,
            R.string.tutExit, R.string.tutXtring};

    public boolean CM = false;
    public int numCommStat = 4;
    public int numCommScroll = 4;
    public int cantFastSendTot = 8;
    int indexTut = -1;
    int maxTut = 0;
    boolean abHidden = false;
    boolean pro = false;

    SharedPreferences shapre;

    public static final int TX_FORM_TXT = 0;
    public static final int TX_FORM_DEC = 1;
    public static final int TX_FORM_HEX = 2;
    public static final int TX_FORM_BIN = 3;
    public static final int TX_FORM_FLOAT = 4;

    private final int SHOW_INSTRUCTIONS = 16;
    private final int ENTER_XTRING = 17;
    private final int ENTER_CONFIG = 18;
    private final int ENTER_IO_CONFIG = 19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shapre = getSharedPreferences(getString(R.string.SHARPREF),MODE_PRIVATE);
        pro = shapre.getBoolean(getString(R.string.isPRO), false);
        setContentView(R.layout.activity_tutorial);
        RX = findViewById(R.id.RX);
        sepLab = findViewById(R.id.sepLab);
        RXn = findViewById(R.id.RXn);
        layNAct = findViewById(R.id.layNAct);
        editNAct = findViewById(R.id.editNAct);
        UpdN = findViewById(R.id.UpdN);
        aCRpLF = findViewById(R.id.aCRpLF);
        endianMode = findViewById(R.id.endianMode);
        byteRCV = findViewById(R.id.byteRCV);
        Conect =  findViewById(R.id.Conect);
        Chan_Ser = findViewById(R.id.chan_ser);
        Send = findViewById(R.id.Send);
        TXs[TX_FORM_TXT] = findViewById(R.id.TXtext);
        TXs[TX_FORM_DEC] = findViewById(R.id.TXnum);
        TXs[TX_FORM_HEX] = findViewById(R.id.TXhex);
        TXs[TX_FORM_BIN] = findViewById(R.id.TXbin);
        TXs[TX_FORM_FLOAT] = findViewById(R.id.TXfloat);
        DelTX = findViewById(R.id.DelTX);
        DelRX = findViewById(R.id.DelRX);
        scro = findViewById(R.id.scro);
        scron = findViewById(R.id.scron);
        commBase = findViewById(R.id.commBase);
        commStaticL = findViewById(R.id.commStaticL);
        commScrollableL = findViewById(R.id.commScrollableL);
        nextTut = findViewById(R.id.nextTut);
        prevTut = findViewById(R.id.prevTut);
        typeTXB = findViewById(R.id.typeTXB);
        typeTXB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterIOConfig(IOc.TX_CONFIG);
            }
        });
        Send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                commanderMode();
                return true;
            }
        });
        UpdN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updPNum(isChecked);
            }
        });
        Conect.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                hideActionBar();
                return true;
            }
        });
        Chan_Ser.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enterSettings();
                return true;
            }
        });
        for(EditText txSh:TXs)
            txSh.setEnabled(false);
        String endian = "⚠ " + getString(R.string.BigEndian);
        endianMode.setText(endian);
        updCommButtons();
        View[] tutOrderTemp = {Chan_Ser, Conect, typeTXB, aCRpLF, endianMode, TXs[0], TXs[1],
                TXs[2], TXs[3], TXs[4], Send, commX[0], DelTX, scro, layNAct, byteRCV/*, typeTXB*/};
        maxTut = tutOrderTemp.length;
        tutOrder = tutOrderTemp;
        prevTut.setEnabled(false);
        RX.setText(R.string.tutPresentation);
        //typeTXB.setBackgroundColor(Color.DKGRAY);
        //byteRCV.setVisibility(View.VISIBLE);
        //Toast.makeText(this, "Sas", Toast.LENGTH_SHORT).show();
        setupActionBar();
    }

    public void updCommButtons() {
        commStaticL.removeAllViewsInLayout();
        commScrollableL.removeAllViewsInLayout();
        for(int i = 0; i < cantFastSendTot; i++) {
            commX[i] = new Button(this);
            int numHum = i + 1;
            String sas = "" + numHum;
            commX[i].setText(sas);
            if(i < numCommStat) {
                commStaticL.addView(commX[i]);
            }else {
                commScrollableL.addView(commX[i]);
            }
        }
    }

    private void updPNum(boolean bool) {
        /*enNumericRcv = bool;
        both = bool;*/
        //sepLab.setText(R.string.byteRX);
        if (bool)
            byteRCV.setVisibility(View.VISIBLE);
        else
            byteRCV.setVisibility(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            actionBar = getActionBar();
            if(actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                if(abHidden && pro)
                    actionBar.hide();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void hideActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (actionBar != null && pro) {
                if (!abHidden) {
                    actionBar.hide();
                    abHidden = true;
                } else {
                    actionBar.show();
                    abHidden = false;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selID = item.getItemId();
        if(selID == android.R.id.home)
            finish();
        else if(selID == R.id.rcvTypT)
            enterIOConfig(IOc.RX_CONFIG);
        else if(selID == R.id.commModeT)
            commanderMode();
        else if(selID == R.id.XtringModeT)
            enterXtringMode();
        else if(selID == R.id.viewInstructionsT)
            enterInstructions();
        else if(selID == R.id.exitTutorialT)
            exitTutorial();
        else if(selID == R.id.action_settingsT)
            enterSettings();
        /*switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.rcvTypT:
                enterIOConfig(IOc.RX_CONFIG);
                return true;
            case R.id.commModeT:
                commanderMode();
                return true;
            case R.id.XtringModeT:
                enterXtringMode();
                return true;
            case R.id.viewInstructionsT:
                enterInstructions();
                return true;
            case R.id.exitTutorialT:
                exitTutorial();
                return true;
            case R.id.action_settingsT:
                enterSettings();
                return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    public void commanderMode() {
        if(!CM) {
            CM = true;
            commBase.setVisibility(View.VISIBLE);
        }else {
            CM = false;
            commBase.setVisibility(View.GONE);
        }
    }

    private void enterXtringMode() {
        Intent startXtring = new Intent(this, XtringActivity.class);
        startActivityForResult(startXtring, ENTER_XTRING);
        overridePendingTransition(R.animator.slide_in_left,
                R.animator.slide_out_right);
    }

    private void enterSettings() {
        Intent startConfig = new Intent(this, Configuration.class);
        startActivityForResult(startConfig, ENTER_CONFIG);
        overridePendingTransition(R.animator.slide_in_right,
                R.animator.slide_out_left);
    }

    private void enterIOConfig(int TX_RX) {
        Intent startIOConfig = new Intent(this, IOc.class);
        /*startIOConfig.putExtra(IOc.TX_TYPE, actualTXtype);
        startIOConfig.putExtra(IOc.TX_FORM, actualTXform);
        startIOConfig.putExtra(IOc.RX_TYPE, actualRXtype);
        startIOConfig.putExtra(IOc.RX_FORM, actualRXform);*/
        startIOConfig.putExtra(IOc.CONFIG_ACT, TX_RX);
        startActivityForResult(startIOConfig, ENTER_IO_CONFIG);
        overridePendingTransition(R.animator.slide_in_top,
                R.animator.slide_out_bottom);
    }

    private void enterInstructions() {
        Intent instructIntent = new Intent(this, InstructionsActivity.class);
        startActivityForResult(instructIntent, SHOW_INSTRUCTIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SHOW_INSTRUCTIONS:
                if(resultCode == Activity.RESULT_OK) {
                    boolean checked = data.getBooleanExtra(PrincipalActivity.SIoS, false);
                    //editor.putBoolean(SIoS, checked);
                    //editor.commit();
                }
                break;
            case ENTER_XTRING:
                if(resultCode == Activity.RESULT_OK) {
                    byte[] newTX = data.getByteArrayExtra(XtringActivity.NEWTX);
                    String newTXs = data.getStringExtra(XtringActivity.NEWTXs);
                }
                break;
            case ENTER_CONFIG:
                if(resultCode == Activity.RESULT_OK) {
                    updCommButtons();
                }
                break;
            case ENTER_IO_CONFIG:
                if(resultCode == Activity.RESULT_OK) {

                }else
                    Toast.makeText(this, R.string.ConfigNotSaved, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void applyTutScreen() {
        if(abHidden)
            hideActionBar();
        aCRpLF.setVisibility(View.VISIBLE);
        endianMode.setVisibility(View.GONE);
        layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.GONE);
        DelRX.setEnabled(false);
        for(int i = 0; i < maxTut; i++)
            tutOrder[i].setEnabled(false);
        for(int i = 0; i < 8; i++)
            commX[i].setEnabled(false);
        if(indexTut > 4 && indexTut < 10) {
            for(int i = 0; i < 5; i++)
                TXs[i].setVisibility(View.GONE);
            tutOrder[indexTut].setVisibility(View.VISIBLE);
        }
        if(indexTut == 4) {
            aCRpLF.setVisibility(View.GONE);
            endianMode.setVisibility(View.VISIBLE);
        }else if(indexTut == 11 && !CM)
            commanderMode();
        else if(indexTut == 12) {
            DelRX.setEnabled(true);
            commanderMode();
        }else if(indexTut == 14) {
            layNAct.setVisibility(View.VISIBLE);
            UpdN.setChecked(false);
        }else if(indexTut == 15) {
            layNAct.setVisibility(View.VISIBLE);
            UpdN.setChecked(true);
            byteRCV.setVisibility(View.VISIBLE);
        /*}else if(indexTut == 16) {
            Conect.setVisibility(View.GONE);
            Chan_Ser.setText(R.string.ComXaddItem);
            aCRpLF.setVisibility(View.GONE);
            typeTXB.setVisibility(View.GONE);
            TXs[4].setVisibility(View.GONE);
            DelTX.setVisibility(View.GONE);
            DelRX.setVisibility(View.GONE);*/
        }
        tutOrder[indexTut].setEnabled(true);
        RX.setText(stringResTut[indexTut]);
    }

    public void nextTut(View view) {
        indexTut++;
        if(indexTut > 0)
            prevTut.setEnabled(true);
        if(indexTut > maxTut - 1)
            nextTut.setEnabled(false);
        if(indexTut < maxTut)
            applyTutScreen();
        else {
            UpdN.setChecked(false);
            layNAct.setVisibility(View.GONE);
            RX.setText(stringResTut[16]);
            Toast.makeText(this, R.string.exitTutToast, Toast.LENGTH_SHORT).show();
        }
    }

    public void prevTut(View view) {
        indexTut--;
        nextTut.setEnabled(true);
        if(indexTut <= 0)
            prevTut.setEnabled(false);
        if(indexTut >= 0) {
            applyTutScreen();
        }
    }

    void exitTutorial() {
        Intent result = new Intent(PrincipalActivity.RESULT_ACTION);
        //result.putExtra(PrincipalActivity.SIoS, checked);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

}
