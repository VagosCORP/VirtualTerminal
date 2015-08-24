package com.vagoscorp.virtualterminal;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class XtringActivity extends Activity implements AdapterView.OnItemSelectedListener,CompoundButton.OnCheckedChangeListener {

    LinearLayout layout_commander_x;
    ActionBar actionBar;
    ListView listView;

    int comCont = 0;
    int[] ButtIDs = {R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5,R.id.button6,R.id.button7};
    Button[] Buttons = new Button[7];

    ArrayList<XtringItem> listItems;
    XtringAdapter xtringAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences shapre = getPreferences(MODE_PRIVATE);
        boolean darkTheme = shapre.getBoolean(PrincipalActivity.theme, true);
        if(darkTheme)
            this.setTheme(R.style.DarkTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commander_x);
        layout_commander_x = (LinearLayout)findViewById(R.id.layout_commander_x);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && darkTheme)
            layout_commander_x.setBackgroundColor(Color.parseColor("#ff303030"));
        for(int i = 0; i < 7; i++)
            Buttons[i] = (Button)findViewById(ButtIDs[i]);
        listItems = new ArrayList<XtringItem>(1);
        xtringAdapter = new XtringAdapter(this, listItems);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(xtringAdapter);
        listView.setOnItemSelectedListener(this);
//        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//        for(int i = 1; i < 33; i++) {
//            ListItems[i] = (LinearLayout) findViewById(ListIDs[i]);
//            ListItems[i].setVisibility(View.GONE);
//            Spinners[i] = (Spinner)findViewById(SpinnerIDs[i]);
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                    R.array.sendtypes_array, android.R.layout.simple_spinner_item);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            Spinners[i].setAdapter(adapter);
//            Spinners[i].setOnItemSelectedListener(this);
//            EditTexts[i] = (EditText)findViewById(EditTextIDs[i]);
//            CheckBoxs[i] = (CheckBox)findViewById(CheckBoxIDs[i]);
//            CheckBoxs[i].setOnCheckedChangeListener(this);
//        }

        setupActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_commander_x, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            actionBar = getActionBar();
            if(actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
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
            case R.id.action_settings: {

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    void addItem(int txT) {
        if(comCont < 32) {
            comCont++;
            listItems.add(new XtringItem(txT));
            xtringAdapter.notifyDataSetChanged();
            if(comCont >= 32) {
                for(int i = 0; i < 7; i++)
                    Buttons[i].setEnabled(false);
            }
        }
    }

    public void delItem(View v) {
        if(comCont > 1) {
            listItems.remove(comCont - 1);
            for(int i = 0; i < 7; i++)
                Buttons[i].setEnabled(true);
            comCont--;
        }
//        if(comCont == 0)
//            listItems.clear();
        xtringAdapter.notifyDataSetChanged();
    }

    public void addByte(View v) {
        addItem(PrincipalActivity.SEND_BYTE);
    }

    public void addBin(View v) {
        addItem(PrincipalActivity.SEND_BIN);
    }

    public void addHex(View v) {
        addItem(PrincipalActivity.SEND_HEX);
    }

    public void addString(View v) {
        addItem(PrincipalActivity.SEND_TXT);
    }

    public void addInt16(View v) {
        addItem(PrincipalActivity.SEND_SHORT);
    }

    public void addInt32(View v) {
        addItem(PrincipalActivity.SEND_INT);
    }

    public void addFloat(View v) {
        addItem(PrincipalActivity.SEND_FLOAT);
    }

    boolean check() {
        boolean res = true;
        try {
        for(int i = 0; i < listItems.size(); i++) {
            XtringItem item = listItems.get(i);
            String Message = item.tx;
            item.textViewX.requestFocus();
            if (!Message.equals("")) {

                    switch (item.sendType) {
                        case (PrincipalActivity.SEND_TXT): {

                            break;
                        }
                        case (PrincipalActivity.SEND_BYTE): {
                            int Messagen = Integer.parseInt(Message);

                            break;
                        }
                        case (PrincipalActivity.SEND_BIN): {
                            int Messagen = Integer.parseInt(Message, 2);

                            break;
                        }
                        case (PrincipalActivity.SEND_HEX): {
                            int Messagen = Integer.parseInt(Message, 16);

                            break;
                        }
                        case (PrincipalActivity.SEND_SHORT): {
                            int Messagen = Integer.parseInt(Message);

                            break;
                        }
                        case (PrincipalActivity.SEND_INT): {
                            int Messagen = Integer.parseInt(Message);

                            break;
                        }
                        case (PrincipalActivity.SEND_FLOAT): {
                            float Messagen = Float.parseFloat(Message);

                            break;
                        }
                    }
                }
            }
        } catch (NumberFormatException nEx) {
            nEx.printStackTrace();
            Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
            res = false;
        }
        return res;
    }

    public void enviarX(View v) {
        if(check()) {

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "miau" + position, Toast.LENGTH_SHORT).show();
//        int vID = parent.getId();
//        int index = 0;
//        for(int i = 1; i < 33; i++) {
//            if (vID == SpinnerIDs[i])
//                index = i;
//        }
//        if(index != 0)
//            setSendType(position, index);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case XtringAdapter.XTRING_EDITOR: {
                if (resultCode == Activity.RESULT_OK) {
                    int pos = data.getIntExtra(XtringAdapter.POS, 0);
                    listItems.get(pos).tx = data.getStringExtra(XtringAdapter.NEWTX);
                    xtringAdapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }
}
