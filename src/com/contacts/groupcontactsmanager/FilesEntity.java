package com.contacts.groupcontactsmanager;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class FilesEntity implements Comparable<FilesEntity>{

	private String fileName;
	private long lastModified;
	private String lastModifiedFormatted;
	private String getAbsolutePath;
	
	public int compareTo(FilesEntity filesEntity) {		
		//instance 변수.compareTo(parameter 변수) : 오름차순
		//parameter 변수.compareTo(instance 변수) : 내림차순
		return filesEntity.getLastModifiedFormatted().compareTo(lastModifiedFormatted);
	}
	
	public FilesEntity(String fileName, long lastModified, String getAbsolutePath) {
		this.fileName = fileName;
		this.lastModified = lastModified;
		this.getAbsolutePath = getAbsolutePath;
		
		SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd hh:mm:ss", Locale.KOREA );
		this.lastModifiedFormatted = formatter.format ( lastModified );		
	}
		
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public long getLastModified() {
		return lastModified;
	}
	
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	public String getLastModifiedFormatted() {
		return lastModifiedFormatted;
	}
	
	public void setLastModifiedFormatted(String lastModifiedFormatted) {
		this.lastModifiedFormatted = lastModifiedFormatted;
	}

	public String getGetAbsolutePath() {
		return getAbsolutePath;
	}

	public void setGetAbsolutePath(String getAbsolutePath) {
		this.getAbsolutePath = getAbsolutePath;
	}
		
}
