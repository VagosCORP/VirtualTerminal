package com.vagoscorp.virtualterminal;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import vclibs.communication.Eventos.OnComunicationListener;
import vclibs.communication.Eventos.OnConnectionListener;
import vclibs.communication.android.Comunic;
import vclibs.communication.android.ComunicBT;

public class PrincipalActivity extends Activity implements OnComunicationListener,OnConnectionListener,/*OnLongClickListener,*/GestureDetector.OnGestureListener {

    Spinner spinner;
    TextView RX;// Received Data
    TextView RXn;// Received Data
    TextView sepLab;// Received Data
	Button Conect;
	Button Chan_Ser;
	Button Send;
    ScrollView scro;
    ScrollView scron;
    //LinearLayout commander;
    LinearLayout commBase;
    LinearLayout commStaticL;
    LinearLayout commScrollableL;
//    ScrollView commScroll;
    LinearLayout byteRCV;
    LinearLayout layout_principal;
    LinearLayout layNAct;
    EditText editNAct;
    CheckBox UpdN;
    CheckBox aCRpLF;

    Button[] commX;
    /*int[] commIDs = {R.id.comm1,R.id.comm2,R.id.comm3,R.id.comm4,R.id.comm5,R.id.comm6,R.id.comm7,
            R.id.comm8,R.id.comm9,R.id.comm10,R.id.comm11,R.id.comm12,R.id.comm13,R.id.comm14,
            R.id.comm15,R.id.comm16,R.id.comm17,R.id.comm18,R.id.comm19,R.id.comm20,R.id.comm21,
            R.id.comm22,R.id.comm23,R.id.comm24,R.id.comm25,R.id.comm26,R.id.comm27,R.id.comm28,
            R.id.comm29,R.id.comm30,R.id.comm31,R.id.comm32,R.id.comm33,R.id.comm34,R.id.comm35,
            R.id.comm36,R.id.comm37,R.id.comm38,R.id.comm39,R.id.comm40,R.id.comm41,R.id.comm42,
            R.id.comm43,R.id.comm44,R.id.comm45,R.id.comm46,R.id.comm47,R.id.comm48};*/

    ActionBar actionBar;

    public String serverip;// IP to Connect
    public int serverport;// Port to Connect
    public boolean enNumericRcv = false;
    public boolean CM = false;
    public boolean darkTheme = true;
    public boolean clearTXAS = false;
    public int numCommStat = 4;
    public int numCommScroll = 4;
    public int cantFastSendTot = 8;
	public int SC;
    public int sendTyp = SEND_TXT;
    int cantDataTyp = 9;

    public BluetoothAdapter BTAdapter;
//    private BluetoothGatt mBluetoothGatt;
//    private BluetoothGattCharacteristic mDataMDLP, mControlMLDP;
	public BluetoothDevice[] BondedDevices;
	public BluetoothDevice mDevice;
	public int mDeviceIndex;
    WifiManager WFM;
    ConnectivityManager CTM;
    String defMyIP = "No IP";
    String myIP = defMyIP;
    Comunic comunic;
	ComunicBT comunicBT;

    EditText[] TXs = new EditText[cantDataTyp];// Data to Send
    InputMethodManager imm;
//    EditText TX;// Data to Send

	public int index;
	public String[] DdeviceNames;
	public String myName;
	public String myAddress;

    private final int defIndex = 0;
    private final int REQUEST_ENABLE_BT = 1;
	private final int SEL_BT_DEVICE = 2;
    private final int REQUEST_ENABLE_WIFI = 15;
    private final int REQUEST_CHANGE_SERVER = 12;
    private final int SHOW_INSTRUCTIONS = 16;
    private final int ENTER_XTRING = 17;
    private final int ENTER_CONFIG = 18;

    public static final String IS_PRO = "IS_PRO";
    public static final String SIoS = "SIoS";
    public static final String SI = "SIP";
    public static final String SP = "SPort";
    public static final String NSI = "NSIP";
    public static final String NSP = "NSPort";
    public static final String RESULT_ACTION = "RESULT_ACTION";
    public static final String SDev = "SD";
    public static final String LD = "LD";
	public static final String indev = "indev";
    public static final String abH = "abH";
    public static final String SnIP = "SnIP";
	public static final String comm = "comm";
	public static final String commN = "commN";
	public static final String commT = "commT";
    public static final String commET = "commET";
    public static final String defIP = "10.0.0.6";
    public static final String VER = "VER";
    public static final int defPort = 2000;
	public static final int defNcomm = 0;
	public static final boolean defBcomm = false;
    public static final int SEND_TXT = 0;
    public static final int SEND_BYTE = 1;
    public static final int SEND_BIN = 2;
    public static final int SEND_HEX = 3;
    public static final int SEND_SHORT = 4;
    public static final int SEND_INT = 5;
    public static final int SEND_LONG = 6;//7
    public static final int SEND_FLOAT = 7;//6
    public static final int SEND_DOUBLE = 8;//-
    public static final int COMMT_STRING = 0;
    public static final int COMMT_INT8 = 1;
    public static final int COMMT_INT16 = 11;
    public static final int COMMT_INT32 = 12;
    public static final int COMMT_INT64 = 13;
    public static final int COMMT_FLOAT = 14;
    public static final int COMMT_DOUBLE = 15;
    public static final int COMMT_DUAL = 21;
    public static final int COMMT_UPD = 22;
    public static final int COMMT_XTRING = 25;

//    private static final String MLDP_PRIVATE_SERVICE = "00035b03-58e6-07dd-021a-08123a000300"; //Private service for Microchip MLDP
//    private static final String MLDP_DATA_PRIVATE_CHAR = "00035b03-58e6-07dd-021a-08123a000301"; //Characteristic for MLDP Data, properties - notify, write
//    private static final String MLDP_CONTROL_PRIVATE_CHAR = "00035b03-58e6-07dd-021a-08123a0003ff"; //Characteristic for MLDP Control, properties - read, write
//    private static final String CHARACTERISTIC_NOTIFICATION_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";	//Special UUID for descriptor needed to enable notifications
//    public boolean lowLvl = true;
    boolean both = false;
    boolean upd = false;
    boolean nextUpd = false;
    boolean nextUpdn = false;
    int contUpd = 0;
    int numUpd = 1;
    boolean TCOM = false;
	boolean pro = false;

    int dataRcvtyp = COMMT_FLOAT;
    boolean advRcv = false;
    boolean dataInit = false;
    byte[] dataBytes = new byte[4];
    int dataCont = 0;
    int dataNBytes = 4;

    int charInit = 13;
    int charEnd = 10;
    boolean abHidden = false;
    boolean checked = false;
    boolean isUpdateable = false;

//    boolean NWiFi = false;
    int defversion = 20150000;
    int versionCode = defversion;
    String versionName = "";

    GestureDetector gesDetector;
    float height = 0;
    float width = 0;
    String sumTextRX = "";
    String sumTextRXn = "";
    TextView tV = null;
    TextView tVn = null;
    Timer timy = new Timer();
    int mCount = 0;

    boolean littleEndian = false;

