package com.pjesekdev.nfctoolkit;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class EraseActivity extends AppCompatActivity {

    private TagTools tagTools;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;
    private Dialog dialog;

    public void ShowPopup(View v) {
        Button btnClose;
        dialog.setContentView(R.layout.popup);
        btnClose = dialog.findViewById(R.id.btn_close);

        btnClose.setOnClickListener(v1 -> {
            dialog.dismiss();
            Intent myIntent = new Intent(EraseActivity.this, MainActivity.class);
            startActivity(myIntent);
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erase);
        tagTools = new TagTools(this, getWindow().getDecorView().findViewById(android.R.id.content));

        dialog = new Dialog(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException(getResources().getString(R.string.errorPrompt), e);
        }
        IntentFilter td = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFilters = new IntentFilter[] {
                ndef, td
        };

        techLists = new String[][]{new String[]{
                NfcV.class.getName(),
                NfcF.class.getName(),
                NfcA.class.getName(),
                NfcB.class.getName()
        }};

    }

    protected NfcAdapter getNfcAdapter() {
        return this.nfcAdapter;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getNfcAdapter() != null) {
            getNfcAdapter().enableForegroundDispatch(this, pendingIntent, intentFilters, techLists);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getNfcAdapter() != null) {
            getNfcAdapter().disableForegroundDispatch(this);
        }
    }

    @Override
    public void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();

        if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED) || action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) || action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {

            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.erasingPrompt), Snackbar.LENGTH_SHORT)
                    .show();
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage message = new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY, null, null, null));

            if (tagTools.writeTag(message, detectedTag, false)) {
                ShowPopup(findViewById(android.R.id.content).getRootView());
            } else {
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.errorPrompt), Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }
}