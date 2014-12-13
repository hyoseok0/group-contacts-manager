package com.contacts.groupcontactsmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroupContactsDatabaseHelper {

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "groupContactsManager.db";
	private static final String TABLE_NAME_OPEN = "open_true_or_not";
	private static final String TABLE_NAME_OWNER_NAME = "owner_name";
	
	public static final String TABLE_NAME_OPEN_COLUMN_ID = "_id";
	//시작여부
	public static final String TABLE_NAME_OPEN_COLUMN_START_TRUE_OR_NOT = "tureornot";	
	
	public static final String TABLE_NAME_OWNER_NAME_COLUMN_ID = "_id";
	//시작여부
	public static final String TABLE_NAME_OWNER_NAME_COLUMN_OWNER_NAME = "ownername";	
		
	private TimeTrackerOpenHelper openHelper;
	private SQLiteDatabase database;
	
	public GroupContactsDatabaseHelper(Context context) {
		openHelper = new TimeTrackerOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}
	
	public void insertOpenTrueOrNot(Integer tureornot) {
		
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(TABLE_NAME_OPEN_COLUMN_START_TRUE_OR_NOT, tureornot);
		database.insert(TABLE_NAME_OPEN, null, contentValues);
	}
	
	public void insertOwnerName(String ownerName) {
		
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(TABLE_NAME_OWNER_NAME_COLUMN_OWNER_NAME, ownerName);
		database.insert(TABLE_NAME_OWNER_NAME, null, contentValues);
	}	
	
	public Cursor retrieveOpenTrueOrNot() {
		//public Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
		String[] columns = new String[2];
		columns[0] = TABLE_NAME_OPEN_COLUMN_ID;
		columns[1] = TABLE_NAME_OPEN_COLUMN_START_TRUE_OR_NOT;		
		Cursor result = database.query(TABLE_NAME_OPEN, columns, null, null, null, null, null);       					
		return result;
	}
	
	public Cursor retrieveOwnerName() {
		//public Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
		String[] columns = new String[2];
		columns[0] = TABLE_NAME_OWNER_NAME_COLUMN_ID;
		columns[1] = TABLE_NAME_OWNER_NAME_COLUMN_OWNER_NAME;		
		Cursor result = database.query(TABLE_NAME_OWNER_NAME, columns, null, null, null, null, null);       					
		return result;
	}	
	
	private class TimeTrackerOpenHelper extends SQLiteOpenHelper {

		//Context - global information of application environment
		TimeTrackerOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		//실제 수행되는 query를 적어야 한다. 컬럼명과 속성 사이에는 반드시 띄어쓰기가 있어야 한다.
		@Override
		public void onCreate(SQLiteDatabase database) {
			// 시작 여부
			database.execSQL("CREATE TABLE " +  TABLE_NAME_OPEN
					                           +  "(" + TABLE_NAME_OPEN_COLUMN_ID + " INTEGER PRIMARY KEY, " 					                           
					                           + TABLE_NAME_OPEN_COLUMN_START_TRUE_OR_NOT + " INTEGER)");
			// 본인 이름
			database.execSQL("CREATE TABLE " +  TABLE_NAME_OWNER_NAME
                    +  "(" + TABLE_NAME_OWNER_NAME_COLUMN_ID + " INTEGER PRIMARY KEY, " 					                           
                    + TABLE_NAME_OWNER_NAME_COLUMN_OWNER_NAME + " TEXT)");			
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			database.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME_OPEN);
			onCreate(database);
		}

	}
}
