package com.vagoscorp.virtualterminal;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.nio.ByteBuffer;

public class XtringActivity extends Activity implements GestureDetector.OnGestureListener{

    LinearLayout layout_xtring;
    LinearLayout xtringList;
    ActionBar actionBar;
    public static int numItems = 10;

    public static final int XTRING_EDITOR = 199;

    public static final String NEWTX = "NEWTX";
    public static final String XRETURN = "XRETURN";
    public static final String POS = "POS";
    public static final String SENDTYPE = "SENDTYPE";
    public static final String sendTypeX = "sendTypeX";
    public static final String valTXX = "valTXX";
    public static final String constantX = "constantX";
    public static final String enabledX = "enabledX";
    public static final String disabledX = "disabledX";

    int[] ListIDs = {0, R.id.XtringItem01,R.id.XtringItem02,R.id.XtringItem03,R.id.XtringItem04,R.id.XtringItem05,
            R.id.XtringItem06,R.id.XtringItem07,R.id.XtringItem08,R.id.XtringItem09,R.id.XtringItem10,R.id.XtringItem11,
            R.id.XtringItem12,R.id.XtringItem13,R.id.XtringItem14,R.id.XtringItem15,R.id.XtringItem16,R.id.XtringItem17,
            R.id.XtringItem18,R.id.XtringItem19,R.id.XtringItem20,R.id.XtringItem21,R.id.XtringItem22,R.id.XtringItem23,
            R.id.XtringItem24,R.id.XtringItem25,R.id.XtringItem26,R.id.XtringItem27,R.id.XtringItem28,R.id.XtringItem29,
            R.id.XtringItem30,R.id.XtringItem31,R.id.XtringItem32,R.id.XtringItem33,R.id.XtringItem34,R.id.XtringItem35,
            R.id.XtringItem36,R.id.XtringItem37,R.id.XtringItem38,R.id.XtringItem39,R.id.XtringItem40,R.id.XtringItem41,
            R.id.XtringItem42,R.id.XtringItem43,R.id.XtringItem44,R.id.XtringItem45,R.id.XtringItem46,R.id.XtringItem47,
            R.id.XtringItem48,R.id.XtringItem49,R.id.XtringItem50,R.id.XtringItem51,R.id.XtringItem52,R.id.XtringItem53,
            R.id.XtringItem54,R.id.XtringItem55,R.id.XtringItem56,R.id.XtringItem57,R.id.XtringItem58,R.id.XtringItem59,
            R.id.XtringItem60,R.id.XtringItem61,R.id.XtringItem62,R.id.XtringItem63,R.id.XtringItem64};
    XtringItem[] listItems;
    XtringItem[] tempListItems;
    GestureDetector gesDetector;
    float height = 0;
    float width = 0;

    int comCont = 0;
    int[] ButtIDs = {R.id.txtButton,R.id.byteButton,R.id.binButton,R.id.hexButton,R.id.shortButton,
            R.id.intButton,R.id.longButton,R.id.floatButton,R.id.doubleButton};
    Button[] Buttons = new Button[9];
    CheckBox XReturn;
    boolean xReturn = false;

