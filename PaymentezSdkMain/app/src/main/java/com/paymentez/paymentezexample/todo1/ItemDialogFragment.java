package com.paymentez.paymentezexample.todo1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rsa.mobilesdk.sdk.MobileAPI;
import com.rsa.mobilesdk.sdk.MobileAPI.CustomElementType;

public class ItemDialogFragment extends DialogFragment {
    public ItemDialogFragment(){
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        int messageId = arguments.getInt(Todo1CollectActivity.DIALOG_ID_KEY);
        final String messageString = arguments.getString(Todo1CollectActivity.DIALOG_TEXT_KEY);
        String messageTitle = arguments.getString(Todo1CollectActivity.DIALOG_TITLE_KEY);
        AlertDialog alertDialog = null;

        switch(messageId){
            case Todo1CollectActivity.IMMEDIATE_RESULT_DIALOG:
                TextView messageText = new TextView(getActivity());
                messageText.setText(messageString);
                messageText.setVerticalScrollBarEnabled(true);
                messageText.setMovementMethod(new ScrollingMovementMethod());
                messageText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager manager =
                                (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                        TextView showTextParam = (TextView) v;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            manager.setPrimaryClip(ClipData.newPlainText("Device data", showTextParam.getText()));
                        }
                        Toast.makeText(v.getContext(), "Copied device data to clipboard",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(messageTitle)
//                        .setMessage(messageString)
                        .setView(messageText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        })
                        .setNegativeButton("Share", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Device data");
                                intent.putExtra(Intent.EXTRA_TEXT, messageString);
                                startActivity(Intent.createChooser(intent, "Share using"));
                            }
                        }).create();

                break;
            case Todo1CollectActivity.PROGRESS_DIALOG:
                ProgressDialog pd = new ProgressDialog(getActivity());
                pd.setMessage("Sending...");
                pd.setIndeterminate(true);
                alertDialog = pd;
                break;
            case Todo1CollectActivity.ERROR_DIALOG:
                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setMessage(messageString)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }).create();
                break;
            case Todo1CollectActivity.STRING_INPUT_DIALOG:
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                final EditText elementName = new EditText(getActivity());
                elementName.setHint("Element Name");
                final EditText elementValue = new EditText(getActivity());
                elementValue.setHint("Element Value");
                linearLayout.addView(elementName);
                linearLayout.addView(elementValue);
                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Custom String Value")
                        .setView(linearLayout)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MobileAPI mobileApi = MobileAPI.getInstance(getActivity());
                                mobileApi.addCustomElement(CustomElementType.STRING, elementName.getText().toString(), elementValue.getText().toString());
                            }
                        }).create();
                break;
        }
        return alertDialog;
    }
}
