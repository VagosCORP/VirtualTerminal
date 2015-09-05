package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class XtringItem {

    public Activity context;
    public boolean enabled = false;
    public boolean disabled = false;
    public int position = 0;
    public String tx = "";
    public boolean constant = false;
    public int sendType = 0;
    public LinearLayout linearLayoutX;
    public TextView itemIndeX;
    public Spinner spinnerX;
    public TextView textViewX;
    public CheckBox checkBoxX;
    public CheckBox disableX;

    public XtringItem(Activity context, int posi) {
        this.context = context;
        this.position = posi;
    }

    public void setConstant(boolean constantVal) {
        if(!textViewX.getText().toString().equals("")) {
            constant = constantVal;
            spinnerX.setEnabled(!constantVal);
            textViewX.setEnabled(!constantVal);
        }else {
            checkBoxX.setChecked(false);
            Toast.makeText(context, R.string.commDVal, Toast.LENGTH_SHORT).show();
        }
    }

    public void setDisabled(boolean DisableVal) {
        disabled = DisableVal;
        spinnerX.setEnabled(!DisableVal);
        textViewX.setEnabled(!DisableVal);
        checkBoxX.setEnabled(!DisableVal);
        if(!DisableVal)
            setConstant(constant);
    }

    public void setvalTX(String newTX) {
        tx = newTX;
        textViewX.setText(tx);
    }

    public void setSendType(int sendT, boolean external) {
        if (external || sendT != sendType) {
            sendType = sendT;
            setvalTX("");
            openEditor();
        }
        spinnerX.setSelection(sendType);
    }

    public void openEditor() {
        Intent editorIntent = new Intent(context, XtringEditor.class);
        editorIntent.putExtra(XtringActivity.POS, position);
        editorIntent.putExtra(XtringActivity.NEWTX, tx);
        editorIntent.putExtra(XtringActivity.SENDTYPE, sendType);
        context.startActivityForResult(editorIntent, XtringActivity.XTRING_EDITOR);
    }
}