    SharedPreferences shapre;
    SharedPreferences.Editor editor;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //shapre = getPreferences(MODE_PRIVATE);
        shapre = getSharedPreferences(getString(R.string.SHARPREF),MODE_PRIVATE);
        editor = shapre.edit();editor.commit();
        abHidden = shapre.getBoolean(abH, false);
        darkTheme = shapre.getBoolean(getString(R.string.DARK_THEME), true);
        pro = shapre.getBoolean(getString(R.string.isPRO), false);
        clearTXAS = shapre.getBoolean(getString(R.string.CLEAR_TX_AFTER_SEND), false);
        numCommStat = shapre.getInt(getString(R.string.NUM_COMM_STAT), Configuration.defNumCommStat);
        numCommScroll = shapre.getInt(getString(R.string.NUM_COMM_SCROLL), Configuration.defNumCommScroll);
        littleEndian = shapre.getBoolean(getString(R.string.LITTLE_ENDIAN), false);
        checked = shapre.getBoolean(SIoS, false);
        if(darkTheme)
            this.setTheme(R.style.DarkTheme);
		setContentView(R.layout.layout_activity_principal);
        layout_principal = findViewById(R.id.layout_principal);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && darkTheme)
            layout_principal.setBackgroundColor(Color.parseColor(getString(R.string.DT_Color)));
        //layout_principal.setBackgroundColor(Color.parseColor("#ff303030"));
        Intent tip = getIntent();
        TCOM = tip.getBooleanExtra(getString(R.string.Extra_TCOM), false);
        SC = tip.getIntExtra(getString(R.string.Extra_TYP), MainActivity.CLIENT);
        //pro = tip.getBooleanExtra(getString(R.string.Extra_LVL), false);
        comunic = new Comunic();
        comunicBT = new ComunicBT();
        comunicBT.littleEndian = littleEndian;
        comunic.littleEndian = littleEndian;
        if(TCOM) {
            BTAdapter = BluetoothAdapter.getDefaultAdapter();
            index = defIndex;
            if (BTAdapter == null) {
                Toast.makeText(PrincipalActivity.this, R.string.NB, Toast.LENGTH_SHORT)
                        .show();
                finish();
                return;
            }
        }else {
            WFM = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
            CTM = (ConnectivityManager)getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        }
        spinner = findViewById(R.id.spinner); // Create an ArrayAdapter using the string array and a default spinner layout
		RX = findViewById(R.id.RX);
        RXn = findViewById(R.id.RXn);
        sepLab = findViewById(R.id.sepLab);
        layNAct = findViewById(R.id.layNAct);
        editNAct = findViewById(R.id.editNAct);
        UpdN = findViewById(R.id.UpdN);
        aCRpLF = findViewById(R.id.aCRpLF);
        TXs[SEND_TXT] = findViewById(R.id.TXtext);
        TXs[SEND_BYTE] = findViewById(R.id.TXnum);
        TXs[SEND_BIN] = findViewById(R.id.TXbin);
        TXs[SEND_HEX] = findViewById(R.id.TXhex);
        TXs[SEND_SHORT] = findViewById(R.id.TXint16);
        TXs[SEND_INT] = findViewById(R.id.TXint32);
        TXs[SEND_LONG] = findViewById(R.id.TXint64);
        TXs[SEND_FLOAT] = findViewById(R.id.TXfloat);
        TXs[SEND_DOUBLE] = findViewById(R.id.TXdouble);
        if(!pro) {
            TXs[SEND_LONG].setEnabled(pro);
            TXs[SEND_DOUBLE].setEnabled(pro);
            TXs[SEND_LONG].setText("");
            TXs[SEND_DOUBLE].setText("");
            TXs[SEND_LONG].setHint(R.string.NEED_PRO);
            TXs[SEND_DOUBLE].setHint(R.string.NEED_PRO);
        }
