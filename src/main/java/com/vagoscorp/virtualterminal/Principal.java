package com.vagoscorp.virtualterminal;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
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
import java.util.Collections;

import vclibs.communication.Eventos.OnComunicationListener;
import vclibs.communication.Eventos.OnConnectionListener;
import vclibs.communication.android.Comunic;
import vclibs.communication.android.ComunicBT;

public class Principal extends Activity implements OnComunicationListener,OnConnectionListener {

    Spinner spinner;
    public TextView RX;// Received Data
    public TextView RXn;// Received Data
    public TextView sepLab;// Received Data
	public EditText TX;// Data to Send
	public Button Conect;
	public Button Chan_Ser;
	public Button Send;
	public TextView SD;
    public TextView inputTyp;
    ScrollView scro;
    ScrollView scron;
    LinearLayout commander;
    LinearLayout commBase;
    ScrollView commScroll;
    LinearLayout byteRCV;
    LinearLayout P_LYT;
    LinearLayout layNAct;
    LinearLayout layComp;
    EditText editNAct;
    CheckBox UpdN;

    Button comm1;
    Button comm2;
    Button comm3;
    Button comm4;
    Button comm5;
    Button comm6;
    Button comm7;
    Button comm8;
    Button comm9;
    Button comm10;
    Button comm11;
    Button comm12;

    public String serverip;// IP to Connect
    public int serverport;// Port to Connect
    public boolean RN;
    public boolean CM;
	public int SC;
    public int sendTyp = TXTSEND;


    public BluetoothAdapter BTAdapter;
	public BluetoothDevice[] BondedDevices;
	public BluetoothDevice mDevice;
	public int mDeviceIndex;
    WifiManager WFM;
    ConnectivityManager CTM;
    String defMyIP = "No IP";
    String myIP = defMyIP;
    Comunic comunic;
	ComunicBT comunicBT;

	public int index;
	public String[] DdeviceNames;
	public String myName;
	public String myAddress;

    private final int REQUEST_ENABLE_BT = 1;
	private final int SEL_BT_DEVICE = 2;
	private final int defIndex = 0;
    private final int REQUEST_ENABLE_WIFI = 15;
    private final int REQUEST_CHANGE_SERVER = 12;

