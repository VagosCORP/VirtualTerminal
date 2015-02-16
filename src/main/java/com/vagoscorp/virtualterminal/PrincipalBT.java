package com.vagoscorp.virtualterminal;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import vclibs.communication.Eventos.OnComunicationListener;
import vclibs.communication.Eventos.OnConnectionListener;
import vclibs.communication.android.ComunicBT;

public class PrincipalBT extends Activity implements OnComunicationListener,OnConnectionListener {

	public TextView RX;// Received Data
    public TextView RXn;// Received Data
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

    public boolean RN;
    public boolean CM;
	public int SC;
    public int sendTyp = TXTSEND;

	public BluetoothAdapter BTAdapter;
	public BluetoothDevice[] BondedDevices;
	public BluetoothDevice mDevice;
	public int mDeviceIndex;
	ComunicBT comunic;

	public int index;
	public String[] DdeviceNames;
	public String myName;
	public String myAddress;

	private final int REQUEST_ENABLE_BT = 1;
	private final int SEL_BT_DEVICE = 2;
	private final int defIndex = 0;

	public static final String LD = "LD";
	public static final String indev = "indev";
    public static final String theme = "Theme";
	public static final String comm = "comm";
	public static final String commN = "commN";
	public static final String commT = "commT";
    public static final String commET = "commET";
	public static final int defNcomm = 0;
	public static final boolean defBcomm = false;
    public static final int TXTSEND = 0;
    public static final int BYTESEND = 1;
    public static final int BINSEND = 2;
    public static final int HEXSEND = 3;
    public static final int SHORTSEND = 11;
    public static final int INTSEND = 12;
    public static final int FLOATSEND = 14;
    public static final int COMMT_STRING = 0;
    public static final int COMMT_INT8 = 1;
    public static final int COMMT_INT16 = 11;
    public static final int COMMT_INT32 = 12;
    public static final int COMMT_FLOAT = 14;
//    public boolean lowLvl = true;
    boolean both = false;
    boolean upd = false;
    boolean nextUpd = false;
    boolean nextUpdn = false;
    int contUpd = 0;
    int numUpd = 1;
	boolean pro = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences shapre = getPreferences(MODE_PRIVATE);
        if(shapre.getBoolean(theme, false))
            this.setTheme(R.style.DarkTheme);
        super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_principal);
        P_LYT = (LinearLayout)findViewById(R.id.P_LYT);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && shapre.getBoolean(theme, false))
            P_LYT.setBackgroundColor(Color.parseColor("#ff303030"));
        comunic = new ComunicBT();
		BTAdapter = BluetoothAdapter.getDefaultAdapter();
		Intent tip = getIntent();
		if (BTAdapter == null) {
			Toast.makeText(PrincipalBT.this, R.string.NB, Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}
		RX = (TextView)findViewById(R.id.RX);
        RXn = (TextView)findViewById(R.id.RXn);
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
                            Toast.makeText(PrincipalBT.this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
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
        RN =  false;
        CM =  false;
		index = defIndex;
		SC = tip.getIntExtra(MainActivity.typ, MainActivity.CLIENT);
		pro = tip.getBooleanExtra(MainActivity.lvl, false);
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
        MenuItem mItem = menu.findItem(R.id.commMode);
        if(pro)
            mItem.setTitle(R.string.commMode);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
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
            case R.id.rcvText: {
                RN = false;
                both = false;
                upd = false;
                if(!CM)
                    layComp.setVisibility(View.GONE);
                layNAct.setVisibility(View.GONE);
                byteRCV.setVisibility(View.GONE);
                scro.setVisibility(View.VISIBLE);
                return true;
            }
            case R.id.rcvNum: {
                RN = true;
                both = false;
                upd = false;
                if(!CM)
                    layComp.setVisibility(View.GONE);
                layNAct.setVisibility(View.GONE);
                byteRCV.setVisibility(View.VISIBLE);
                scro.setVisibility(View.GONE);
                return true;
            }
            case R.id.rcvBoth: {
                RN = true;
                both = true;
                upd = false;
                if(!CM)
                    layComp.setVisibility(View.GONE);
                layNAct.setVisibility(View.GONE);
                byteRCV.setVisibility(View.VISIBLE);
                scro.setVisibility(View.VISIBLE);
                return true;
            }
            case R.id.rcvUpd: {
                RN = false;
                both = false;
                upd = true;
                UpdN.setChecked(false);
                layComp.setVisibility(View.VISIBLE);
                layNAct.setVisibility(View.VISIBLE);
                byteRCV.setVisibility(View.GONE);
                scro.setVisibility(View.VISIBLE);
                return true;
            }
            case R.id.endCOM: {
                comunic.Detener_Actividad();
                return true;
            }
            case R.id.txtSend: {
                sendTyp = TXTSEND;
                TX.setText("");
                TX.setHint(R.string.Text_TX);
                TX.setInputType(InputType.TYPE_CLASS_TEXT);
                inputTyp.setText(R.string.txt);
                return true;
            }
            case R.id.byteSend: {
                sendTyp = BYTESEND;
                TX.setText("");
                TX.setHint(R.string.Text_TXn);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER);
                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                inputTyp.setText(R.string.num);
                return true;
            }
            case R.id.binSend: {
                sendTyp = BINSEND;
                TX.setText("");
                TX.setHint(R.string.Text_TXb);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER);
                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                inputTyp.setText(R.string.bin);
                return true;
            }
            case R.id.hexSend: {
                sendTyp = HEXSEND;
                TX.setText("");
                TX.setHint(R.string.Text_TXh);
                TX.setInputType(InputType.TYPE_CLASS_TEXT);
                inputTyp.setText(R.string.hex);
                return true;
            }
            case R.id.shortSend: {
                sendTyp = SHORTSEND;
                TX.setText("");
                TX.setHint(R.string.Text_TXn);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER);
                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                inputTyp.setText(R.string.num16);
                return true;
            }
            case R.id.intSend: {
                sendTyp = INTSEND;
                TX.setText("");
                TX.setHint(R.string.Text_TXn);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_SIGNED);
                inputTyp.setText(R.string.num32);
                return true;
            }
            case R.id.floatSend: {
                sendTyp = FLOATSEND;
                TX.setText("");
                TX.setHint(R.string.Text_TXf);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
//                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                inputTyp.setText(R.string.fNum);
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
		} else {
			SD.setText(R.string.NoPD);
			Conect.setEnabled(false);
		}
	}
	
	@Override
	protected void onResume() {
		SharedPreferences shapre = getPreferences(MODE_PRIVATE);
        if(shapre.getBoolean(theme, false))
            setTheme(R.style.DarkTheme);
		index = shapre.getInt(indev, defIndex);
		if(SC == MainActivity.SERVER)
			Chan_Ser.setVisibility(View.GONE);//Enabled(false);
		if (BTAdapter.isEnabled()) {
			BondedDevices = BTAdapter.getBondedDevices().toArray(
                    new BluetoothDevice[BTAdapter.getBondedDevices().size()]);
			initBTD(BondedDevices);
		} else {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		UcommUI();
		super.onResume();
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
				index = data.getIntExtra(Device_List.SDev, defIndex);
				SharedPreferences shapre = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = shapre.edit();
				editor.putInt(indev, index);
				editor.commit();
				mDevice = BondedDevices[index];
				SD.setText(mDevice.getName() + "\n" + mDevice.getAddress());
			}
			break;
		}
		}
	}

	@Override
	protected void onDestroy() {
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
                    comunic.enviar_Int8(shapre.getInt(comm + n, defNcomm));
                    break;
                }
                case(COMMT_INT16): {
                    comunic.enviar_Int16(shapre.getInt(comm + n, defNcomm));
                    break;
                }
                case(COMMT_INT32): {
                    comunic.enviar_Int32(shapre.getInt(comm + n, defNcomm));
                    break;
                }
                case(COMMT_FLOAT): {
                    comunic.enviar(shapre.getFloat(comm + n, defNcomm));
                    break;
                }
                default: {
                    if (!commN)
                        comunic.enviar(shapre.getString(comm + n, getResources().getString(R.string.commDVal)));
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
			Intent sel_dev = new Intent(PrincipalBT.this, Device_List.class);
			sel_dev.putExtra(LD, DdeviceNames);
			startActivityForResult(sel_dev, SEL_BT_DEVICE);
		} else
			Toast.makeText(this, R.string.NoPD, Toast.LENGTH_SHORT).show();
	}

	public void conect(View view) {
		if(comunic.estado == comunic.NULL) {
			if(SC == MainActivity.CLIENT) {
				comunic = new ComunicBT(this, mDevice);
			}else if(SC == MainActivity.SERVER) {
				comunic = new ComunicBT(this, BTAdapter);
			}
			comunic.setComunicationListener(this);
			comunic.setConnectionListener(this);
			Chan_Ser.setEnabled(false);
			Conect.setText(getResources().getString(R.string.Button_Conecting));
			comunic.execute();
		}else
			comunic.Detener_Actividad();
	}

	public void enviar(View view) {
        if (TX.length() > 0) {
            String Message = TX.getText().toString();
            try {
                switch (sendTyp) {
                    case (TXTSEND): {
                        comunic.enviar(Message);
                        break;
                    }
                    case (BYTESEND): {
                        int Messagen = Integer.parseInt(Message);
                        comunic.enviar(Messagen);
                        break;
                    }
                    case (BINSEND): {
                        int Messagen = Integer.parseInt(Message, 2);
                        comunic.enviar(Messagen);
                        break;
                    }
                    case (HEXSEND): {
                        int Messagen = Integer.parseInt(Message, 16);
                        comunic.enviar(Messagen);
                        break;
                    }
                    case (SHORTSEND): {
                        int Messagen = Integer.parseInt(Message);
                        comunic.enviar_Int16(Messagen);
                        break;
                    }
                    case (INTSEND): {
                        int Messagen = Integer.parseInt(Message);
                        comunic.enviar_Int32(Messagen);
                        break;
                    }
                    case (FLOATSEND): {
                        float Messagen = Float.parseFloat(Message);
                        comunic.enviar(Messagen);
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

	@Override
	public void onDataReceived(String dato, int[] ndato) {
        if(nextUpd) {
            RX.setText(dato);
            nextUpd = false;
        }else
            RX.append(dato);
        if(RN) {
            for(int val:ndato) {
                if (nextUpdn) {
                    RXn.setText(val + " ");
                    nextUpdn = false;
                }else
                    RXn.append(val + " ");
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