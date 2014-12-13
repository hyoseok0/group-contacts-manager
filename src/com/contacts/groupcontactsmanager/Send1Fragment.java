package com.contacts.groupcontactsmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class Send1Fragment extends Fragment {
			
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.
						
        View rootView = inflater.inflate(
                R.layout.display_send_1_format_phone_email, container, false);
        
        EditText emailAddress = (EditText) rootView.findViewById(R.id.emailAddress);
        emailAddress.setEnabled(false);
        
        Button sendButton = (Button) rootView.findViewById(R.id.sendButton);
        sendButton.setEnabled(false);
        
        return rootView;
    }
	
}
