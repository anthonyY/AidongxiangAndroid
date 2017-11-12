package com.aiitec.openapi.utils;

import android.util.Log;

public class LogUtil {

	public static final String DEFAULT_TAG = "AIDONGXIANG";
	public static boolean showLog = false;

	public static void v(String logText) {
		if (showLog) {
			Log.v(DEFAULT_TAG, String.valueOf(logText));
		}
	}

	public static void v(String TAG, String logText) {
		if (showLog) {
			Log.v(TAG,  logText);
		}
	}

	public static void d(String logText) {
		if (showLog) {
			Log.d(DEFAULT_TAG, String.valueOf(logText));
		}
	}

	public static void e(String logText) {
		if (showLog) {
			Log.e(DEFAULT_TAG, String.valueOf(logText));
		}
	}

	public static void d(String TAG, String logText) {
		if (showLog) {
			Log.d(TAG,  logText);
		}
	}

	public static void i(String logText) {
		if (showLog) {
			Log.i(DEFAULT_TAG,  logText);
		}
	}
	
	public static void i(String TAG, String logText) {
		if (showLog) {
			Log.i(TAG, logText);
		}
	}

	public static void w(String TAG, String logText) {
		if (showLog) {
			Log.w(TAG,  logText);
		}
	}

	public static void w(String logText) {
		if (showLog) {
			Log.w(DEFAULT_TAG, String.valueOf(logText));
		}
	}

	public static void e(String TAG, String logText) {
		if (showLog) {
			Log.e(TAG,  logText);
		}
	}

	public static void d(Class<?> c, String logText) {
		if (showLog) {
			Log.d(DEFAULT_TAG, "[" + c.getSimpleName() + "]" + logText);
		}
	}
	
	public static void d(Object c, String logText) {
		if (showLog) {
			Log.d(DEFAULT_TAG, "[" + c.getClass().getSimpleName() + "]" + logText);
		}
	}
}
