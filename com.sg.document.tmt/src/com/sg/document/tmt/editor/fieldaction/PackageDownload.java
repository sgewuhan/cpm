package com.sg.document.tmt.editor.fieldaction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.AbstractFieldPart;
import com.sg.widget.editor.field.actions.IFieldActionHandler;
import com.sg.widget.util.FileUtil;

public class PackageDownload implements IFieldActionHandler {

	public PackageDownload() {
	}

	@Override
	public Object run(AbstractFieldPart abstractFieldPart, IEditorInput input) {

		BasicDBList fileList = (BasicDBList) ((ISingleObjectEditorInput) input)
				.getInputData()
				.getValue(abstractFieldPart.getField().getName());
		if (fileList == null || fileList.isEmpty()) {
			return null;
		}

		try {
			BufferedInputStream origin = null;

			String pathname = System.getProperty("user.dir") + "/temp";
			File tempFolder = new File(pathname);
			if (!tempFolder.isDirectory()) {
				tempFolder.mkdir();
			}
			String zipFileName = pathname+"/"
					+ System.currentTimeMillis() + ".zip";
			FileOutputStream dest = new FileOutputStream(zipFileName);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest),Charset.forName("GBK"));
			
			byte data[] = new byte[2048];

			Set<String> fileNameSet = new HashSet<String>();

			for (int i = 0; i < fileList.size(); i++) {
				DBObject fileData = (DBObject) fileList.get(i);

				String oid = fileData.get("_id").toString();
				String namespace = (String) fileData.get("namespace");
				String fileName = (String) fileData.get("fileName");
				fileName = checkName(fileName, fileNameSet);

				InputStream is = FileUtil.getInputSteamFromGridFS(namespace,
						oid);

				origin = new BufferedInputStream(is, 2048);
				ZipEntry entry = new ZipEntry(fileName);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, 2048)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
			
			File zipFile = new File(zipFileName);
			if(zipFile.isFile()){
				FileUtil.download(zipFile, zipFile.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private String checkName(String fileName, Set<String> fileNameSet) {
		fileName = fileName.replaceAll("/", "_");
		int i = 1;
		while (fileNameSet.contains(fileName)) {
			fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "(" + (i++) + ")"
					+ fileName.substring(fileName.lastIndexOf("."));
		}
		fileNameSet.add(fileName);
		return fileName;
	}

}
