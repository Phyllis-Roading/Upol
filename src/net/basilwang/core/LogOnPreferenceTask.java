package net.basilwang.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import net.basilwang.CheckCodeDialog;
import net.basilwang.R;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LogOnPreferenceTask extends AsyncTask<String, Integer, Boolean> {
	static final String TAG = "LogOnPreferenceTask";
	private Context mContext;
	private AsyncTask<Object, Integer, String> mTask;
	String title;
	int lastMileStone = 0;
	private int mileStoneInterval = 0;
	private HttpClient mHttpClient;

	public LogOnPreferenceTask(Context context,
			AsyncTask<Object, Integer, String> task, HttpClient httpclient) {
		mContext = context;
		mTask = task;
		this.mHttpClient = httpclient;
	}

	@Override
	protected void onPreExecute() {

		title = ((Activity) mContext).getTitle().toString();
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String url = "http://xueli.upol.cn/M4/upol/platform/login.jsp";
		HttpPost post = new HttpPost(url);
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("login_id", params[0]
				.toString()));
		postParameters.add(new BasicNameValuePair("input_password", params[1]
				.toString()));
		postParameters.add(new BasicNameValuePair("type", "STUDENT"));
		postParameters
				.add(new BasicNameValuePair("rand", params[2].toString()));
		try {
			post.setEntity(new UrlEncodedFormEntity(postParameters));
			HttpResponse httpResponse = mHttpClient.execute(post);
			Log.v("status", httpResponse.getStatusLine().getStatusCode() + "");
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				if (result.contains("验证码错误或已过期！"))
					return false;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	// private Boolean HttpClient(String... params) {
	// OnDownloadProgressListener listener = new OnDownloadProgressListener() {
	//
	// @Override
	// public void onDownloadProgress(int percent, int downloadedData,
	// int total,TAHelperDownloadPhrase phrase) {
	//
	// publishProgress(percent, downloadedData, total,phrase.getValue());
	// }
	//
	// };
	// return TAHelper.Instance().logOn(params[0], params[1], params[2],
	// listener);
	// }

	protected void onProgressUpdate(Integer... progress) {

		if (progress[3] > lastMileStone) {
			mileStoneInterval = progress[3] - lastMileStone;
		}
		int progressInterval = (int) (progress[0] * mileStoneInterval / 100);
		((CheckCodeDialog) mContext).getProgressBar().setProgress(
				lastMileStone + progressInterval);
		if (progress[0] == 100) {
			lastMileStone += mileStoneInterval;
		}
		int lp = progress[1] / 1000;
		int rp = progress[2] / 1000;
	}

	protected void onPostExecute(Boolean isLogOn) {
		if (isLogOn) {
			mTask.execute();
		} else {
			((Activity) mContext).finish();
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.errorpasswordmaybe), Toast.LENGTH_LONG)
					.show();

		}
	}
}
