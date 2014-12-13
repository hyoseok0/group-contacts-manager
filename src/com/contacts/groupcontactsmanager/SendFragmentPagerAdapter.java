package com.contacts.groupcontactsmanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SendFragmentPagerAdapter extends FragmentPagerAdapter {

    public SendFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    
	@Override
	public Fragment getItem(int i) {
        
		Fragment fragment =  new Send1Fragment();
				
		switch(i) {
		case 0 : fragment = new Send1Fragment();
		               break;
		case 1 : fragment = new Send2Fragment();
		               break;
		case 2 : fragment = new Send3Fragment();
		               break;
		case 3 : fragment = new Send4Fragment();
		               break;
		case 4 : fragment = new Send5Fragment();
		               break;
		case 5 : fragment = new Import6Fragment();
		               break;
		}
        return fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 6;
	}

    @Override
    public CharSequence getPageTitle(int i) {
        String title ="";
        
		switch(i) {
		case 0 : title = "first";
		               break;
		case 1 : title = "second";
		               break;
		case 2 : title = "third";
		               break;
		case 3 : title = "fourth";
		               break;
		case 4 : title = "fifth";
		               break;
		case 5 : title = "sixth";
		               break;
		}
        
        return title;
    }
	
}
