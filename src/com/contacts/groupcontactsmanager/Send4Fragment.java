package com.contacts.groupcontactsmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contacts.groupcontactsmanager.R;

public class Send4Fragment extends Fragment {
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.display_send_4_format_pc_email, container, false);
        return rootView;
    }
}
