package com.aiitec.openapi.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import com.aiitec.openapi.json.annotation.JSONField;


public class FileListResponseQuery extends ListResponseQuery {

	@JSONField(entityName="file")
	private List<File> files;
	private List<Integer> ids;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.files);
        dest.writeList(this.ids);
    }

    public FileListResponseQuery() {
    }

    protected FileListResponseQuery(Parcel in) {
        this.files = in.createTypedArrayList(File.CREATOR);
        this.ids = new ArrayList<Integer>();
        in.readList(this.ids, Integer.class.getClassLoader());
    }

    public static final Creator<FileListResponseQuery> CREATOR = new Creator<FileListResponseQuery>() {
        @Override
        public FileListResponseQuery createFromParcel(Parcel source) {
            return new FileListResponseQuery(source);
        }

        @Override
        public FileListResponseQuery[] newArray(int size) {
            return new FileListResponseQuery[size];
        }
    };
}
