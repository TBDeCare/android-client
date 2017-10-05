package com.garuda45.tbdecarelab;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.garuda45.tbdecarelab.wiplist.WipListAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mure on 10/5/2017.
 */

public class WorkInProgressActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wip);

        //generate list
        ArrayList<String[]> list = new ArrayList<String[]>();
        File imgDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
        File[] files = imgDirectory.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                File patientDirectory = new File(imgDirectory, inFile.getName());
                String subtitle = "";
                if (patientDirectory.listFiles().length <= 0) {
                    subtitle = patientDirectory.listFiles().length + " photo of sample";
                }
                else {
                    subtitle = patientDirectory.listFiles().length + " photos of samples";
                }
                list.add(new String[]{inFile.getName(), subtitle});
            }
        }

        //instantiate custom adapter
        WipListAdapter adapter = new WipListAdapter(list, this);

        //handle listview and assign adapter
        ListView lView = (ListView)findViewById(R.id.listview_wip);
        lView.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
