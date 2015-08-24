package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class XtringItem {

    public Activity context;
    public int position = 0;
    public String tx = "";
    public boolean constant = false;
    public int sendType = 0;
    public Spinner spinnerX;
    public TextView textViewX;
    public CheckBox checkBoxX;

    public XtringItem(int sendType) {
        this.context = context;
        this.sendType = sendType;
    }

    public XtringItem(Activity context, int posi) {
        this.context = context;
        position = posi;
    }

    public XtringItem(Activity context, int posi, int sendType) {
        this.context = context;
        position = posi;
        this.sendType = sendType;
    }

    public XtringItem(Activity context, int posi, int sendType, String tx, boolean constant) {
        this.context = context;
        position = posi;
        this.sendType = sendType;
        this.tx = tx;
        this.constant = constant;
    }

    public void setConstant(boolean constantVal) {
        constant = constantVal;
        spinnerX.setEnabled(!constantVal);
        textViewX.setEnabled(!constantVal);
    }

    public void setSendType(int sendT, XtringItem item) {
        spinnerX = item.spinnerX;
        textViewX = item.textViewX;
        setSendType(sendT);
    }

    public void openEditor(Activity context) {
        Intent editorIntent = new Intent(context, XtringEditor.class);
        editorIntent.putExtra(XtringAdapter.POS, position);
        editorIntent.putExtra(XtringAdapter.NEWTX, tx);
        editorIntent.putExtra(XtringAdapter.SENDTYPE, sendType);
        context.startActivityForResult(editorIntent, XtringAdapter.XTRING_EDITOR);
    }

    public void setSendType(int sendT) {
        if(sendT != sendType) {
            tx = "";
            textViewX.setText(tx);
        }
        sendType = sendT;
        spinnerX.setSelection(sendType);
        textViewX.setHint(R.string.commDVal);
//        switch (sendType) {
//            case (PrincipalActivity.SEND_TXT): {
//                textViewX.setHint(R.string.commDVal);
//                textViewX.setInputType(InputType.TYPE_CLASS_TEXT);
//                break;
//            }
//            case (PrincipalActivity.SEND_BYTE): {
//                textViewX.setHint(R.string.Text_TXn);
//                textViewX.setInputType(InputType.TYPE_CLASS_NUMBER);
////                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
//                break;
//            }
//            case (PrincipalActivity.SEND_BIN): {
//                textViewX.setHint(R.string.Text_TXb);
//                textViewX.setInputType(InputType.TYPE_CLASS_NUMBER);
////                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
//                break;
//            }
//            case (PrincipalActivity.SEND_HEX): {
//                textViewX.setHint(R.string.Text_TXh);
//                textViewX.setInputType(InputType.TYPE_CLASS_TEXT);
//                break;
//            }
//            case (PrincipalActivity.SEND_SHORT): {
//                textViewX.setHint(R.string.Text_TXn);
//                textViewX.setInputType(InputType.TYPE_CLASS_NUMBER);
////                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
//                break;
//            }
//            case (PrincipalActivity.SEND_INT): {
//                textViewX.setHint(R.string.Text_TXn);
//                textViewX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
////                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                break;
//            }
//            case (PrincipalActivity.SEND_FLOAT): {
//                textViewX.setHint(R.string.Text_TXf);
//                textViewX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
//                        InputType.TYPE_NUMBER_FLAG_SIGNED);
////                TX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT |
////                        InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                break;
//            }
//        }
    }
}