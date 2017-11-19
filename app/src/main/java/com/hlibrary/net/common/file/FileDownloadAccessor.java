package com.hlibrary.net.common.file;

import android.content.Context;

import com.hlibrary.net.listener.FileDownloadListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;


/**
 * 文件下载（以HttpUrlConnect方式来实现）
 */
public class FileDownloadAccessor extends BaseFileAccessor {

	private HttpURLConnection urlConnection = null;

	public FileDownloadAccessor(Context mCtx) {
		super(mCtx);
	}

	/**
	 * @param urlStr
	 *            下载地址
	 * @param path
	 *            保存路径
	 * @param downloadConnectTimeout
	 *            socket连接超时时间
	 * @param downloadReadTimeout
	 *            连接超时时间
	 * @param downloadListener
	 * @return
	 */
	public boolean doGetSaveFile(String urlStr, String path,
			int downloadConnectTimeout, int downloadReadTimeout,
			FileDownloadListener downloadListener) {

		File file = new File(path);
		if (file.exists())
			file.delete();

		BufferedOutputStream out = null;
		InputStream in = null;

		try {

			URL url = new URL(urlStr);
			urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(false);
			urlConnection.setConnectTimeout(downloadConnectTimeout);
			urlConnection.setReadTimeout(downloadReadTimeout);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection
					.setRequestProperty("Accept-Encoding", "gzip, deflate");

			urlConnection.connect();

			int status = urlConnection.getResponseCode();

			if (status != HttpURLConnection.HTTP_OK) {
				return false;
			}

			int bytetotal =  urlConnection.getContentLength();
			int bytesum = 0;
			int byteread = 0;
			out = new BufferedOutputStream(new FileOutputStream(file));

			InputStream is = urlConnection.getInputStream();
			String content_encode = urlConnection.getContentEncoding();
			if (null != content_encode && !"".equals(content_encode)
					&& content_encode.equals("gzip")) {
				is = new GZIPInputStream(is);
			}
			in = new BufferedInputStream(is);

			final Thread thread = Thread.currentThread();
			byte[] buffer = new byte[1444];
			while ((byteread = in.read(buffer)) != -1) {
				if (thread.isInterrupted()) {
					if (((float) bytesum / (float) bytetotal) < 0.8f) {
						file.delete();
						throw new InterruptedIOException();
					}
				}

				bytesum += byteread;
				out.write(buffer, 0, byteread);
				if (downloadListener != null && bytetotal > 0) {
					downloadListener.onProgress(1.0 * bytesum / bytetotal);
				}
			}
			if (downloadListener != null) {
				downloadListener.completed();
			}
			abort();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			if (downloadListener!=null)
				downloadListener.cancel();
		} finally {
			closeSilently(in);
			closeSilently(out);
			abort();
		}

		return false;
	}

	@Override
	public void abort() {
		if (urlConnection != null) {
			urlConnection.disconnect();
			urlConnection = null;
		}
	}

}
