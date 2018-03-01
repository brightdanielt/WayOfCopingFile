package com.example.danielt.wayofcopingfile.tool;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.danielt.wayofcopingfile.AdapterFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Created by danielt on 2018/2/21.
 */

public class CopyFile {
    public static String FILE_EXISTS = "file already exists";
    public static String FILE_NOT_EXISTS = "file does not exist";

    Context myContext;

    public CopyFile(Context context) {
        this.myContext = context;

        File dest_dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + "100MEDIA" + "/" + "Copy");
        if (!dest_dir.exists()) {
            dest_dir.mkdir();
        }

    }


    public String verifyFile(String pathOfFile) {
        File file = new File(pathOfFile);

        if (file.exists()) {
            return FILE_EXISTS;
        } else {
            return FILE_NOT_EXISTS;
        }
    }

    public String verifyFile(Uri uri) {
        File file = new File(uri.getPath());

        if (file.exists()) {
            return FILE_EXISTS;
        } else {
            return FILE_NOT_EXISTS;
        }
    }

    public String getPath(Uri uri) {

        String path = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = myContext.getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }


    public void copyFileUsingFileStreams(List<AdapterFile.FileItem> fileItemList)
            throws IOException {
        for (AdapterFile.FileItem item : fileItemList) {
            File src = new File(item.szFilePathName);
            File dest = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + "100MEDIA" + "/" + "Copy" + "/" + item.szFileName);

            InputStream input = null;
            OutputStream output = null;
            try {

                input = new FileInputStream(src);
                output = new FileOutputStream(dest);
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }
            } finally {

                input.close();
                output.close();
            }
        }


    }

    public void copyFileUsingFileChannels(List<AdapterFile.FileItem> fileItemList)
            throws IOException {
        for (AdapterFile.FileItem item : fileItemList) {
            File src = new File(item.szFilePathName);
            File dest = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + "100MEDIA" + "/" + "Copy" + "/" + item.szFileName);

            FileChannel inputChannel = null;
            FileChannel outputChannel = null;
            try {
                inputChannel = new FileInputStream(src).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
//                inputChannel.transferTo(0, inputChannel.size(), outputChannel);

            } finally {
                inputChannel.close();
                outputChannel.close();
            }
        }
    }

    public void copyFileUsingJava7Files(List<AdapterFile.FileItem> fileItemList)
            throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (AdapterFile.FileItem item : fileItemList) {
                File src = new File(item.szFilePathName);
                File dest = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + "100MEDIA" + "/" + "Copy" + "/" + item.szFileName);

                Files.copy(src.toPath(), dest.toPath());
            }
        }
    }

    public void copyFileUsingApacheCommonsIO(List<AdapterFile.FileItem> fileItemList)
            throws IOException {
        for (AdapterFile.FileItem item : fileItemList) {
            File src = new File(item.szFilePathName);
            File dest = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + "100MEDIA" + "/" + "Copy" + "/" + item.szFileName);

            FileUtils.copyFile(src, dest);
        }
    }

}
