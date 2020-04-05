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
    EditText[] newTXs = new EditText[9];

    int sendType = 0;
    int position = 0;
    String txVal = "";

    SharedPreferences shapre;
    boolean pro = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shapre = getSharedPreferences(getString(R.string.SHARPREF),MODE_PRIVATE);
        pro = shapre.getBoolean(getString(R.string.isPRO), false);
        position = getIntent().getIntExtra(XtringActivity.POS, 0);
        sendType = getIntent().getIntExtra(XtringActivity.SENDTYPE, 0);
        txVal = getIntent().getStringExtra(XtringActivity.NEWTX);
        setContentView(R.layout.activity_xtring_editor);
        editorXdetails = findViewById(R.id.editorXdetails);
//        newTX = findViewById(R.id.newTX);
        newTXs[0] = findViewById(R.id.newTXtext);
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
        }
        setSendType();
        String[] sendT = getResources().getStringArray(R.array.sendtypes_array2);
        String tempS = "Item" + position + " ( " + sendT[sendType] + " )";
        editorXdetails.setText(tempS);
        newTXs[sendType].setText(txVal);
        newTXs[sendType].selectAll();
        newTXs[sendType].requestFocus();
    }

    boolean check(String message) {
        boolean res = true;
        try {
            if(!message.equals("")) {
                switch (sendType) {
                    case (PrincipalActivity.SEND_TXT):

                        break;
                    case (PrincipalActivity.SEND_BYTE):
                        int MessageB = Integer.parseInt(message);

                        break;
                    case (PrincipalActivity.SEND_BIN):
                        int Messageb = Integer.parseInt(message, 2);

                        break;
                    case (PrincipalActivity.SEND_HEX):
                        int Messageh = Integer.parseInt(message, 16);

                        break;
                    case (PrincipalActivity.SEND_SHORT):
                        int Messages = Integer.parseInt(message);

                        break;
                    case (PrincipalActivity.SEND_INT):
                        int Messagei = Integer.parseInt(message);

                        break;
                    case (PrincipalActivity.SEND_LONG):
                        long Messagel = Long.parseLong(message);

                        break;
                    case (PrincipalActivity.SEND_FLOAT):
                        float Messagef = Float.parseFloat(message);

                        break;
                    case (PrincipalActivity.SEND_DOUBLE):
                        double Messaged = Double.parseDouble(message);

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

}
