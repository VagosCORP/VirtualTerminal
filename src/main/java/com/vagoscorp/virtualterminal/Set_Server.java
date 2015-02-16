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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent Data = getIntent();
		IP = Data.getStringExtra(getString(R.string.Extra_SI));
		Port = Data.getIntExtra(getString(R.string.Extra_SP), Principal.defPort);
		setContentView(R.layout.set_server);
		Server_IP = (EditText) findViewById(R.id.Server_IP);
		Server_Port = (EditText) findViewById(R.id.Server_Port);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Server_IP.setText(IP);
		Server_Port.setText(Port + "");
	}

	public void Cambiar(View view) {
		final String SIP = Server_IP.getText().toString();
        try {
            final int SPort = Integer.parseInt(Server_Port.getText().toString());
            Intent result = new Intent(getString(R.string.Extra_RESULT_ACTION));
            result.putExtra(getString(R.string.Extra_NSI), SIP);
            result.putExtra(getString(R.string.Extra_NSP), SPort);
            setResult(Activity.RESULT_OK, result);
            finish();
        } catch (NumberFormatException nEx) {
            nEx.printStackTrace();
            Toast.makeText(Set_Server.this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
        }
	}
}
