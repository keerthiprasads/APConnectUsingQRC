package com.keerthi.routerconnect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;

    public void resumeScanning(){
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScannerActivity.this);
            }
        }, 2000);
    }

    public void onScan(Result rawResult){

        if(rawResult.getBarcodeFormat() == BarcodeFormat.QR_CODE){

            String resultString = rawResult.getText();

            String []parts = resultString.split("\n");

            if(parts.length >= 3) {
                String ssid = parts[0].substring(6);
                String password = parts[1].substring(10);
                String security = parts[2].substring(10);

                Toast.makeText(this, "SSID = " + ssid +
                        "\nPassword = " + password +
                        "\nSecurity = " + security, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, NetworkConnectActivity.class);
                intent.putExtra("ssid", ssid);
                intent.putExtra("password", password);
                intent.putExtra("security", security);

                startActivity(intent);
            }else{
                resumeScanning();
            }

        }else{
            resumeScanning();
        }
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.scanner_activity);
        setupToolbar();

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        mScannerView.setResultHandler(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        onScan(rawResult);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
