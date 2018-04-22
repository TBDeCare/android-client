package com.garuda45.tbdecarelab.listadapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.garuda45.tbdecarelab.Config;
import com.garuda45.tbdecarelab.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mure on 10/5/2017.
 */

public class WipListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String[]> list = new ArrayList<String[]>();
    private Context context;

    public WipListAdapter(ArrayList<String[]> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_wip, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position)[0]);

        TextView subtitleText = (TextView)view.findViewById(R.id.status_item);
        subtitleText.setText(list.get(position)[1]);

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parent.getContext());
                alertDialogBuilder.setTitle("Delete the whole folder?");
                alertDialogBuilder
                        .setMessage("Click yes to confirm!!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            File imgDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
                                            File patientDirectory = new File(imgDirectory, list.get(position)[0]);

                                            String[] children = patientDirectory.list();
                                            for (int i = 0; i < children.length; i++)
                                            {
                                                new File(patientDirectory, children[i]).delete();
                                            }
                                            patientDirectory.delete();

                                            //do something
                                            list.remove(position); //or some other task
                                            notifyDataSetChanged();
                                        }
                                        catch(Exception e) {

                                        }
                                    }
                                })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        return view;
    }
}