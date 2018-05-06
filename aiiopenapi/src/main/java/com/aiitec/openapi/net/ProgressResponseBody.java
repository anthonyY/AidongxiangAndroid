package com.aiitec.openapi.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

    public interface ProgressListener {
        void onPreExecute(long contentLength);
        void update(long totalBytes, long currnet, int progress);
        void onSuccess(File file);
        void onStart();
        void onFailure();
    }

    private static final int PRE_EXECUTE = 0;
    private static final int UPDATE = 1;

    private final ResponseBody responseBody;
    private final ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        handler.sendEmptyMessage(PRE_EXECUTE);

    }

    // @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    // @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    // @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long current = 0L;

            // @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                current += bytesRead != -1 ? bytesRead : 0;
                if (null != progressListener) {
                    if (bytesRead != -1) {
                        long total = contentLength();
                    	Message message = handler.obtainMessage(UPDATE);
                        Bundle bundle = new Bundle();
                        bundle.putLong("total", total);
                        bundle.putLong("current", current);
                        message.setData(bundle);
                    	handler.sendMessage(message);
                    }
                }
                return bytesRead;
            }
        };
    }
    private Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what){
			case UPDATE:
			    Bundle bundle = msg.getData();
			    if(bundle != null){
                    long total = bundle.getLong("total", 0);
                    long current = bundle.getLong("current", 0);
                    int progress = 0;
                    if(total > 0){
                        progress = (int) (current*100/total);
                    }
                    if (progressListener != null) {
                        progressListener.update(total, current, progress);
                    }
                }


				break;
			case PRE_EXECUTE:
				if (progressListener != null) {
		            progressListener.onPreExecute(contentLength());
		        }
				break;
			}
			return false;
		}
	});


}