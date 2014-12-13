package com.contacts.groupcontactsmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Import_6_FormatToContacts extends ActionBarActivity {

	final static int PICK_FROM_DOWNLOADS = 1;
	int columnIndex;
	String retrievedFileName;    
    ProgressDialog dialog;
    
    String contactsItems = "";
    
	// 연락처 항목들 순서값
	int nameIndex = -1;
	int groupIndex = -1;
	int celluarPhoneIndex = -1;
	int officePhoneIndex = -1;
	int homePhoneIndex = -1;
	int personalEmailIndex = -1;
	int companyEmailIndex = -1;
    
	final Context context = this;
	
	String phoneOwnerName = "";
	
	private GroupContactsDatabaseHelper groupContactsDatabaseHelper;
	
	Handler handler;
	
	ArrayList<String> recipientsList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_6_format_to_contacts);
		groupContactsDatabaseHelper = new GroupContactsDatabaseHelper(this);
		handler = new Handler();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.import_6__format_to_contacts, menu);
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
	
	public void addContacts(View view) {
		try {
		Intent intent = new Intent(this, FilesList.class);
		startActivityForResult(intent, PICK_FROM_DOWNLOADS);		
		overridePendingTransition(0,0);
		} catch(Exception e) {
			Log.e("addContacts_Exception", e.toString());
		}
	}
	
	public void onContactsAdd(View view) {
		
		dialog = ProgressDialog.show(this, "inserting", "inserting data into contacts");
		
		new Thread(new Runnable() {
			@Override
			public void run() {				
				insertDataintoContacts();
				dialog.dismiss();
			}
		}).start();

	}

	private void insertDataintoContacts() {
		
		confirmInsertContacts();
			
	}

	private void confirmInsertContacts() {
		String displayRetrievedFileName = retrievedFileName.replace("/storage/emulated/0", "");

		//선택한 파일로 연락처 입력을 할 것인지, 하지 않을 것인지 선택
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		alertDialogBuilder
		.setTitle("연락처 입력 파일 확인")
		.setMessage(displayRetrievedFileName + " 로 연락처를 입력하시겠습니까?")
		.setCancelable(false)
		.setPositiveButton("확 인",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				confirmInsertPhoneOwnerContacts();
			}
		  })
		.setNegativeButton("취 소",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	private void confirmInsertPhoneOwnerContacts() {
		//선택한 파일로 연락처 입력을 할 것인지, 하지 않을 것인지 선택
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		alertDialogBuilder
		.setTitle("입력 제외 연락처 확인")
		.setMessage("본인과 같은 이름의 연락처가 있다면, 제외하시겠습니까?")
		.setCancelable(false)
		.setPositiveButton("예",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
				
				//본인 이름 조회
				Cursor result = groupContactsDatabaseHelper.retrieveOwnerName();
				
				if (result.moveToFirst()) {
					do {
						phoneOwnerName = result.getString(result.getColumnIndex(groupContactsDatabaseHelper.TABLE_NAME_OWNER_NAME_COLUMN_OWNER_NAME));
					} while(result.moveToNext());
				}
				
				if (!result.isClosed()) {
					result.close();
				}
				
				if ("".equals(phoneOwnerName)) {
				writePhoneOwnerName();
				} else {
					confirmOwnerName(phoneOwnerName);
				}				
			}
		  })
		.setNegativeButton("아니요",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
				insertDataIntoContactsOperation();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	private void confirmOwnerName(String phoneOwnerName) {
		//선택한 파일로 연락처 입력을 할 것인지, 하지 않을 것인지 선택
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		alertDialogBuilder
		.setTitle("본인 이름 확인")
		.setMessage("본인의 이름이 '" + phoneOwnerName + "'이 맞습니까?")
		.setCancelable(false)
		.setPositiveButton("예",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
				insertDataIntoContactsOperation();				
			}
		  })
		.setNegativeButton("아니요",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
				writePhoneOwnerName();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}	
		
	private void writePhoneOwnerName() {
		
		//선택한 파일로 연락처 입력을 할 것인지, 하지 않을 것인지 선택
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View writePhoneOwnerNameView = layoutInflater.inflate(R.layout.dialog_insert_name, null);
		final EditText phoneOwnerNameEditText = (EditText) writePhoneOwnerNameView.findViewById(R.id.phoneOwnerName);
		alertDialogBuilder
		.setView(writePhoneOwnerNameView)
		.setPositiveButton("입력",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {			
				dialog.cancel();
				phoneOwnerName = phoneOwnerNameEditText.getText().toString();
				
				if ("".equals(phoneOwnerName)) {
					confirmIncludePhonOwnerName("본인 이름 입력하지 않았습니다.");
				} else {
					groupContactsDatabaseHelper.insertOwnerName(phoneOwnerName);
					insertDataIntoContactsOperation();
				}			
			}			
		  })
		.setNegativeButton("취 소",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				phoneOwnerName = "";
				dialog.cancel();
				confirmIncludePhonOwnerName("본인 이름 입력을 취소하셨습니다.");
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	private void confirmIncludePhonOwnerName(String message) {
		//본인의 이름을 포함할 것인지 확인
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		alertDialogBuilder
		.setTitle("본인 이름 입력 확인")
		.setMessage(message + "\n본인 이름을 제외하지 않고 연락처를 입력하시겠습니까?")
		.setCancelable(false)
		.setPositiveButton("예",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
				insertDataIntoContactsOperation();				
			}
		  })
		.setNegativeButton("아니요",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing		
				dialog.cancel();
				writePhoneOwnerName();
				
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_FROM_DOWNLOADS && resultCode == RESULT_OK) {
			retrievedFileName = data.getStringExtra("retrievedFileName");
			//smart phone에 설치되어 있는 file exploer app 이용, default로 설치되어 있지 않기 때문에 file explorer app이 없는 경우, 제대로 동작하지 않는다.			
			 insertDataintoContacts();
			
		}
	}
	
	private long getGroupSourceId(String title) {
		long id = 0;
		
		//ContactsContract.Groups 의 '_ID', 'TITLE' 조회
		//public final Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
		String[] mProjection =
		{ContactsContract.Groups._ID};
								
		String[] mSelectionArgs = {""};
		mSelectionArgs[0] = title;
		
		//String mSelection = null;
		String mSelection = ContactsContract.Groups.TITLE + " = ?"; 
						
		Cursor cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI,
				mProjection, mSelection, mSelectionArgs, null);
		if (cursor.moveToFirst()) {
			do {
				id = cursor.getLong(0);							
			} while(cursor.moveToNext());
		}
		
		if (!cursor.isClosed()) {
			cursor.close();
		}
		return id;
	}
	
	/*
	 columnRow 에서 연락처 항목들의 순서 구하기		 
	 */
	private void setContactsItemIndex(String columnRow) {
						
		String[] columns = columnRow.split(",");

		// 연락처 항목의 값 목록
		String names[] = { "이름", "성명", "성함", "존함", "name" };
		String groups[] = { "그룹", "그룹명", "프로젝트", "회사", "단체","group" };
		String celluarPhones[] = { "휴대폰 번호", "전화번호", "전화 번호", "celluar phone number" };
		String officePhones[] = { "회사 번호", "office phone number" };
		String homePhones[] = { "집 번호" };
		String personalEmails[] = { "개인 이메일", "개인 이메일 주소", "이메일", "이메일 주소", "email", "email address"};
		String companyEmails[] = { "회사 이메일", "회사 이메일 주소", "company email", "company email address"};
					
		for (int i = 0; i < columns.length; i++) {
           String column = columns[i].trim();
			
			// 연락처 항목(name)의 순서값이 정해지지 않았을 때만, 연락처 항목(name)의 값 목록을 조회해서 순서값을 구한다.
			if (nameIndex == -1) {
				for (String name : names) {
					if (column.equals(name)) {
						nameIndex = i;
						break;
					}
				}
				// 연락처 항목(name)의 순서값이 정해졌다면,  다른 연락처 항목(name 제외)의 값 목록을 조회하지 않는다.
				if (nameIndex != -1) {
					continue;
				}
			}

			if (groupIndex == -1) {
				for (String group : groups) {
					if (column.equals(group)) {
						groupIndex = i;
						break;
					}
				}
				if (groupIndex != -1) {
					continue;
				}								
			}
			
			if (celluarPhoneIndex == -1) {
				for (String celluarPhone : celluarPhones) {
					if (column.equals(celluarPhone)) {
						celluarPhoneIndex = i;
						break;
					}
				}				
				if (celluarPhoneIndex != -1) {
					continue;
				}					
			}

			if (officePhoneIndex == -1) {
				for (String officePhone : officePhones) {
					if (column.equals(officePhone)) {
						officePhoneIndex = i;
						break;
					}
				}				
				if (officePhoneIndex != -1) {
					continue;
				}	
			}

			if (homePhoneIndex == -1) {
				for (String homePhone : homePhones) {
					if (column.equals(homePhone)) {
						homePhoneIndex = i;
						break;
					}
				}
				if (homePhoneIndex != -1) {
					continue;
				}					
			}
			
			if (personalEmailIndex == -1) {
				for (String personEmail : personalEmails) {
					if (column.equals(personEmail)) {
						personalEmailIndex = i;
						break;
					}
				}
				if (personalEmailIndex != -1) {
					continue;
				}					
			}
			
			if (companyEmailIndex == -1) {
				for (String companyEmail : companyEmails) {
					if (column.equals(companyEmail)) {
						companyEmailIndex = i;
						break;
					}
				}
				if (companyEmailIndex != -1) {
					continue;
				}					
			}		
		}
	}
	
	private void insertDataIntoContactsOperation() {
	
		final ProgressDialog progressDialog = ProgressDialog.show(this, null, "연락처를 휴대폰에 입력중입니다");
		
		Thread thread = new Thread() {
			
			public void run() {
				
				boolean result = insertDataIntoContacts();
				
				if (result == true) { 
					handler.post(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Context ctx = getApplicationContext();
							
							int duration = Toast.LENGTH_SHORT;
							Toast toast = Toast.makeText(ctx, "연락처 입력 성공!", duration);
							toast.show();
														
							if ("manager".equals(MainActivity.userType )) {
								sendInformationMailDialog();											
							}
						}
					});
				} else {
					handler.post(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Context ctx = getApplicationContext();
							
							int duration = Toast.LENGTH_SHORT;
							Toast toast = Toast.makeText(ctx, "연락처 입력 실패!", duration);
							toast.show();
							
							
							
						}
					});					
				}
			}
		};
		
		thread.start();
		
		
		
	}
	private boolean insertDataIntoContacts() {
		
		try {
			
			if (retrievedFileName != null) {
								
				BufferedReader groupReader = new BufferedReader(new InputStreamReader(new FileInputStream(retrievedFileName),"euc-kr"));
												
				int groupLineNumber = 0;
				
				Set<String> groups = new HashSet<String>();
				
				//목록의 group 명을 no duplicate allowed 해서 가져오기
				while((contactsItems = groupReader.readLine()) != null) {
					if (groupLineNumber ==0) {
						setContactsItemIndex(contactsItems);
					}
					else {
						String[] a = contactsItems.split(",");																		
						groups.add(a[groupIndex]);
					}
					groupLineNumber++;
				}
							
				groupReader.close();
				
				Iterator it = groups.iterator();
				
				//Contacts group 입력
				while(it.hasNext()) {
					
					String title = (String) it.next();
					
					//입력하려는 그룹명으로 Groups table 에서 id 조회
					long groupId = getGroupSourceId(title);
					
					//입력하려는 그룹명으로 등록되지 않은 경우 Groups table 에 그룹 등록
					if (groupId <1) {
						//Create a new array of ContentProviderOperation objects
						ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
						
						//Create a new raw contact
						ContentProviderOperation.Builder op = ContentProviderOperation.newInsert(ContactsContract.Groups.CONTENT_URI)
								                                                                                                               .withValue(ContactsContract.Groups.ACCOUNT_NAME, null)
								                                                                                                               .withValue(ContactsContract.Groups.ACCOUNT_TYPE, null)
								                                                                                                               .withValue(ContactsContract.Groups.TITLE, title);
						
						ops.add(op.build());
						
						try {
							getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
						} catch(Exception e) {					
							
							Log.e("insertDataIntoContacts_Exception", e.toString());
							Context ctx = getApplicationContext();
							
							int duration = Toast.LENGTH_SHORT;
							Toast toast = Toast.makeText(ctx, "contactGroupCreationFailure", duration);
							toast.show();	
							return false;
						}													
					} 																									                                                                                                             
				}
																		
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(retrievedFileName),"euc-kr"));
				
				int lineNumber = 0;
				//컬럼 갯수
				int contactsItemsCount = 0;										
				while ((contactsItems = reader.readLine()) != null) {
															
					// 첫번째 row는 column 명이므로 입력하지 않음
					if (lineNumber != 0) {
						
						String[] a = contactsItems.split(",");
						
						//각 항목의 index 값이 contactsItemsCount 보다 크거나 같을 경우, a[항목index]값 호출 시 ArrayIndexOutOfBoundsException 이 난다. 
						//따라서 각 항목의 index 값이 contactsItemsCount 보다 작을 경우에만 a[항목index]값을 호출한다.
						contactsItemsCount = a.length;		
						
						//사용자가 본인 이름을 입력하지 않은 경우, 전체 입력
						if ("".equals(phoneOwnerName)) {
							insertOneContactOperation(contactsItemsCount, a);
						} else {
							//사용자가 본인 이름을 입력한 경우, 입력한 본인 이름에 대해서는 입력하지 않도록 설정
							if (!(a[nameIndex].replaceAll(" ", "").trim().contains(phoneOwnerName) || phoneOwnerName.contains(a[nameIndex].replaceAll(" ", "").trim()))) {
								insertOneContactOperation(contactsItemsCount, a);						
							}
						}
					}

					lineNumber++;
				}
				
				reader.close();

				phoneOwnerName = "";

				
			}
		} catch (Exception e) {
			Log.e("insertDataIntoContacts_Exception", e.toString());			
			return false;
		}
				
		return true;
		
	}

	private void insertOneContactOperation(int contactsItemsCount, String[] a) {
		//Create a new array of ContentProviderOperation objects
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
													
		//Create a new raw contact
		//ACCOUNT_TYPE, ACCOUNT_NAME 은 online service 의 연락처 sync 를 위해 필요한 항목으로, 여기서는 null로 설정한다. 
		//단 반드시, 설정하는 부분을 누락해서는 안된다. 누락했을 때는 NullPointerException이 난다. 
		ContentProviderOperation.Builder op = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				                                                                                                               .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null) 
				                                                                                                               .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)								                                                                                                               
				                                                                                                               ;
		
		ops.add(op.build());
		
		if (contactsItemsCount > nameIndex) {
			//Create the display name for the new raw contact, as a StructuredName data row
			op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, a[nameIndex]);
			
			ops.add(op.build());
		}
		
		if (contactsItemsCount > celluarPhoneIndex) {
			op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, a[celluarPhoneIndex])
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "TYPE_MOBILE")
			        .withValue(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY, 1);
			
			ops.add(op.build());
		}
		
		if (contactsItemsCount > officePhoneIndex ) {
			op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, a[officePhoneIndex])
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "TYPE_WORK");
			
			ops.add(op.build());
		}
		
		if (contactsItemsCount > homePhoneIndex) {
			op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, a[homePhoneIndex])
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "TYPE_HOME");
			
			ops.add(op.build());
		}			
		
		if (contactsItemsCount > groupIndex) {
			op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					//withValueBackReference - insert or modify a column with the result of a previous operation
					//parameter - key(column to be inserted), previousResult(0 - success)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)						
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
				    .withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, getGroupSourceId(a[groupIndex]))
					;
			
			ops.add(op.build());
		}
		
		if (contactsItemsCount > companyEmailIndex  ) {
			op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, a[companyEmailIndex])
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE, "TYPE_WORK");
			
			ops.add(op.build());
		
		}
		
		if (contactsItemsCount > personalEmailIndex  ) {
			op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, a[personalEmailIndex])
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE, "TYPE_HOME");
			
			//make other process run, close transaction
			//usually, set a yield point at the last operation
			op.withYieldAllowed(true);
			
			ops.add(op.build());
		
		   //'그룹 연락처 휴대폰 입력 안내' 메일 발송을 위해 메일 주소 입력
			if (!"".equals(a[personalEmailIndex])) {
			   recipientsList.add(a[personalEmailIndex]);
			}
		}
				
		try {
			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		} catch(Exception e) {					
			
			Log.e("insertOneContactOperation_Exception", e.toString());
			Context ctx = getApplicationContext();
			
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(ctx, "연락처 입력 실패", duration);
			toast.show();
																	
		}
	}
	
	public void sendInformationMailDialog() {
		
		//선택한 파일로 연락처 입력을 할 것인지, 하지 않을 것인지 선택
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		alertDialogBuilder
		.setTitle("구성원에게 메일 발송")
		.setMessage("그룹 구성원들의 개인 메일 주소로 연락처 파일과 앱 설치 주소를 보내시겠습니까?")
		.setPositiveButton("확 인",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {							
			    dialog.cancel();
			    sendInformationMail();
			}
		  })
		.setNegativeButton("취 소",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {								
			    dialog.cancel();
			}
		  })		  
		  ;

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	public void sendInformationMail() {
		
		int recipientsListSize = recipientsList.size();
		
		String recipients[] = new String[recipientsListSize];
		
        for (int i = 0; i<recipientsListSize; i++) {
        	recipients[i] = recipientsList.get(i);        	
        }
		               
		Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));		
		email.setType("message/rfc822");

		email.putExtra(Intent.EXTRA_EMAIL, recipients);
		email.putExtra(Intent.EXTRA_SUBJECT, "그룹 연락처 휴대폰 입력 안내");
		email.putExtra(Intent.EXTRA_TEXT,
				Html.fromHtml(new StringBuilder()
				.append("<p>☆안드로이드 스마트폰만 사용 가능!☆</p>")
				.append("<p>1. 연락처 첨부 파일을 휴대폰에 다운로드 하세요.</p>")
				.append("<p>2. '앱 설치 페이지 열기'를 눌러 [그룹 연락처 매니저 2.0] 앱을 설치하세요.(Play Store로 열기)</p>")
				.append("<p><br><br /></p>")
				.append("<p><a href='https://play.google.com/store/apps/details?id=com.contacts.groupcontactsmanager'><span style='color: rgb(0, 85, 255);'>[앱 설치 페이지 열기]</span></a></p>")
				.append("<p><br><br /></p>")
				.append("<p>3. 앱을 실행하고, '연락처 입력' 버튼을 눌러 다운로드한 연락처 첨부 파일을 선택하세요.</p>")
				.append("<p>4. 휴대폰의 연락처의 '그룹' 메뉴에서 입력된 연락처를 확인하세요.</p>")
			    .toString()));
		Uri uri = Uri.fromFile(new File(retrievedFileName));
		if (uri != null) {
			email.putExtra(Intent.EXTRA_STREAM, uri);
		}
		
		try {
			// the user can choose the email client
			startActivityForResult(Intent.createChooser(email,
					"이메일 앱을 선택하세요"), Send_1FormatPhoneEmail.MAIL_REQUEST_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
						
			Toast.makeText(getApplicationContext(),
					"이메일 앱을 설치해주세요", Toast.LENGTH_LONG).show();
		}
	}
}

  
