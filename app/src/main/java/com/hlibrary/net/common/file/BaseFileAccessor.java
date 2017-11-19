package com.hlibrary.net.common.file;

import android.content.Context;

import com.hlibrary.net.common.BaseHttpAccessor;

public abstract class BaseFileAccessor extends BaseHttpAccessor {

	public BaseFileAccessor(Context mCtx) {
		super(mCtx);
	}

	protected static String getBoundry() {
		StringBuffer _sb = new StringBuffer();
		for (int t = 1; t < 12; t++) {
			long time = System.currentTimeMillis() + t;
			if (time % 3 == 0) {
				_sb.append((char) time % 9);
			} else if (time % 3 == 1) {
				_sb.append((char) (65 + time % 26));
			} else {
				_sb.append((char) (97 + time % 26));
			}
		}
		return _sb.toString();
	}

}
