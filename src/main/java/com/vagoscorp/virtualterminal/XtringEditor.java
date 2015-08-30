package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class XtringEditor extends Activity {

    TextView editorXdetails;
    EditText newTX;

    int sendType = 0;
    int position = 0;
    String txVal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getIntent().getIntExtra(XtringActivity.POS, 0);
        sendType = getIntent().getIntExtra(XtringActivity.SENDTYPE, 0);
        txVal = getIntent().getStringExtra(XtringActivity.NEWTX);
        setContentView(R.layout.activity_xtring_editor);
        editorXdetails = (TextView)findViewById(R.id.editorXdetails);
        newTX = (EditText)findViewById(R.id.newTX);
        setSendType();
        String[] sendT = getResources().getStringArray(R.array.sendtypes_array2);
        editorXdetails.setText("Item" + position + " ( " + sendT[sendType] + " )");
        newTX.setText(txVal);
        newTX.requestFocus();
    }

    boolean check(String message) {
        boolean res = true;
        try {
            if(!message.equals("")) {
                switch (sendType) {
                    case (PrincipalActivity.SEND_TXT): {

                        break;
                    }
                    case (PrincipalActivity.SEND_BYTE): {
                        int Messagen = Integer.parseInt(message);

                        break;
                    }
                    case (PrincipalActivity.SEND_BIN): {
                        int Messagen = Integer.parseInt(message, 2);

                        break;
                    }
                    case (PrincipalActivity.SEND_HEX): {
                        int Messagen = Integer.parseInt(message, 16);

                        break;
                    }
                    case (PrincipalActivity.SEND_SHORT): {
                        int Messagen = Integer.parseInt(message);

                        break;
                    }
                    case (PrincipalActivity.SEND_INT): {
                        int Messagen = Integer.parseInt(message);

                        break;
                    }
                    case (PrincipalActivity.SEND_FLOAT): {
                        float Messagen = Float.parseFloat(message);

                        break;
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

    public void updateXtringItem(View v) {
        txVal = newTX.getText().toString();
        if(check(txVal)) {
            Intent resIntent = new Intent(PrincipalActivity.RESULT_ACTION);
            resIntent.putExtra(XtringActivity.POS, position);
            resIntent.putExtra(XtringActivity.NEWTX, txVal);
            setResult(Activity.RESULT_OK, resIntent);
            finish();
        }
    }

    public void setSendType() {
        switch (sendType) {
            case(PrincipalActivity.SEND_TXT): {
                newTX.setHint(R.string.VagosCORP);
                newTX.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            }
            case(PrincipalActivity.SEND_BYTE): {
                newTX.setHint(R.string.Text_TXn);
                newTX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                break;
            }
            case(PrincipalActivity.SEND_BIN): {
                newTX.setHint(R.string.Text_TXb);
                newTX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                break;
            }
            case(PrincipalActivity.SEND_HEX): {
                newTX.setHint(R.string.Text_TXh);
                newTX.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            }
            case(PrincipalActivity.SEND_SHORT): {
                newTX.setHint(R.string.Text_TXn);
                newTX.setInputType(InputType.TYPE_CLASS_NUMBER);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                break;
            }
            case(PrincipalActivity.SEND_INT): {
                newTX.setHint(R.string.Text_TXn);
                newTX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_SIGNED);
                break;
            }
            case(PrincipalActivity.SEND_FLOAT): {
                newTX.setHint(R.string.Text_TXf);
                newTX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
//                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT |
//                        InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                break;
            }
        }
    }

}
