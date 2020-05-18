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
    //public int sendType = 0;
    public LinearLayout linearLayoutX;
    public TextView itemIndeX;
    //public Spinner spinnerX;
    public TextView typeTXB;
    public TextView textViewX;
    public CheckBox checkBoxX;
    public CheckBox disableX;

    int actualTXtype = 0;
    int actualTXform = 0;

    public XtringItem(Activity context, int posi) {
        this.context = context;
        this.position = posi;
    }

    public void setConstant(boolean constantVal) {
        if(!textViewX.getText().toString().equals("")) {
            constant = constantVal;
            //spinnerX.setEnabled(!constantVal);
            typeTXB.setEnabled(!constantVal);
            textViewX.setEnabled(!constantVal);
        }else if(constantVal) {
            checkBoxX.setChecked(false);
            Toast.makeText(context, R.string.commDVal, Toast.LENGTH_SHORT).show();
        }
    }

    public void setDisabled(boolean DisableVal) {
        disabled = DisableVal;
        //spinnerX.setEnabled(!DisableVal);
        typeTXB.setEnabled(!DisableVal);
        textViewX.setEnabled(!DisableVal);
        checkBoxX.setEnabled(!DisableVal);
        if(!DisableVal)
            setConstant(constant);
    }

    public void setvalTX(String newTX) {
        tx = newTX;
        textViewX.setText(tx);
    }

    public String genTXtypeLbl() {
        String premisa = "";
        if(actualTXtype > 0 && actualTXtype < 5)
            premisa = context.getString(IOc.formStrings[actualTXform]);
        return premisa + context.getString(IOc.typeStrings[actualTXtype]) + "▼";
    }

    private boolean isSameTXtype(int type,  int newType) {
        if(type > 0 && type < 5 && newType > 0 && newType < 5)
            return true;
        else return type > 4 && type < 7 && newType > 4 && newType < 7;
    }

    public void setSendType(int sendT, int sendF, boolean external) {
        if (external || sendT != actualTXtype || sendF != actualTXform) {
            if(sendF != actualTXform || !isSameTXtype(actualTXtype, sendT))
                tx = "";
            actualTXtype = sendT;
            actualTXform = sendF;
            setvalTX(tx);
            openEditor();
        }
        typeTXB.setText(genTXtypeLbl());
    }

    /*public void setSendType(int sendT, boolean external) {
        if (external || sendT != sendType) {
            sendType = sendT;
            setvalTX("");
            openEditor();
        }
        //spinnerX.setSelection(sendType);
    }*/

    public void openEditor() {
        Intent editorIntent = new Intent(context, XtringEditor.class);
        editorIntent.putExtra(XtringActivity.POS, position);
        editorIntent.putExtra(XtringActivity.NEWTX, tx);
        editorIntent.putExtra(IOc.TX_TYPE, actualTXtype);
        editorIntent.putExtra(IOc.TX_FORM, actualTXform);
        //editorIntent.putExtra(XtringActivity.SENDTYPE, sendType);
        context.startActivityForResult(editorIntent, XtringActivity.XTRING_EDITOR);
        context.overridePendingTransition(R.animator.slide_in_bottom,
                R.animator.slide_out_top);
    }
}