package org.mckayscience.looper;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Dialog Box to pop when user does not save before clicking menu in the looper activity.
 */
public class ShareDialogFragment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        builder.setView(input);
        builder.setMessage(R.string.share_dialog_message)
                .setPositiveButton(R.string.dialog_click, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (input.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), "Please enter a valid ID", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
                        sharedPreferences
                                .edit()
                                .putString("Shared", input.getText().toString())
                                .apply();
                        Intent i = new Intent(getActivity().getApplicationContext(), LoadActivity.class);
                        startActivity(i);
                        dialog.dismiss();

                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();

    }


    public ShareDialogFragment() {
        // Required empty public constructor
    }



}