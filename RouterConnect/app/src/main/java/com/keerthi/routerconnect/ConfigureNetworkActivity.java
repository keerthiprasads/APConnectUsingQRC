package com.keerthi.routerconnect;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by keerthi on 28/01/17.
 */

public class ConfigureNetworkActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle state){
        super.onCreate(state);

        setContentView(R.layout.activity_configure_network);

        Button btnConfigureOK = (Button) findViewById(R.id.btnConfigureOK);

        btnConfigureOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText edtSSID = (EditText) findViewById(R.id.edtSSID);
                if(edtSSID.getText().length() > 0) {
                    Intent intent = new Intent(ConfigureNetworkActivity.this, ConfigCompletedActivity.class);

                    startActivity(intent);
                }else{
                    new AlertDialog.Builder(ConfigureNetworkActivity.this)
                            .setTitle(getText(R.string.invalid_ssid))
                            .setMessage(getText(R.string.enter_valid_ssid))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
    }
}
