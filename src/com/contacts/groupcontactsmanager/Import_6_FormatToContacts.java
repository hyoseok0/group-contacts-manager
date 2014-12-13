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
    
	// ����ó �׸�� ������
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

		//������ ���Ϸ� ����ó �Է��� �� ������, ���� ���� ������ ����
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		alertDialogBuilder
		.setTitle("����ó �Է� ���� Ȯ��")
		.setMessage(displayRetrievedFileName + " �� ����ó�� �Է��Ͻðڽ��ϱ�?")
		.setCancelable(false)
		.setPositiveButton("Ȯ ��",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				confirmInsertPhoneOwnerContacts();
			}
		  })
		.setNegativeButton("�� ��",new DialogInterface.OnClickListener() {
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
		//������ ���Ϸ� ����ó �Է��� �� ������, ���� ���� ������ ����
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		alertDialogBuilder
		.setTitle("�Է� ���� ����ó Ȯ��")
		.setMessage("���ΰ� ���� �̸��� ����ó�� �ִٸ�, �����Ͻðڽ��ϱ�?")
		.setCancelable(false)
		.setPositiveButton("��",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
				
				//���� �̸� ��ȸ
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
		.setNegativeButton("�ƴϿ�",new DialogInterface.OnClickListener() {
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
		//������ ���Ϸ� ����ó �Է��� �� ������, ���� ���� ������ ����
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		alertDialogBuilder
		.setTitle("���� �̸� Ȯ��")
		.setMessage("������ �̸��� '" + phoneOwnerName + "'�� �½��ϱ�?")
		.setCancelable(false)
		.setPositiveButton("��",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
				insertDataIntoContactsOperation();				
			}
		  })
		.setNegativeButton("�ƴϿ�",new DialogInterface.OnClickListener() {
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
		
		//������ ���Ϸ� ����ó �Է��� �� ������, ���� ���� ������ ����
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View writePhoneOwnerNameView = layoutInflater.inflate(R.layout.dialog_insert_name, null);
		final EditText phoneOwnerNameEditText = (EditText) writePhoneOwnerNameView.findViewById(R.id.phoneOwnerName);
		alertDialogBuilder
		.setView(writePhoneOwnerNameView)
		.setPositiveButton("�Է�",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {			
				dialog.cancel();
				phoneOwnerName = phoneOwnerNameEditText.getText().toString();
				
				if ("".equals(phoneOwnerName)) {
					confirmIncludePhonOwnerName("���� �̸� �Է����� �ʾҽ��ϴ�.");
				} else {
					groupContactsDatabaseHelper.insertOwnerName(phoneOwnerName);
					insertDataIntoContactsOperation();
				}			
			}			
		  })
		.setNegativeButton("�� ��",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				phoneOwnerName = "";
				dialog.cancel();
				confirmIncludePhonOwnerName("���� �̸� �Է��� ����ϼ̽��ϴ�.");
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	private void confirmIncludePhonOwnerName(String message) {
		//������ �̸��� ������ ������ Ȯ��
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		alertDialogBuilder
		.setTitle("���� �̸� �Է� Ȯ��")
		.setMessage(message + "\n���� �̸��� �������� �ʰ� ����ó�� �Է��Ͻðڽ��ϱ�?")
		.setCancelable(false)
		.setPositiveButton("��",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
				insertDataIntoContactsOperation();				
			}
		  })
		.setNegativeButton("�ƴϿ�",new DialogInterface.OnClickListener() {
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
			//smart phone�� ��ġ�Ǿ� �ִ� file exploer app �̿�, default�� ��ġ�Ǿ� ���� �ʱ� ������ file explorer app�� ���� ���, ����� �������� �ʴ´�.			
			 insertDataintoContacts();
			
		}
	}
	
	private long getGroupSourceId(String title) {
		long id = 0;
		
		//ContactsContract.Groups �� '_ID', 'TITLE' ��ȸ
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
	 columnRow ���� ����ó �׸���� ���� ���ϱ�		 
	 */
	private void setContactsItemIndex(String columnRow) {
						
		String[] columns = columnRow.split(",");

		// ����ó �׸��� �� ���
		String names[] = { "�̸�", "����", "����", "����", "name" };
		String groups[] = { "�׷�", "�׷��", "������Ʈ", "ȸ��", "��ü","group" };
		String celluarPhones[] = { "�޴��� ��ȣ", "��ȭ��ȣ", "��ȭ ��ȣ", "celluar phone number" };
		String officePhones[] = { "ȸ�� ��ȣ", "office phone number" };
		String homePhones[] = { "�� ��ȣ" };
		String personalEmails[] = { "���� �̸���", "���� �̸��� �ּ�", "�̸���", "�̸��� �ּ�", "email", "email address"};
		String companyEmails[] = { "ȸ�� �̸���", "ȸ�� �̸��� �ּ�", "company email", "company email address"};
					
		for (int i = 0; i < columns.length; i++) {
           String column = columns[i].trim();
			
			// ����ó �׸�(name)�� �������� �������� �ʾ��� ����, ����ó �׸�(name)�� �� ����� ��ȸ�ؼ� �������� ���Ѵ�.
			if (nameIndex == -1) {
				for (String name : names) {
					if (column.equals(name)) {
						nameIndex = i;
						break;
					}
				}
				// ����ó �׸�(name)�� �������� �������ٸ�,  �ٸ� ����ó �׸�(name ����)�� �� ����� ��ȸ���� �ʴ´�.
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
	
		final ProgressDialog progressDialog = ProgressDialog.show(this, null, "����ó�� �޴����� �Է����Դϴ�");
		
		Thread thread = new Thread() {
			
			public void run() {
				
				boolean result = insertDataIntoContacts();
				
				if (result == true) { 
					handler.post(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Context ctx = getApplicationContext();
							
							int duration = Toast.LENGTH_SHORT;
							Toast toast = Toast.makeText(ctx, "����ó �Է� ����!", duration);
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
							Toast toast = Toast.makeText(ctx, "����ó �Է� ����!", duration);
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
				
				//����� group ���� no duplicate allowed �ؼ� ��������
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
				
				//Contacts group �Է�
				while(it.hasNext()) {
					
					String title = (String) it.next();
					
					//�Է��Ϸ��� �׷������ Groups table ���� id ��ȸ
					long groupId = getGroupSourceId(title);
					
					//�Է��Ϸ��� �׷������ ��ϵ��� ���� ��� Groups table �� �׷� ���
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
				//�÷� ����
				int contactsItemsCount = 0;										
				while ((contactsItems = reader.readLine()) != null) {
															
					// ù��° row�� column ���̹Ƿ� �Է����� ����
					if (lineNumber != 0) {
						
						String[] a = contactsItems.split(",");
						
						//�� �׸��� index ���� contactsItemsCount ���� ũ�ų� ���� ���, a[�׸�index]�� ȣ�� �� ArrayIndexOutOfBoundsException �� ����. 
						//���� �� �׸��� index ���� contactsItemsCount ���� ���� ��쿡�� a[�׸�index]���� ȣ���Ѵ�.
						contactsItemsCount = a.length;		
						
						//����ڰ� ���� �̸��� �Է����� ���� ���, ��ü �Է�
						if ("".equals(phoneOwnerName)) {
							insertOneContactOperation(contactsItemsCount, a);
						} else {
							//����ڰ� ���� �̸��� �Է��� ���, �Է��� ���� �̸��� ���ؼ��� �Է����� �ʵ��� ����
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
		//ACCOUNT_TYPE, ACCOUNT_NAME �� online service �� ����ó sync �� ���� �ʿ��� �׸�����, ���⼭�� null�� �����Ѵ�. 
		//�� �ݵ��, �����ϴ� �κ��� �����ؼ��� �ȵȴ�. �������� ���� NullPointerException�� ����. 
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
		
		   //'�׷� ����ó �޴��� �Է� �ȳ�' ���� �߼��� ���� ���� �ּ� �Է�
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
			Toast toast = Toast.makeText(ctx, "����ó �Է� ����", duration);
			toast.show();
																	
		}
	}
	
	public void sendInformationMailDialog() {
		
		//������ ���Ϸ� ����ó �Է��� �� ������, ���� ���� ������ ����
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		alertDialogBuilder
		.setTitle("���������� ���� �߼�")
		.setMessage("�׷� ���������� ���� ���� �ּҷ� ����ó ���ϰ� �� ��ġ �ּҸ� �����ðڽ��ϱ�?")
		.setPositiveButton("Ȯ ��",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {							
			    dialog.cancel();
			    sendInformationMail();
			}
		  })
		.setNegativeButton("�� ��",new DialogInterface.OnClickListener() {
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
		email.putExtra(Intent.EXTRA_SUBJECT, "�׷� ����ó �޴��� �Է� �ȳ�");
		email.putExtra(Intent.EXTRA_TEXT,
				Html.fromHtml(new StringBuilder()
				.append("<p>�پȵ���̵� ����Ʈ���� ��� ����!��</p>")
				.append("<p>1. ����ó ÷�� ������ �޴����� �ٿ�ε� �ϼ���.</p>")
				.append("<p>2. '�� ��ġ ������ ����'�� ���� [�׷� ����ó �Ŵ��� 2.0] ���� ��ġ�ϼ���.(Play Store�� ����)</p>")
				.append("<p><br><br /></p>")
				.append("<p><a href='https://play.google.com/store/apps/details?id=com.contacts.groupcontactsmanager'><span style='color: rgb(0, 85, 255);'>[�� ��ġ ������ ����]</span></a></p>")
				.append("<p><br><br /></p>")
				.append("<p>3. ���� �����ϰ�, '����ó �Է�' ��ư�� ���� �ٿ�ε��� ����ó ÷�� ������ �����ϼ���.</p>")
				.append("<p>4. �޴����� ����ó�� '�׷�' �޴����� �Էµ� ����ó�� Ȯ���ϼ���.</p>")
			    .toString()));
		Uri uri = Uri.fromFile(new File(retrievedFileName));
		if (uri != null) {
			email.putExtra(Intent.EXTRA_STREAM, uri);
		}
		
		try {
			// the user can choose the email client
			startActivityForResult(Intent.createChooser(email,
					"�̸��� ���� �����ϼ���"), Send_1FormatPhoneEmail.MAIL_REQUEST_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
						
			Toast.makeText(getApplicationContext(),
					"�̸��� ���� ��ġ���ּ���", Toast.LENGTH_LONG).show();
		}
	}
}

  
