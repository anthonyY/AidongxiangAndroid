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

    public FileListResponseQuery() {
    }

}
