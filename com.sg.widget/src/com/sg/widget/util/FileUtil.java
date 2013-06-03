package com.sg.widget.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.bson.types.ObjectId;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.ServiceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.sg.db.DBActivator;

public class FileUtil {

    public static void download(String exportOutputPath) {

        File file = new File(exportOutputPath);
        String fileName = file.getName();

        download(file, fileName);
    }

    public static void download(File file, String fileName) {

        UrlLauncher launcher = RWT.getClient().getService(UrlLauncher.class);
        launcher.openURL(getDownloadUrl(file.getPath(), fileName));

    }

    public static void download(String fileServerPath, String fileName) {

        File file = new File(fileServerPath);

        download(file, fileName);
    }

    public static void downloadFromGridFS(String namepace, String oid, String fileName) {

        UrlLauncher launcher = RWT.getClient().getService(UrlLauncher.class);
        launcher.openURL(getGridfsDownloadUrl(namepace, oid, fileName));

    }

    public static ObjectId upload(InputStream in, String fileName, String namespace) {
        ObjectId gridfsObjectId = new ObjectId();
        GridFS gridfs = new GridFS(DBActivator.getDatabase(), namespace);
        GridFSInputFile file = gridfs.createFile(in, true);
        file.put("_id", gridfsObjectId);
        file.setFilename(fileName);
        file.save();
        return gridfsObjectId;
    }

    public static void remove(String fileName, String namespace) {
        GridFS gridfs = new GridFS(DBActivator.getDatabase(), namespace);
        gridfs.remove(fileName);
    }

    public static String getDownloadUrl(String filepath, String filename) {
        String handlerUrl = getHandlerUrl("downloadServiceHandler", new DownloadServiceHandler());

        StringBuilder url = new StringBuilder();
        url.append(handlerUrl);
        url.append("&filepath=");
        url.append(filepath.replace("\\", "/"));
        url.append("&filename=");
        try {
            String fn = URLEncoder.encode(filename, "utf-8");
            url.append(fn);
        } catch (UnsupportedEncodingException e) {
            url.append(filename);
        }
        url.append(filename);
        String encodedURL = RWT.getResponse().encodeURL(url.toString());
        return encodedURL;
    }

    public static String getGridfsDownloadUrl(String namespace, String oid, String filename) {
        String handlerUrl = getHandlerUrl("gridFSDownloadServiceHandler",
                new GridFSDownloadServiceHandler());

        StringBuilder url = new StringBuilder();
        url.append(handlerUrl);
        url.append("&namespace=");
        url.append(namespace);
        url.append("&oid=");
        url.append(oid);
        url.append("&filename=");
        try {
            url.append(URLEncoder.encode(filename, "utf-8"));
        } catch (UnsupportedEncodingException e) {
        }
        String encodedURL = RWT.getResponse().encodeURL(url.toString());
        return encodedURL;
    }

    public static String getHandlerUrl(String id, ServiceHandler serviceHandler) {
        ServiceManager manager = RWT.getServiceManager();
        manager.unregisterServiceHandler(id);
        manager.registerServiceHandler(id, serviceHandler);
        String handlerUrl = manager.getServiceHandlerUrl(id);
        return handlerUrl;
    }

    public static InputStream getInputSteamFromGridFSByFileName(String namespace, String fileName) {

        GridFS gridfs = new GridFS(DBActivator.getDatabase(), namespace);
        GridFSDBFile result = gridfs.findOne(fileName);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            result.writeTo(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static InputStream getInputSteamFromGridFS(String namespace, String oid) {

        GridFS gridfs = new GridFS(DBActivator.getDatabase(), namespace);
        GridFSDBFile result = gridfs.find(new ObjectId(oid));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            result.writeTo(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    @SuppressWarnings("resource")
    public static byte[] getBytesFromFile(File file) throws IOException {

        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        /*
         * You cannot create an array using a long type. It needs to be an int type. Before
         * converting to an int type, check to ensure that file is not loarger than
         * Integer.MAX_VALUE;
         */
        if (length > Integer.MAX_VALUE) {
            return null;
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while ((offset < bytes.length)
                && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {

            offset += numRead;

        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;

    }

    public static byte[] getBytesFromGridFS(String namespace, String oid) {

        GridFS gridfs = new GridFS(DBActivator.getDatabase(), namespace);
        GridFSDBFile result = gridfs.find(new ObjectId(oid));
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            result.writeTo(out);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
            }
        }

        return null;
    }

    public static Image getImageFileFromGridFS(String namespace, String oid) {

        GridFS gridfs = new GridFS(DBActivator.getDatabase(), namespace);
        GridFSDBFile result = gridfs.find(new ObjectId(oid));
        if (result == null)
            return null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            result.writeTo(out);
            InputStream inputstream = new ByteArrayInputStream(out.toByteArray());
            return new Image(Display.getCurrent(), inputstream);
        } catch (Exception e) {
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static String getImageLocationFromDatabase(String namespace, ObjectId fileObjectid) {

        GridFS gridfs = new GridFS(DBActivator.getDatabase(), namespace);
        GridFSDBFile result = gridfs.find(fileObjectid);
        if (result == null) {
            return null;
        } else {

            String resourceName = fileObjectid.toString() + "_" + result.getMD5();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                result.writeTo(out);
                InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
                return getImageLocationFromInputStream(resourceName, inputStream);
            } catch (IOException e) {
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }

        }

        return null;
    }

    public static String getImageLocationFromInputStream(String resourceName,
            InputStream inputStream) {

        ResourceManager resourceManager = RWT.getResourceManager();
        if (!resourceManager.isRegistered(resourceName)) {
            try {
                try {
                    resourceManager.register(resourceName, inputStream);
                } finally {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
        }
        return resourceManager.getLocation(resourceName);

    }

    public static String getImageUrl(String location) {

        return RWT.getRequest().getContextPath() + "/" + location;
    }

}