    public static final String SI = "SIP";
    public static final String SP = "SPort";
    public static final String LD = "LD";
	public static final String indev = "indev";
    public static final String theme = "Theme";
	public static final String comm = "comm";
	public static final String commN = "commN";
	public static final String commT = "commT";
    public static final String commET = "commET";
    public static final String defIP = "10.0.0.6";
    public static final int defPort = 2000;
	public static final int defNcomm = 0;
	public static final boolean defBcomm = false;
    public static final int TXTSEND = 0;
    public static final int BYTESEND = 1;
    public static final int BINSEND = 2;
    public static final int HEXSEND = 3;
    public static final int SHORTSEND = 4;
    public static final int INTSEND = 5;
    public static final int LONGSEND = 6;
    public static final int FLOATSEND = 7;
    public static final int COMMT_STRING = 0;
    public static final int COMMT_INT8 = 1;
    public static final int COMMT_INT16 = 11;
    public static final int COMMT_INT32 = 12;
    public static final int COMMT_INT64 = 13;
    public static final int COMMT_FLOAT = 14;
    public static final int COMMT_DUAL = 21;
    public static final int COMMT_UPD = 22;
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
//    boolean NWiFi = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences shapre = getPreferences(MODE_PRIVATE);
        boolean darkTheme = shapre.getBoolean(theme, false);
        if(darkTheme)
            this.setTheme(R.style.DarkTheme);
        super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_principal);
        P_LYT = (LinearLayout)findViewById(R.id.P_LYT);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && darkTheme)
            P_LYT.setBackgroundColor(Color.parseColor("#ff303030"));
        Intent tip = getIntent();
        TCOM = tip.getBooleanExtra(getString(R.string.Extra_TCOM), false);
        SC = tip.getIntExtra(getString(R.string.Extra_TYP), MainActivity.CLIENT);
        pro = tip.getBooleanExtra(getString(R.string.Extra_LVL), false);
        RN =  false;
        CM =  false;
        comunic = new Comunic();
        comunicBT = new ComunicBT();
        if(TCOM) {
            BTAdapter = BluetoothAdapter.getDefaultAdapter();
            index = defIndex;
            if (BTAdapter == null) {
                Toast.makeText(Principal.this, R.string.NB, Toast.LENGTH_SHORT)
                        .show();
                finish();
                return;
            }
        }else {
            WFM = (WifiManager) getSystemService(WIFI_SERVICE);
            CTM = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        }
        spinner = (Spinner)findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sendtypes_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
		RX = (TextView)findViewById(R.id.RX);
        RXn = (TextView)findViewById(R.id.RXn);
        sepLab = (TextView)findViewById(R.id.sepLab);
        layNAct = (LinearLayout)findViewById(R.id.layNAct);
        layComp = (LinearLayout)findViewById(R.id.layComp);
        editNAct = (EditText)findViewById(R.id.editNAct);
        UpdN = (CheckBox)findViewById(R.id.UpdN);
		TX = (EditText)findViewById(R.id.TX);
        byteRCV = (LinearLayout)findViewById(R.id.byteRCV);
		SD = (TextView) findViewById(R.id.label_ser);
		Conect = (Button) findViewById(R.id.Conect);
		Chan_Ser = (Button) findViewById(R.id.chan_ser);
		Send = (Button) findViewById(R.id.Send);
        scro = (ScrollView)findViewById(R.id.scro);
        scron = (ScrollView)findViewById(R.id.scron);
        commander = (LinearLayout)findViewById(R.id.commander);
        commBase = (LinearLayout)findViewById(R.id.commBase);
        commScroll = (ScrollView)findViewById(R.id.commScroll);
        inputTyp = (TextView)findViewById(R.id.inputTyp);
        UpdN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RN = isChecked;
                both = isChecked;
                sepLab.setText(R.string.byteRX);
                if(isChecked)
                    byteRCV.setVisibility(View.VISIBLE);
                else
                    byteRCV.setVisibility(View.GONE);
            }
        });
        OnLongClickListener oLClistener = new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View view) {
                int commType = COMMT_STRING;
                boolean N = true;
                int n = nComm(view);
                if(n != 0) {
                    SharedPreferences shapre = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = shapre.edit();
                    if(TX.length() > 0) {
                        String Message = TX.getText().toString();
                        try {
                            switch (sendTyp) {
                                case (TXTSEND): {
                                    editor.putString(comm + n, Message);
                                    editor.putString(commN + n, Message);
                                    commType = COMMT_STRING;
                                    N = false;
                                    break;
                                }
                                case (BYTESEND): {
                                    int Messagen = Integer.parseInt(Message);
                                    editor.putInt(comm + n, Messagen);
                                    editor.putString(commN + n, getString(R.string.num) + Message);
                                    commType = COMMT_INT8;
                                    break;
                                }
                                case (BINSEND): {
                                    int Messagen = Integer.parseInt(Message, 2);
                                    editor.putInt(comm + n, Messagen);
                                    editor.putString(commN + n, getString(R.string.bin) + Message);
                                    commType = COMMT_INT8;
                                    break;
                                }
                                case (HEXSEND): {
                                    int Messagen = Integer.parseInt(Message, 16);
                                    editor.putInt(comm + n, Messagen);
                                    editor.putString(commN + n, getString(R.string.hex) + Message);
                                    commType = COMMT_INT8;
                                    break;
                                }
                                case (SHORTSEND): {
                                    int Messagen = Integer.parseInt(Message);
                                    editor.putInt(comm + n, Messagen);
                                    editor.putString(commN + n, getString(R.string.num16) + Message);
                                    commType = COMMT_INT16;
                                    break;
                                }
                                case (INTSEND): {
                                    int Messagen = Integer.parseInt(Message);
                                    editor.putInt(comm + n, Messagen);
                                    editor.putString(commN + n, getString(R.string.num32) + Message);
                                    commType = COMMT_INT32;
                                    break;
                                }
                                case (FLOATSEND): {
                                    float Messagen = Float.parseFloat(Message);
                                    editor.putFloat(comm + n, Messagen);
                                    editor.putString(commN + n, getString(R.string.fNum) + Message);
                                    commType = COMMT_FLOAT;
                                    break;
                                }
                            }
                        } catch (NumberFormatException nEx) {
                            nEx.printStackTrace();
                            Toast.makeText(Principal.this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
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
		};
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSendType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        comm1  = (Button)findViewById(R.id.comm1);
        comm2  = (Button)findViewById(R.id.comm2);
        comm3  = (Button)findViewById(R.id.comm3);
        comm4  = (Button)findViewById(R.id.comm4);
        comm5  = (Button)findViewById(R.id.comm5);
        comm6  = (Button)findViewById(R.id.comm6);
        comm7  = (Button)findViewById(R.id.comm7);
        comm8  = (Button)findViewById(R.id.comm8);
        comm9  = (Button)findViewById(R.id.comm9);
        comm10 = (Button)findViewById(R.id.comm10);
        comm11 = (Button)findViewById(R.id.comm11);
        comm12 = (Button)findViewById(R.id.comm12);
        comm1.setOnLongClickListener(oLClistener);
        comm2.setOnLongClickListener(oLClistener);
        comm3.setOnLongClickListener(oLClistener);
        comm4.setOnLongClickListener(oLClistener);
        comm5.setOnLongClickListener(oLClistener);
        comm6.setOnLongClickListener(oLClistener);
        comm7.setOnLongClickListener(oLClistener);
        comm8.setOnLongClickListener(oLClistener);
        comm9.setOnLongClickListener(oLClistener);
        comm10.setOnLongClickListener(oLClistener);
        comm11.setOnLongClickListener(oLClistener);
        comm12.setOnLongClickListener(oLClistener);

		Chan_Ser.setEnabled(true);
		Send.setEnabled(false);
		setupActionBar();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar aB = getActionBar();
			if(aB != null)
				aB.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_principal, menu);
//        MenuItem mItem = menu.findItem(R.id.commMode);
//        if(pro)
//            mItem.setTitle(R.string.commMode);
		return true;
	}

    public void setRcvTtxt(View view) {
        RN = false;
        both = false;
        upd = false;
        advRcv = false;
        if(!CM)
            layComp.setVisibility(View.GONE);
        layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.GONE);
        scro.setVisibility(View.VISIBLE);
    }

    public void setRcvTint8(View view) {
        RN = true;
        both = false;
        upd = false;
        advRcv = false;
        sepLab.setText(R.string.byteRX);
        if(!CM)
            layComp.setVisibility(View.GONE);
        layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.VISIBLE);
        scro.setVisibility(View.GONE);
    }

    public void setRcvTboth(View view) {
        RN = true;
        both = true;
        upd = false;
        advRcv = false;
        sepLab.setText(R.string.byteRX);
        if(!CM)
            layComp.setVisibility(View.GONE);
        layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.VISIBLE);
        scro.setVisibility(View.VISIBLE);
    }

    public void setRcvTupd(View view) {
        RN = false;
        both = false;
        upd = true;
        UpdN.setEnabled(true);
        UpdN.setChecked(false);
        advRcv = false;
        layComp.setVisibility(View.VISIBLE);
        layNAct.setVisibility(View.VISIBLE);
        byteRCV.setVisibility(View.GONE);
        scro.setVisibility(View.VISIBLE);
    }

    public void setRcvTint16(View view) {
        dataBytes = new byte[2];
        dataNBytes = 2;
        RN = true;
        dataRcvtyp = COMMT_INT16;
        advRcv = true;
        both = false;
//                upd = false;
        sepLab.setText(R.string.shortRX);
        UpdN.setEnabled(false);
        if(!CM)
            layComp.setVisibility(View.GONE);
//                layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.VISIBLE);
        scro.setVisibility(View.GONE);
    }

    public void setRcvTint32(View view) {
        dataBytes = new byte[4];
        dataNBytes = 4;
        RN = true;
        dataRcvtyp = COMMT_INT32;
        advRcv = true;
        both = false;
//                upd = false;
        sepLab.setText(R.string.intRX);
        UpdN.setEnabled(false);
        if(!CM)
            layComp.setVisibility(View.GONE);
//                layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.VISIBLE);
        scro.setVisibility(View.GONE);
    }

    public void setRcvTint64(View view) {
        dataBytes = new byte[8];
        dataNBytes = 8;
        RN = true;
        dataRcvtyp = COMMT_INT32;
        advRcv = true;
        both = false;
//                upd = false;
        sepLab.setText(R.string.intRX);
        UpdN.setEnabled(false);
        if(!CM)
            layComp.setVisibility(View.GONE);
//                layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.VISIBLE);
        scro.setVisibility(View.GONE);
    }

    public void setRcvTfloat(View view) {
        dataBytes = new byte[4];
        dataNBytes = 4;
        RN = true;
        dataRcvtyp = COMMT_FLOAT;
        advRcv = true;
        both = false;
//                upd = false;
        sepLab.setText(R.string.floatRX);
        UpdN.setEnabled(false);
        if(!CM)
            layComp.setVisibility(View.GONE);
//                layNAct.setVisibility(View.GONE);
        byteRCV.setVisibility(View.VISIBLE);
        scro.setVisibility(View.GONE);
    }

    public void setSendType(int sendType) {
        if(sendTyp != sendType) {
            sendTyp = sendType;
            TX.setText("");
            switch (sendType) {
                case (TXTSEND): {
                    TX.setHint(R.string.Text_TX);
                    TX.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputTyp.setText(R.string.txt);
                    break;
                }
                case (BYTESEND): {
                    TX.setHint(R.string.Text_TXn);
                    TX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                    inputTyp.setText(R.string.num);
                    break;
                }
                case (BINSEND): {
                    TX.setHint(R.string.Text_TXb);
                    TX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                    inputTyp.setText(R.string.bin);
                    break;
                }
                case (HEXSEND): {
                    TX.setHint(R.string.Text_TXh);
                    TX.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputTyp.setText(R.string.hex);
                    break;
                }
                case (SHORTSEND): {
                    TX.setHint(R.string.Text_TXn);
                    TX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                    inputTyp.setText(R.string.num16);
                    break;
                }
                case (INTSEND): {
                    TX.setHint(R.string.Text_TXn);
                    TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    inputTyp.setText(R.string.num32);
                    break;
                }
                case (FLOATSEND): {
                    TX.setHint(R.string.Text_TXf);
                    TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
                            InputType.TYPE_NUMBER_FLAG_SIGNED);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT |
//                        InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    inputTyp.setText(R.string.fNum);
                    break;
                }
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
            case R.id.rcvText: {
                setRcvTtxt(null);
                return true;
            }
            case R.id.rcvNum: {
                setRcvTint8(null);
                return true;
            }
            case R.id.rcvBoth: {
                setRcvTboth(null);
                return true;
            }
            case R.id.rcvUpd: {
                setRcvTupd(null);
                return true;
            }
            case R.id.shortRcv: {
                setRcvTint16(null);
                return true;
            }
            case R.id.intRcv: {
                setRcvTint32(null);
                return true;
            }
            case R.id.floatRcv: {
                setRcvTfloat(null);
                return true;
            }
            case R.id.txtSend: {
                setSendType(TXTSEND);
                return true;
            }
            case R.id.byteSend: {
                setSendType(BYTESEND);
                return true;
            }
            case R.id.binSend: {
                setSendType(BINSEND);
                return true;
            }
            case R.id.hexSend: {
                setSendType(HEXSEND);
                return true;
            }
            case R.id.shortSend: {
                setSendType(SHORTSEND);
                return true;
            }
            case R.id.intSend: {
                setSendType(INTSEND);
                return true;
            }
            case R.id.floatSend: {
                setSendType(FLOATSEND);
                return true;
            }
            case R.id.themeDark: {
                SharedPreferences shapre = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = shapre.edit();
                editor.putBoolean(theme, true);
                editor.commit();
                Toast.makeText(this, R.string.cThemeToast, Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.themeNormal: {
                SharedPreferences shapre = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = shapre.edit();
                editor.putBoolean(theme, false);
                editor.commit();
                Toast.makeText(this, R.string.cThemeToast, Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.commMode: {
                if(!CM) {
                    CM = true;
                    item.setTitle(R.string.exitCommMode);
                    layComp.setVisibility(View.VISIBLE);
                    commScroll.setVisibility(View.VISIBLE);
                    commBase.setVisibility(View.VISIBLE);
                    if(pro)
                        commander.setVisibility(View.VISIBLE);
                }else {
                    CM = false;
                    item.setTitle(R.string.commMode);
                    if(!upd)
                        layComp.setVisibility(View.GONE);
                    commScroll.setVisibility(View.GONE);
                    commBase.setVisibility(View.GONE);
                    if (pro)
                        commander.setVisibility(View.GONE);
                }
//                Toast.makeText(this, R.string.noPro, Toast.LENGTH_LONG).show();
                return true;
            }
//            case R.id.endCOM: {
//                comunic.Detener_Actividad();
//                return true;
//            }
        }
        return super.onOptionsItemSelected(item);
    }

	private void initBTD(BluetoothDevice[] BonDev) {
		myName = BTAdapter.getName();
		myAddress = BTAdapter.getAddress();
		if (BonDev.length > 0) {
			if (BonDev.length < index)
				index = 0;
			mDevice = BondedDevices[index];
			SD.setText(mDevice.getName() + "\n" + mDevice.getAddress());
			if(SC == MainActivity.SERVER)
				SD.setText(myName + "\n" + myAddress);
			Conect.setEnabled(true);
            Chan_Ser.setVisibility(View.VISIBLE);
		} else {
			SD.setText(R.string.NoPD);
            Chan_Ser.setVisibility(View.GONE);
			Conect.setEnabled(false);
		}
	}
	
	@Override
	protected void onResume() {
		SharedPreferences shapre = getPreferences(MODE_PRIVATE);
        if(shapre.getBoolean(theme, false))
            setTheme(R.style.DarkTheme);
        if(TCOM)
            resumeBT(shapre);
        else
            resumeW(shapre);
		if(SC == MainActivity.SERVER)
			Chan_Ser.setVisibility(View.GONE);
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
//                            textStatus.append("\n" + addr.getHostName() );
//                            textStatus.append("\n" + addr.getCanonicalHostName() );
//                            textStatus.append("\n\n" + intf.toString() );
//                            textStatus.append("\n\n" + intf.getName() );
//                            textStatus.append("\n\n" + intf.isUp() );
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
//        int ipAddress = WFM.getConnectionInfo().getIpAddress();
////      myIP = Formatter.formatIpAddress(ipAddress);
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
        serverip = shapre.getString(getString(R.string.Extra_SI), defIP);
        serverport = shapre.getInt(getString(R.string.Extra_SP), defPort);
        SD.setText(serverip + ":" + serverport);
        if(SC == MainActivity.SERVER)
            SD.setText(myIP + ":" + serverport);
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
		switch (requestCode) {
		case REQUEST_ENABLE_BT: {
			if (resultCode != Activity.RESULT_OK) {
				Toast.makeText(this, R.string.EnBT, Toast.LENGTH_SHORT).show();
				finish();
			} else {
				BondedDevices = BTAdapter.getBondedDevices().toArray(
						new BluetoothDevice[BTAdapter.getBondedDevices().size()]);
				initBTD(BondedDevices);
			}
			break;
		}
		case SEL_BT_DEVICE: {
			if (resultCode == Activity.RESULT_OK) {
				index = data.getIntExtra(getString(R.string.Extra_SDev), defIndex);
				SharedPreferences shapre = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = shapre.edit();
				editor.putInt(indev, index);
				editor.commit();
				mDevice = BondedDevices[index];
				SD.setText(mDevice.getName() + "\n" + mDevice.getAddress());
			}
			break;
		}
        case REQUEST_ENABLE_WIFI: {
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
        }
        case REQUEST_CHANGE_SERVER: {
            if (resultCode == Activity.RESULT_OK) {
                serverip = data.getStringExtra(getString(R.string.Extra_NSI));
                serverport = data.getIntExtra(getString(R.string.Extra_NSP), defPort);
                SharedPreferences shapre = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = shapre.edit();
                editor.putString(getString(R.string.Extra_SI), serverip);
                editor.putInt(getString(R.string.Extra_SP), serverport);
                editor.commit();
                if(SC == MainActivity.CLIENT)
                    SD.setText(serverip + ":" + serverport);
                else if(SC == MainActivity.SERVER)
                    SD.setText(myIP + ":" + serverport);
            }
            break;
        }
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
	
	int nComm(View view) {
		int num = 0;
		switch (view.getId()) {
		case R.id.comm1:{
			num = 1;
			break;
		}
		case R.id.comm2:{
			num = 2;
			break;
		}
		case R.id.comm3:{
			num = 3;
			break;
		}
		case R.id.comm4:{
			num = 4;
			break;
		}
		case R.id.comm5:{
			num = 5;
			break;
		}
		case R.id.comm6:{
			num = 6;
			break;
		}
		case R.id.comm7:{
			num = 7;
			break;
		}
		case R.id.comm8:{
			num = 8;
			break;
		}
		case R.id.comm9:{
			num = 9;
			break;
		}
		case R.id.comm10:{
			num = 10;
			break;
		}
		case R.id.comm11:{
			num = 11;
			break;
						}
		case R.id.comm12:{
			num = 12;
			break;
		}
		}
		return num;
	}
	
	public void commClick(View view) {
		int n = nComm(view);
		if(n != 0) {
			SharedPreferences shapre = getPreferences(MODE_PRIVATE);
            int commType = shapre.getInt(commET + n, COMMT_STRING);
            boolean commN = shapre.getBoolean(commT + n, defBcomm);
            switch(commType) {
                case(COMMT_INT8): {
                    if(TCOM)
                        comunicBT.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                    else
                        comunic.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                    break;
                }
                case(COMMT_INT16): {
                    if(TCOM)
                        comunicBT.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                    else
                        comunic.enviar_Int16(shapre.getInt(comm + n, defNcomm));
                    break;
                }
                case(COMMT_INT32): {
                    if(TCOM)
                        comunicBT.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                    else
                        comunic.enviar_Int32(shapre.getInt(comm + n, defNcomm));
                    break;
                }
                case(COMMT_FLOAT): {
                    if(TCOM)
                        comunicBT.enviar_Float(shapre.getFloat(comm + n, defNcomm));
                    else
                        comunic.enviar_Float(shapre.getFloat(comm + n, defNcomm));
                    break;
                }
                default: {
                    if (!commN)
                        if(TCOM)
                            comunicBT.enviar(shapre.getString(comm + n, getString(R.string.commDVal)));
                        else
                            comunic.enviar(shapre.getString(comm + n, getString(R.string.commDVal)));
                    else
                    if(TCOM)
                        comunicBT.enviar(shapre.getInt(comm + n, defNcomm));
                    else
                        comunic.enviar(shapre.getInt(comm + n, defNcomm));
                }
            }
		}
	}
	
	void UcommUI() {
		SharedPreferences shapre = getPreferences(MODE_PRIVATE);
		comm1.setText(shapre.getString(commN + 1, getResources().getString(R.string.commDVal)));
		comm2.setText(shapre.getString(commN + 2, getResources().getString(R.string.commDVal)));
		comm3.setText(shapre.getString(commN + 3, getResources().getString(R.string.commDVal)));
		comm4.setText(shapre.getString(commN + 4, getResources().getString(R.string.commDVal)));
		comm5.setText(shapre.getString(commN + 5, getResources().getString(R.string.commDVal)));
		comm6.setText(shapre.getString(commN + 6, getResources().getString(R.string.commDVal)));
		comm7.setText(shapre.getString(commN + 7, getResources().getString(R.string.commDVal)));
		comm8.setText(shapre.getString(commN + 8, getResources().getString(R.string.commDVal)));
		comm9.setText(shapre.getString(commN + 9, getResources().getString(R.string.commDVal)));
		comm10.setText(shapre.getString(commN + 10, getResources().getString(R.string.commDVal)));
		comm11.setText(shapre.getString(commN + 11, getResources().getString(R.string.commDVal)));
		comm12.setText(shapre.getString(commN + 12, getResources().getString(R.string.commDVal)));
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
            CS.putExtra(SP, serverport);
            startActivityForResult(CS, REQUEST_CHANGE_SERVER);
        }
	}

	public void conect(View view) {
        if(TCOM) {
            if (comunicBT.estado == comunicBT.NULL) {
                if (SC == MainActivity.CLIENT) {
                    comunicBT = new ComunicBT(this, mDevice);
                } else if (SC == MainActivity.SERVER) {
                    comunicBT = new ComunicBT(this, BTAdapter);
                }
                comunicBT.setComunicationListener(this);
                comunicBT.setConnectionListener(this);
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
                    Chan_Ser.setEnabled(false);
                    Conect.setText(getResources().getString(R.string.Button_Conecting));
                    comunic.execute();
                }else
                    comunic.Detener_Actividad();
            }
        }
	}

	public void enviar(View view) {
        if (TX.length() > 0) {
            String Message = TX.getText().toString();
            try {
                switch (sendTyp) {
                    case (TXTSEND): {
                        if(TCOM)
                            comunicBT.enviar(Message);
                        else
                            comunic.enviar(Message);
                        break;
                    }
                    case (BYTESEND): {
                        int Messagen = Integer.parseInt(Message);
                        if(TCOM)
                            comunicBT.enviar(Messagen);
                        else
                            comunic.enviar(Messagen);
                        break;
                    }
                    case (BINSEND): {
                        int Messagen = Integer.parseInt(Message, 2);
                        if(TCOM)
                            comunicBT.enviar(Messagen);
                        else
                            comunic.enviar(Messagen);
                        break;
                    }
                    case (HEXSEND): {
                        int Messagen = Integer.parseInt(Message, 16);
                        if(TCOM)
                            comunicBT.enviar(Messagen);
                        else
                            comunic.enviar(Messagen);
                        break;
                    }
                    case (SHORTSEND): {
                        int Messagen = Integer.parseInt(Message);
                        if(TCOM)
                            comunicBT.enviar_Int16(Messagen);
                        else
                            comunic.enviar_Int16(Messagen);
                        break;
                    }
                    case (INTSEND): {
                        int Messagen = Integer.parseInt(Message);
                        if(TCOM)
                            comunicBT.enviar_Int32(Messagen);
                        else
                            comunic.enviar_Int32(Messagen);
                        break;
                    }
                    case (FLOATSEND): {
                        float Messagen = Float.parseFloat(Message);
                        if(TCOM)
                            comunicBT.enviar_Float(Messagen);
                        else
                            comunic.enviar_Float(Messagen);
                        break;
                    }
                }
            } catch (NumberFormatException nEx) {
                nEx.printStackTrace();
                Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
            }
        }
	}

	public void BTX(View view) {
		TX.setText("");
	}

	public void BRX(View view) {
        if(both) {
            RX.setText("");
            RXn.setText("");
        }else if(!RN)
            RX.setText("");
        else
            RXn.setText("");
	}

	private void printOnTV(boolean isNum, TextView textView, String text) {
        if(isNum) {
            if (nextUpdn) {
                textView.setText(text);
                nextUpdn = false;
            }
            else
                textView.append(text);
        }else {
            if (nextUpd) {
                textView.setText(text);
                nextUpd = false;
            }
            else
                textView.append(text);
        }
    }

    @Override
	public void onDataReceived(final int nbytes, String dato, int[] ndato, final byte[] bdato) {
        printOnTV(false, RX, dato);
        if(RN) {
            if(advRcv) {
                for(int i = 0; i < nbytes; i++) {
                    if(!dataInit) {
                        if (bdato[i] == charInit)
                            dataInit = true;
                    }else {
                        if(dataCont < dataNBytes) {
                            dataBytes[dataCont] = bdato[i];
                            dataCont++;
                        }else {
                            if(bdato[i] == charEnd) {
                                ByteBuffer byteBuffer = ByteBuffer.wrap(dataBytes);
                                switch (dataRcvtyp) {
                                    case(COMMT_FLOAT): {
                                        printOnTV(true, RXn, byteBuffer.getFloat() + " ");
                                        break;
                                    }
                                    case(COMMT_INT32): {
                                        printOnTV(true, RXn, byteBuffer.getInt() + " ");
                                        break;
                                    }
                                    case(COMMT_INT16): {
                                        printOnTV(true, RXn, byteBuffer.getShort() + " ");
                                        break;
                                    }
                                }
                            }else
                                RXn.append("Err");
                            dataCont = 0;
                            dataInit = false;
                        }
                    }
                }
            }else {
                for (int val : ndato)
                    printOnTV(true, RXn, val + " ");
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
        if(upd && dato.endsWith("\n")) {
            contUpd++;
            String nup = editNAct.getText().toString();
            if(!nup.equals(""))
                numUpd = Integer.parseInt(nup);
            else
                numUpd = 1;
            if(contUpd >= numUpd) {
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
		Chan_Ser.setEnabled(true);
		Send.setEnabled(false);
	}
}