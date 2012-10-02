package de.higger.examtrainer.tool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import de.higger.examtrainer.Constants;
import de.higger.examtrainer.R;

public class ImageDownloader {
	private final String LOG_TAG = Constants.LOG_TAG_PRE
			+ getClass().getSimpleName();

	private final String url;

	public ImageDownloader(Context context) {
		url = context.getString(R.string.constants_uri_ws_prefix)
				+ "show_image.php?id_question=";
	}

	public void saveImage(int imageId, File target) {
		final AndroidHttpClient client = AndroidHttpClient
				.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url + imageId);
		Log.v(LOG_TAG, getRequest.getURI().toString());
		
		try {

			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();

					BufferedInputStream bis = new BufferedInputStream(
							inputStream);

					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					int current = 0;
					while ((current = bis.read()) != -1) {
						baf.append((byte) current);
					}

					FileOutputStream fos = new FileOutputStream(target);
					fos.write(baf.toByteArray());
					fos.close();
					
					target.setReadable(true, false);

				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}

		} catch (Exception e) {
			Log.w(LOG_TAG, "Error while retrieving image from " + url, e);
		} finally {
			client.close();
		}

		Log.v("IMAGE_DOWNLOADER", "download image");
	}
}