//        TX = (EditText)findViewById(TX);
//        TX.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                mDataMDLP.setValue("=>R" + TX.getText() + "\r\n");                     //Set value of MLDP characteristic to send die roll information
//                writeCharacteristic(mDataMDLP);
//                return true;
//            }
//        });
        byteRCV = findViewById(R.id.byteRCV);
		Conect =  findViewById(R.id.Conect);
		Chan_Ser = findViewById(R.id.chan_ser);
        buttSetAllCaps(Chan_Ser);
		Send = findViewById(R.id.Send);
        scro = findViewById(R.id.scro);
        scron = findViewById(R.id.scron);
        //commander = findViewById(R.id.commander);
        commBase = findViewById(R.id.commBase);
        commStaticL = findViewById(R.id.commStaticL);
        commScrollableL = findViewById(R.id.commScrollableL);
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
//        commScroll = findViewById(R.id.commScroll);
        UpdN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updPNum(isChecked);
            }
        });
        Conect.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                hideActionBar();
                return true;
            }
        });
        Send.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                commanderMode();
                return true;
            }
        });
        Chan_Ser.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enterSettings();
                return true;
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sendtypes_array, android.R.layout.simple_spinner_item); // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSendType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
		Chan_Ser.setEnabled(true);
		Send.setEnabled(false);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int ver = shapre.getInt(VER, defversion);
        if(ver != versionCode) {
            editor.putInt(VER, versionCode);
            editor.commit();
            showInstructions();
        }else if(!checked)
            showInstructions();
        UpdN.setChecked(false);
        /*commX = new Button[48];
        for(int i = 0; i < 48; i++) {
            commX[i] = findViewById(commIDs[i]);
            buttSetAllCaps(commX[i]);
            commX[i].setOnLongClickListener(this);
        }*/
        updCommButtons();
        gesDetector = new GestureDetector(this, this);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        timy.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mCount++;
                if(mCount > 10) {
//                    isUpdateable = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!sumTextRXn.equals("")/*isNum*/) {
                                if (nextUpdn) {
                                    RXn.setText(sumTextRXn);
                                    nextUpdn = false;
                                } else
                                    RXn.append(sumTextRXn);
                            }
                            if(!sumTextRX.equals(""))/*} else */{
                                if (nextUpd) {
                                    RX.setText(sumTextRX);
                                    nextUpd = false;
                                } else
                                    RX.append(sumTextRX);
                            }
                            sumTextRX = "";
                            sumTextRXn = "";
                        }
                    });
                    mCount=0;
                }
            }
        },1,10);
        setupActionBar();
        //registerForContextMenu(RX);
        //registerForContextMenu(RXn);
	}

	public void updCommButtons() {
        numCommStat = shapre.getInt(getString(R.string.NUM_COMM_STAT), Configuration.defNumCommStat);
        numCommScroll = shapre.getInt(getString(R.string.NUM_COMM_SCROLL), Configuration.defNumCommScroll);
        cantFastSendTot = numCommStat + numCommScroll;
        commX = new Button[cantFastSendTot];
        commStaticL.removeAllViewsInLayout();
        commScrollableL.removeAllViewsInLayout();
        for(int i = 0; i < cantFastSendTot; i++) {
            commX[i] = new Button(this);
            buttSetAllCaps(commX[i]);
            final int n = i + 1;
            commX[i].setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int commType = COMMT_STRING;
                    boolean N = true;
                    //final int n = nComm(view);
                    if(n != 0) {
                        if(TXs[sendTyp].length() > 0) {
                            String eom = "\r\n";
                            if(!aCRpLF.isChecked())
                                eom = "";
                            String message = TXs[sendTyp].getText().toString() + eom;
                            try {
                                switch (sendTyp) {
                                    case (SEND_TXT): {
                                        editor.putString(comm + n, message);
                                        editor.putString(commN + n, message); //n + " " +
                                        commType = COMMT_STRING;
                                        N = false;
                                        break;
                                    }
                                    case (SEND_BYTE): {
                                        int Messagen = Integer.parseInt(message);
                                        editor.putInt(comm + n, Messagen);
                                        editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_BYTE] + message);
                                        commType = COMMT_INT8;
                                        break;
                                    }
                                    case (SEND_BIN): {
                                        int Messagen = Integer.parseInt(message, 2);
                                        editor.putInt(comm + n, Messagen);
                                        editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_BIN] + message);
                                        commType = COMMT_INT8;
                                        break;
                                    }
                                    case (SEND_HEX): {
                                        int Messagen = Integer.parseInt(message, 16);
                                        editor.putInt(comm + n, Messagen);
                                        editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_HEX] + message);
                                        commType = COMMT_INT8;
                                        break;
                                    }
                                    case (SEND_SHORT): {
                                        int Messagen = Integer.parseInt(message);
                                        editor.putInt(comm + n, Messagen);
                                        editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_SHORT] + message);
                                        commType = COMMT_INT16;
                                        break;
                                    }
                                    case (SEND_INT): {
                                        int Messagen = Integer.parseInt(message);
                                        editor.putInt(comm + n, Messagen);
                                        editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_INT] + message);
                                        commType = COMMT_INT32;
                                        break;
                                    }
                                    case (SEND_LONG): {
                                        long Messagen = Long.parseLong(message);
                                        editor.putLong(comm + n, Messagen);
                                        editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_LONG] + message);
                                        commType = COMMT_INT64;
                                        break;
                                    }
                                    case (SEND_FLOAT): {
                                        float Messagen = Float.parseFloat(message);
                                        editor.putFloat(comm + n, Messagen);
                                        editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_FLOAT] + message);
                                        commType = COMMT_FLOAT;
                                        break;
                                    }
                                    case (SEND_DOUBLE): {
                                        double Messagen = Double.parseDouble(message);
                                        ByteBuffer buffer = ByteBuffer.allocate(8);
                                        buffer.putDouble(Messagen);
                                        buffer.flip();
                                        long Messagenx = buffer.getLong();
                                        editor.putLong(comm + n, Messagenx);
                                        editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_DOUBLE] + message);
                                        commType = COMMT_DOUBLE;
                                        break;
                                    }
                                }
                            } catch (NumberFormatException nEx) {
                                nEx.printStackTrace();
                                Toast.makeText(PrincipalActivity.this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            N = false;
                            editor.putString(comm + n, getResources().getString(R.string.commDVal));
                            editor.putString(commN + n, getResources().getString(R.string.commDVal));
                        }
                        editor.putBoolean(commT + n, N);
                        editor.putInt(commET + n, commType);
                        editor.commit();
                        UcommUI();
                    }
                    return true;
                }
            });
            commX[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //int n = nComm(view);
                    if(n != 0) {
                        int commType = shapre.getInt(commET + n, COMMT_STRING);
                        boolean commN = shapre.getBoolean(commT + n, defBcomm);
                        switch(commType) {
                            case(COMMT_INT8):
                                if(TCOM)
                                    comunicBT.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                                else
                                    comunic.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                                break;
                            case(COMMT_INT16):
                                if(TCOM)
                                    comunicBT.enviar_Int16(shapre.getInt(comm + n, defNcomm));
                                else
                                    comunic.enviar_Int16(shapre.getInt(comm + n, defNcomm));
                                break;
                            case(COMMT_INT32):
                                if(TCOM)
                                    comunicBT.enviar_Int32(shapre.getInt(comm + n, defNcomm));
                                else
                                    comunic.enviar_Int32(shapre.getInt(comm + n, defNcomm));
                                break;
                            case(COMMT_INT64): // Fallthrough
                            case(COMMT_DOUBLE):
                                if(TCOM)
                                    comunicBT.enviar_Int64(shapre.getLong(comm + n, defNcomm));
                                else
                                    comunic.enviar_Int64(shapre.getLong(comm + n, defNcomm));
                                break;
                            case(COMMT_FLOAT):
                                if(TCOM)
                                    comunicBT.enviar_Float(shapre.getFloat(comm + n, defNcomm));
                                else
                                    comunic.enviar_Float(shapre.getFloat(comm + n, defNcomm));
                                break;
                            default:
                                if (!commN){
                                    if (TCOM)
                                        comunicBT.enviar(shapre.getString(comm + n, getString(R.string.commDVal)));
                                    else
                                        comunic.enviar(shapre.getString(comm + n, getString(R.string.commDVal)));
                                }else {
                                    if (TCOM)
                                        comunicBT.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                                    else
                                        comunic.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                                }
                        }
                    }
                }
            });
            if(i < numCommStat) {
                commStaticL.addView(commX[i]);
            }else {
                commScrollableL.addView(commX[i]);
            }
            //commX[i].setVisibility(View.GONE);
            /*if(i < numCommStat && i < 16)
                commX[i].setVisibility(View.VISIBLE);
            else if(i > 15 && i < 16 + numCommScroll)
                commX[i].setVisibility(View.VISIBLE);*/

        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void buttSetAllCaps(Button b) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            b.setAllCaps(false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        gesDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private void updPNum(boolean bool) {
        enNumericRcv = bool;
        both = bool;
        sepLab.setText(R.string.byteRX);
        if (bool)
            byteRCV.setVisibility(View.VISIBLE);
        else
            byteRCV.setVisibility(View.GONE);
    }

    /*@Override
    public boolean onLongClick(View view) {
        int commType = COMMT_STRING;
        boolean N = true;
        int n = nComm(view);
        if(n != 0) {
            if(TXs[sendTyp].length() > 0) {
                String eom = "\r\n";
                if(!aCRpLF.isChecked())
                    eom = "";
                String message = TXs[sendTyp].getText().toString() + eom;
                try {
                    switch (sendTyp) {
                        case (SEND_TXT): {
                            editor.putString(comm + n, message);
                            editor.putString(commN + n, message); //n + " " +
                            commType = COMMT_STRING;
                            N = false;
                            break;
                        }
                        case (SEND_BYTE): {
                            int Messagen = Integer.parseInt(message);
                            editor.putInt(comm + n, Messagen);
                            editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_BYTE] + message);
                            commType = COMMT_INT8;
                            break;
                        }
                        case (SEND_BIN): {
                            int Messagen = Integer.parseInt(message, 2);
                            editor.putInt(comm + n, Messagen);
                            editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_BIN] + message);
                            commType = COMMT_INT8;
                            break;
                        }
                        case (SEND_HEX): {
                            int Messagen = Integer.parseInt(message, 16);
                            editor.putInt(comm + n, Messagen);
                            editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_HEX] + message);
                            commType = COMMT_INT8;
                            break;
                        }
                        case (SEND_SHORT): {
                            int Messagen = Integer.parseInt(message);
                            editor.putInt(comm + n, Messagen);
                            editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_SHORT] + message);
                            commType = COMMT_INT16;
                            break;
                        }
                        case (SEND_INT): {
                            int Messagen = Integer.parseInt(message);
                            editor.putInt(comm + n, Messagen);
                            editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_INT] + message);
                            commType = COMMT_INT32;
                            break;
                        }
                        case (SEND_LONG): {
                            long Messagen = Long.parseLong(message);
                            editor.putLong(comm + n, Messagen);
                            editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_LONG] + message);
                            commType = COMMT_INT64;
                            break;
                        }
                        case (SEND_FLOAT): {
                            float Messagen = Float.parseFloat(message);
                            editor.putFloat(comm + n, Messagen);
                            editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_FLOAT] + message);
                            commType = COMMT_FLOAT;
                            break;
                        }
                        case (SEND_DOUBLE): {
                            double Messagen = Double.parseDouble(message);
                            ByteBuffer buffer = ByteBuffer.allocate(8);
                            buffer.putDouble(Messagen);
                            buffer.flip();
                            long Messagenx = buffer.getLong();
                            editor.putLong(comm + n, Messagenx);
                            editor.putString(commN + n, getResources().getStringArray(R.array.sendtypes_array)[SEND_DOUBLE] + message);
                            commType = COMMT_DOUBLE;
                            break;
                        }
                    }
                } catch (NumberFormatException nEx) {
                    nEx.printStackTrace();
                    Toast.makeText(PrincipalActivity.this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
                }
            }else {
                N = false;
                editor.putString(comm + n, getResources().getString(R.string.commDVal));
                editor.putString(commN + n, getResources().getString(R.string.commDVal));
            }
            editor.putBoolean(commT + n, N);
            editor.putInt(commET + n, commType);
            editor.commit();
            UcommUI();
        }
        return true;
    }*/

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

    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //user has long pressed your TextView
        //menu.add(0, v.getId(), 0, "Copied");
        Toast.makeText(this, getText(R.string.textCopied), Toast.LENGTH_SHORT).show();

        //cast the received View to TextView so that you can get its text
        TextView yourTextView = (TextView) v;

        //place your TextView's text in clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            //clipboard.setText(yourTextView.getText());
            clipboard.setPrimaryClip(ClipData.newPlainText(getText(R.string.textCopied), yourTextView.getText()));
        }
    }*/

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
                editor.putBoolean(abH, abHidden);
                editor.commit();
            }
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_principal, menu);
		return true;
	}

    public void setRcvTtxt(View view) {
        enNumericRcv = false;
        both = false;
        upd = false;
        advRcv = false;
        layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.GONE);
        scro.setVisibility(View.VISIBLE);
    }

    public void setRcvTint8(View view) {
        enNumericRcv = true;
        both = false;
        upd = false;
        advRcv = false;
        sepLab.setText(R.string.byteRX);
        layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.VISIBLE);
        scro.setVisibility(View.GONE);
    }

    public void setRcvTboth(View view) {
        enNumericRcv = true;
        both = true;
        upd = false;
        advRcv = false;
        sepLab.setText(R.string.byteRX);
        layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.VISIBLE);
        scro.setVisibility(View.VISIBLE);
    }

    public void setRcvTupd(View view) {
        enNumericRcv = false;
        both = false;
        upd = true;
        UpdN.setEnabled(true);
        UpdN.setChecked(false);
        advRcv = false;
        layNAct.setVisibility(View.VISIBLE);
        byteRCV.setVisibility(View.GONE);
        scro.setVisibility(View.VISIBLE);
    }

    public void setRcvTint16(View view) {
        dataBytes = new byte[2];
        dataNBytes = 2;
        enNumericRcv = true;
        dataRcvtyp = COMMT_INT16;
        advRcv = true;
        both = false;
//        upd = false;
        sepLab.setText(R.string.shortRX);
        UpdN.setEnabled(false);
//            layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.VISIBLE);
        scro.setVisibility(View.GONE);
    }

    public void setRcvTint32(View view) {
        dataBytes = new byte[4];
        dataNBytes = 4;
        enNumericRcv = true;
        dataRcvtyp = COMMT_INT32;
        advRcv = true;
        both = false;
//        upd = false;
        sepLab.setText(R.string.intRX);
        UpdN.setEnabled(false);
//            layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.VISIBLE);
        scro.setVisibility(View.GONE);
    }

    public void setRcvTint64(View view) {
        if(pro) {
            dataBytes = new byte[8];
            dataNBytes = 8;
            enNumericRcv = true;
            dataRcvtyp = COMMT_INT64;
            advRcv = true;
            both = false;
            //upd = false;
            sepLab.setText(R.string.longRX);
            UpdN.setEnabled(false);
            //layNAct.setVisibility(View.GONE);
            byteRCV.setVisibility(View.VISIBLE);
            scro.setVisibility(View.GONE);
        }else
            Toast.makeText(this, R.string.NEED_PRO, Toast.LENGTH_SHORT).show();
    }

    public void setRcvTfloat(View view) {
        dataBytes = new byte[4];
        dataNBytes = 4;
        enNumericRcv = true;
        dataRcvtyp = COMMT_FLOAT;
        advRcv = true;
        both = false;
//        upd = false;
        sepLab.setText(R.string.floatRX);
        UpdN.setEnabled(false);
//            layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.VISIBLE);
        scro.setVisibility(View.GONE);
    }

    public void setRcvTdouble(View view) {
        if(pro) {
            dataBytes = new byte[8];
            dataNBytes = 8;
            enNumericRcv = true;
            dataRcvtyp = COMMT_DOUBLE;
            advRcv = true;
            both = false;
            //upd = false;
            sepLab.setText(R.string.doubleRX);
            UpdN.setEnabled(false);
            //layNAct.setVisibility(View.GONE);
            byteRCV.setVisibility(View.VISIBLE);
            scro.setVisibility(View.GONE);
        }else
            Toast.makeText(this, R.string.NEED_PRO, Toast.LENGTH_SHORT).show();
    }

    public void setSendType(int sendType) {
        if(sendTyp != sendType) {
            sendTyp = sendType;
//            TX.setText("");
            aCRpLF.setEnabled(false);
            for(int i = 0; i < cantDataTyp; i++)
                TXs[i].setVisibility(View.GONE);
            TXs[sendType].setVisibility(View.VISIBLE);
            TXs[sendType].requestFocus();
            imm.showSoftInput(TXs[sendType], InputMethodManager.SHOW_IMPLICIT);
            if(sendType==SEND_TXT)
                aCRpLF.setEnabled(true);
//            switch (sendType) {
//                case (SEND_TXT): {
//                    TX.setHint(R.string.VagosCORP);
//                    TX.setInputType(InputType.TYPE_CLASS_TEXT);
//                    aCRpLF.setEnabled(true);
//                    break;
//                }
//                case (SEND_BYTE): {
//                    TX.setHint(R.string.Text_TXn);
//                    TX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
//                    break;
//                }
//                case (SEND_BIN): {
//                    TX.setHint(R.string.Text_TXb);
//                    TX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
//                    break;
//                }
//                case (SEND_HEX): {
//                    TX.setHint(R.string.Text_TXh);
//                    TX.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//                    break;
//                }
//                case (SEND_SHORT): {
//                    TX.setHint(R.string.Text_TXn);
//                    TX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
//                    break;
//                }
//                case (SEND_INT): {
//                    TX.setHint(R.string.Text_TXn);
//                    TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    break;
//                }
//                case (SEND_FLOAT): {
//                    TX.setHint(R.string.Text_TXf);
//                    TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
//                            InputType.TYPE_NUMBER_FLAG_SIGNED);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT |
//                        InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                    break;
//                }
//            }
        }
    }

    void commanderMode() {
        if(!CM) {
            CM = true;
//            item.setTitle(R.string.exitCommMode);
//            commScroll.setVisibility(View.VISIBLE);
            commBase.setVisibility(View.VISIBLE);
            /*if(pro)
                commander.setVisibility(View.VISIBLE);*/
        }else {
            CM = false;
//            item.setTitle(R.string.commMode);
//            commScroll.setVisibility(View.GONE);
            commBase.setVisibility(View.GONE);
            //commander.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.rcvText:
                setRcvTtxt(null);
                return true;
            case R.id.rcvNum:
                setRcvTint8(null);
                return true;
            case R.id.rcvBoth:
                setRcvTboth(null);
                return true;
            case R.id.rcvUpd:
                setRcvTupd(null);
                return true;
            case R.id.shortRcv:
                setRcvTint16(null);
                return true;
            case R.id.intRcv:
                setRcvTint32(null);
                return true;
            case R.id.longRcv:
                setRcvTint64(null);
                return true;
            case R.id.floatRcv:
                setRcvTfloat(null);
                return true;
            case R.id.doubleRcv:
                setRcvTdouble(null);
                return true;
            case R.id.viewInstructions:
                showInstructions();
                return true;
            case R.id.XtringMode:
                enterXtringMode();
                return true;
            /*case R.id.themeDark:
                themeSavePref(true);
                return true;
            case R.id.themeNormal:
                themeSavePref(false);
                return true;*/
            case R.id.commMode:
                commanderMode();
                return true;
            /*case R.id.bigEndian:
                changeEndian(true);
                return true;
            case R.id.littleEndian:
                changeEndian(false);
                return true;*/
            case R.id.action_settings:
                enterSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyEndian() {
        littleEndian = shapre.getBoolean(getString(R.string.LITTLE_ENDIAN), false);
        comunicBT.littleEndian = littleEndian;
        comunic.littleEndian = littleEndian;
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

    void showInstructions() {
        Intent enableIntent = new Intent(this, InstructionsActivity.class);
        startActivityForResult(enableIntent, SHOW_INSTRUCTIONS);
    }

    private void initBTD(BluetoothDevice[] BonDev) {
		myName = BTAdapter.getName();
		myAddress = BTAdapter.getAddress();
		if(BonDev.length > 0) {
			if(BonDev.length <= index)
				index = 0;
			mDevice = BonDev[index];
            Conect.setEnabled(true);
            Chan_Ser.setEnabled(true);
            String tempString = mDevice.getName() + "\n" + mDevice.getAddress();
			if(SC == MainActivity.SERVER) {
                tempString = myName + "\n" + myAddress;
                Chan_Ser.setEnabled(false);
            }
            Chan_Ser.setText(tempString);
		}else {
            Chan_Ser.setText(R.string.NoPD);
            Chan_Ser.setEnabled(false);
            Conect.setEnabled(false);
		}
	}
	
	@Override
	protected void onResume() {
        if(TCOM)
            resumeBT(shapre);
        else
            resumeW(shapre);
        clearTXAS = shapre.getBoolean(getString(R.string.CLEAR_TX_AFTER_SEND), false);
        applyEndian();
        updCommButtons();
        UcommUI();
		super.onResume();
	}

    private void resumeW(SharedPreferences shapre) {
//    NetworkInfo nWI = CTM.getActiveNetworkInfo();
//    if(!NWiFi && (!WFM.isWifiEnabled() || !(nWI != null && nWI.getState() ==
//            NetworkInfo.State.CONNECTED))) {
//        WFM.setWifiEnabled(true);
//        Intent enableIntent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
//        startActivityForResult(enableIntent, REQUEST_ENABLE_WIFI);
//    }else {
        myIP = defMyIP;
        try {
            for (NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress addr : Collections.list(intf.getInetAddresses())) {
                    if (!addr.isLoopbackAddress()){
                        myIP = addr.getHostAddress();
//                          textStatus.append("\n" + addr.getHostName() );
//                          textStatus.append("\n" + addr.getCanonicalHostName() );
//                          textStatus.append("\n\n" + intf.toString() );
//                          textStatus.append("\n\n" + intf.getName() );
//                          textStatus.append("\n\n" + intf.isUp() );
                    }
                }
            }
//            if(myIP.equals(defMyIP)) {
////                Toast.makeText(this, R.string.EnWF, Toast.LENGTH_SHORT).show();
////                    finish();
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        if(myIP == defMyIP && WFM.isWifiEnabled()) {}
//        int ipAddress = WFM.getConnectionInfo().getIpAddress();
//        myIP = Formatter.formatIpAddress(ipAddress);
//        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
//            ipAddress = Integer.reverseBytes(ipAddress);
//        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();
//        try {
//            myIP = InetAddress.getByAddress(ipByteArray).getHostAddress();
//        }catch (UnknownHostException ex) {
////          Log.e("WIFIIP", "Unable to get host address.");
//            myIP = "0.0.0.0";
//        }
//    }
        serverip = shapre.getString(SI, defIP);
        serverport = shapre.getInt(SP, defPort);
        String tempString = serverip + ":" + serverport;
        if(SC == MainActivity.SERVER)
            tempString = myIP + ":" + serverport;
        Chan_Ser.setText(tempString);
    }

    private void resumeBT(SharedPreferences shapre) {
        index = shapre.getInt(indev, defIndex);
        if (BTAdapter.isEnabled()) {
            BondedDevices = BTAdapter.getBondedDevices().toArray(
                    new BluetoothDevice[BTAdapter.getBondedDevices().size()]);
            initBTD(BondedDevices);
        } else {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(this, R.string.EnBT, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    BondedDevices = BTAdapter.getBondedDevices().toArray(
                            new BluetoothDevice[BTAdapter.getBondedDevices().size()]);
                    initBTD(BondedDevices);
                }
                break;
            case SEL_BT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    index = data.getIntExtra(SDev, defIndex);
                    editor.putInt(indev, index);
                    editor.commit();
                    mDevice = BondedDevices[index];
                    String tempString = mDevice.getName() + "\n" + mDevice.getAddress();
                    Chan_Ser.setText(tempString);
                }
                break;
            case REQUEST_ENABLE_WIFI:
                if(!WFM.isWifiEnabled()/*resultCode != Activity.RESULT_OK*/) {
                    Toast.makeText(this, R.string.EnWF, Toast.LENGTH_SHORT).show();
    //                finish();
                }else {
                    NetworkInfo nWI = CTM.getActiveNetworkInfo();
                    if(!(nWI != null && nWI.getType() == ConnectivityManager.TYPE_WIFI &&
                            nWI.getState() == NetworkInfo.State.CONNECTED)){
                        Toast.makeText(this, R.string.EWF, Toast.LENGTH_SHORT).show();
    //                    finish();
                    }
                }
    //            NWiFi = true;
                break;
            case REQUEST_CHANGE_SERVER:
                if (resultCode == Activity.RESULT_OK) {
                    serverip = data.getStringExtra(NSI);
                    serverport = data.getIntExtra(NSP, defPort);
                    editor.putString(SI, serverip);
                    editor.putInt(SP, serverport);
                    editor.commit();
                    String tempString = "";
                    if (SC == MainActivity.CLIENT) {
                        tempString = serverip + ":" + serverport;
                        Chan_Ser.setText(tempString);
                    }else if(SC == MainActivity.SERVER) {
                        if(serverport < 1100)
                            Toast.makeText(this, R.string.shortServerPort, Toast.LENGTH_SHORT).show();
                        tempString = myIP + ":" + serverport;
                        Chan_Ser.setText(tempString);
                    }
                }
                break;
            case SHOW_INSTRUCTIONS:
                if(resultCode == Activity.RESULT_OK) {
                    checked = data.getBooleanExtra(SIoS, false);
                    editor.putBoolean(PrincipalActivity.SIoS, checked);
                    editor.commit();
                }
                break;
            case ENTER_XTRING:
                if(resultCode == Activity.RESULT_OK) {
                    byte[] newTX = data.getByteArrayExtra(XtringActivity.NEWTX);
                    String newTXs = data.getStringExtra(XtringActivity.NEWTXs);
                    if(TCOM) {
                        //comunicBT.enviar(newTXs);
                        comunicBT.enviar_ByteArray(newTX);
                    }else {
                        //comunic.enviar(newTXs);
                        comunic.enviar_ByteArray(newTX);
                    }
                    boolean XReturn = data.getBooleanExtra(XtringActivity.XRETURN,false);
                    if(XReturn)
                        enterXtringMode();
                }
                break;
            case ENTER_CONFIG:
                if(resultCode == Activity.RESULT_OK) {
                    darkTheme = shapre.getBoolean(getString(R.string.DARK_THEME), true);
                    clearTXAS = shapre.getBoolean(getString(R.string.CLEAR_TX_AFTER_SEND), false);
                    applyEndian();
                    updCommButtons();
                }
                break;
		}
	}

	@Override
	protected void onDestroy() {
        if(TCOM)
            comunicBT.Detener_Actividad();
		else
            comunic.Detener_Actividad();
		super.onDestroy();
	}
	
	/*public int nComm(View view) {
        int num = 0;
		for(int i = 0; i < 48; i++) {
		    if(view.getId() == commIDs[i]) {
                num = i + 1;
                break;
            }
        }
		return num;
	}*/
	
	/*public void commClick(View view) {
		int n = nComm(view);
		if(n != 0) {
            int commType = shapre.getInt(commET + n, COMMT_STRING);
            boolean commN = shapre.getBoolean(commT + n, defBcomm);
            switch(commType) {
                case(COMMT_INT8):
                    if(TCOM)
                        comunicBT.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                    else
                        comunic.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                    break;
                case(COMMT_INT16):
                    if(TCOM)
                        comunicBT.enviar_Int16(shapre.getInt(comm + n, defNcomm));
                    else
                        comunic.enviar_Int16(shapre.getInt(comm + n, defNcomm));
                    break;
                case(COMMT_INT32):
                    if(TCOM)
                        comunicBT.enviar_Int32(shapre.getInt(comm + n, defNcomm));
                    else
                        comunic.enviar_Int32(shapre.getInt(comm + n, defNcomm));
                    break;
                case(COMMT_INT64): // Fallthrough
                case(COMMT_DOUBLE):
                    if(TCOM)
                        comunicBT.enviar_Int64(shapre.getLong(comm + n, defNcomm));
                    else
                        comunic.enviar_Int64(shapre.getLong(comm + n, defNcomm));
                    break;
                case(COMMT_FLOAT):
                    if(TCOM)
                        comunicBT.enviar_Float(shapre.getFloat(comm + n, defNcomm));
                    else
                        comunic.enviar_Float(shapre.getFloat(comm + n, defNcomm));
                    break;
                default:
                    if (!commN){
                        if (TCOM)
                            comunicBT.enviar(shapre.getString(comm + n, getString(R.string.commDVal)));
                        else
                            comunic.enviar(shapre.getString(comm + n, getString(R.string.commDVal)));
                    }else {
                        if (TCOM)
                            comunicBT.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                        else
                            comunic.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                    }
            }
		}
	}*/
	
	public void UcommUI() {
		for(int i = 0; i < cantFastSendTot; i++) {
		    int num = i + 1;
            commX[i].setText(shapre.getString(commN + num, getResources().getString(R.string.commDVal)));
        }
	}

	public void Chan_Ser(View view) {
        if(TCOM) {
            if (BondedDevices.length > 0) {
                int deviceCount = BondedDevices.length;

                if (mDeviceIndex < deviceCount)
                    mDevice = BondedDevices[mDeviceIndex];
                else {
                    mDeviceIndex = 0;
                    mDevice = BondedDevices[0];
                }
                DdeviceNames = new String[deviceCount];
                int i = 0;
                for (BluetoothDevice device : BondedDevices) {
                    DdeviceNames[i++] = device.getName() + "\n"
                            + device.getAddress();
                }
                Intent sel_dev = new Intent(this, Device_List.class);
                sel_dev.putExtra(LD, DdeviceNames);
                startActivityForResult(sel_dev, SEL_BT_DEVICE);
            } else
                Toast.makeText(this, R.string.NoPD, Toast.LENGTH_SHORT).show();
        }else {
            Intent CS = new Intent(this, Set_Server.class);
            CS.putExtra(SI, serverip);
            if(SC == MainActivity.SERVER)
                CS.putExtra(SnIP, true);
            CS.putExtra(SP, serverport);
            startActivityForResult(CS, REQUEST_CHANGE_SERVER);
        }
	}

	public void conect(View view) {
        if(TCOM) {
//            mBluetoothGatt = mDevice.connectGatt(this, false, mGattCallback);
//				mBluetoothGatt.disconnect();
            if (comunicBT.estado == comunicBT.NULL) {
                if (SC == MainActivity.CLIENT) {
                    comunicBT = new ComunicBT(this, mDevice);
                } else if (SC == MainActivity.SERVER) {
                    comunicBT = new ComunicBT(this, BTAdapter);
                }
                comunicBT.setComunicationListener(this);
                comunicBT.setConnectionListener(this);
                comunicBT.littleEndian = littleEndian;
                Chan_Ser.setEnabled(false);
                Conect.setText(getString(R.string.Button_Conecting));
                comunicBT.execute();
            } else
                comunicBT.Detener_Actividad();
        }else {
            if(myIP.equals(defMyIP)) {
                Intent enableIntent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
                startActivityForResult(enableIntent, REQUEST_ENABLE_WIFI);
            }else {
                if (comunic.estado == comunic.NULL) {
                    if (SC == MainActivity.CLIENT) {
                        comunic = new Comunic(this, serverip, serverport);
                    } else if (SC == MainActivity.SERVER) {
                        comunic = new Comunic(this, serverport);
                    }
                    comunic.setComunicationListener(this);
                    comunic.setConnectionListener(this);
                    comunic.littleEndian = littleEndian;
                    Chan_Ser.setEnabled(false);
                    Conect.setText(getString(R.string.Button_Conecting));
                    comunic.execute();
                }else
                    comunic.Detener_Actividad();
            }
        }
	}

	public void enviar(View view) {
        if (TXs[sendTyp].length() > 0) {
            String message = TXs[sendTyp].getText().toString();
            try {
                switch (sendTyp) {
                    case (SEND_TXT): {
                        String eom = "\r\n";
                        if(!aCRpLF.isChecked())
                            eom = "";
                        if(TCOM) {
                            comunicBT.enviar(message + eom);//???
                        }else {
                            comunic.enviar(message + eom);//???
                        }
                        break;
                    }
                    case (SEND_BYTE): {
                        int Messagen = Integer.parseInt(message);
                        if(TCOM)
                            comunicBT.enviar_Int8(Messagen);
                        else
                            comunic.enviar_Int8(Messagen);
                        break;
                    }
                    case (SEND_BIN): {
                        int Messagen = Integer.parseInt(message, 2);
                        if(TCOM)
                            comunicBT.enviar_Int8(Messagen);
                        else
                            comunic.enviar_Int8(Messagen);
                        break;
                    }
                    case (SEND_HEX): {
                        int Messagen = Integer.parseInt(message, 16);
                        if(TCOM)
                            comunicBT.enviar_Int8(Messagen);
                        else
                            comunic.enviar_Int8(Messagen);
                        break;
                    }
                    case (SEND_SHORT): {
                        int Messagen = Integer.parseInt(message);
                        if(TCOM)
                            comunicBT.enviar_Int16(Messagen);
                        else
                            comunic.enviar_Int16(Messagen);
                        break;
                    }
                    case (SEND_INT): {
                        int Messagen = Integer.parseInt(message);
                        if(TCOM)
                            comunicBT.enviar_Int32(Messagen);
                        else
                            comunic.enviar_Int32(Messagen);
                        break;
                    }
                    case (SEND_LONG): {
                        long Messagen = Long.parseLong(message);
                        if(TCOM)
                            comunicBT.enviar_Int64(Messagen);
                        else
                            comunic.enviar_Int64(Messagen);
                        break;
                    }
                    case (SEND_FLOAT): {
                        float Messagen = Float.parseFloat(message);
                        if(TCOM)
                            comunicBT.enviar_Float(Messagen);
                        else
                            comunic.enviar_Float(Messagen);
                        break;
                    }
                    case (SEND_DOUBLE): {
                        double Messagen = Double.parseDouble(message);
                        if(TCOM)
                            comunicBT.enviar_Double(Messagen);
                        else
                            comunic.enviar_Double(Messagen);
                        break;
                    }
                }
            } catch (NumberFormatException nEx) {
                nEx.printStackTrace();
                Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
            }
        }else if(sendTyp == SEND_TXT && aCRpLF.isChecked()) {
            if (TCOM) {
                comunicBT.enviar_Int8(13);
                comunicBT.enviar_Int8(10);
            } else {
                comunic.enviar_Int8(13);
                comunic.enviar_Int8(10);
            }
        }
        if(clearTXAS)
            BTX(null);
	}

	public void BTX(View view) { //Borrar TX
//        for(int i = 0; i< 9; i++)
//            TXs[i].setText("");
        TXs[sendTyp].setText("");
	}

	public void BRX(View view) { //Borrar RX
        if(both) {
            RX.setText("");
            RXn.setText("");
        }else if(!enNumericRcv)
            RX.setText("");
        else
            RXn.setText("");
	}

	private void printOnTV(final boolean isNum, TextView textView, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isNum)
                    sumTextRXn += text;
                else
                    sumTextRX += text;
            }
        });
