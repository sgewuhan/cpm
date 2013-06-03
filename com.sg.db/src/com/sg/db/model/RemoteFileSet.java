package com.sg.db.model;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.mongodb.BasicDBList;

public class RemoteFileSet {

	private Set<RemoteFile> originalRemoteFileSet = new HashSet<RemoteFile>();
	private Set<RemoteFile> updatedRemoteFileSet = new HashSet<RemoteFile>();
	private String namespace;

	public RemoteFileSet(String namespace) {
		this.namespace = namespace;
	}

	public void add(RemoteFile remoteFile) {
		updatedRemoteFileSet.add(remoteFile);
	}
	
	public void initAdd(RemoteFile remoteFile){
		originalRemoteFileSet.add(remoteFile);
		updatedRemoteFileSet.add(remoteFile);
	}

	public void remove(RemoteFile remoteFile) {
		updatedRemoteFileSet.remove(remoteFile);
	}


	public Set<RemoteFile> getOriginalRemoteFileSet() {
		return originalRemoteFileSet;
	}

	public void setOriginalRemoteFileSet(Set<RemoteFile> originalRemoteFileSet) {
		this.originalRemoteFileSet = originalRemoteFileSet;
	}

	public Set<RemoteFile> getUpdatedRemoteFileSet() {
		return updatedRemoteFileSet;
	}

	public void setUpdatedRemoteFileSet(Set<RemoteFile> updatedRemoteFileSet) {
		this.updatedRemoteFileSet = updatedRemoteFileSet;
	}

	public boolean isDirty() {
		return !(updatedRemoteFileSet.containsAll(originalRemoteFileSet) && originalRemoteFileSet.containsAll(updatedRemoteFileSet));
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return namespace;
	}

	public void saveServerFileToDB() {
		// 将文件保存到数据库
		// 1..从数据库中移去要删除的文件
		Iterator<RemoteFile> iter = originalRemoteFileSet.iterator();
		while(iter.hasNext()){
			RemoteFile rf = iter.next();
			if(!updatedRemoteFileSet.contains(rf)){
				//从数据库删除
				rf.remove();
			}
		}
		// 2..向数据库添加要添加的文件
		iter = updatedRemoteFileSet.iterator();
		while(iter.hasNext()){
			RemoteFile rf = iter.next();
			if(!originalRemoteFileSet.contains(rf)){
				//保存到数据库
				try {
					rf.add();
				} catch (FileNotFoundException e) {
					// 3..对错误异常的处理
				}
			}
		}
		
		// 4..保存成功后,同步originalRemoteFileSet, updatedRemoteFileSet
		originalRemoteFileSet.clear();
		originalRemoteFileSet.addAll(updatedRemoteFileSet);
		
	}

	public BasicDBList getUpdatedData() {
		Iterator<RemoteFile> iter = updatedRemoteFileSet.iterator();
		BasicDBList list = new BasicDBList();
		while (iter.hasNext()) {
			RemoteFile rf = iter.next();
			list.add(rf.getOutputRefData());
		}
		return list;
	}

}
