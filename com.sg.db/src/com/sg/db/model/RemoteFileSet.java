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
		// ���ļ����浽���ݿ�
		// 1..�����ݿ�����ȥҪɾ�����ļ�
		Iterator<RemoteFile> iter = originalRemoteFileSet.iterator();
		while(iter.hasNext()){
			RemoteFile rf = iter.next();
			if(!updatedRemoteFileSet.contains(rf)){
				//�����ݿ�ɾ��
				rf.remove();
			}
		}
		// 2..�����ݿ����Ҫ��ӵ��ļ�
		iter = updatedRemoteFileSet.iterator();
		while(iter.hasNext()){
			RemoteFile rf = iter.next();
			if(!originalRemoteFileSet.contains(rf)){
				//���浽���ݿ�
				try {
					rf.add();
				} catch (FileNotFoundException e) {
					// 3..�Դ����쳣�Ĵ���
				}
			}
		}
		
		// 4..����ɹ���,ͬ��originalRemoteFileSet, updatedRemoteFileSet
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