    @Override
    protected void onStop() {
        saveAll();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isPro = getIntent().getBooleanExtra(PrincipalActivity.IS_PRO, false);
        boolean darkTheme = getIntent().getBooleanExtra(PrincipalActivity.theme, true);
        if(isPro)
            numItems = 64;
        listItems = new XtringItem[numItems + 1];
        tempListItems = new XtringItem[numItems + 1];
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sendtypes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(darkTheme)
            this.setTheme(R.style.DarkTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xtring);
        layout_xtring = findViewById(R.id.layout_xtring);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && darkTheme)
            layout_xtring.setBackgroundColor(Color.parseColor("#ff303030"));
        for(int i = 0; i < 9; i++)
            Buttons[i] = findViewById(ButtIDs[i]);
        xtringList = findViewById(R.id.xtringList);
        XReturn = findViewById(R.id.xReturn);
        if(isPro)
            XReturn.setVisibility(View.VISIBLE);
        XReturn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                xReturn = isChecked;
            }
        });
        restore(0);
        for(int i = 1; i <= numItems; i++) {
            final XtringItem item = new XtringItem(this, i);
            item.linearLayoutX = findViewById(ListIDs[i]);
            item.enabled = false;
            item.disabled = false;
            item.itemIndeX = item.linearLayoutX.findViewById(R.id.itemIndeX);
            item.spinnerX = item.linearLayoutX.findViewById(R.id.spinnerX);
            item.textViewX = item.linearLayoutX.findViewById(R.id.textViewX);
            item.checkBoxX = item.linearLayoutX.findViewById(R.id.checkBoxX);
            item.disableX = item.linearLayoutX.findViewById(R.id.disableX);
            String tempS = "" + i;
            item.itemIndeX.setText(tempS);
            item.spinnerX.setAdapter(adapter);
            item.spinnerX.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionT, long id) {
                    item.setSendType(positionT, false);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            item.textViewX.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.openEditor();
                }
            });
            item.checkBoxX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setConstant(isChecked);
                }
            });
            item.disableX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setDisabled(isChecked);
                }
            });
            if(isPro)
                item.disableX.setVisibility(View.VISIBLE);
            listItems[i] = item;
            tempListItems[i] = item;
            restore(i);
        }
        gesDetector = new GestureDetector(this, this);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        setupActionBar();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        gesDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    public void restore(int n) {
        SharedPreferences shapre = getPreferences(MODE_PRIVATE);
        if(n > 0) {
            boolean enX = shapre.getBoolean(enabledX + n, false);
            if (enX) {
                boolean disX = shapre.getBoolean(disabledX + n, false);
                int commType = shapre.getInt(sendTypeX + n, PrincipalActivity.SEND_TXT);
                String valTX = shapre.getString(valTXX + n, "");
                boolean constX = shapre.getBoolean(constantX + n, false);
                listItems[n].sendType = commType;
                listItems[n].setSendType(commType, false);
                listItems[n].setvalTX(valTX);
                listItems[n].checkBoxX.setChecked(constX);
                listItems[n].enabled = enX;
                listItems[n].disableX.setChecked(disX);
                listItems[n].linearLayoutX.setVisibility(View.VISIBLE);
                comCont++;
            }
            if(comCont == numItems)
                enableButtons(false);
        }else if(n == 0) {
            xReturn = shapre.getBoolean(XRETURN, false);
            XReturn.setChecked(xReturn);
        }
    }

    public void saveAll() {
        SharedPreferences shapre = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = shapre.edit();
        editor.putBoolean(XRETURN, xReturn);
        for(int n = 1; n <= numItems; n++) {
            editor.putInt(sendTypeX + n, listItems[n].sendType);
            editor.putString(valTXX + n, listItems[n].tx);
            editor.putBoolean(constantX + n, listItems[n].constant);
            editor.putBoolean(enabledX + n, listItems[n].enabled);
            editor.putBoolean(disabledX + n, listItems[n].disabled);
        }
        editor.commit();
    }

    public void save(int n) {
        if(n > 0 && n <= numItems) {
            SharedPreferences shapre = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = shapre.edit();
            editor.putInt(sendTypeX + n, listItems[n].sendType);
            editor.putString(valTXX + n, listItems[n].tx);
            editor.putBoolean(constantX + n, listItems[n].constant);
            editor.putBoolean(enabledX + n, listItems[n].enabled);
            editor.putBoolean(disabledX + n, listItems[n].disabled);
            editor.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_xtring, menu);
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

    void exitXtring() {
        finish();
        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                exitXtring();
                return true;
            }
            case R.id.action_settings: {

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    void enableButtons(boolean en) {
        for(int i = 0; i < 9; i++)
            Buttons[i].setEnabled(en);
    }

    public void addItem(View view) {
        int txT = 0;
        int pressed = view.getId();
        int i = 0;
        for(int id:ButtIDs) {
            if(id == pressed) {
                txT = i;
                break;
            }
            i++;
        }
        if(comCont < numItems) {
            comCont++;
            listItems[comCont].enabled = true;
            listItems[comCont].setSendType(txT, true);
            listItems[comCont].linearLayoutX.setVisibility(View.VISIBLE);
            if(comCont == numItems)
                enableButtons(false);
        }
    }

    public void delItem(View v) {
        if(comCont > 0) {
            listItems[comCont] = tempListItems[comCont];
            listItems[comCont].enabled = false;
            listItems[comCont].setConstant(false);
            listItems[comCont].setDisabled(false);
            listItems[comCont].checkBoxX.setChecked(false);
            listItems[comCont].disableX.setChecked(false);
            listItems[comCont].linearLayoutX.setVisibility(View.GONE);
            enableButtons(true);
            save(comCont);
            comCont--;
        }
    }

    public class CanSendPlusData {
        public boolean can = false;
        public byte[] data;
    }

    public int checkData() {
        int dataCont = 0;
        try {
            for(int i = 1; i <= numItems; i++) {
                if(listItems[i].enabled && !listItems[i].disabled) {
                    String Message = listItems[i].tx;
                    if (!Message.equals("")) {
                        switch (listItems[i].sendType) {
                            case (PrincipalActivity.SEND_TXT): {
                                dataCont += Message.length();
                                break;
                            }
                            case (PrincipalActivity.SEND_BYTE): {
                                int Messagen = Integer.parseInt(Message);
                                dataCont++;
                                break;
                            }
                            case (PrincipalActivity.SEND_BIN): {
                                int Messagen = Integer.parseInt(Message, 2);
                                dataCont++;
                                break;
                            }
                            case (PrincipalActivity.SEND_HEX): {
                                int Messagen = Integer.parseInt(Message, 16);
                                dataCont++;
                                break;
                            }
                            case (PrincipalActivity.SEND_SHORT): {
                                int Messagen = Integer.parseInt(Message);
                                dataCont += 2;
                                break;
                            }
                            case (PrincipalActivity.SEND_INT): {
                                int Messagen = Integer.parseInt(Message);
                                dataCont += 4;
                                break;
                            }
                            case (PrincipalActivity.SEND_LONG): {
                                int Messagen = Integer.parseInt(Message);
                                dataCont += 8;
                                break;
                            }
                            case (PrincipalActivity.SEND_FLOAT): {
                                float Messagen = Float.parseFloat(Message);
                                dataCont += 4;
                                break;
                            }
                            case (PrincipalActivity.SEND_DOUBLE): {
                                float Messagen = Float.parseFloat(Message);
                                dataCont += 8;
                                break;
                            }
                        }
                    }else {
                        listItems[i].openEditor();
                        Toast.makeText(this, R.string.commDVal, Toast.LENGTH_SHORT).show();
                        dataCont = 0;
                        break;
                    }
                }
            }
        } catch (NumberFormatException nEx) {
            nEx.printStackTrace();
            Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
            dataCont = 0;
        }
        return dataCont;
    }

    public CanSendPlusData genBuff() {
        int numDat = checkData();
        CanSendPlusData resp = new CanSendPlusData();
        resp.can = false;
        if(numDat > 0) {
            resp.can = true;
            ByteBuffer byteArray = ByteBuffer.allocate(numDat);
            try {
                for (int i = 1; i < numItems; i++) {
                    if (listItems[i].enabled && !listItems[i].disabled) {
                        String Message = listItems[i].tx;
                        if (!Message.equals("")) {
                            switch (listItems[i].sendType) {
                                case (PrincipalActivity.SEND_TXT): {
                                    byteArray.put(Message.getBytes());
                                    break;
                                }
                                case (PrincipalActivity.SEND_BYTE): {
                                    int Messagen = Integer.parseInt(Message);
                                    byteArray.put((byte) Messagen);
                                    break;
                                }
                                case (PrincipalActivity.SEND_BIN): {
                                    int Messagen = Integer.parseInt(Message, 2);
                                    byteArray.put((byte) Messagen);
                                    break;
                                }
                                case (PrincipalActivity.SEND_HEX): {
                                    int Messagen = Integer.parseInt(Message, 16);
                                    byteArray.put((byte) Messagen);
                                    break;
                                }
                                case (PrincipalActivity.SEND_SHORT): {
                                    int Messagen = Integer.parseInt(Message);
                                    byteArray.putShort((short) Messagen);
                                    break;
                                }
                                case (PrincipalActivity.SEND_INT): {
                                    int Messagen = Integer.parseInt(Message);
                                    byteArray.putInt(Messagen);
                                    break;
                                }
                                case (PrincipalActivity.SEND_LONG): {
                                    long Messagen = Long.parseLong(Message);
                                    byteArray.putLong(Messagen);
                                    break;
                                }
                                case (PrincipalActivity.SEND_FLOAT): {
                                    float Messagen = Float.parseFloat(Message);
                                    byteArray.putFloat(Messagen);
                                    break;
                                }
                                case (PrincipalActivity.SEND_DOUBLE): {
                                    double Messagen = Double.parseDouble(Message);
                                    byteArray.putDouble(Messagen);
                                    break;
                                }
                            }
                        } else {
                            resp.can = false;
                            listItems[i].openEditor();
                            Toast.makeText(this, R.string.commDVal, Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
                resp.data = byteArray.array();
            } catch (NumberFormatException nEx) {
                nEx.printStackTrace();
                resp.can = false;
                Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
            }
        }
        return resp;
    }

    public void enviarX(View v) {
        CanSendPlusData tempData = genBuff();
        if(tempData.can) {
            saveAll();
            Intent resIntent = new Intent(PrincipalActivity.RESULT_ACTION);
            resIntent.putExtra(NEWTX, tempData.data);
            resIntent.putExtra(XRETURN, xReturn);
            setResult(Activity.RESULT_OK, resIntent);
            exitXtring();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case XTRING_EDITOR: {
                if (resultCode == Activity.RESULT_OK) {
                    int pos = data.getIntExtra(POS, 0);
                    listItems[pos].setvalTX(data.getStringExtra(NEWTX));
                    save(pos);
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        exitXtring();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float x1 = e1.getX();
        if(x1 > width*0.85 && velocityX < 0)
            exitXtring();
        return true;
    }

//    public byte [] string2ByteArray(String value) {
//        return value.getBytes();
//    }
//
//    public byte [] byte2ByteArray(int value) {
//        return ByteBuffer.allocate(1).put((byte) value).array();
//    }
//
//    public byte [] short2ByteArray(int value) {
//        return ByteBuffer.allocate(2).putShort((short) value).array();
//    }
//
//    public byte [] int2ByteArray(int value) {
//        return ByteBuffer.allocate(4).putInt(value).array();
//    }
//
//    public byte [] float2ByteArray(float value) {
//        return ByteBuffer.allocate(4).putFloat(value).array();
//    }
}