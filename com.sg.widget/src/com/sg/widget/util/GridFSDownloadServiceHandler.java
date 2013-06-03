package com.sg.widget.util;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.service.ServiceHandler;

public class GridFSDownloadServiceHandler implements ServiceHandler {

	public void service() throws IOException, ServletException {
		// Which file to download?

	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String namespace = request.getParameter("namespace");

		String oid = request.getParameter("oid");

		String filename = request.getParameter("filename");

		byte[] download = FileUtil.getBytesFromGridFS(namespace, oid);
		// Send the file in the response
		response.setContentType("application/octet-stream");

		response.setContentLength(download.length);

		String contentDisposition = "attachment; filename=\""
//                + URLEncoder.encode(filename, "UTF-8") 
                + new String(filename.getBytes( "UTF-8"),"ISO8859-1") 
				+ "\"";
		response.setHeader("Content-Disposition", contentDisposition);
		try {
			response.getOutputStream().write(download);
		} catch (IOException e1) {
		}
	}

}