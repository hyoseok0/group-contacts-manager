package com.contacts.groupcontactsmanager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVWriteProc;
import au.com.bytecode.opencsv.CSVWriter;

public class Send_1FormatPhoneEmail extends ActionBarActivity {

	String filename = "";
	public static final int MAIL_REQUEST_CODE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_send_1_format_phone_email);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_1_format_phone_email, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void send_1FormatPhoneEmail(View view) {
		Intent intent = new Intent(this, Import_6_FormatToContacts.class);
		startActivity(intent);
		overridePendingTransition(0,0);
		//finish();
	}
	
	public void makeCsvTemplateFile() {
		// 직접 csv를 만드는 방식이나, 엑셀에서 열어서 저장 시 ','(seperator)가 사라지고, 유니코드로 저장되는 문제가 있음
		//https://code.google.com/p/opencsv/ 에서 방법 가져옴
		CSV csv = CSV
			    .separator(',')        // delimiter of fields
			    .quote('"')               // quote character
			    .charset("euc-kr") // 한글이 깨지지 않도록 설정
			    .create();                // new instance is immutable
		
		SimpleDateFormat formatter = new SimpleDateFormat ( "yyyyMMdd", Locale.KOREA );
		Date currentTime = new Date ( );
		String dTime = formatter.format ( currentTime );
		
		filename = "PJOJECT_Contacts_" + dTime + ".csv";
		
		csv.write(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename, new CSVWriteProc() {
		    public void process(CSVWriter out) {
		        out.writeNext("이름","그룹","휴대폰 번호","회사 번호","집 번호","개인 이메일", "회사 이메일");
		   }
		});			
	}
	
	public void sendEmail(View view) {
							
		EditText timeView = (EditText) findViewById(R.id.emailAddress);

		String recipients[] = { timeView.getText().toString() };
		String emailReg = "^[A-Za-z0-9._-]+@[A-Za-z0-9._-]+$";
		if ("abc@example.com".equals(recipients[0]) || "".equals(recipients[0])) {
			//선택한 파일로 연락처 입력을 할 것인지, 하지 않을 것인지 선택
			emailValidationAlert("이메일 주소를 입력하세요");
		} else if(!"".equals(recipients[0]) && !recipients[0].matches(emailReg) ) {
			emailValidationAlert("이메일 주소를 이메일 주소 형식에 맞게 입력하세요");
		} else {
			//연락처 입력 양식 파일 만들기		
			makeCsvTemplateFile();
			
			Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
			email.setType("message/rfc822");

			email.putExtra(Intent.EXTRA_EMAIL, recipients);
			email.putExtra(Intent.EXTRA_SUBJECT, "연락처 입력 양식 파일");
			email.putExtra(Intent.EXTRA_TEXT,
					"연락처 입력 양식 파일에 '이름', '그룹', '휴대폰 번호', '회사 번호', '집 번호', '개인 이메일', '회사 이메일'을 입력하세요.\n" +
					"개인 이메일은 '그룹 연락처 휴대폰 입력 안내' 메일 발송을 위해 사용됩니다.");

			Uri uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename));
			if (uri != null) {
				email.putExtra(Intent.EXTRA_STREAM, uri);
			}
			
			try {

				// the user can choose the email client
				startActivityForResult(Intent.createChooser(email,
						"이메일 앱을 선택하세요"), MAIL_REQUEST_CODE);
			} catch (android.content.ActivityNotFoundException ex) {
				deleteCsvTemplateFile();
				
				Toast.makeText(getApplicationContext(),
						"이메일 앱을 설치해주세요", Toast.LENGTH_LONG).show();
			}
		}		
	}

	private void emailValidationAlert(String msg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		alertDialogBuilder
		.setMessage(msg)
		.setCancelable(false)
		.setPositiveButton("확 인",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {			
				return;
			}
		  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		if (requestCode == MAIL_REQUEST_CODE) {
			
			//연락처 입력 파일 삭제
			deleteCsvTemplateFile();
			
			Intent intent = new Intent(this, Import_6_FormatToContacts.class);
			startActivity(intent);
			overridePendingTransition(0,0);
		}
	}

	private void deleteCsvTemplateFile() {
		File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if (directory.isDirectory()) {
			//특정 directory 내 파일 목록 가져오기
			   File[] files = directory.listFiles();
			 
			   for (File file : files) {
			 
			      //파일이 directory 가 아닌 file 일때
			      if (file.isFile()) {
			    	  if (file.getName().equals(filename)) {				    		 
			    		  file.delete();
			    	  }
			      }
			   }
		}
	}
	
	public void navigate6(View view) {
		Intent intent = new Intent(this, Import_6_FormatToContacts.class);
		startActivity(intent);		
		overridePendingTransition(0,0);
		finish();
	}
	
	//email EditText 를 클릭했을 때, 예시 text 삭제 및 색 변경
	public void resetText(View view) {
		EditText emailAddress = (EditText) view;
		emailAddress.setText("");
		emailAddress.setTextColor(Color.BLACK);
	}
}
