package com.hlibrary.net.common.file;

import android.content.Context;

import com.hlibrary.net.listener.FileUploaderProgressListener;
import com.hlibrary.net.listener.IFileUploadAccessor;
import com.hlibrary.net.model.FileRequest;
import com.hlibrary.net.model.Requests;
import com.hlibrary.net.model.Respond;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class FileUploadAccessor extends BaseFileAccessor implements
		IFileUploadAccessor {
	private HttpURLConnection urlConnection = null;

	public FileUploadAccessor(Context mCtx) {
		super(mCtx);
	}

	@Override
	public Respond doUploadFile(String urlStr, Requests params,
								int uploadConnectTimeout, int uploadReadTimeout) {

		byte[] barry = null;
		String BOUNDARYSTR = getBoundry();
		int contentLength = 0;
		String sendStr = "";
		String[] results = null;
		File targetFile = null;
		try {
			barry = ("--" + BOUNDARYSTR + "--\r\n").getBytes("UTF-8");
			results = params.getBoundaryMessage(BOUNDARYSTR);
			sendStr = results[0];
			targetFile = new File(results[1]);
			contentLength += sendStr.getBytes("UTF-8").length
					+ targetFile.length() + 2 * barry.length;
		} catch (UnsupportedEncodingException e) {
		}

		String lenstr = Integer.toString(contentLength);

		BufferedOutputStream out = null;
		FileInputStream fis = null;
		Respond respond = null;
		try {
			URL url = null;
			url = new URL(urlStr);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(uploadConnectTimeout);
			urlConnection.setReadTimeout(uploadReadTimeout);
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection.setRequestProperty("Content-type",
					"multipart/form-data;boundary=" + BOUNDARYSTR);
			urlConnection.setRequestProperty("Content-Length", lenstr);
			((HttpURLConnection) urlConnection)
					.setFixedLengthStreamingMode(contentLength);
			urlConnection.connect();

			out = new BufferedOutputStream(urlConnection.getOutputStream());

			int bytesRead;
			byte[] buffer;
			final int maxBufferSize = 1024;
			buffer = new byte[maxBufferSize];
			long transferred = 0;
			final Thread thread = Thread.currentThread();
			out.write(sendStr.getBytes("UTF-8"));
			fis = new FileInputStream(targetFile);
			FileRequest fileRequest = params.getFileRequest(0);
			while ((bytesRead = fis.read(buffer)) > -1) {
				if (thread.isInterrupted()) {
					throw new InterruptedIOException();
				}
				out.write(buffer, 0, bytesRead);
				transferred += bytesRead;
				if (transferred % 50 == 0) {
					out.flush();
				}
				if (fileRequest.getProgressListener() != null) {
					fileRequest.getProgressListener().onProgress(
							1.0 * transferred / targetFile.length());
				}
			}
			out.write(barry);
			out.write(barry);
			out.flush();
			out.close();
			if (fileRequest.getProgressListener() != null
					&& fileRequest.getProgressListener() instanceof FileUploaderProgressListener) {
				((FileUploaderProgressListener) fileRequest
						.getProgressListener()).waitServerResponse();
			}
			respond = handleResponse(urlConnection, false);
		} catch (IOException e) {
			e.printStackTrace();
			respond = new Respond(Respond.False, Respond.TimeOut);
		} finally {
			closeSilently(fis);
			closeSilently(out);
			abort();
		}
		return respond;
	}

	@Override
	public void abort() {
		if (urlConnection != null) {
			urlConnection.disconnect();
			urlConnection = null;
		}
	}

}
