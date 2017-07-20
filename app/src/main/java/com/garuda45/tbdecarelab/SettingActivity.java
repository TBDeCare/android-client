package com.garuda45.tbdecarelab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mure on 2/3/2017.
 */
public class SettingActivity extends AppCompatActivity {
    private EditText serverAddress;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        serverAddress = (EditText) findViewById(R.id.serverAddress);
        btnSave = (Button) findViewById(R.id.btnSave);

        serverAddress.setText(Config.SERVER);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String server_str = ((EditText) findViewById(R.id.serverAddress)).getText().toString();
                if (server_str != null && !server_str.equals("")) {
                    SharedPreferences.Editor editor = getSharedPreferences(Config.MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("server", serverAddress.getText().toString());
                    Config.SERVER = serverAddress.getText().toString();
                    editor.commit();
                    Toast.makeText(getApplicationContext(),
                            "Server address saved!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Sorry, server address can't be empty!", Toast.LENGTH_LONG).show();
                }
            }
        });

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
