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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class XtringActivity extends Activity implements GestureDetector.OnGestureListener {

    LinearLayout layout_xtring;
    LinearLayout xtringList;
    LinearLayout commBase;
    LinearLayout commStaticL;
    LinearLayout commScrollableL;
    LinearLayout commX_Lay;
    LinearLayout typesLay1;
    LinearLayout typesLay2;
    EditText commX_Name;
    Button newXtringItem;
    InputMethodManager imm;
    ActionBar actionBar;
    public int numItems = 0;
    public static int maxNumItems = 10;

    public static final int XTRING_EDITOR = 199;
    public static final int valXReturn = 0;
    public int numCommStat = Configuration.defNumCommStat;
    public int numCommScroll = Configuration.defNumCommScroll;
    public int numFastSendTot = numCommStat + numCommScroll;

    private final int SHOW_INSTRUCTIONS = 16;
    private final int ENTER_CONFIG = 18;
    private final int ENTER_IO_CONFIG = 19;

    public static final String NEWTX = "NEWTX";
    public static final String NEWTXs = "NEWTXs";
    public static final String XRETURN = "XRETURN";
    public static final String POS = "POS";
    public static final String EXTERNAL = "EXTERNAL";
    public static final String SENDTYPE = "SENDTYPE";
    public static final String sendTypeX = "sendTypeX";
    public static final String sendFormX = "sendFormX";
    public static final String valTXX = "valTXX";
    public static final String constantX = "constantX";
    public static final String enabledX = "enabledX";
    public static final String disabledX = "disabledX";
    public static final String cantXItems = "cantXItems";

    Button[] commX;

    XtringItem[] listItems;
    XtringItem[] tempListItems;
    GestureDetector gesDetector;
    float height = 0;
    float width = 0;

    //int comCont = 0;
    int[] ButtIDs = {R.id.txtButton,R.id.byteButton,/*R.id.binButton,R.id.hexButton,*/R.id.shortButton,
            R.id.intButton,R.id.longButton,R.id.floatButton,R.id.doubleButton, R.id.advButton, R.id.newXtringItem};
    Button[] Buttons = new Button[9];
    CheckBox XReturn;
    Button sendX;
    //ArrayAdapter<CharSequence> adapter;
    boolean xReturn = false;
    boolean pro = false;
    boolean CM = false;

    boolean littleEndian = false;
    int actualTXtype = 0;
    int actualTXform = 0;

    SharedPreferences shapre;
    SharedPreferences.Editor editor;

    public static class CanSendPlusData {
        boolean can = false;
        byte[] data;
    }

    boolean addItemsByType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shapre = getSharedPreferences(getString(R.string.SHARPREF),MODE_PRIVATE);
        editor = shapre.edit();editor.commit();
        boolean darkTheme = shapre.getBoolean(getString(R.string.DARK_THEME), true);
        pro = shapre.getBoolean(getString(R.string.isPRO), false);
        numItems = shapre.getInt(cantXItems, 0);
        if(pro) {
            numCommStat = shapre.getInt(getString(R.string.NUM_COMM_STAT), Configuration.defNumCommStat);
            numCommScroll = shapre.getInt(getString(R.string.NUM_COMM_SCROLL), Configuration.defNumCommScroll);
            littleEndian = shapre.getBoolean(getString(R.string.LITTLE_ENDIAN), false);
        }else if(numItems > 10)
            numItems = 10;
        if(darkTheme)
            this.setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_xtring);
        layout_xtring = findViewById(R.id.layout_xtring);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && darkTheme)
            layout_xtring.setBackgroundColor(Color.parseColor(getString(R.string.DT_Color)));
        commX_Lay = findViewById(R.id.CommX_Lay);
        typesLay1 = findViewById(R.id.TypesLay1);
        typesLay2 = findViewById(R.id.TypesLay2);
        commX_Name = findViewById(R.id.CommX_Name);
        newXtringItem = findViewById(R.id.newXtringItem);
        xtringList = findViewById(R.id.xtringList);
        commBase = findViewById(R.id.commBase);
        commStaticL = findViewById(R.id.commStaticL);
        commScrollableL = findViewById(R.id.commScrollableL);
        XReturn = findViewById(R.id.xReturn);
        sendX = findViewById(R.id.SendX);
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        View.OnLongClickListener disLay = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showAddItemLays();
                return true;
            }
        };
        for(int i = 0; i < 9; i++) {
            Buttons[i] = findViewById(ButtIDs[i]);
            Buttons[i].setOnLongClickListener(disLay);
        }
        Buttons[IOc.TYPE_LONG].setEnabled(pro);
        Buttons[IOc.TYPE_DOUBLE].setEnabled(pro);
        newXtringItem.setOnLongClickListener(disLay);
        if(pro)
            XReturn.setVisibility(View.VISIBLE);
        XReturn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                xReturn = isChecked;
            }
        });
        sendX.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                commanderMode(true);
                return true;
            }
        });
        restore(valXReturn);
        /*adapter = ArrayAdapter.createFromResource(this,
                R.array.sendtypes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
        updateListItems();
        updCommButtons();
        gesDetector = new GestureDetector(this, this);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        setupActionBar();
    }

    void showAddItemLays() {
        addItemsByType = !addItemsByType;
        if(addItemsByType) {
            commX_Lay.setVisibility(View.GONE);
            typesLay1.setVisibility(View.VISIBLE);
            typesLay2.setVisibility(View.VISIBLE);
        }else {
            commX_Lay.setVisibility(View.VISIBLE);
            typesLay1.setVisibility(View.GONE);
            typesLay2.setVisibility(View.GONE);
        }
    }

    void commanderMode(boolean isButton) {
        if(pro && !CM) {
            CM = true;
            commBase.setVisibility(View.VISIBLE);
            commX_Lay.setVisibility(View.VISIBLE);
            typesLay1.setVisibility(View.GONE);
            typesLay2.setVisibility(View.GONE);
            commX_Name.setVisibility(View.VISIBLE);
            addItemsByType =  false;
            updateListItems();
            String tempX = getString(R.string.FastSendButtName) + " " + getString(R.string.Empty);
            commX_Name.requestFocus();
            imm.showSoftInput(commX_Name, InputMethodManager.SHOW_IMPLICIT);
            if(commX_Name.length() <= 0)
                Toast.makeText(XtringActivity.this, tempX, Toast.LENGTH_SHORT).show();
        }else if(pro) {
            CM = false;
            commBase.setVisibility(View.GONE);
            /*commX_Lay.setVisibility(View.GONE);
            typesLay1.setVisibility(View.VISIBLE);
            typesLay2.setVisibility(View.VISIBLE);*/
            commX_Name.setVisibility(View.GONE);
            updateListItems();
            commX_Name.clearFocus();
            imm.hideSoftInputFromWindow(commX_Name.getWindowToken(), 0);
        }else {
            if(!isButton)
                Toast.makeText(this, R.string.NEED_PRO, Toast.LENGTH_SHORT).show();
            else
                enviarX(null);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        gesDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    public void updateListItems() {
        xtringList.removeAllViewsInLayout();
        listItems = new XtringItem[numItems + 1];
        tempListItems = new XtringItem[numItems + 1];
        for(int i = 1; i <= numItems; i++) {
            final XtringItem item = new XtringItem(this, i);
            item.linearLayoutX = (LinearLayout)getLayoutInflater().inflate(R.layout.xtring_item, xtringList, false);
            xtringList.addView(item.linearLayoutX);
            item.disabled = false;
            item.itemIndeX = item.linearLayoutX.findViewById(R.id.itemIndeX);
            //item.spinnerX = item.linearLayoutX.findViewById(R.id.spinnerX);
            item.typeTXB = item.linearLayoutX.findViewById(R.id.typeTXB);
            item.textViewX = item.linearLayoutX.findViewById(R.id.textViewX);
            item.checkBoxX = item.linearLayoutX.findViewById(R.id.checkBoxX);
            item.disableX = item.linearLayoutX.findViewById(R.id.disableX);
            String tempS = i + " ";
            item.itemIndeX.setText(tempS);
            //item.spinnerX.setAdapter(adapter);
            item.typeTXB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //item.setSendType(positionT, false);
                    enterIOConfig(item.position, false);
                }
            });
            /*item.spinnerX.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionT, long id) {
                    item.setSendType(positionT, false);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/
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
                    save(item.position);
                }
            });
            item.disableX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setDisabled(isChecked);
                    save(item.position);
                }
            });
            if(CM) {
                item.checkBoxX.setText(R.string.ComXConstantL);
                item.disableX.setText(R.string.ComXIgnoreL);
            }
            if(pro)
                item.disableX.setVisibility(View.VISIBLE);
            listItems[i] = item;
            tempListItems[i] = item;
            restore(i);
        }
        if(!pro && numItems >= maxNumItems)
            enableButtons(false);
    }

    public void updCommButtons() {
        numCommStat = shapre.getInt(getString(R.string.NUM_COMM_STAT), Configuration.defNumCommStat);
        numCommScroll = shapre.getInt(getString(R.string.NUM_COMM_SCROLL), Configuration.defNumCommScroll);
        numFastSendTot = numCommStat + numCommScroll;
        commX = new Button[numFastSendTot];
        commStaticL.removeAllViewsInLayout();
        commScrollableL.removeAllViewsInLayout();
        for(int i = 0; i < numFastSendTot; i++) {
            commX[i] = new Button(this);
            buttSetAllCaps(commX[i]);
            final int n = i + 1;
            commX[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int commType = PrincipalActivity.COMMT_STRING;
                    boolean N = false;
                    String name = getResources().getString(R.string.commDVal);
                    String message = getResources().getString(R.string.commDVal);
                    if(n != 0) {
                        CanSendPlusData tempData = genBuff();
                        if(tempData.can) {
                            saveAll();
                            boolean mutable = false;
                            try {
                                message = new String(tempData.data, "ISO-8859-1");
                                commType = PrincipalActivity.COMMT_XTRING;
                                mutable = true;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            String theName = "Pack" + n;
                            if(mutable){
                                if(commX_Name.length() > 0) {
                                    name = "Xtr:" + commX_Name.getText().toString();
                                }else {
                                    name = "Xtr:" + theName;
                                    //String tempX = getString(R.string.FastSendButtName) + " " + getString(R.string.Empty);
                                    //commX_Name.requestFocus();
                                    //commX_Name.setText(theName);
                                    commX_Name.setHint(theName);
                                    //commX_Name.clearFocus();
                                    //imm.showSoftInput(commX_Name, InputMethodManager.SHOW_IMPLICIT);

                                }
                                imm.hideSoftInputFromWindow(commX_Name.getWindowToken(), 0);
                                String tempX = getString(R.string.SavedAsFS) + " " + name;
                                Toast.makeText(XtringActivity.this, tempX, Toast.LENGTH_SHORT).show();
                            }
                        }
                        editor.putString(PrincipalActivity.comm + n, message);
                        editor.putString(PrincipalActivity.commN + n, name);
                        editor.putBoolean(PrincipalActivity.commT + n, N);
                        editor.putInt(PrincipalActivity.commET + n, commType);
                        editor.commit();
                        UcommUI();
                    }
                    return true;
                }
            });
            commX[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(XtringActivity.this, R.string.LongClick2Save, Toast.LENGTH_SHORT).show();
                }
            });
            if(i < numCommStat) {
                commStaticL.addView(commX[i]);
            }else {
                commScrollableL.addView(commX[i]);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void buttSetAllCaps(Button b) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            b.setAllCaps(false);
    }

    public void restore(int n) {
        if(n > 0) {
            boolean disX = shapre.getBoolean(disabledX + n, false);
            int commType = shapre.getInt(sendTypeX + n, IOc.TYPE_TEXT);
            int commForm = shapre.getInt(sendFormX + n, IOc.INT_FORM_DEC);
            String valTX = shapre.getString(valTXX + n, "");
            boolean constX = shapre.getBoolean(constantX + n, false);
            //listItems[n].sendType = commType;
            listItems[n].actualTXtype = commType;
            listItems[n].actualTXform = commForm;
            //listItems[n].setSendType(commType, false);
            listItems[n].setSendType(commType, commForm, false);
            listItems[n].setvalTX(valTX);
            listItems[n].checkBoxX.setChecked(constX);
            listItems[n].disableX.setChecked(disX);
        }else if(n == 0) {
            xReturn = shapre.getBoolean(XRETURN, false);
            XReturn.setChecked(xReturn);
        }
    }

    private void theSave(int n) {
        //editor.putInt(sendTypeX + n, listItems[n].sendType);
        editor.putInt(sendTypeX + n, listItems[n].actualTXtype);
        editor.putInt(sendFormX + n, listItems[n].actualTXform);
        editor.putString(valTXX + n, listItems[n].tx);
        editor.putBoolean(constantX + n, listItems[n].constant);
        //editor.putBoolean(enabledX + n, listItems[n].enabled);
        editor.putBoolean(disabledX + n, listItems[n].disabled);
    }

    public void saveAll() {
        editor.putBoolean(XRETURN, xReturn);
        for(int n = 1; n <= numItems; n++) {
            theSave(n);
        }
        editor.putInt(cantXItems, numItems);
        editor.commit();
    }

    public void save(int n) {
        if(n > 0 && n <= numItems) {
            theSave(n);
            editor.putInt(cantXItems, numItems);
            editor.commit();
        }
    }

    public void UcommUI() {
        for(int i = 0; i < numFastSendTot; i++) {
            int num = i + 1;
            commX[i].setText(shapre.getString(PrincipalActivity.commN + num, getResources().getString(R.string.commDVal)));
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
        int selID = item.getItemId();
        if(selID == android.R.id.home)
            exitXtring();
        else if(selID == R.id.saveComm)
            commanderMode(false);
        else if(selID == R.id.showAddItemLays)
            showAddItemLays();
        else if(selID == R.id.viewInstructions)
            enterInstructions();
        else if(selID == R.id.action_settings)
            enterSettings();
        /*switch (item.getItemId()) {
            case android.R.id.home:
                exitXtring();
                return true;
            case R.id.saveComm:
                commanderMode(false);
                return true;
            case R.id.showAddItemLays:
                showAddItemLays();
                return true;
            case R.id.viewInstructions:
                enterInstructions();
                return true;
            case R.id.action_settings:
                enterSettings();
                return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void enterInstructions() {
        Intent instructIntent = new Intent(this, InstructionsActivity.class);
        startActivityForResult(instructIntent, SHOW_INSTRUCTIONS);
    }

    private void enterSettings() {
        Intent startConfig = new Intent(this, Configuration.class);
        startActivityForResult(startConfig, ENTER_CONFIG);
        overridePendingTransition(R.animator.slide_in_right,
                R.animator.slide_out_left);
    }

    void enableButtons(boolean en) {
        newXtringItem.setEnabled(en);
        for(int i = 0; i < 9; i++)
            Buttons[i].setEnabled(en);
        if(!pro) {
            Buttons[IOc.TYPE_LONG].setEnabled(pro);
            Buttons[IOc.TYPE_DOUBLE].setEnabled(pro);
        }
    }
    /*void enableButtonss(boolean en) {
        for(int i = 0; i < 9; i++)
            Buttons[i].setEnabled(en);
        if(!pro) {
            Buttons[6].setEnabled(pro);
            Buttons[8].setEnabled(pro);
        }
    }*/

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
        if(pro || numItems < maxNumItems) {
            numItems++;
            updateListItems();
            save(numItems);
            if(txT > 6)
                enterIOConfig(numItems, true);
            else {
                //listItems[numActualItems].enabled = true;
                listItems[numItems].setSendType(txT, 0, true);
                //listItems[comCont].setVisibility(View.VISIBLE);
            }
        }
    }

    /*public void addItems(View view) {
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
        if(pro || numItems < maxNumItems) {
            numItems++;
            updateListItems();
            //listItems[numActualItems].enabled = true;
            listItems[numItems].setSendType(txT, true);
            //listItems[comCont].setVisibility(View.VISIBLE);
        }
    }*/

    private void enterIOConfig(int itemPos, boolean external) {
        Intent startIOConfig = new Intent(this, IOc.class);
        startIOConfig.putExtra(IOc.CONFIG_ACT, IOc.XTRING_CONFIG);
        startIOConfig.putExtra(IOc.TX_TYPE, listItems[itemPos].actualTXtype);
        startIOConfig.putExtra(IOc.TX_FORM, listItems[itemPos].actualTXform);
        startIOConfig.putExtra(EXTERNAL, external);
        startIOConfig.putExtra(POS, itemPos);
        startActivityForResult(startIOConfig, ENTER_IO_CONFIG);
        overridePendingTransition(R.animator.slide_in_top,
                R.animator.slide_out_bottom);
    }

    public void delItem(View v) {
        if(numItems > 0) {
            listItems[numItems] = tempListItems[numItems];
            //listItems[comCont].enabled = false;
            listItems[numItems].setConstant(false);
            listItems[numItems].setDisabled(false);
            listItems[numItems].checkBoxX.setChecked(false);
            listItems[numItems].disableX.setChecked(false);
            listItems[numItems].actualTXtype = IOc.TYPE_TEXT;
            listItems[numItems].actualTXform = IOc.INT_FORM_DEC;
            listItems[numItems].tx = "";
            //listItems[comCont].linearLayoutX.setVisibility(View.GONE);
            enableButtons(true);
            save(numItems);
            numItems--;
            updateListItems();
        }
    }

    public int checkData() {
        int dataCont = 0;
        int theRadix = 0;
        try {
            for(int i = 1; i <= numItems; i++) {
                theRadix = IOc.radixTX[listItems[i].actualTXform];
                if(/*listItems[i].enabled && */!listItems[i].disabled) {
                    String Message = listItems[i].tx;
                    if (!Message.equals("")) {
                        switch (listItems[i].actualTXtype) {
                            case (IOc.TYPE_TEXT):
                                dataCont += Message.length();
                                break;
                            case (IOc.TYPE_BYTE):
                                Integer.parseInt(Message, theRadix);
                                dataCont++;
                                break;
                            /*case (PrincipalActivity.SEND_BIN):
                                Integer.parseInt(Message, 2);
                                dataCont++;
                                break;
                            case (PrincipalActivity.SEND_HEX):
                                Integer.parseInt(Message, 16);
                                dataCont++;
                                break;*/
                            case (IOc.TYPE_SHORT):
                                Integer.parseInt(Message, theRadix);
                                dataCont += 2;
                                break;
                            case (IOc.TYPE_INT):
                                Integer.parseInt(Message, theRadix);
                                dataCont += 4;
                                break;
                            case (IOc.TYPE_LONG):
                                Long.parseLong(Message, theRadix);
                                dataCont += 8;
                                break;
                            case (IOc.TYPE_FLOAT):
                                Float.parseFloat(Message);
                                dataCont += 4;
                                break;
                            case (IOc.TYPE_DOUBLE):
                                Double.parseDouble(Message);
                                dataCont += 8;
                                break;
                        }
                    }else {
                        listItems[i].openEditor();
                        Toast.makeText(this, R.string.EmptyXtringItem, Toast.LENGTH_SHORT).show();
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

    /*public int checkData() {
        int dataCont = 0;
        try {
            for(int i = 1; i <= numItems; i++) {
                if(!listItems[i].disabled) {
                    String Message = listItems[i].tx;
                    if (!Message.equals("")) {
                        switch (listItems[i].sendType) {
                            case (PrincipalActivity.SEND_TXT):
                                dataCont += Message.length();
                                break;
                            case (PrincipalActivity.SEND_BYTE):
                                Integer.parseInt(Message);
                                dataCont++;
                                break;
                            case (PrincipalActivity.SEND_BIN):
                                Integer.parseInt(Message, 2);
                                dataCont++;
                                break;
                            case (PrincipalActivity.SEND_HEX):
                                Integer.parseInt(Message, 16);
                                dataCont++;
                                break;
                            case (PrincipalActivity.SEND_SHORT):
                                Integer.parseInt(Message);
                                dataCont += 2;
                                break;
                            case (PrincipalActivity.SEND_INT):
                                Integer.parseInt(Message);
                                dataCont += 4;
                                break;
                            case (PrincipalActivity.SEND_LONG):
                                Long.parseLong(Message);
                                dataCont += 8;
                                break;
                            case (PrincipalActivity.SEND_FLOAT):
                                Float.parseFloat(Message);
                                dataCont += 4;
                                break;
                            case (PrincipalActivity.SEND_DOUBLE):
                                Double.parseDouble(Message);
                                dataCont += 8;
                                break;
                        }
                    }else {
                        listItems[i].openEditor();
                        Toast.makeText(this, R.string.EmptyXtringItem, Toast.LENGTH_SHORT).show();
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
    }*/

    public CanSendPlusData genBuff() {
        int numDat = checkData();
        CanSendPlusData resp = new CanSendPlusData();
        resp.can = false;
        if(numDat > 0) {
            resp.can = true;
            ByteBuffer byteArray = ByteBuffer.allocate(numDat);
            int theRadix;
            try {
                for (int i = 1; i <= numItems; i++) {
                    theRadix = IOc.radixTX[listItems[i].actualTXform];
                    if (/*listItems[i].enabled && */!listItems[i].disabled) {
                        String Message = listItems[i].tx;
                        if (!Message.equals("")) {
                            int messageInt;
                            int byteIndex;
                            byte[] dataBytes;
                            switch (listItems[i].actualTXtype) {
                                case (IOc.TYPE_TEXT):
                                    byteArray.put(Message.getBytes());
                                    break;
                                case (IOc.TYPE_BYTE):
                                    messageInt = Integer.parseInt(Message, theRadix);
                                    byteArray.put((byte) messageInt);
                                    break;
                                /*case (PrincipalActivity.SEND_BIN):
                                    messageInt = Integer.parseInt(Message, 2);
                                    byteArray.put((byte) messageInt);
                                    break;
                                case (PrincipalActivity.SEND_HEX):
                                    messageInt = Integer.parseInt(Message, 16);
                                    byteArray.put((byte) messageInt);
                                    break;*/
                                case (IOc.TYPE_SHORT):
                                    messageInt = Integer.parseInt(Message, theRadix);
                                    if(littleEndian) {
                                        dataBytes = ByteBuffer.allocate(4).putInt(messageInt).array();
                                        byteArray.put((byte)(0xff&dataBytes[3]));
                                        byteArray.put((byte)(0xff&dataBytes[2]));
                                    }else
                                        byteArray.putShort((short) messageInt);
                                    break;
                                case (IOc.TYPE_INT):
                                    messageInt = Integer.parseInt(Message, theRadix);
                                    if(littleEndian) {
                                        dataBytes = ByteBuffer.allocate(4).putInt(messageInt).array();
                                        for(byteIndex = 3; byteIndex >= 0; byteIndex--)
                                            byteArray.put((byte)(0xff&dataBytes[i]));
                                    }else
                                        byteArray.putInt(messageInt);
                                    break;
                                case (IOc.TYPE_LONG):
                                    long messageLong = Long.parseLong(Message, theRadix);
                                    if(littleEndian) {
                                        dataBytes = ByteBuffer.allocate(8).putLong(messageLong).array();
                                        for(byteIndex = 7; byteIndex >= 0; byteIndex--)
                                            byteArray.put((byte)(0xff&dataBytes[i]));
                                    }else
                                        byteArray.putLong(messageLong);
                                    break;
                                case (IOc.TYPE_FLOAT):
                                    float messageFloat = Float.parseFloat(Message);
                                    if(littleEndian) {
                                        dataBytes = ByteBuffer.allocate(4).putFloat(messageFloat).array();
                                        for(byteIndex = 3; byteIndex >= 0; byteIndex--)
                                            byteArray.put((byte)(0xff&dataBytes[i]));
                                    }else
                                        byteArray.putFloat(messageFloat);
                                    break;
                                case (IOc.TYPE_DOUBLE):
                                    double messageDouble = Double.parseDouble(Message);
                                    if(littleEndian) {
                                        dataBytes = ByteBuffer.allocate(8).putDouble(messageDouble).array();
                                        for(byteIndex = 7; byteIndex >= 0; byteIndex--)
                                            byteArray.put((byte)(0xff&dataBytes[i]));
                                    }else
                                        byteArray.putDouble(messageDouble);
                                    break;
                            }
                        } else {
                            resp.can = false;
                            listItems[i].openEditor();
                            Toast.makeText(this, R.string.EmptyXtringItem, Toast.LENGTH_SHORT).show();
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
        }else
            Toast.makeText(this, R.string.NoXtring, Toast.LENGTH_SHORT).show();
        return resp;
    }

    /*public CanSendPlusData genBuff() {
        int numDat = checkData();
        CanSendPlusData resp = new CanSendPlusData();
        resp.can = false;
        if(numDat > 0) {
            resp.can = true;
            ByteBuffer byteArray = ByteBuffer.allocate(numDat);
            try {
                for (int i = 1; i <= numItems; i++) {
                    if (!listItems[i].disabled) {
                        String Message = listItems[i].tx;
                        if (!Message.equals("")) {
                            int messageInt;
                            int byteIndex;
                            byte[] dataBytes;
                            switch (listItems[i].sendType) {
                                case (PrincipalActivity.SEND_TXT):
                                    byteArray.put(Message.getBytes());
                                    break;
                                case (PrincipalActivity.SEND_BYTE):
                                    messageInt = Integer.parseInt(Message);
                                    byteArray.put((byte) messageInt);
                                    break;
                                case (PrincipalActivity.SEND_BIN):
                                    messageInt = Integer.parseInt(Message, 2);
                                    byteArray.put((byte) messageInt);
                                    break;
                                case (PrincipalActivity.SEND_HEX):
                                    messageInt = Integer.parseInt(Message, 16);
                                    byteArray.put((byte) messageInt);
                                    break;
                                case (PrincipalActivity.SEND_SHORT):
                                    messageInt = Integer.parseInt(Message);
                                    if(littleEndian) {
                                        dataBytes = ByteBuffer.allocate(4).putInt(messageInt).array();
                                        byteArray.put((byte)(0xff&dataBytes[3]));
                                        byteArray.put((byte)(0xff&dataBytes[2]));
                                    }else
                                        byteArray.putShort((short) messageInt);
                                    break;
                                case (PrincipalActivity.SEND_INT):
                                    messageInt = Integer.parseInt(Message);
                                    if(littleEndian) {
                                        dataBytes = ByteBuffer.allocate(4).putInt(messageInt).array();
                                        for(byteIndex = 3; byteIndex >= 0; byteIndex--)
                                            byteArray.put((byte)(0xff&dataBytes[i]));
                                    }else
                                        byteArray.putInt(messageInt);
                                    break;
                                case (PrincipalActivity.SEND_LONG):
                                    long messageLong = Long.parseLong(Message);
                                    if(littleEndian) {
                                        dataBytes = ByteBuffer.allocate(8).putLong(messageLong).array();
                                        for(byteIndex = 7; byteIndex >= 0; byteIndex--)
                                            byteArray.put((byte)(0xff&dataBytes[i]));
                                    }else
                                        byteArray.putLong(messageLong);
                                    break;
                                case (PrincipalActivity.SEND_FLOAT):
                                    float messageFloat = Float.parseFloat(Message);
                                    if(littleEndian) {
                                        dataBytes = ByteBuffer.allocate(4).putFloat(messageFloat).array();
                                        for(byteIndex = 3; byteIndex >= 0; byteIndex--)
                                            byteArray.put((byte)(0xff&dataBytes[i]));
                                    }else
                                        byteArray.putFloat(messageFloat);
                                    break;
                                case (PrincipalActivity.SEND_DOUBLE):
                                    double messageDouble = Double.parseDouble(Message);
                                    if(littleEndian) {
                                        dataBytes = ByteBuffer.allocate(8).putDouble(messageDouble).array();
                                        for(byteIndex = 7; byteIndex >= 0; byteIndex--)
                                            byteArray.put((byte)(0xff&dataBytes[i]));
                                    }else
                                        byteArray.putDouble(messageDouble);
                                    break;
                            }
                        } else {
                            resp.can = false;
                            listItems[i].openEditor();
                            Toast.makeText(this, R.string.EmptyXtringItem, Toast.LENGTH_SHORT).show();
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
        }else
            Toast.makeText(this, R.string.NoXtring, Toast.LENGTH_SHORT).show();
        return resp;
    }*/

    public void enviarX(View v) {
        CanSendPlusData tempData = genBuff();
        String txPack = "";
        if(tempData.can) {
            saveAll();
            try {
                txPack = new String(tempData.data, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                //txPack = "";
            }
            Intent resIntent = new Intent(PrincipalActivity.RESULT_ACTION);
            resIntent.putExtra(NEWTX, tempData.data);
            resIntent.putExtra(NEWTXs, txPack);
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
                    String temp = data.getStringExtra(NEWTX);
                    listItems[pos].setvalTX(temp);
                    save(pos);
                }else
                    //if(listItems[pos].tx.equals(""))
                    Toast.makeText(this, R.string.xItemNoEdited, Toast.LENGTH_SHORT).show();
                break;
            }
            case ENTER_CONFIG:
                if(resultCode == Activity.RESULT_OK) {
                    littleEndian = shapre.getBoolean(getString(R.string.LITTLE_ENDIAN), false);
                    updCommButtons();
                }
                break;
            case SHOW_INSTRUCTIONS:
                if(resultCode == Activity.RESULT_OK) {
                    boolean checked = data.getBooleanExtra(PrincipalActivity.SIoS, false);
                    //editor.putBoolean(PrincipalActivity.SIoS, checked);
                    //editor.commit();
                }
                break;
            case ENTER_IO_CONFIG:
                if(resultCode == Activity.RESULT_OK) {
                    actualTXtype = data.getIntExtra(IOc.TX_TYPE, IOc.TYPE_TEXT);
                    actualTXform = data.getIntExtra(IOc.TX_FORM, IOc.INT_FORM_DEC);
                    int ioPos = data.getIntExtra(POS, 1);
                    boolean external = data.getBooleanExtra(EXTERNAL, true);
                    if(listItems[ioPos].tx.equals(""))
                        external = true;
                    listItems[ioPos].setSendType(actualTXtype, actualTXform, external);
                    //Toast.makeText(this, "" + actualTXtype, Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(this, R.string.ConfigNotSaved, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        UcommUI();
        super.onResume();
    }

    @Override
    protected void onStop() {
        saveAll();
        super.onStop();
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