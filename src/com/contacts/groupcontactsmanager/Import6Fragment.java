package com.contacts.groupcontactsmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Import6Fragment extends Fragment {
		 
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.display_import_6_format_to_contacts, container, false);
        
        Button sendButton = (Button) rootView.findViewById(R.id.insertContactsButton);
        sendButton.setEnabled(false);
        
        return rootView;
    }
	           
}
