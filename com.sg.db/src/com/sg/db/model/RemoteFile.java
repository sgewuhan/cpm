package com.sg.db.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.sg.db.DBActivator;

public class RemoteFile implements Comparable<RemoteFile>{

	public static final int FILE_UPLOADED = 0;

	public static final int FILE_REMOVED = 1;

	public static final int EMPTY = 2;

	public static final int FILE_IN_DB = 3;

	private String fileName;

	private File serverFile;

	private String namespace;
	private ObjectId gridfsObjectId;

	private GridFS gridfs;

	private int status;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public DBObject getOutputRefData() {
		DBObject db = new BasicDBObject();
		db.put("_id", gridfsObjectId);
		db.put("namespace", namespace);
		db.put("fileName", fileName);
		return db;
	}

	public void remove() {
		if (status == FILE_IN_DB) {
			GridFS gridFS = getGridFS();
			gridFS.remove(gridfsObjectId);

			fileName = null;
			gridfs = null;
			serverFile = null;
			status = EMPTY;
		}
	}

	public void add() throws FileNotFoundException {
		// get file inputstream
		GridFSInputFile gfsFile;
		FileInputStream in = new FileInputStream(serverFile);

		GridFS gridFS = getGridFS();
		gfsFile = gridFS.createFile(in, fileName, true);
		gfsFile.put("_id", gridfsObjectId);
		gfsFile.save();
		serverFile.delete();
		
		status = FILE_IN_DB;
	}

	private GridFS getGridFS() {
		if (this.gridfs == null) {
			DB db = DBActivator.getDatabase();
			if (namespace == null) {
				gridfs = new GridFS(db);
			} else {
				gridfs = new GridFS(db, namespace);
			}
		}
		return gridfs;
	}

	public static RemoteFile createRemoteFileFromDB(String _fileName, String _nameSpace, ObjectId _oid) {
		RemoteFile fs = new RemoteFile();

		fs.namespace = _nameSpace;
		
		fs.gridfsObjectId = _oid;

		fs.fileName = _fileName;
		
		fs.status = FILE_IN_DB;
		
		return fs;
	}

	public static Object createEmptyRemoteFile(String _namespace) {
		RemoteFile fs = new RemoteFile();

		fs.namespace = _namespace;

		fs.status = EMPTY;
		return fs;
	}
	

	public boolean hasFile() {
		return status==FILE_IN_DB||status==FILE_UPLOADED;
	}

	public void setFileUploaded(String selectedFileName, File uploadedFile, ObjectId objectId) {
		fileName = selectedFileName;
		serverFile = uploadedFile;
		gridfsObjectId = objectId;
		status = FILE_UPLOADED;
	}

	public void setFileRemoved() {
		status = FILE_REMOVED;
	}

	public int getStatus(){
		return status;
	}

	public File getServerFile() {
		return serverFile;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getObjectId() {
		return gridfsObjectId.toString();
	}

	@Override
	public int compareTo(RemoteFile o) {
		return fileName.compareTo(o.fileName);
	}

}