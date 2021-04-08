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
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import static com.pjesekdev.nfctoolkit.TagTools.createUri;

public class EncodeActivity extends AppCompatActivity {

    private TagTools tagTools;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;
    private NdefRecord createdUri;

    private ConstraintLayout tapTagLayout;
    private ScrollView scrollView;
    private Switch mReadableOnly;

    private Dialog dialog;

    public void ShowPopup(View v) {
        Button btnClose;
        dialog.setContentView(R.layout.popup);
        btnClose = dialog.findViewById(R.id.btn_close);

        btnClose.setOnClickListener(v1 -> {
            dialog.dismiss();
            Intent myIntent = new Intent(EncodeActivity.this, MainActivity.class);
            startActivity(myIntent);
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);
        tagTools = new TagTools(this, getWindow().getDecorView().findViewById(android.R.id.content));

        mReadableOnly = findViewById(R.id.chk_read_only);
        tapTagLayout = findViewById(R.id.cslTag);
        scrollView = findViewById(R.id.scrl_view);
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
            try {
                if(createdUri != null) {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.writingPrompt), Snackbar.LENGTH_SHORT)
                            .show();
                    Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    NdefMessage message = new NdefMessage(createdUri);

                    if (tagTools.writeTag(message, detectedTag, mReadableOnly.isChecked())) {
                        ShowPopup(findViewById(android.R.id.content).getRootView());
                        tapTagLayout.setVisibility(View.INVISIBLE);
                        scrollView.setVisibility(View.VISIBLE);
                        createdUri = null;
                    } else {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.errorPrompt), Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
            } catch (Exception e) {
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.errorPrompt)+ " "+ e.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void encodeTel(View v){
        tapTagLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);

        EditText inputTel = findViewById(R.id.inputTel);
        if (inputTel != null) {
            final String tel = inputTel.getText().toString();
            createdUri = createUri("tel:"+tel);
        }
    }

    public void encodeSMS(View v){
        tapTagLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);

        EditText inputSmsReceipent = findViewById(R.id.inputTelSMS);
        EditText inputSmsBody = findViewById(R.id.inputMessageSMS);
        if (inputSmsReceipent != null && inputSmsBody != null) {
            final String receipent = inputSmsReceipent.getText().toString();
            final String body = inputSmsBody.getText().toString();
            createdUri = createUri("sms:"+receipent+"?body="+body);
        }
    }

    public void encodeGeo(View v){
        tapTagLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);

        EditText inputLat = findViewById(R.id.inputLat);
        EditText inputLon = findViewById(R.id.inputLon);

        if (inputLat != null && inputLon != null) {
            final double lat = Double.parseDouble(inputLat.getText().toString());
            final double lon = Double.parseDouble(inputLon.getText().toString());
            createdUri = createUri("geo:"+lat+","+lon);
        }
    }

    public void encodeEmail(View v){
        tapTagLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);

        EditText inputReceipent = findViewById(R.id.inputEmailReceipent);
        EditText inputSubject = findViewById(R.id.inputEmailSubject);
        EditText inputMessage = findViewById(R.id.inputEmailMessage);

        if (inputReceipent != null && inputSubject != null && inputMessage != null) {
            final String receipent = inputReceipent.getText().toString();
            final String subject = inputSubject.getText().toString();
            final String message = inputMessage.getText().toString();
            createdUri = createUri("mailto:"+receipent+"?subject="+subject+"&body="+message);
        }
    }

    public void encodeBluetooth(View v){
        tapTagLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);

        EditText inputBluetooth = findViewById(R.id.inputBluetooth);
        if (inputBluetooth != null) {
            final String MAC = inputBluetooth.getText().toString();
            createdUri = createUri("ftp:"+MAC);
        }
    }

    public void encodeText(View v){
        tapTagLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);

        EditText inputText = findViewById(R.id.inputText);
        if (inputText != null) {
            final String text = inputText.getText().toString();
            createdUri = createUri("text:"+text);
        }
    }

    public void encodeURI(View v){
        tapTagLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);

        EditText inputUri = findViewById(R.id.inputURIMessage);
        if (inputUri != null) {
            final String message = inputUri.getText().toString();
            createdUri = createUri(message);
        }
    }
}