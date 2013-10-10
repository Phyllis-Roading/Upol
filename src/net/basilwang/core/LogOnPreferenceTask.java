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
	// private AsyncTask<Integer, Integer, String> mTask;
	String title;
	private int accountId;
	int lastMileStone = 0;
	private int mileStoneInterval = 0;

	public LogOnPreferenceTask(Context context) {
		mContext = context;
		// mTask = task;
	}

	@Override
	protected void onPreExecute() {

		title = ((Activity) mContext).getTitle().toString();
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		accountId = Integer.valueOf(params[3]);
		String url = "http://xueli.upol.cn:9888/M4/upol/platform/login.jsp";
		HttpClient httpClicent = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("login_id", params[0]));
		postParameters.add(new BasicNameValuePair("input_password", params[1]));
		postParameters.add(new BasicNameValuePair("type", "STUDENT"));
		postParameters.add(new BasicNameValuePair("rand", params[2]));
		try {
			post.setEntity(new UrlEncodedFormEntity(postParameters));
			HttpResponse httpResponse = httpClicent.execute(post);
			Log.v("status", httpResponse.getStatusLine().getStatusCode() + "");
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				if (result.contains("验证码错误或已过期！"))
					return false;
				Log.v("result", result);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.v("error", e.toString());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.v("error1", e.toString());
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
			// mTask.execute(accountId);
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
