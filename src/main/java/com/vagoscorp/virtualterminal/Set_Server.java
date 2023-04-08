package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Set_Server extends Activity {

	LinearLayout serverMaster;
	Button[] WF_Servers;
	EditText My_IP;
	EditText Server_IP;
	EditText Server_Port;

	String MyNIF;
	String MyIP;
	String IP;
	String[] IP_L;
	String[] IF_L;
	int Port;
	boolean snip;
	int numIntf = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent Data = getIntent();
		IP_L = Data.getStringArrayExtra(PrincipalActivity.SIP_L);
		IF_L = Data.getStringArrayExtra(PrincipalActivity.SIF_L);
		numIntf = IP_L.length;
		MyNIF = Data.getStringExtra(PrincipalActivity.SIF);
		MyIP = Data.getStringExtra(PrincipalActivity.MIP);
		IP = Data.getStringExtra(PrincipalActivity.SI);
		Port = Data.getIntExtra(PrincipalActivity.SP, PrincipalActivity.defPort);
		snip = Data.getBooleanExtra(PrincipalActivity.SnIP, false);
		setContentView(R.layout.set_server);
		serverMaster = findViewById(R.id.serverMaster);
		My_IP = findViewById(R.id.My_IP);
		Server_IP = findViewById(R.id.Server_IP);
		Server_Port = findViewById(R.id.Server_Port);
		loadServers();
	}

	private void loadServers() {
		serverMaster.removeAllViewsInLayout();
		WF_Servers = new Button[numIntf];
		for(int i=0;i<numIntf;i++) {
			final int index = i;
			WF_Servers[i] = new Button(this);
			String temp = IF_L[i]+" : "+IP_L[i];
			WF_Servers[i].setText(temp);
			WF_Servers[i].setEnabled(snip);
			WF_Servers[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyIP = IP_L[index];
					MyNIF = IF_L[index];
					My_IP.setText(MyIP);
				}
			});
			serverMaster.addView(WF_Servers[i]);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		String tempS = Port + "";
		My_IP.setText(MyIP);
		Server_IP.setText(IP);
		Server_Port.setText(tempS);
		if(snip) {
			My_IP.setVisibility(View.VISIBLE);
			Server_IP.setVisibility(View.GONE);
		}else {
			My_IP.setVisibility(View.GONE);
			Server_IP.setVisibility(View.VISIBLE);
		}
	}

	public void Cambiar(View view) {
		final String SIP = Server_IP.getText().toString();
        try {
			final int SPort = Integer.parseInt(Server_Port.getText().toString());
            Intent result = new Intent(PrincipalActivity.RESULT_ACTION);
			result.putExtra(PrincipalActivity.NSIF, MyNIF);
			result.putExtra(PrincipalActivity.NMIP, MyIP);
            result.putExtra(PrincipalActivity.NSI, SIP);
            result.putExtra(PrincipalActivity.NSP, SPort);
            setResult(Activity.RESULT_OK, result);
            finish();
        } catch (NumberFormatException nEx) {
            nEx.printStackTrace();
            Toast.makeText(Set_Server.this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
        }
	}
}
