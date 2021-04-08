package com.pjesekdev.nfctoolkit;

import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import pl.droidsonroids.gif.GifImageView;


public class DecodeActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;

    private EditText nfcOutput;
    private TextView nfcInfo;
    private GifImageView gifDecode;
    private CheckBox checkData, checkTech;
    private TagTools tagTools;

    private String data;
    private String[] info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

        tagTools = new TagTools(this, getWindow().getDecorView().findViewById(android.R.id.content));

        checkTech = findViewById(R.id.checkTech);
        checkData = findViewById(R.id.chk_data);

        FloatingActionButton btnCopy = findViewById(R.id.btn_copy);
        FloatingActionButton btnParseUri = findViewById(R.id.btn_parse);

        checkTech.setOnClickListener(v -> nfcOutput.setText(setOutput()));

        checkData.setOnClickListener(v -> nfcOutput.setText(setOutput()));

        btnCopy.setOnClickListener(v -> copyOutput());

        btnParseUri.setOnClickListener(v -> parseUri());

        nfcInfo = findViewById(R.id.tv_info);
        gifDecode = findViewById(R.id.gif_encode);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcOutput = findViewById(R.id.tv_output);

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
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
        if(getNfcAdapter() != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        data = "";

        String action = intent.getAction();
        if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED) || action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) || action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
            info = tagTools.getTagInformation(intent);

            gifDecode.setVisibility(View.INVISIBLE);
            nfcInfo.setVisibility(View.INVISIBLE);
            nfcOutput.setVisibility(View.VISIBLE);

            try {
                data += tagTools.getMessage(intent);
            } catch (Exception e) {
                data = getResources().getString(R.string.errorPrompt) + "\n" + e.getMessage() + "\n\n";
            }

            nfcOutput.setText(setOutput());
        }
    }

    private void parseUri(){
        if(!checkTech.isChecked()) {
            if(checkData.isChecked()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                    startActivity(intent);
                } catch (Exception e) {
                    showSnackBar(getResources().getString(R.string.errorPrompt) + e.getMessage());

                }
            }else{
                showSnackBar(getResources().getString(R.string.checkPrompt));
            }
        }else{
            showSnackBar(getResources().getString(R.string.uncheckPrompt));
        }
    }

    private void copyOutput() {
        if(nfcOutput.getVisibility() == View.VISIBLE) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("nfcOutput", nfcOutput.getText());
            clipboard.setPrimaryClip(clip);

            showSnackBar(getResources().getString(R.string.tagCopied));
        }
    }

    private String setOutput(){
        if(checkData.isChecked() && !checkTech.isChecked()){
            return data;
        }
        if(checkTech.isChecked() && !checkData.isChecked()){
            return info[0];
        }
        if(checkTech.isChecked() && checkData.isChecked()){
            return getResources().getString(R.string.tagContent) + "\n"+data+info[0];
        }

        return "";
    }

    private void showSnackBar(String message){
        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content)
                , message, Snackbar.LENGTH_SHORT)
                .show();
    }
}