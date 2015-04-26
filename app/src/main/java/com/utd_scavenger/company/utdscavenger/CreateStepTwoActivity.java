package com.utd_scavenger.company.utdscavenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.utd_scavenger.company.utdscavenger.Data.Item;
import com.utd_scavenger.company.utdscavenger.Exceptions.NfcNotAvailableException;
import com.utd_scavenger.company.utdscavenger.Exceptions.NfcNotEnabledException;
import com.utd_scavenger.company.utdscavenger.Helpers.ItemSerializer;
import com.utd_scavenger.company.utdscavenger.Helpers.NfcHelper;

import java.util.ArrayList;
import java.util.List;


public class CreateStepTwoActivity extends Activity {

    // Helpers.
    NfcHelper mNfcHelper;

    // Storage.
    ArrayList<Item> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_steptwo);

        // Set up helpers.
        try {
            mNfcHelper = new NfcHelper(this, getClass());
        } catch (NfcNotAvailableException | NfcNotEnabledException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Get the list of items form step 1.
        mItems = (ArrayList<Item>)getIntent().getSerializableExtra("items");

        // Re-serialize the items.
        String itemsSerialized = ItemSerializer.serializeItemList(mItems);
        List<Item> itemsTest = ItemSerializer.deserializeItemList(itemsSerialized);

        // Create the NDEF message to send to recipients.
        NdefMessage ndefMessage = mNfcHelper.createNdefMessage(itemsSerialized);
        mNfcHelper.setNdefPushMessage(ndefMessage);
    }

    public void onClickDone (View view){
        new AlertDialog.Builder(this)
                .setTitle("Start Game")
                .setMessage("Are you sure you have added all your players?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    /**
     * Called after onRestoreInstanceState, onRestart, or onPause, for your
     * activity to start interacting with the user.
     *
     * Written by Jonathan Darling and Stephen Kuehl
     */
    @Override
    public void onResume() {
        super.onResume();

        // Enable foreground dispatch. This will ensure that when the NFC tag is
        // read, it will immediately be processed by this activity.
        mNfcHelper.enableForegroundDispatch();
    }
}
