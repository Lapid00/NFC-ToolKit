package com.pjesekdev.nfctoolkit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.io.BaseEncoding;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TagTools {

    private final Context context;
    private View view;

    public TagTools(Context context, View v){
        this.context=context;
        this.view = v;
    }

    final String[] URI_PREFIX = new String[]{
            /* 0x00 */ "",
            /* 0x01 */ "http://www.",
            /* 0x02 */ "https://www.",
            /* 0x03 */ "http://",
            /* 0x04 */ "https://",
            /* 0x05 */ "tel:",
            /* 0x06 */ "mailto:",
            /* 0x07 */ "ftp://anonymous:anonymous@",
            /* 0x08 */ "ftp://ftp.",
            /* 0x09 */ "ftps://",
            /* 0x0A */ "sftp://",
            /* 0x0B */ "smb://",
            /* 0x0C */ "nfs://",
            /* 0x0D */ "ftp://",
            /* 0x0E */ "dav://",
            /* 0x0F */ "news:",
            /* 0x10 */ "telnet://",
            /* 0x11 */ "imap:",
            /* 0x12 */ "rtsp://",
            /* 0x13 */ "urn:",
            /* 0x14 */ "pop:",
            /* 0x15 */ "sip:",
            /* 0x16 */ "sips:",
            /* 0x17 */ "tftp:",
            /* 0x18 */ "btspp://",
            /* 0x19 */ "btl2cap://",
            /* 0x1A */ "btgoep://",
            /* 0x1B */ "tcpobex://",
            /* 0x1C */ "irdaobex://",
            /* 0x1D */ "file://",
            /* 0x1E */ "urn:epc:id:",
            /* 0x1F */ "urn:epc:tag:",
            /* 0x20 */ "urn:epc:pat:",
            /* 0x21 */ "urn:epc:raw:",
            /* 0x22 */ "urn:epc:",
            /* 0x23 */ "urn:nfc:"
    };

    public String[] getTagInformation(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String prefix = "android.nfc.tech.";
        String[] info = new String[2];

        info[0] = "[UID] \n" + BaseEncoding.base16().lowerCase().encode(tag.getId()) + "\n\n";
        String[] techList = tag.getTechList();
        String techListConcat = context.getResources().getString(R.string.tagTechnologies)+ "\n";
        for (int i = 0; i < techList.length; i++) {
            techListConcat += techList[i].substring(prefix.length()) + ",";
        }
        info[0] += techListConcat.substring(0, techListConcat.length() - 1) + "\n\n";

        info[0] += context.getResources().getString(R.string.tagType)+ "\n";
        String type = context.getResources().getString(R.string.tagUnknown)+ "\n\n";
        for (int i = 0; i < techList.length; i++) {
            if (techList[i].equals(MifareClassic.class.getName())) {
                info[1] = "Mifare Classic";
                MifareClassic mifareClassicTag = MifareClassic.get(tag);

                switch (mifareClassicTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                info[0] += "Mifare " + type + "\n\n";

                info[0] += R.string.tagSize+ "\n" + mifareClassicTag.getSize() + " b \n\n" +
                        R.string.tagSectorCount+ "\n" + mifareClassicTag.getSectorCount() + "\n\n" +
                        R.string.tagBlockCount+ "\n" + mifareClassicTag.getBlockCount() + "\n\n";
            } else if (techList[i].equals(MifareUltralight.class.getName())) {
                info[1] = "Mifare UltraLight";
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);

                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                info[0] += "Mifare " + type + "\n\n";
            } else if (techList[i].equals(IsoDep.class.getName())) {
                info[1] = "IsoDep";
                IsoDep isoDepTag = IsoDep.get(tag);
                info[0] += "IsoDep \n\n";
            } else if (techList[i].equals(Ndef.class.getName())) {
                Ndef ndefTag = Ndef.get(tag);
                if (ndefTag.isWritable()) {
                    info[0] += context.getResources().getString(R.string.tagWritable)+ "\n" + context.getResources().getString(R.string.yes)+ "\n\n";
                } else {
                    info[0] += context.getResources().getString(R.string.tagWritable)+ "\n" + context.getResources().getString(R.string.no)+ "\n\n";
                }
                if (ndefTag.canMakeReadOnly()) {
                    info[0] += context.getResources().getString(R.string.tagReadOnly)+ "\n" + context.getResources().getString(R.string.yes)+ "\n\n";
                } else {
                    info[0] += context.getResources().getString(R.string.tagReadOnly)+ "\n" + context.getResources().getString(R.string.no)+ "\n\n";
                }

            } else if (techList[i].equals(NdefFormatable.class.getName())) {
                NdefFormatable ndefFormatableTag = NdefFormatable.get(tag);
            }
        }
        return info;
    }

    public String getMessage(Intent intent) {

        String uri="";
        Parcelable[] messages =
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);


        for (int i = 0; i < messages.length; i++) {
                NdefMessage message = (NdefMessage) messages[i];
                NdefRecord[] records = message.getRecords();

            for (int j = 0; j < records.length; j++) {
                    NdefRecord record = records[j];
                    byte[] payload = record.getPayload();

                if (record == null || record.getPayload() == null || record.getPayload().length == 0) {
                    return context.getResources().getString(R.string.tagEmpty)+"\n\n";
                }

                int prefixCode = payload[0] & 0x0FF;
                    if (prefixCode >= URI_PREFIX.length) prefixCode = 0;

                    String reducedUri = new String(payload, 1, payload.length - 1, StandardCharsets.UTF_8);

                    uri = URI_PREFIX[prefixCode] + reducedUri;
                }
            }
            return uri+"\n\n";
        }

    public static NdefRecord createUri(String uriString) {
        return NdefRecord.createUri(Uri.parse(uriString));
    }

    public boolean writeTag(NdefMessage message, Tag tag, boolean makeReadOnly) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Snackbar.make(view, context.getResources().getString(R.string.tagWritable), Snackbar.LENGTH_SHORT)
                            .show();
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    Snackbar.make(view, context.getResources().getString(R.string.tagToSmall), Snackbar.LENGTH_SHORT)
                            .show();
                    return false;
                }
                ndef.writeNdefMessage(message);
                if(makeReadOnly && ndef.canMakeReadOnly()){
                    ndef.makeReadOnly();
                }
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void showSnackBar(String message){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .show();
    }

    public void checkNFCState(){

        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {

            showSnackBar(context.getResources().getString(R.string.nfcOnPrompt));
        }else if(adapter != null && !adapter.isEnabled()){

            showSnackBar(context.getResources().getString(R.string.nfcOffPrompt));
        }else{

            showSnackBar(context.getResources().getString(R.string.nfcNonePrompt));
        }
    }
}
