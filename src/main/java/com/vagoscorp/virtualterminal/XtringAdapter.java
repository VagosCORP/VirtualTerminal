package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class XtringAdapter extends ArrayAdapter<XtringItem> {

    public static final int XTRING_EDITOR = 199;
    public static final String NEWTX = "NEWTX";
    public static final String POS = "POS";
    public static final String SENDTYPE = "SENDTYPE";

    Activity context;
    List<XtringItem> listItems = new ArrayList<XtringItem>(0);
//    private static LayoutInflater inflater = null;

    public XtringAdapter(Activity contexto, List<XtringItem> list) {
        super(contexto, R.layout.xtring_item, list);
        context = contexto;
        listItems = list;
//        inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public XtringItem getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        protected int position;
        protected Spinner spinnerX;
        protected TextView textViewX;
        protected CheckBox checkBoxX;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.xtring_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.position = position;
            viewHolder.spinnerX = (Spinner) view.findViewById(R.id.spinnerX);
            viewHolder.textViewX = (TextView) view.findViewById(R.id.textViewX);
            viewHolder.checkBoxX = (CheckBox) view.findViewById(R.id.checkBoxX);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                    R.array.sendtypes_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            viewHolder.spinnerX.setAdapter(adapter);
            viewHolder.spinnerX.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionT, long id) {
                    XtringItem element = (XtringItem) viewHolder.spinnerX.getTag();
                    element.position = viewHolder.position;
                    element.spinnerX = viewHolder.spinnerX;
                    element.textViewX = viewHolder.textViewX;
                    element.checkBoxX = viewHolder.checkBoxX;
                    element.setSendType(positionT);
                    element.openEditor(context);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            viewHolder.textViewX.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    XtringItem element = (XtringItem) viewHolder.textViewX.getTag();
                    element.tx = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            viewHolder.textViewX.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XtringItem element = (XtringItem) viewHolder.textViewX.getTag();
                    element.position = viewHolder.position;
                    element.openEditor(context);
                }
            });
            viewHolder.checkBoxX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                XtringItem element = (XtringItem)viewHolder.checkBoxX.getTag();
                element.spinnerX = viewHolder.spinnerX;
                element.textViewX = viewHolder.textViewX;
                element.checkBoxX = viewHolder.checkBoxX;
                element.setConstant(isChecked);
                }
            });
            view.setTag(viewHolder);
            viewHolder.spinnerX.setTag(listItems.get(position));
            viewHolder.textViewX.setTag(listItems.get(position));
            viewHolder.checkBoxX.setTag(listItems.get(position));
        }else {
            view = convertView;
            ((ViewHolder)view.getTag()).spinnerX.setTag(listItems.get(position));
            ((ViewHolder)view.getTag()).textViewX.setTag(listItems.get(position));
            ((ViewHolder)view.getTag()).checkBoxX.setTag(listItems.get(position));
        }
        ViewHolder holder = (ViewHolder)view.getTag();
        holder.spinnerX.setSelection(listItems.get(position).sendType);
        holder.textViewX.setText(listItems.get(position).tx);
        holder.checkBoxX.setChecked(listItems.get(position).constant);
        return view;
    }
}