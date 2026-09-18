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

import android.graphics.Insets;
import android.view.WindowInsets;

public class TutorialActivity extends Activity {

    ActionBar actionBar;
    TextView endianMode;
    TextView RX;// Received Data
    TextView RXn;// Received Data
    TextView sepLab;// Received Data
    Button Connect;
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
    Button typeTXB; // TextView typeTXB;
    Button DelTX;
    Button DelRX;
    Button nextTut;
    Button prevTut;
    LinearLayout layout_principal;

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
    boolean darkTheme = true;

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
        darkTheme = shapre.getBoolean(getString(R.string.DARK_THEME), true);
        if(darkTheme)
            this.setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_tutorial);
        layout_principal = findViewById(R.id.layout_principal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            layout_principal.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    int hMargin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
                    int vMargin = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Insets systemBars = insets.getInsets(WindowInsets.Type.systemBars());
                        v.setPadding(systemBars.left + hMargin, systemBars.top + vMargin,
                                systemBars.right + hMargin, systemBars.bottom + vMargin);
                    } else {
                        v.setPadding(insets.getSystemWindowInsetLeft() + hMargin,
                                insets.getSystemWindowInsetTop() + vMargin,
                                insets.getSystemWindowInsetRight() + hMargin,
                                insets.getSystemWindowInsetBottom() + vMargin);
                    }
                    return insets;
                }
            });
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && darkTheme)
            layout_principal.setBackgroundColor(getResources().getColor(R.color.DT));
        RX = findViewById(R.id.RX);
        sepLab = findViewById(R.id.sepLab);
        RXn = findViewById(R.id.RXn);
        layNAct = findViewById(R.id.layNAct);
        editNAct = findViewById(R.id.editNAct);
        UpdN = findViewById(R.id.UpdN);
        aCRpLF = findViewById(R.id.aCRpLF);
        endianMode = findViewById(R.id.endianMode);
        byteRCV = findViewById(R.id.byteRCV);
        Connect =  findViewById(R.id.Connect);
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
        if(darkTheme) {
            IOc.formatDT_Button(Connect);
            IOc.formatDT_Button(Chan_Ser);
            IOc.formatDT_Button(typeTXB);
            IOc.formatDT_Button(Send);
            IOc.formatDT_Button(DelTX);
            IOc.formatDT_Button(DelRX);
        }
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
        Connect.setOnLongClickListener(new View.OnLongClickListener() {
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
        View[] tutOrderTemp = {Chan_Ser, Connect, typeTXB, aCRpLF, endianMode, TXs[0], TXs[1],
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

    public void updateTXtype(int indexTut) {
        for(int i = 0; i < 5; i++)
            TXs[i].setVisibility(View.GONE);
        tutOrder[indexTut].setVisibility(View.VISIBLE);
        String txText = "TX ";
        if(indexTut > 5 && indexTut < 9)
            txText += getString(IOc.formStrings[indexTut-6]);
        else if(indexTut == 5)
            txText += getString(IOc.typeStrings[0]);
        else if(indexTut == 9)
            txText += getString(IOc.typeStrings[5]);
        txText += "▼";
        typeTXB.setText(txText);
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
            updateTXtype(indexTut);
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
            Connect.setVisibility(View.GONE);
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
