package com.JLotto.JLotto.Activity.FragMent_Two.QRCord;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.JLotto.JLotto.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class QRCodeActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener, View.OnClickListener {

    protected final String TAG = "QRcodeActivity";

    private CaptureManager captureManager;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton barcode_flash, barcode_cancel;
    private Boolean flash_state = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        barcode_flash = findViewById(R.id.barcode_flash);
        barcode_flash.setOnClickListener(this);

        barcode_cancel = findViewById(R.id.barcode_cancel);
        barcode_cancel.setOnClickListener(this);

        barcodeScannerView = findViewById(R.id.barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        captureManager = new CaptureManager(this, barcodeScannerView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();


    }

    protected String createTag() {
        return TAG;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.barcode_cancel:
                finish();
                break;

            case R.id.barcode_flash:
                if(flash_state){
                    barcodeScannerView.setTorchOn();
                } else {
                    barcodeScannerView.setTorchOff();
                }
                break;
        }
    }

    @Override
    public void onTorchOn() {
        barcode_flash.setImageResource(R.drawable.ic_flash_on);
        flash_state = false;
    }

    @Override
    public void onTorchOff() {
        barcode_flash.setImageResource(R.drawable.ic_flash_off);
        flash_state = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        captureManager.onSaveInstanceState(outState);
    }
}