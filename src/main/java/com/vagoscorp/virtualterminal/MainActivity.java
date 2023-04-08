package com.vagoscorp.virtualterminal;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends Activity implements View.OnLongClickListener {

	Button CB;
	Button CW;
	Button SB;
	Button SW;
	TextView proLabel;
	TextView verLab;
    LinearLayout serverBT;
    LinearLayout serverW;
	public static final int CLIENT = 1;
	public static final int SERVER = 2;
	//public static final String VER = "VER";
	public static final String PRO = "PRO";
	int isPRO = 2015;
	int defver = 20150000;
	boolean pro = false;

    public BluetoothAdapter BTAdapter;

    Intent Init;
	String proNPack = "com.vagoscorp.virtualterminalprokey";
    String proPack = "com.vagoscorp.virtualterminal.prokey";

    int versionCode = defver;
    String versionName = "";

	SharedPreferences shapre;
	SharedPreferences.Editor editor;

	int BT_Permit_Try_Cont = 0;
	boolean askingForBT_PE = false;
	Toast permitBT;
	Toast permitBTx;
	Toast permitBT_NG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Intent launcherIntent = getIntent();
		//SharedPreferences shapre = getPreferences(MODE_PRIVATE);
		shapre = getSharedPreferences(getString(R.string.SHARPREF),MODE_PRIVATE);
		editor = shapre.edit();editor.commit();
		setContentView(R.layout.layout_activity_main);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
		verLab = findViewById(R.id.verLab);
		proLabel = findViewById(R.id.proLabel);
        serverBT = findViewById(R.id.serverBT);
        serverW = findViewById(R.id.serverW);
		CB = findViewById(R.id.Sel_BT);
		CW = findViewById(R.id.Sel_W);
		SB = findViewById(R.id.Sel_SBT);
		SW = findViewById(R.id.Sel_SW);
        Init = new Intent(this, PrincipalActivity.class);
        if(BTAdapter == null) {
            CB.setEnabled(false);
            SB.setEnabled(false);
        }
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
		String versioning = "v" + versionName;
		verLab.setText(versioning);
		isPRO = launcherIntent.getIntExtra(PRO, isPRO);
		pro = shapre.getBoolean(getString(R.string.isPRO), false);
		/*int ver = shapre.getInt(VER, defver);
        if(ver != versionCode) {
			pro = false;
			shapreEditor.putInt(VER, versionCode);
        }*/
		pro = checkPro();
		editor.putBoolean(getString(R.string.isPRO), pro);
		editor.commit();
		if(pro) {
			proLabel.setVisibility(View.GONE);
			serverBT.setVisibility(View.VISIBLE);
			serverW.setVisibility(View.VISIBLE);
		}
		permitBT_NG = Toast.makeText(MainActivity.this, R.string.NBC, Toast.LENGTH_SHORT);
		CB.setOnLongClickListener(this);
		SB.setOnLongClickListener(this);
	}

	@Override
	public boolean onLongClick(View v) {
		if(!checkBTcomPermit(false))
			openSettings();
		return true;
	}

	private void openSettings() {
		permitBT = Toast.makeText(MainActivity.this, R.string.PGPS, Toast.LENGTH_LONG);
		permitBT.show();
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromParts("package", getPackageName(), null);
		intent.setData(uri);
		startActivity(intent);
	}

	@TargetApi(Build.VERSION_CODES.TIRAMISU)
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 1: {
				// If request is cancelled, the result arrays are empty.
				permitBT.cancel();
				permitBTx.cancel();
				permitBT_NG.cancel();
				askingForBT_PE = false;
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission was granted, yay! Do the contacts-related task you need to do.
					Toast.makeText(MainActivity.this, R.string.NBCG, Toast.LENGTH_SHORT).show();
					checkBTcomPermit(true);
				} else {
					// permission denied, boo! Disable the functionality that depends on this permission.
					BT_Permit_Try_Cont++;
					if(BT_Permit_Try_Cont > 4)
						openSettings();
					else {
						permitBT_NG = Toast.makeText(MainActivity.this, R.string.NBC, Toast.LENGTH_SHORT);
						permitBT_NG.show();
					}
//					finish();
				}
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.S)
	private boolean checkBTcomPermit(boolean alsoAsk) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
				CB.setText(R.string.GrantBT_P);
				SB.setText(R.string.GrantBT_P);
				if (alsoAsk && !askingForBT_PE) {
					askingForBT_PE = true;
					permitBT = Toast.makeText(MainActivity.this, R.string.PGP, Toast.LENGTH_LONG);
					permitBTx = Toast.makeText(MainActivity.this, R.string.PGP, Toast.LENGTH_LONG);
					permitBT.show();
					permitBTx.show();
					requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
				}
				return false;
			} else {
				CB.setText(R.string.Button_Sel);
				SB.setText(R.string.Button_Sel);
				return true;
			}
		}else return true;
	}

	@Override
	protected void onResume() {
		checkBTcomPermit(false);
		super.onResume();
	}

	boolean checkPro() {
		PackageManager manager = getPackageManager();
		Intent intent = manager.getLaunchIntentForPackage(proPack);
		Intent intentN = manager.getLaunchIntentForPackage(proNPack);
		boolean itsPro = false;
		if(intentN != null)
			Toast.makeText(this, R.string.UNProPack, Toast.LENGTH_SHORT).show();
		if(intent != null && !pro && isPRO <= 2015) {
			startActivity(intent);
			finish();
		}else if(intent != null)
			itsPro = true;
		return itsPro;
	}

	/*@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void openAppSysSettings() {
		BT_Permit_Try_Cont++;
		if(BT_Permit_Try_Cont > 5) {
			Toast.makeText(MainActivity.this, R.string.PGP, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts("package", getPackageName(), null);
			intent.setData(uri);
			startActivity(intent);
		}
	}*/

	public void viewBuild(View view) {
		String versioning = "v" + versionName + " b" + versionCode;
		verLab.setText(versioning);
	}

	public void InitBT(View view) {
		Init = new Intent(this, PrincipalActivity.class);
        Init.putExtra(getString(R.string.Extra_TCOM), true);
        Init.putExtra(getString(R.string.Extra_TYP), CLIENT);
		if(checkBTcomPermit(true))
			startActivity(Init);
//			openAppSysSettings();
        //Init.putExtra(getString(R.string.Extra_LVL), pro);
	}

	public void InitW(View view) {
		Init = new Intent(this, PrincipalActivity.class);
        Init.putExtra(getString(R.string.Extra_TCOM), false);
        Init.putExtra(getString(R.string.Extra_TYP), CLIENT);
        //Init.putExtra(getString(R.string.Extra_LVL), pro);
		startActivity(Init);
	}

	public void InitBTs(View view) {
        Init = new Intent(this, PrincipalActivity.class);
        Init.putExtra(getString(R.string.Extra_TCOM), true);
        Init.putExtra(getString(R.string.Extra_TYP), SERVER);
		if(checkBTcomPermit(true))
			startActivity(Init);
//			openAppSysSettings();
        //Init.putExtra(getString(R.string.Extra_LVL), pro);
	}

	public void InitWs(View view) {
        Init = new Intent(this, PrincipalActivity.class);
        Init.putExtra(getString(R.string.Extra_TCOM), false);
		Init.putExtra(getString(R.string.Extra_TYP), SERVER);
		//Init.putExtra(getString(R.string.Extra_LVL), pro);
		startActivity(Init);
	}
}