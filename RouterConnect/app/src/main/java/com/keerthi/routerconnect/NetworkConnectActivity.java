package com.keerthi.routerconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.keerthi.routerconnect.utils.RLog;

/**
 * Created by keerthi on 26/01/17.
 */

public class NetworkConnectActivity extends AppCompatActivity {

    private WiFiStateReceiver wifiReceiver;
    private Button mBtnConfigure;

    private void updateTextView(String text) {
        TextView textView = (TextView) findViewById(R.id.txtNetworkStatus);
        textView.setText(text);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        Bundle bundle = getIntent().getExtras();

        RLog.i("SSID: " + bundle.getString("ssid"));
        RLog.i("Password: " +bundle.getString("password"));
        RLog.i("Security" + bundle.getString("security"));

        setContentView(R.layout.activity_network_connect);


        updateTextView(getString(R.string.attempt_connection) + bundle.getString("ssid"));

        wifiReceiver = new WiFiStateReceiver();
        this.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));

        connectToWiFiNetwork(bundle.getString("ssid"), bundle.getString("password"),
                bundle.getString("security"), Boolean.parseBoolean(bundle.getString("hidden")));

        mBtnConfigure = (Button) findViewById(R.id.btnConfigure);
        mBtnConfigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NetworkConnectActivity.this, ConfigureNetworkActivity.class);
                startActivity(intent);
            }
        });
    }

    public void connectToWiFiNetwork(String ssid, String keyPass, String security, boolean hidden){

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID =  "\"" + ssid + "\"";

        conf.hiddenSSID = hidden;

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


        RLog.i("Connecting to Network: " + conf.SSID);
    }

    private void onCompleted(){
        updateTextView(getString(R.string.successful_connection));

        mBtnConfigure.setEnabled(true);
    }

    private void onAuthenticationError(){
        updateTextView(getString(R.string.auth_error));
    }

    private void onError(){

    }

    private class WiFiStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION))
            {
                SupplicantState state;
                state = (SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);

                {
                    RLog.d("New network state: " +state);

                    if (intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1)==WifiManager.ERROR_AUTHENTICATING) {
                        RLog.d("Error authenticating");
                        onAuthenticationError();

                    }
                    else if ((intent.getParcelableExtra(WifiManager
                            .EXTRA_NEW_STATE))== SupplicantState.COMPLETED){
                        RLog.d("Connection to WiFi network completed");
                        onCompleted();
                    }
                }
            }
        }
    }
}
