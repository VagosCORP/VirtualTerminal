package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Set_Server extends Activity {

	EditText Server_IP;
	EditText Server_Port;

	String IP;
	int Port;
	boolean snip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent Data = getIntent();
		IP = Data.getStringExtra(PrincipalActivity.SI);
		Port = Data.getIntExtra(PrincipalActivity.SP, PrincipalActivity.defPort);
		snip = Data.getBooleanExtra(PrincipalActivity.SnIP, false);
		setContentView(R.layout.set_server);
		Server_IP = (EditText) findViewById(R.id.Server_IP);
		Server_Port = (EditText) findViewById(R.id.Server_Port);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Server_IP.setText(IP);
		Server_Port.setText(Port + "");
		if(snip)
			Server_IP.setVisibility(View.GONE);
	}

	public void Cambiar(View view) {
		final String SIP = Server_IP.getText().toString();
        try {
            final int SPort = Integer.parseInt(Server_Port.getText().toString());
            Intent result = new Intent(PrincipalActivity.RESULT_ACTION);
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
