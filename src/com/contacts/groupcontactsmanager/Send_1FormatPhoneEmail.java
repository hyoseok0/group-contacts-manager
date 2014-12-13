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
		// ���� csv�� ����� ����̳�, �������� ��� ���� �� ','(seperator)�� �������, �����ڵ�� ����Ǵ� ������ ����
		//https://code.google.com/p/opencsv/ ���� ��� ������
		CSV csv = CSV
			    .separator(',')        // delimiter of fields
			    .quote('"')               // quote character
			    .charset("euc-kr") // �ѱ��� ������ �ʵ��� ����
			    .create();                // new instance is immutable
		
		SimpleDateFormat formatter = new SimpleDateFormat ( "yyyyMMdd", Locale.KOREA );
		Date currentTime = new Date ( );
		String dTime = formatter.format ( currentTime );
		
		filename = "PJOJECT_Contacts_" + dTime + ".csv";
		
		csv.write(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename, new CSVWriteProc() {
		    public void process(CSVWriter out) {
		        out.writeNext("�̸�","�׷�","�޴��� ��ȣ","ȸ�� ��ȣ","�� ��ȣ","���� �̸���", "ȸ�� �̸���");
		   }
		});			
	}
	
	public void sendEmail(View view) {
							
		EditText timeView = (EditText) findViewById(R.id.emailAddress);

		String recipients[] = { timeView.getText().toString() };
		String emailReg = "^[A-Za-z0-9._-]+@[A-Za-z0-9._-]+$";
		if ("abc@example.com".equals(recipients[0]) || "".equals(recipients[0])) {
			//������ ���Ϸ� ����ó �Է��� �� ������, ���� ���� ������ ����
			emailValidationAlert("�̸��� �ּҸ� �Է��ϼ���");
		} else if(!"".equals(recipients[0]) && !recipients[0].matches(emailReg) ) {
			emailValidationAlert("�̸��� �ּҸ� �̸��� �ּ� ���Ŀ� �°� �Է��ϼ���");
		} else {
			//����ó �Է� ��� ���� �����		
			makeCsvTemplateFile();
			
			Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
			email.setType("message/rfc822");

			email.putExtra(Intent.EXTRA_EMAIL, recipients);
			email.putExtra(Intent.EXTRA_SUBJECT, "����ó �Է� ��� ����");
			email.putExtra(Intent.EXTRA_TEXT,
					"����ó �Է� ��� ���Ͽ� '�̸�', '�׷�', '�޴��� ��ȣ', 'ȸ�� ��ȣ', '�� ��ȣ', '���� �̸���', 'ȸ�� �̸���'�� �Է��ϼ���.\n" +
					"���� �̸����� '�׷� ����ó �޴��� �Է� �ȳ�' ���� �߼��� ���� ���˴ϴ�.");

			Uri uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename));
			if (uri != null) {
				email.putExtra(Intent.EXTRA_STREAM, uri);
			}
			
			try {

				// the user can choose the email client
				startActivityForResult(Intent.createChooser(email,
						"�̸��� ���� �����ϼ���"), MAIL_REQUEST_CODE);
			} catch (android.content.ActivityNotFoundException ex) {
				deleteCsvTemplateFile();
				
				Toast.makeText(getApplicationContext(),
						"�̸��� ���� ��ġ���ּ���", Toast.LENGTH_LONG).show();
			}
		}		
	}

	private void emailValidationAlert(String msg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		alertDialogBuilder
		.setMessage(msg)
		.setCancelable(false)
		.setPositiveButton("Ȯ ��",new DialogInterface.OnClickListener() {
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
			
			//����ó �Է� ���� ����
			deleteCsvTemplateFile();
			
			Intent intent = new Intent(this, Import_6_FormatToContacts.class);
			startActivity(intent);
			overridePendingTransition(0,0);
		}
	}

	private void deleteCsvTemplateFile() {
		File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if (directory.isDirectory()) {
			//Ư�� directory �� ���� ��� ��������
			   File[] files = directory.listFiles();
			 
			   for (File file : files) {
			 
			      //������ directory �� �ƴ� file �϶�
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
	
	//email EditText �� Ŭ������ ��, ���� text ���� �� �� ����
	public void resetText(View view) {
		EditText emailAddress = (EditText) view;
		emailAddress.setText("");
		emailAddress.setTextColor(Color.BLACK);
	}
}
