package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class XtringEditor extends Activity {

    TextView editorXdetails;
//    EditText newTX;
    EditText[] newTXs = new EditText[5];

    int sendType = 0;
    int position = 0;
    String txVal = "";

    SharedPreferences shapre;
    boolean pro = false;
    int actualTXtype = 0;
    int actualTXform = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        shapre = getSharedPreferences(getString(R.string.SHARPREF),MODE_PRIVATE);
        pro = shapre.getBoolean(getString(R.string.isPRO), false);
        position = data.getIntExtra(XtringActivity.POS, 0);
        sendType = data.getIntExtra(XtringActivity.SENDTYPE, 0);
        txVal = data.getStringExtra(XtringActivity.NEWTX);
        actualTXtype = data.getIntExtra(IOc.TX_TYPE, IOc.TYPE_TEXT);
        actualTXform = data.getIntExtra(IOc.TX_FORM, IOc.INT_FORM_DEC);
        setContentView(R.layout.activity_xtring_editor);
        editorXdetails = findViewById(R.id.editorXdetails);
//        newTX = findViewById(R.id.newTX);
        newTXs[PrincipalActivity.TX_FORM_TXT] = findViewById(R.id.newTXtext);
        newTXs[PrincipalActivity.TX_FORM_DEC] = findViewById(R.id.newTXnum);
        newTXs[PrincipalActivity.TX_FORM_HEX] = findViewById(R.id.newTXhex);
        newTXs[PrincipalActivity.TX_FORM_BIN] = findViewById(R.id.newTXbin);
        newTXs[PrincipalActivity.TX_FORM_FLOAT] = findViewById(R.id.newTXfloat);
        /*newTXs[0] = findViewById(R.id.newTXtext);
        newTXs[1] = findViewById(R.id.newTXnum);
        newTXs[2] = findViewById(R.id.newTXbin);
        newTXs[3] = findViewById(R.id.newTXhex);
        newTXs[4] = findViewById(R.id.newTXint16);
        newTXs[5] = findViewById(R.id.newTXint32);
        newTXs[6] = findViewById(R.id.newTXint64);
        newTXs[7] = findViewById(R.id.newTXfloat);
        newTXs[8] = findViewById(R.id.newTXdouble);
        if(!pro) {
            newTXs[6].setEnabled(pro);
            newTXs[8].setEnabled(pro);
            newTXs[6].setText("");
            newTXs[8].setText("");
            newTXs[6].setHint(R.string.NEED_PRO);
            newTXs[8].setHint(R.string.NEED_PRO);
        }*/
        //setSendType();
        setTXType();
        String tempS = getString(R.string.Item) + " " + position + " ( " + genTXtypeLbl() + " ) ";
        if(txVal.equals(""))
            tempS += getString(R.string.Empty);
        //String[] sendT = getResources().getStringArray(R.array.sendtypes_array2);
        //String tempS = getString(R.string.Item) + position + " ( " + sendT[sendType] + " ) " + getString(R.string.Empty);
        editorXdetails.setText(tempS);
        newTXs[sendType].setText(txVal);
        newTXs[sendType].selectAll();
        newTXs[sendType].requestFocus();
    }

    public String genTXtypeLbl() {
        String premisa = "";
        if(actualTXtype > 0 && actualTXtype < 5)
            premisa = getString(IOc.formStrings[actualTXform]);
        return premisa + getString(IOc.typeStrings[actualTXtype]);
    }

    boolean check(String message) {
        boolean res = true;
        try {
            if(!message.equals("")) {
                switch (actualTXtype) {
                    case (IOc.TYPE_TEXT):
                        break;
                    case (IOc.TYPE_BYTE):
                    case (IOc.TYPE_SHORT):
                    case (IOc.TYPE_INT):
                        //Integer.parseInt(message);
                        Integer.parseInt(message, IOc.radixTX[actualTXform]);
                        break;
                    /*case (PrincipalActivity.SEND_BIN):
                        Integer.parseInt(message, 2);
                        break;
                    case (PrincipalActivity.SEND_HEX):
                        Integer.parseInt(message, 16);
                        break;*/
                    case (IOc.TYPE_LONG):
                        //Long.parseLong(message);
                        Long.parseLong(message, IOc.radixTX[actualTXform]);
                        break;
                    case (IOc.TYPE_FLOAT):
                        Float.parseFloat(message);
                        break;
                    case (IOc.TYPE_DOUBLE):
                        Double.parseDouble(message);
                        break;
                }
            }else {
                Toast.makeText(this, R.string.EmptyXtringItem, Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException nEx) {
            nEx.printStackTrace();
            Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
            res = false;
        }
        return res;
    }

    boolean checks(String message) {
        boolean res = true;
        try {
            if(!message.equals("")) {
                switch (sendType) {
                    case (PrincipalActivity.SEND_TXT):
                        break;
                    case (PrincipalActivity.SEND_BYTE):
                    case (PrincipalActivity.SEND_SHORT):
                    case (PrincipalActivity.SEND_INT):
                        Integer.parseInt(message);
                        break;
                    case (PrincipalActivity.SEND_BIN):
                        Integer.parseInt(message, 2);
                        break;
                    case (PrincipalActivity.SEND_HEX):
                        Integer.parseInt(message, 16);
                        break;
                    case (PrincipalActivity.SEND_LONG):
                        Long.parseLong(message);
                        break;
                    case (PrincipalActivity.SEND_FLOAT):
                        Float.parseFloat(message);
                        break;
                    case (PrincipalActivity.SEND_DOUBLE):
                        Double.parseDouble(message);
                        break;
                }
            }
        } catch (NumberFormatException nEx) {
            nEx.printStackTrace();
            Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
            res = false;
        }
        return res;
    }

    public void updateXtringItem(View v) {
        txVal = newTXs[sendType].getText().toString();
        if(check(txVal)) {
            Intent resIntent = new Intent(PrincipalActivity.RESULT_ACTION);
            resIntent.putExtra(XtringActivity.POS, position);
            resIntent.putExtra(XtringActivity.NEWTX, txVal);
            setResult(Activity.RESULT_OK, resIntent);
            finish();
            overridePendingTransition(R.animator.slide_in_top,
                    R.animator.slide_out_bottom);
        }
    }

    public void setSendType() {
        for(int i = 0; i < 9; i++)
            newTXs[i].setVisibility(View.GONE);
        newTXs[sendType].setVisibility(View.VISIBLE);
        /*switch (sendType) {
            case(PrincipalActivity.SEND_TXT): {
//                newTX.setHint(R.string.VagosCORP);
//                newTX.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            }
            case(PrincipalActivity.SEND_BYTE): {
//                newTX.setHint(R.string.Text_TXn);
//                newTX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                break;
            }
            case(PrincipalActivity.SEND_BIN): {
//                newTX.setHint(R.string.Text_TXb);
//                newTX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                break;
            }
            case(PrincipalActivity.SEND_HEX): {
//                newTX.setHint(R.string.Text_TXh);
//                newTX.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            }
            case(PrincipalActivity.SEND_SHORT): {
//                newTX.setHint(R.string.Text_TXn);
//                newTX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                break;
            }
            case(PrincipalActivity.SEND_INT): {
//                newTX.setHint(R.string.Text_TXn);
//                newTX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_SIGNED);
                break;
            }
            case(PrincipalActivity.SEND_FLOAT): {
//                newTX.setHint(R.string.Text_TXf);
//                newTX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
//                        InputType.TYPE_NUMBER_FLAG_SIGNED);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT |
//                        InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                break;
            }
        }*/
    }

    public void setTXType() {
        //typeTXB.setText(genTXtypeLbl());
        //aCRpLF.setEnabled(false);
        for (int i = 0; i < 5; i++)
            newTXs[i].setVisibility(View.GONE);
        sendType = PrincipalActivity.TX_FORM_TXT;
        if(actualTXtype == IOc.TYPE_TEXT) {
            //aCRpLF.setEnabled(true);
        }else if(actualTXtype < IOc.TYPE_FLOAT){
            if(actualTXform == IOc.INT_FORM_DEC)
                sendType = PrincipalActivity.TX_FORM_DEC;
            else if(actualTXform == IOc.INT_FORM_HEX)
                sendType = PrincipalActivity.TX_FORM_HEX;
            else
                sendType = PrincipalActivity.TX_FORM_BIN;
        }else
            sendType = PrincipalActivity.TX_FORM_FLOAT;
        newTXs[sendType].setVisibility(View.VISIBLE);
        newTXs[sendType].requestFocus();
        //imm.showSoftInput(TXs[sendTX], InputMethodManager.SHOW_IMPLICIT);
    }

}