//	    if(isUpdateable) {
//            isUpdateable = false;
//            if (!sumTextRXn.equals("")/*isNum*/) {
//                if (nextUpdn) {
//                    RXn.setText(sumTextRXn);
//                    nextUpdn = false;
//                } else
//                    RXn.append(sumTextRXn);
//            }
//            if(!sumTextRX.equals(""))/*} else */{
//                if (nextUpd) {
//                    RX.setText(sumTextRX);
//                    nextUpd = false;
//                } else
//                    RX.append(sumTextRX);
//            }
//            sumTextRX = "";
//            sumTextRXn = "";
//      }
    }

    @Override
	public void onDataReceived(final int nbytes, String dato, int[] ndato, final byte[] bdato) {
        printOnTV(false, RX, dato);
        if (enNumericRcv) {
            String hexStr;
            String binStr;
            if (advRcv) {
                for (int i = 0; i < nbytes; i++) {
                    if (!dataInit) {
                        if (bdato[i] == charInit)
                            dataInit = true;
                    } else {
                        if (dataCont < dataNBytes) {
                            dataBytes[dataCont] = bdato[i];
                            dataCont++;
                        } else {
                            if (bdato[i] == charEnd) {
                                ByteBuffer byteBuffer = ByteBuffer.wrap(dataBytes);
                                byte[] myBytes;
                                ByteBuffer bbb;
                                switch (dataRcvtyp) {
                                    case (COMMT_INT16):
                                        short messageShort = byteBuffer.getShort();
                                        if(littleEndian) {
                                            myBytes = ByteBuffer.allocate(2).putShort(messageShort).array();
                                            bbb = ByteBuffer.wrap(myBytes);
                                            bbb.order(ByteOrder.LITTLE_ENDIAN);
                                            messageShort = bbb.getShort();
                                        }
                                        hexStr = String.format("0x%04x", messageShort);
                                        binStr = String.format("0b%16s", Integer.toBinaryString(messageShort)).replace(" ", "0");
                                        printOnTV(true, RXn, messageShort + " ");
                                        //printOnTV(true, RXn, hexStr + " ");
                                        break;
                                    case (COMMT_INT32):
                                        int messageInt = byteBuffer.getInt();
                                        if(littleEndian) {
                                            myBytes = ByteBuffer.allocate(4).putInt(messageInt).array();
                                            bbb = ByteBuffer.wrap(myBytes);
                                            bbb.order(ByteOrder.LITTLE_ENDIAN);
                                            messageInt = bbb.getInt();
                                        }
                                        hexStr = String.format("0x%08x", messageInt);
                                        binStr = String.format("0b%32s", Integer.toBinaryString(messageInt)).replace(" ", "0");
                                        printOnTV(true, RXn, messageInt + " ");
                                        //printOnTV(true, RXn, hexStr + " ");
                                        break;
                                    case (COMMT_INT64):
                                        long messageLong = byteBuffer.getLong();
                                        if(littleEndian) {
                                            myBytes = ByteBuffer.allocate(8).putLong(messageLong).array();
                                            bbb = ByteBuffer.wrap(myBytes);
                                            bbb.order(ByteOrder.LITTLE_ENDIAN);
                                            messageLong = bbb.getLong();
                                        }
                                        hexStr = String.format("0x%016x", messageLong);
                                        binStr = String.format("0b%64s", Long.toBinaryString(messageLong)).replace(" ", "0");
                                        printOnTV(true, RXn, messageLong + " ");
                                        //printOnTV(true, RXn, hexStr + " ");
                                        break;
                                    case (COMMT_FLOAT):
                                        float messageFloat = byteBuffer.getFloat();
                                        if(littleEndian) {
                                            myBytes = ByteBuffer.allocate(4).putFloat(messageFloat).array();
                                            bbb = ByteBuffer.wrap(myBytes);
                                            bbb.order(ByteOrder.LITTLE_ENDIAN);
                                            messageFloat = bbb.getFloat();
                                        }
                                        printOnTV(true, RXn, messageFloat + " ");
                                        break;
                                    case (COMMT_DOUBLE):
                                        double messageDouble = byteBuffer.getDouble();
                                        if(littleEndian) {
                                            myBytes = ByteBuffer.allocate(8).putDouble(messageDouble).array();
                                            bbb = ByteBuffer.wrap(myBytes);
                                            bbb.order(ByteOrder.LITTLE_ENDIAN);
                                            messageDouble = bbb.getDouble();
                                        }
                                        printOnTV(true, RXn, messageDouble + " ");
                                        break;
                                }
                            } else
                                RXn.append("Err");
                            dataCont = 0;
                            dataInit = false;
                        }
                    }
                }
            } else {
                for (int val : ndato) {
                    hexStr = String.format("0x%02x", val);
                    binStr = String.format("0b%8s", Integer.toBinaryString(val)).replace(" ", "0");
                    printOnTV(true, RXn, val + " ");
                }
            }
            scron.post(new Runnable() {
                @Override
                public void run() {
                    scron.scrollTo(0, scron.getBottom() + scron.getScrollY());//*/fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
        scro.post(new Runnable() {
            @Override
            public void run() {
                scro.scrollTo(0, scro.getBottom() + scro.getScrollY());//*/fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        if (upd && dato.endsWith("\n")) {
            contUpd++;
            String nup = editNAct.getText().toString();
            if (!nup.equals(""))
                numUpd = Integer.parseInt(nup);
            else
                numUpd = 1;
            if (contUpd >= numUpd) {
                nextUpd = true;
                nextUpdn = true;
                contUpd = 0;
            }
        }
	}

	@Override
	public void onConnectionstablished() {
		Chan_Ser.setEnabled(false);
		Conect.setText(getResources().getString(R.string.Button_DisConect));
		Conect.setEnabled(true);
		Send.setEnabled(true);
	}

	@Override
	public void onConnectionfinished() {
		Conect.setText(getResources().getString(R.string.Button_Conect));
		Conect.setEnabled(true);
        if(!(TCOM && SC == MainActivity.SERVER))
		    Chan_Ser.setEnabled(true);
		Send.setEnabled(false);
	}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float x1 = e1.getX();
        float y1 = e1.getY();
        if(y1 < height*0.20) {
            if(!abHidden) {
                if (velocityY < 0)
                    hideActionBar();
            }else {
                if(velocityY > 0)
                    hideActionBar();
            }
        }else {
            if (!CM) {
                if (x1 > width * 0.85 && velocityX < 0)
                    commanderMode();
            } else {
                if (x1 > width * 0.5 && x1 < width * 0.7 && velocityX > 0)
                    commanderMode();
            }
            if (x1 < width * 0.15 && velocityX > 0)
                enterXtringMode();
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    /*// ----------------------------------------------------------------------------------------------------------------
    // Implements callback methods for GATT events that the app cares about.  For example: connection change and services discovered.
    // When onConnectionStateChange() is called with newState = STATE_CONNECTED then it calls mBluetoothGatt.discoverServices()
    // resulting in another callback to onServicesDiscovered()
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) { //Change in connection state
            if (newState == BluetoothProfile.STATE_CONNECTED) {                         //See if we are connected
                Log.i("Achu", "Connected to GATT server.");
//				mConnected = true;                                                      //Record the new connection state
//				updateConnectionState(R.string.connected);                              //Update the display to say "Connected"
//				invalidateOptionsMenu();                                                //Force the Options menu to be regenerated to show the disconnect option
				mBluetoothGatt.discoverServices();                                      // Attempt to discover services after successful connection.
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {                 //See if we are not connected
                Log.i("Achu", "Disconnected from GATT server.");
//				mConnected = false;                                                     //Record the new connection state
//				updateConnectionState(R.string.disconnected);                           //Update the display to say "Disconnected"
//				invalidateOptionsMenu();                                                //Force the Options menu to be regenerated to show the connect option
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {              //Service discovery complete
            if (status == BluetoothGatt.GATT_SUCCESS && mBluetoothGatt != null) {       //See if the service discovery was successful
				findMldpGattService(mBluetoothGatt.getServices());                      //Get the list of services and call method to look for MLDP service
            }
            else {                                                                      //Service discovery was not successful
                Log.w("Achu", "onServicesDiscovered received: " + status);
            }
        }

        //For information only. This application uses Indication to receive updated characteristic data, not Read
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) { //A request to Read has completed
            if (status == BluetoothGatt.GATT_SUCCESS) {                                 //See if the read was successful
				String dataValue = characteristic.getStringValue(0);                        //Get the value of the characteristic
                RX.append(dataValue);
            // processIncomingPacket(dataValue);                                           //Process the data that was received
            }
        }

        //For information only. This application sends small packets infrequently and does not need to know what the previous write completed
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) { //A request to Write has completed
//			if (status == BluetoothGatt.GATT_SUCCESS) {                                 //See if the write was successful
////				writeComplete = true;                                                   //Record that the write has completed
//			}
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) { //Indication or notification was received
			String dataValue = characteristic.getStringValue(0);                        //Get the value of the characteristic
//			processIncomingPacket(dataValue);                                           //Process the data that was received
            RX.append(dataValue);
        }
    };

    // ----------------------------------------------------------------------------------------------------------------
    // Write to a given characteristic. The completion of the write is reported asynchronously through the
    // BluetoothGattCallback onCharacteristicWrire callback method.
    private void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (BTAdapter == null || mBluetoothGatt == null) {                      //Check that we have access to a Bluetooth radio
            Log.w("Achu", "BluetoothAdapter not initialized");
            return;
        }
        int test = characteristic.getProperties();                                      //Get the properties of the characteristic
        if ((test & BluetoothGattCharacteristic.PROPERTY_WRITE) == 0 && (test & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0) { //Check that the property is writable
            return;
        }

        if (mBluetoothGatt.writeCharacteristic(characteristic)) {                       //Request the BluetoothGatt to do the Write
            Log.d("Achu", "writeCharacteristic successful");                               //The request was accepted, this does not mean the write completed
        }
        else {
            Log.d("Achu", "writeCharacteristic failed");                                   //Write request was not accepted by the BluetoothGatt
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Request a read of a given BluetoothGattCharacteristic. The Read result is reported asynchronously through the
    // BluetoothGattCallback onCharacteristicRead callback method.
    // For information only. This application uses Indication to receive updated characteristic data, not Read

    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (BTAdapter == null || mBluetoothGatt == null) {                      //Check that we have access to a Bluetooth radio
            Log.w("Achu", "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);                              //Request the BluetoothGatt to Read the characteristic
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Iterate through the supported GATT Services/Characteristics to see if the MLDP srevice is supported
    private void findMldpGattService(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {                                                     //Verify that list of GATT services is valid
            Log.d("Achu", "findMldpGattService found no Services");
            return;
        }
        String uuid;                                                                    //String to compare received UUID with desired known UUIDs
        mDataMDLP = null;                                                               //Searching for a characteristic, start with null value

        for (BluetoothGattService gattService : gattServices) {                         //Test each service in the list of services
            uuid = gattService.getUuid().toString();                                    //Get the string version of the service's UUID
            if (uuid.equals(MLDP_PRIVATE_SERVICE)) {                                    //See if it matches the UUID of the MLDP service
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics(); //If so then get the service's list of characteristics
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) { //Test each characteristic in the list of characteristics
                    uuid = gattCharacteristic.getUuid().toString();                     //Get the string version of the characteristic's UUID
                    if (uuid.equals(MLDP_DATA_PRIVATE_CHAR)) {                          //See if it matches the UUID of the MLDP data characteristic
                        mDataMDLP = gattCharacteristic;                                 //If so then save the reference to the characteristic
                        Log.d("Achu", "Found MLDP data characteristics");
                    }
                    else if (uuid.equals(MLDP_CONTROL_PRIVATE_CHAR)) {                  //See if UUID matches the UUID of the MLDP control characteristic
                        mControlMLDP = gattCharacteristic;                              //If so then save the reference to the characteristic
                        Log.d("Achu", "Found MLDP control characteristics");
                    }
                    final int characteristicProperties = gattCharacteristic.getProperties(); //Get the properties of the characteristic
                    if ((characteristicProperties & (BluetoothGattCharacteristic.PROPERTY_NOTIFY)) > 0) { //See if the characteristic has the Notify property
                        mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true); //If so then enable notification in the BluetoothGatt
                        BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(UUID.fromString(CHARACTERISTIC_NOTIFICATION_CONFIG)); //Get the descripter that enables notification on the server
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE); //Set the value of the descriptor to enable notification
                        mBluetoothGatt.writeDescriptor(descriptor);                     //Write the descriptor
                    }
                    if ((characteristicProperties & (BluetoothGattCharacteristic.PROPERTY_INDICATE)) > 0) { //See if the characteristic has the Indicate property
                        mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true); //If so then enable notification (and indication) in the BluetoothGatt
                        BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(UUID.fromString(CHARACTERISTIC_NOTIFICATION_CONFIG)); //Get the descripter that enables indication on the server
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE); //Set the value of the descriptor to enable indication
                        mBluetoothGatt.writeDescriptor(descriptor);                     //Write the descriptor
                    }
                    if ((characteristicProperties & (BluetoothGattCharacteristic.PROPERTY_WRITE)) > 0) { //See if the characteristic has the Write (acknowledged) property
                        gattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT); //If so then set the write type (write with acknowledge) in the BluetoothGatt
                    }
                    if ((characteristicProperties & (BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0) { //See if the characteristic has the Write (unacknowledged) property
                        gattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE); //If so then set the write type (write with no acknowledge) in the BluetoothGatt
                    }
                }
                break;                                                                  //Found the MLDP service and are not looking for any other services
            }
        }
        if (mDataMDLP == null) {                                                        //See if the MLDP data characteristic was not found
            Toast.makeText(this, "mldp_not_supported", Toast.LENGTH_SHORT).show(); //If so then show an error message
                    Log.d("Achu", "findMldpGattService found no MLDP service");
            finish();                                                                   //and end the activity
        }         //Do it after 200ms delay to give the RN4020 time to configure the characteristic
    }*/

}