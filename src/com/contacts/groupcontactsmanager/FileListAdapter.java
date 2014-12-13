package com.contacts.groupcontactsmanager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import com.contacts.groupcontactsmanager.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {

	ArrayList<FilesEntity> list = new ArrayList<FilesEntity>();
	/*Context 
	   1) concept : application environment, access to application-specific resources and classes
	                        , access to application-level operations such as launching activities, broadcasting and receiving intents.
	   2) usage : constructor 에서 설정, Activity activity = (Activity) context;                     	  
	 */
	private Context context;
	
	public FileListAdapter(Context context) {
		this.context = context;
		//특정 위치의 파일 목록 가져오기	
		File directory = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_DOWNLOADS);				
		if (directory.isDirectory()) {
			
			File[] files = directory.listFiles();
						
			for (File file : files) {

			//파일이 directory 가 아닌 file 일때
			if (file.isFile()) {
			//file 명 console 창에 뿌려주기

			SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd hh:mm:ss", Locale.KOREA );
//			Date currentTime = new Date ( );
			String dTime = formatter.format ( file.lastModified() );
			
			list.add(new FilesEntity(file.getName(), file.lastModified(), file.getAbsolutePath()));
			
			Collections.sort(list);
			
			}

			}
		}				
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return getItem(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
						
		if (convertView == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			convertView = layoutInflater.inflate(R.layout.activity_files, parent, false);
		} 
		
		FilesEntity filesEntity = list.get(position);
        
		TextView fileNameView = (TextView) convertView.findViewById(R.id.fileName_view);
		fileNameView.setText(filesEntity.getFileName());
		
		TextView lastModifiedView = (TextView) convertView.findViewById(R.id.lastModified_view);
		lastModifiedView.setText(filesEntity.getLastModifiedFormatted());		
		
		//OnClickListener 설정
		OnClickListener onClickListener = new OnClickListener() {
			
			@Override 
			public void onClick(View clickedView) {      
				Activity activity = (Activity) context;
				Intent intent = activity.getIntent();
				TextView textView = (TextView)clickedView;
				String retrievedFileName = "";
				
				for (FilesEntity filesEntity : list) {
					if (filesEntity.getFileName().equals(textView.getText().toString())
							|| filesEntity.getLastModifiedFormatted().equals(textView.getText().toString())) {
						retrievedFileName = filesEntity.getGetAbsolutePath();
					}
				}
				
				intent.putExtra("retrievedFileName", retrievedFileName);
				activity.setResult(activity.RESULT_OK, intent);
				activity.finish();
				
			}
		};
		
		fileNameView.setOnClickListener(onClickListener);		
		lastModifiedView.setOnClickListener(onClickListener);
		
		return convertView;		
	}
	
	
	
}
