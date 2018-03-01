package com.example.danielt.wayofcopingfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.List;

/**
 * Created by VictorWu on 2015/8/11.
 */
public class AdapterFile extends BaseAdapter {
    private static final String LOG_TAG = AdapterFile.class.getSimpleName();

    private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;
    private List<FileItem> mFileList = null;

    public AdapterFile(Context context, List<FileItem> fileList) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mFileList = fileList;
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            //Create new display view
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item_file, null);
            //Set view's cache
            viewHolder.cbFileSelectState = (CheckBox) convertView.findViewById(R.id.cbFileSelectState);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            FileItem item = mFileList.get(position);
            viewHolder.cbFileSelectState.setChecked(item.isSelected);
            viewHolder.cbFileSelectState.setText(item.szFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public static class ViewHolder {
        public CheckBox cbFileSelectState;
    }

    public static class FileItem {
        public boolean isSelected;
        public String szFileName;
        public String szFilePathName;
    }
}
