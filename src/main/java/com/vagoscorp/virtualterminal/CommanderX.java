package com.vagoscorp.virtualterminal;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class CommanderX extends Activity implements AdapterView.OnItemSelectedListener,CompoundButton.OnCheckedChangeListener {

    public static final int SEND_TXT = 0;
    public static final int SEND_BYTE = 1;
    public static final int SEND_BIN = 2;
    public static final int SEND_HEX = 3;
    public static final int SEND_SHORT = 4;
    public static final int SEND_INT = 5;
    public static final int SEND_FLOAT = 6;
    public static final int SEND_LONG = 7;

    LinearLayout layout_commander_x;
    ActionBar actionBar;
    int comCont = 0;
    int[] sendTyp = new int[33];
    int[] ButtIDs = {R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5,R.id.button6,R.id.button7};
    int[] ListIDs = {0, R.id.ComX1,R.id.ComX2,R.id.ComX3,R.id.ComX4,R.id.ComX5,R.id.ComX6,R.id.ComX7,R.id.ComX8,R.id.ComX9,
            R.id.ComX10,R.id.ComX11,R.id.ComX12,R.id.ComX13,R.id.ComX14,R.id.ComX15,R.id.ComX16,R.id.ComX17,R.id.ComX18,
            R.id.ComX19,R.id.ComX20,R.id.ComX21,R.id.ComX22,R.id.ComX23,R.id.ComX24,R.id.ComX25,R.id.ComX26,R.id.ComX27,
            R.id.ComX28,R.id.ComX29,R.id.ComX30,R.id.ComX31,R.id.ComX32};
    int[] SpinnerIDs = {0, R.id.spinner1,R.id.spinner2,R.id.spinner3,R.id.spinner4,R.id.spinner5,R.id.spinner6,R.id.spinner7,
            R.id.spinner8,R.id.spinner9,R.id.spinner10,R.id.spinner11,R.id.spinner12,R.id.spinner13,R.id.spinner14,
            R.id.spinner15,R.id.spinner16,R.id.spinner17,R.id.spinner18,R.id.spinner19,R.id.spinner20,R.id.spinner21,
            R.id.spinner22,R.id.spinner23,R.id.spinner24,R.id.spinner25,R.id.spinner26,R.id.spinner27,R.id.spinner28,
            R.id.spinner29,R.id.spinner30,R.id.spinner31,R.id.spinner32};
    int[] EditTextIDs = {0, R.id.editText1,R.id.editText2,R.id.editText3,R.id.editText4,R.id.editText5,R.id.editText6,
            R.id.editText7,R.id.editText8,R.id.editText9,R.id.editText10,R.id.editText11,R.id.editText12,R.id.editText13,
            R.id.editText14,R.id.editText15,R.id.editText16,R.id.editText17,R.id.editText18,R.id.editText19,R.id.editText20,
            R.id.editText21,R.id.editText22,R.id.editText23,R.id.editText24,R.id.editText25,R.id.editText26,R.id.editText27,
            R.id.editText28,R.id.editText29,R.id.editText30,R.id.editText31,R.id.editText32};
    int[] CheckBoxIDs = {0, R.id.checkBox1,R.id.checkBox2,R.id.checkBox3,R.id.checkBox4,R.id.checkBox5,R.id.checkBox6,
            R.id.checkBox7,R.id.checkBox8,R.id.checkBox9,R.id.checkBox10,R.id.checkBox11,R.id.checkBox12,R.id.checkBox13,
            R.id.checkBox14,R.id.checkBox15,R.id.checkBox16,R.id.checkBox17,R.id.checkBox18,R.id.checkBox19,R.id.checkBox20,
            R.id.checkBox21,R.id.checkBox22,R.id.checkBox23,R.id.checkBox24,R.id.checkBox25,R.id.checkBox26,R.id.checkBox27,
            R.id.checkBox28,R.id.checkBox29,R.id.checkBox30,R.id.checkBox31,R.id.checkBox32};
    Button[] Buttons = new Button[7];
    LinearLayout[] ListItems = new LinearLayout[33];
    Spinner[] Spinners = new Spinner[33];
    EditText[] EditTexts = new EditText[33];
    CheckBox[] CheckBoxs = new CheckBox[33];

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
        for(int i = 1; i < 33; i++) {
            ListItems[i] = (LinearLayout) findViewById(ListIDs[i]);
            ListItems[i].setVisibility(View.GONE);
            Spinners[i] = (Spinner)findViewById(SpinnerIDs[i]);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.sendtypes_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinners[i].setAdapter(adapter);
            Spinners[i].setOnItemSelectedListener(this);
            EditTexts[i] = (EditText)findViewById(EditTextIDs[i]);
            CheckBoxs[i] = (CheckBox)findViewById(CheckBoxIDs[i]);
            CheckBoxs[i].setOnCheckedChangeListener(this);
        }
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

    void addItem() {
        if(comCont < 32) {
            comCont++;
            ListItems[comCont].setVisibility(View.VISIBLE);
            if(comCont >= 32) {
                for(int i = 0; i < 7; i++)
                    Buttons[i].setEnabled(false);
            }
        }
    }

    public void delItem(View v) {
        if(comCont > 0) {
            ListItems[comCont].setVisibility(View.GONE);
            for(int i = 0; i < 7; i++)
                Buttons[i].setEnabled(true);
            comCont--;
        }
    }

    public void addByte(View v) {
        addItem();
        Spinners[comCont].setSelection(SEND_BYTE);
        setSendType(SEND_BYTE, comCont);
    }

    public void addBin(View v) {
        addItem();
        Spinners[comCont].setSelection(SEND_BIN);
        setSendType(SEND_BIN, comCont);
    }

    public void addHex(View v) {
        addItem();
        Spinners[comCont].setSelection(SEND_HEX);
        setSendType(SEND_HEX, comCont);
    }

    public void addString(View v) {
        addItem();
        Spinners[comCont].setSelection(SEND_TXT);
        setSendType(SEND_TXT, comCont);
    }

    public void addInt16(View v) {
        addItem();
        Spinners[comCont].setSelection(SEND_SHORT);
        setSendType(SEND_SHORT, comCont);
    }

    public void addInt32(View v) {
        addItem();
        Spinners[comCont].setSelection(SEND_INT);
        setSendType(SEND_INT, comCont);
    }

    public void addFloat(View v) {
        addItem();
        Spinners[comCont].setSelection(SEND_FLOAT);
        setSendType(SEND_FLOAT, comCont);
    }

    boolean check() {
        boolean res = true;
        for(int i = 1; i < 33; i++) {
            if (EditTexts[i].length() > 0) {
                String Message = EditTexts[i].getText().toString();
                try {
                    switch (sendTyp[i]) {
                        case (SEND_TXT): {

                            break;
                        }
                        case (SEND_BYTE): {
                            int Messagen = Integer.parseInt(Message);

                            break;
                        }
                        case (SEND_BIN): {
                            int Messagen = Integer.parseInt(Message, 2);

                            break;
                        }
                        case (SEND_HEX): {
                            int Messagen = Integer.parseInt(Message, 16);

                            break;
                        }
                        case (SEND_SHORT): {
                            int Messagen = Integer.parseInt(Message);

                            break;
                        }
                        case (SEND_INT): {
                            int Messagen = Integer.parseInt(Message);

                            break;
                        }
                        case (SEND_FLOAT): {
                            float Messagen = Float.parseFloat(Message);

                            break;
                        }
                    }
                } catch (NumberFormatException nEx) {
                    nEx.printStackTrace();
                    Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
                    res = false;
                }
            }
        }
        return res;
    }

    public void enviarX(View v) {
        boolean gut = check();
        if(gut) {

        }
    }

    public void setSendType(int sendType, int index) {
        if(sendTyp[index] != sendType) {
            sendTyp[index] = sendType;
            EditTexts[index].setText("");
            switch (sendType) {
                case (SEND_TXT): {
                    EditTexts[index].setHint(R.string.VagosCORP);
                    EditTexts[index].setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                }
                case (SEND_BYTE): {
                    EditTexts[index].setHint(R.string.Text_TXn);
                    EditTexts[index].setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                    break;
                }
                case (SEND_BIN): {
                    EditTexts[index].setHint(R.string.Text_TXb);
                    EditTexts[index].setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                    break;
                }
                case (SEND_HEX): {
                    EditTexts[index].setHint(R.string.Text_TXh);
                    EditTexts[index].setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                }
                case (SEND_SHORT): {
                    EditTexts[index].setHint(R.string.Text_TXn);
                    EditTexts[index].setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                    break;
                }
                case (SEND_INT): {
                    EditTexts[index].setHint(R.string.Text_TXn);
                    EditTexts[index].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    break;
                }
                case (SEND_FLOAT): {
                    EditTexts[index].setHint(R.string.Text_TXf);
                    EditTexts[index].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
                            InputType.TYPE_NUMBER_FLAG_SIGNED);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT |
//                        InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    break;
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int vID = parent.getId();
        int index = 0;
        for(int i = 1; i < 33; i++) {
            if (vID == SpinnerIDs[i])
                index = i;
        }
        if(index != 0)
            setSendType(position, index);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}