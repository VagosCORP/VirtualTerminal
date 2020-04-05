package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

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

	public void viewBuild(View view) {
		String versioning = "v" + versionName + " b" + versionCode;
		verLab.setText(versioning);
	}

	public void InitBT(View view) {
		Init = new Intent(this, PrincipalActivity.class);
        Init.putExtra(getString(R.string.Extra_TCOM), true);
        Init.putExtra(getString(R.string.Extra_TYP), CLIENT);
        //Init.putExtra(getString(R.string.Extra_LVL), pro);
		startActivity(Init);
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
        //Init.putExtra(getString(R.string.Extra_LVL), pro);
		startActivity(Init);
	}

	public void InitWs(View view) {
        Init = new Intent(this, PrincipalActivity.class);
        Init.putExtra(getString(R.string.Extra_TCOM), false);
		Init.putExtra(getString(R.string.Extra_TYP), SERVER);
		//Init.putExtra(getString(R.string.Extra_LVL), pro);
		startActivity(Init);
	}
}