package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {

	Button CB;
	Button CW;
	Button SB;
	Button SW;
    TextView serverLabel;
    LinearLayout serverBT;
    LinearLayout serverW;
	public static final int CLIENT = 1;
	public static final int SERVER = 2;
	boolean SDread = false;
	boolean SDwrite = false;
	File path;
	File[] fileList;
	String[] fileNames;
	int nfil = 0;
	boolean pro = false;
    boolean mkdirsDone = false;
    int readBuffDone = 0;
    Intent Init;
    String baseVer = "1\n1\n0\n0\n0";
    String config = "config";
    String ext = ".vtconfig";
    String proPack = "com.vagoscorp.virtualterminalprokey";
    String sPath = "/Android/data/com.vagoscorp.vcvt";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_main);
        serverLabel = (TextView)findViewById(R.id.serverLabel);
        serverBT = (LinearLayout)findViewById(R.id.serverBT);
        serverW = (LinearLayout)findViewById(R.id.serverW);
		CB = (Button)findViewById(R.id.Sel_BT);
		CW = (Button)findViewById(R.id.Sel_W);
		SB = (Button)findViewById(R.id.Sel_SBT);
		SW = (Button)findViewById(R.id.Sel_SW);
		path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + sPath);
        mkdirsDone = path.mkdirs();
        Init = new Intent(this, Principal.class);
		getNames();
	}
	
	@Override
	protected void onResume() {
		if(nfil == 0) {
			write(baseVer);
			getNames();
		}
        checkPro();
		super.onResume();
	}

	public void InitBT(View view) {
		Init = new Intent(this, Principal.class);
        Init.putExtra(getString(R.string.Extra_TCOM), true);
        Init.putExtra(getString(R.string.Extra_TYP), CLIENT);
        Init.putExtra(getString(R.string.Extra_LVL), pro);
		startActivity(Init);
	}

	public void InitW(View view) {
		Init = new Intent(this, Principal.class);
        Init.putExtra(getString(R.string.Extra_TCOM), false);
        Init.putExtra(getString(R.string.Extra_TYP), CLIENT);
        Init.putExtra(getString(R.string.Extra_LVL), pro);
		startActivity(Init);
	}

	public void InitBTs(View view) {
        Init = new Intent(this, Principal.class);
        Init.putExtra(getString(R.string.Extra_TCOM), true);
        Init.putExtra(getString(R.string.Extra_TYP), SERVER);
        Init.putExtra(getString(R.string.Extra_LVL), pro);
		startActivity(Init);
	}

	public void InitWs(View view) {
        Init = new Intent(this, Principal.class);
        Init.putExtra(getString(R.string.Extra_TCOM), false);
		Init.putExtra(getString(R.string.Extra_TYP), SERVER);
		Init.putExtra(getString(R.string.Extra_LVL), pro);
		startActivity(Init);
	}
	
	void checkSD() {
		String state = Environment.getExternalStorageState();
        switch (state) {
            case Environment.MEDIA_MOUNTED:
                SDread = true;
                SDwrite = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                SDread = true;
                SDwrite = false;
                break;
            default:
                SDread = false;
                SDwrite = false;
                break;
        }
	}

    void checkPro() {
        Intent intent;
        PackageManager manager = getPackageManager();
        intent = manager.getLaunchIntentForPackage(proPack);
        if(intent != null) {
            serverLabel.setVisibility(View.VISIBLE);
            serverBT.setVisibility(View.VISIBLE);
            serverW.setVisibility(View.VISIBLE);
            pro = true;
        }else {
            processFile(read());
        }
    }
	
	void processFile(String file) {
		String[] enab;
		enab = file.split("\n");
		if(enab.length != 5) {
			write(baseVer);
			getNames();
		}else {
			if(enab[2].equals("1")) {
                serverLabel.setVisibility(View.VISIBLE);
                serverBT.setVisibility(View.VISIBLE);
			}
			if(enab[3].equals("1")) {
                serverLabel.setVisibility(View.VISIBLE);
                serverW.setVisibility(View.VISIBLE);
			}
            pro = enab[4].equals("1");
		}
	}

	void write(/*String name, */String data) {
		checkSD();
		if(SDread && SDwrite) {
			byte[] buff = data.getBytes();
			File file = new File(path, config + ext);
			OutputStream os;
			try {
				os = new FileOutputStream(file);
				os.write(buff);
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	String read(/*String name*/) {
		checkSD();
		String val = "";
		if(SDread) {
			byte[] buff;
			File file = new File(path, config + ext);
			InputStream is;
			try {
				is = new FileInputStream(file);
				buff = new byte[is.available()];
                readBuffDone = is.read(buff);
				is.close();
				val = new String(buff);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return val;
	}

	void getNames() {
		checkSD();
		if(SDread && SDwrite) {
			fileList = path.listFiles();
			int i = 0;
			if(fileList != null)
				nfil = fileList.length;
			else
				nfil = 0;
			if(nfil > 0) {
				fileNames = new String[nfil];
				for(File file:fileList) {
					fileNames[i] = file.getName().split(ext)[0];
				}
			}
		}
	}
}
