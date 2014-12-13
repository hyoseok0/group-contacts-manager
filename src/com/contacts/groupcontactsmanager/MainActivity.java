package com.contacts.groupcontactsmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends FragmentActivity {

    private SendFragmentPagerAdapter sendFragmentPagerAdapter;
    private ViewPager mViewPager;
    private GroupContactsDatabaseHelper groupContactsDatabaseHelper;    
    private int openTrueOrNotValue = 0;
    Context context = this;
    
    public static String userType = "";
    final String member = "member";
    final String manager = "manager";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		selectUserType();
		                
	}

	private void startFragmentActivity() {
		
		groupContactsDatabaseHelper = new GroupContactsDatabaseHelper(this);
		
		//사용자 유형이 [구성원]일 때는 연락처 입력 화면으로 바로 이동
		if (member.equals(userType)) {
			startImport_6_format_to_contacts();		
		} 
		//사용자 유형이 [관리자]일 때는 사용자 설명 화면으로 이동, (만약 두번째로 앱을 실행했다면, 연락처 양식 파일을 메일로 보내는 화면으로 이동)
		else {
			
			//'시작여부' 테이블의 '시작여부' 조회
			retrieveOpenTrueOrNot();

			// 기존에 '시작하기' 버튼을 누른 적이 있다면, 연락처 입력 양식을 보내는 화면으로 바로 이동하기
			if (openTrueOrNotValue == 1) {
				startSend_1FormatPhoneEmai();
			} else {
			
			sendFragmentPagerAdapter = new SendFragmentPagerAdapter(getSupportFragmentManager());
	        mViewPager = (ViewPager) findViewById(R.id.pager);
	        mViewPager.setAdapter(sendFragmentPagerAdapter);
	        
	      //Bind the title indicator to the adapter
	        CirclePageIndicator circleIndicator = (CirclePageIndicator)findViewById(R.id.circles);
	        circleIndicator.setViewPager(mViewPager);        
	        	        	        
			}
		}				
	}
	
	public void navigate1(View view) {
		
		//'시작하기' 버튼을 누른 후 '시작여부' 테이블의 '시작여부' 컬럼 값을 1로 insert
		//'시작여부' 테이블의 id 조회
		retrieveOpenTrueOrNot();
				
		//'시작여부' 테이블의  '시작여부' 컬럼 값을 1로 insert
		groupContactsDatabaseHelper.insertOpenTrueOrNot(1);		
		startSend_1FormatPhoneEmai();
	}

	private void startSend_1FormatPhoneEmai() {
		Intent intent = new Intent(this, Send_1FormatPhoneEmail.class);
		startActivity(intent);
		finish();
		overridePendingTransition(0,0);
	}

	private void startImport_6_format_to_contacts() {
		Intent intent = new Intent(this, Import_6_FormatToContacts.class);
		startActivity(intent);
		finish();
		overridePendingTransition(0,0);
	}
	
	private void retrieveOpenTrueOrNot() {
		Cursor cursor = groupContactsDatabaseHelper.retrieveOpenTrueOrNot();
		if (cursor.moveToFirst()) {
			do {
				openTrueOrNotValue = cursor.getInt(cursor.getColumnIndex(GroupContactsDatabaseHelper.TABLE_NAME_OPEN_COLUMN_START_TRUE_OR_NOT));				
			}while(cursor.moveToNext());
		}
		
		if (!cursor.isClosed()) {
			cursor.close();
		}
	}
	
	private void selectUserType() {
		
		//선택한 파일로 연락처 입력을 할 것인지, 하지 않을 것인지 선택
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		final View selectUserTypeView = layoutInflater.inflate(R.layout.dialog_select_user_type, null);
		final RadioGroup userTypeRadioGroup = (RadioGroup) selectUserTypeView.findViewById(R.id.userTypeRadioGroup);

		alertDialogBuilder
		.setTitle("사용자 유형 선택")
		.setView(selectUserTypeView)		
		.setPositiveButton("확 인",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
								
				int selectedId = userTypeRadioGroup.getCheckedRadioButtonId();
			   
				RadioButton userTypeRadioButton = (RadioButton) findViewById(selectedId);
				
				if (selectedId == R.id.member) {
					userType = member;
				} else {
					userType = manager;
				}
				
				startFragmentActivity();
			    dialog.cancel();
			}
		  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}	
}
