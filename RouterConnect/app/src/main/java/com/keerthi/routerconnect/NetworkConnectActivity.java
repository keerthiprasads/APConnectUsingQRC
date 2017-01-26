package com.keerthi.routerconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

/**
 * Created by keerthi on 26/01/17.
 */

public class NetworkConnectActivity extends AppCompatActivity {

    private WiFiStateReciver wifiReceiver;

    private void updateTextView(String text) {
        TextView textView = (TextView) findViewById(R.id.txtNetworkStatus);
        textView.setText(text);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        Bundle bundle = getIntent().getExtras();

        Log.i("RouterApp: ", bundle.getString("ssid"));
        Log.i("RouterApp: ", bundle.getString("password"));

        setContentView(R.layout.network_connect_activity);


        updateTextView(getString(R.string.attempt_connection) + bundle.getString("ssid"));

        wifiReceiver = new WiFiStateReciver();
        this.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));

        connectToWiFiNetwork(bundle.getString("ssid"), bundle.getString("password"), bundle.getString("security"));
    }

    public void connectToWiFiNetwork(String ssid, String keyPass, String security){

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID =  "\"" + ssid + "\"";

        switch (security){
            case "WPA":
                conf.preSharedKey = "\""+ keyPass +"\"";
                break;
            case "WEP":
                conf.wepKeys[0] = "\"" + keyPass + "\"";
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

                break;
            case "Open":
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
        }

        WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        //remember id
        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();


        Log.i("RouterConnect", "Connecting to Network: " + conf.SSID);
    }

    private void onCompleted(){
        updateTextView(getString(R.string.successful_connection));
    }

    private void onAuthenticationError(){
        updateTextView(getString(R.string.auth_error));
    }

    private void onError(){

    }

    private class WiFiStateReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION))
            {
                SupplicantState state;
                state = (SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);

                {
                    Log.d("NetworkView", "New state: "+state);
                    Log.d("NetworkView", "New error: "+intent.getIntExtra(WifiManager
                            .EXTRA_SUPPLICANT_ERROR,-1));
                    if (intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1)==WifiManager.ERROR_AUTHENTICATING) {
                        Log.d("NetworkView", "Error authenticating");
                        onAuthenticationError();

                    }
                    else if ((intent.getParcelableExtra(WifiManager
                            .EXTRA_NEW_STATE))== SupplicantState.COMPLETED){
                        Log.d("NetworkUtil", "Connection Completed");
                        onCompleted();
                    }
                }
            }
        }
    }
}
