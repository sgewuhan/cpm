package com.sg.widget.util;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.RWT;

public class DownloadServiceHandler implements ServiceHandler {

    public void service() throws IOException, ServletException {

    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Which file to download?
        String filepath = RWT.getRequest().getParameter("filepath");
        // Get the file content
        File file = new File(filepath);
        if (!file.isFile()) {
            return;
        }

        String filename = request.getParameter("filename");

        byte[] download = FileUtil.getBytesFromFile(file);
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
            e1.printStackTrace();
        }
    }

}