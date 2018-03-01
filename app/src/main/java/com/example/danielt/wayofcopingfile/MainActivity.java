package com.example.danielt.wayofcopingfile;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.danielt.wayofcopingfile.tool.CopyFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    private Button btn_FileStreams, btn_FileChannel, btn_java7Files, btn_ApacheCommonsIO;
    private TextView tv_timeOfCoping, tv_note;
    private Button btn_deleteFile;
    private ListView fileList, fileList_copy;
    private AdapterFile adapterFile, adapterFile_copy;

    private List<AdapterFile.FileItem> mFileList = new ArrayList<AdapterFile.FileItem>();
    private List<AdapterFile.FileItem> mSelectedFile = new ArrayList<AdapterFile.FileItem>();

    private List<AdapterFile.FileItem> mFileList_copy = new ArrayList<AdapterFile.FileItem>();
    private List<AdapterFile.FileItem> mSelectedFile_copy = new ArrayList<AdapterFile.FileItem>();

    private CopyFile cf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cf = new CopyFile(MainActivity.this);
        findView();

    }

    /*只有複製出的檔案會透過程式變更，所以只需要更新該 List*/
    private void updateFileList_copy() {
        mFileList_copy.clear();
        List<AdapterFile.FileItem> fileListTemp = getFileList(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + "100MEDIA" + "/" + "Copy");
        for (AdapterFile.FileItem item : fileListTemp)
            mFileList_copy.add(item);
        fileListTemp.clear();
        adapterFile_copy.notifyDataSetChanged();
    }

    /*取得參數路徑中的檔案資訊*/
    private List<AdapterFile.FileItem> getFileList(String filePath) {
        List<AdapterFile.FileItem> fileList = new ArrayList<AdapterFile.FileItem>();
        do {
            FilenameFilter filenameFilter =
                    new FilenameFilter() {
                        final private String[] mFilters = new String[]{".avi", ".mp3", ".mp4", ".m2ts", ".png", ".jpg"};

                        @Override
                        public boolean accept(File dir, String filename) {
                            // Filter by file extension.
                            for (String filter : mFilters) {
                                int offset = filename.lastIndexOf(filter);
                                if ((offset != -1) && (filename.substring(offset).contains(filter)))
                                    return true;
                            }
                            return false;
                        }
                    };

            File appDir = new File(filePath);
            File[] files = appDir.listFiles(filenameFilter);
            if (files == null) {
//                LogManager.DumpInfo(LOG_TAG, "No file in VivaCAP folder...");
                break;
            }
            Arrays.sort(files);

            for (File file : files) {
                AdapterFile.FileItem item = new AdapterFile.FileItem();
                item.szFileName = file.getName();
                item.szFilePathName = file.getAbsolutePath();
                item.isSelected = false;
                fileList.add(item);
            }
        } while (false);
        return fileList;
    }

    private void findView() {
        btn_FileStreams = findViewById(R.id.btn_FileStreams);
        btn_FileChannel = findViewById(R.id.btn_FileChannel);
        btn_java7Files = findViewById(R.id.btn_Java7Files);
        btn_ApacheCommonsIO = findViewById(R.id.btn_ApacheCommonsIO);
        btn_deleteFile = findViewById(R.id.btn_delete);

        tv_timeOfCoping = findViewById(R.id.tv_timeOfCoping);
        tv_note = findViewById(R.id.tv_note);

        btn_FileStreams.setOnClickListener(this);
        btn_FileChannel.setOnClickListener(this);
        btn_java7Files.setOnClickListener(this);
        btn_ApacheCommonsIO.setOnClickListener(this);
        btn_deleteFile.setOnClickListener(this);

        fileList = findViewById(R.id.fileList_pic);
        fileList_copy = findViewById(R.id.fileList_pic_copy);

        fileList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AdapterFile.FileItem item = mFileList.get(position);
                item.isSelected = !item.isSelected;
                adapterFile.notifyDataSetChanged();
                updateCopyButtonState();
            }
        });
        fileList_copy.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AdapterFile.FileItem item = mFileList_copy.get(position);
                item.isSelected = !item.isSelected;
                adapterFile_copy.notifyDataSetChanged();
                updateDeleteButtonState();
            }
        });

        mFileList = getFileList(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + "100MEDIA");
        adapterFile = new AdapterFile(MainActivity.this, mFileList);
        fileList.setAdapter(adapterFile);

        mFileList_copy = getFileList(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + "100MEDIA" + "/" + "Copy");
        adapterFile_copy = new AdapterFile(MainActivity.this, mFileList_copy);
        fileList_copy.setAdapter(adapterFile_copy);

    }

    /*有勾選檔案時才能點擊 Copy 按鈕*/
    private void updateCopyButtonState() {
        mSelectedFile.clear();
        if (mFileList != null) {
            for (AdapterFile.FileItem item : mFileList) {
                if (item.isSelected) {
                    if (CopyFile.FILE_NOT_EXISTS == cf.verifyFile(item.szFilePathName)) {
                        tv_note.setText("Note: one or more than one file doesn't exist");
                        return;
                    } else {
                        mSelectedFile.add(item);
                    }
                }

            }
            if (mSelectedFile.size() > 0) {
                tv_note.setText("Note: Files selected exist");

                btn_FileStreams.setEnabled(true);
                btn_FileChannel.setEnabled(true);
                btn_java7Files.setEnabled(true);
                btn_ApacheCommonsIO.setEnabled(true);
            } else {
                tv_note.setText("Note: No File selected");

                btn_FileStreams.setEnabled(false);
                btn_FileChannel.setEnabled(false);
                btn_java7Files.setEnabled(false);
                btn_ApacheCommonsIO.setEnabled(false);
            }

        }

    }

    /*有勾選檔案時才能點擊 Delete 按鈕*/
    private void updateDeleteButtonState() {
        mSelectedFile_copy.clear();
        if (mFileList_copy != null) {
            for (AdapterFile.FileItem item : mFileList_copy) {
                if (item.isSelected) {
                    if (CopyFile.FILE_NOT_EXISTS == cf.verifyFile(item.szFilePathName)) {
                        tv_note.setText("Note: one or more than one file doesn't exist");
                        return;
                    } else {
                        mSelectedFile_copy.add(item);
                    }
                }

            }

            if (mSelectedFile_copy.size() > 0) {
                tv_note.setText("Note: Files selected exist");
                btn_deleteFile.setEnabled(true);
            } else {
                tv_note.setText("Note: No File selected");
                btn_deleteFile.setEnabled(false);
            }

        } else {
            btn_deleteFile.setEnabled(false);
        }

    }


    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.btn_FileStreams: {
                    long start = System.nanoTime();
                    long end;
                    cf.copyFileUsingFileStreams(mSelectedFile);
                    end = System.nanoTime();
                    long t_millis = end - start;
                    Double sec = (double) t_millis / 1000000000.0;
                    tv_timeOfCoping.setText("Time of coping : " + sec + " s");
                    System.out.println("Time taken by FileStreams Copy = " + sec);
                    break;
                }
                case R.id.btn_FileChannel: {
                    long start = System.nanoTime();
                    long end;
                    cf.copyFileUsingFileChannels(mSelectedFile);
                    end = System.nanoTime();
                    long t_millis = end - start;
                    Double sec = (double) t_millis / 1000000000.0;
                    tv_timeOfCoping.setText("Time of coping : " + sec + " s");
                    System.out.println("Time taken by FileChannel Copy = " + sec);
                    break;
                }
                case R.id.btn_Java7Files: {
                    long start = System.nanoTime();
                    long end;
                    cf.copyFileUsingJava7Files(mSelectedFile);
                    end = System.nanoTime();
                    long t_millis = end - start;
                    Double sec = (double) t_millis / 1000000000.0;
                    tv_timeOfCoping.setText("Time of coping : " + sec + " s");
                    System.out.println("Time taken by FileChannel Copy = " + sec);

                    break;
                }
                case R.id.btn_ApacheCommonsIO: {
                    long start = System.nanoTime();
                    long end;
                    cf.copyFileUsingApacheCommonsIO(mSelectedFile);
                    end = System.nanoTime();
                    long t_millis = end - start;
                    Double sec = (double) t_millis / 1000000000.0;
                    tv_timeOfCoping.setText("Time of coping : " + sec + " s");
                    System.out.println("Time taken by ApacheCommonsIO Copy = " + sec);

                    break;
                }
                case R.id.btn_delete: {
                    long start = System.nanoTime();
                    long end;

                    for (AdapterFile.FileItem item : mSelectedFile_copy) {
                        File file = new File(item.szFilePathName);
                        file.delete();
                    }
                    end = System.nanoTime();
                    long t_millis = end - start;
                    Double sec = (double) t_millis / 1000000000.0;
                    tv_timeOfCoping.setText("Time of deleting : " + sec + " s");
                    System.out.println("Time taken by Delete = " + sec);
                    updateFileList_copy();
                    updateDeleteButtonState();
                    break;
                }

            }
            updateFileList_copy();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
