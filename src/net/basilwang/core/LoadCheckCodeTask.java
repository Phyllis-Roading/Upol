package net.basilwang.core;

import java.io.IOException;

import net.basilwang.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class LoadCheckCodeTask extends AsyncTask<Object, Integer, Bitmap> {
	private Context mContext;
	private HttpClient mHttpClient;

	public LoadCheckCodeTask(Context context, HttpClient httpclient) {
		mContext = context;
		mHttpClient=httpclient;
	}

	@Override
	protected Bitmap doInBackground(Object... params) {
		Bitmap result = null;
		String url = "http://xueli.upol.cn/M4/upol/platform/image.jsp";
		HttpPost post = new HttpPost(url);
		try {
			HttpResponse response = mHttpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				byte[] data = EntityUtils.toByteArray(response.getEntity());
				result = BitmapFactory.decodeByteArray(data, 0, data.length);
				Log.v("height", result.getHeight() + "");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	protected void onProgressUpdate(Integer... progress) {
	}

	protected void onPostExecute(Bitmap bitmapCheckCode) {
		ImageView mImageCheckCode = (ImageView) (((Activity) mContext)
				.findViewById(R.id.score_checkcode_image));
		if (mImageCheckCode != null) {
			if (bitmapCheckCode != null)
				mImageCheckCode.setImageBitmap(bitmapCheckCode);
			else {
				// 2012-07-09 basilwang sometimes we can't see verify code
				Toast.makeText(mContext, "教务系统无法登录，请稍后再试", Toast.LENGTH_SHORT)
						.show();

			}
		}
	}

}
