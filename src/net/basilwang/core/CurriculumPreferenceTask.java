package net.basilwang.core;

import java.io.IOException;

import net.basilwang.CheckCodeDialog;
import net.basilwang.dao.CurriculumService;
import net.basilwang.entity.Curriculum;
import net.basilwang.enums.TAHelperDownloadPhrase;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class CurriculumPreferenceTask extends
		AsyncTask<Object, Integer, String> {
	static final String TAG = "CurriculumPreferenceTask";
	private Context mContext;
	private CurriculumService curriculumService;
	String title;
//	private int accountId;
	private String semesterIndex;
	private String semesterYear;
	private String semesterValue;
	int lastMileStone = 15;
	private int mileStoneInterval = 0;
	private HttpClient mHttpClient;

	public CurriculumPreferenceTask(Context context,HttpClient httpClient) {
		curriculumService = new CurriculumService(context);
		mContext = context;
		mHttpClient=httpClient;
		semesterValue = PreferenceManager.getDefaultSharedPreferences(mContext)
				.getString("curriculum_semester_name", "");
	}

	@Override
	protected void onPreExecute() {
		// ((Activity)mContext).setProgressBarVisibility(true);
		title = ((Activity) mContext).getTitle().toString();
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(Object... params) {
		String result="";
		String url = "http://xueli.upol.cn/M4/entity/student/xspt.jsp";
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse httpResponse = mHttpClient.execute(get);
			Log.v("status", httpResponse.getStatusLine().getStatusCode() + "");
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

	}

	private String HttpClient(Object... params) {
		OnDownloadProgressListener listener = new OnDownloadProgressListener() {

			@Override
			public void onDownloadProgress(int percent, int downloadedData,
					int total, TAHelperDownloadPhrase phrase) {

				publishProgress(percent, downloadedData, total,
						phrase.getValue());
			}

		};
		String[] cemesterSettings = semesterValue.split("\\|");
		semesterYear = cemesterSettings[0];
		semesterIndex = cemesterSettings[1];
		return TAHelper.Instance().getCurriculumBySemesterIndex(semesterYear,
				semesterIndex, listener);
	}

	protected void onProgressUpdate(Integer... progress) {
		Log.i(TAG, "lastMileStone is " + String.valueOf(lastMileStone));
		if (progress[3] > lastMileStone) {
			mileStoneInterval = progress[3] - lastMileStone;
			Log.i(TAG, String.valueOf(mileStoneInterval));
		}
		int progressInterval = (int) (progress[0] * mileStoneInterval / 100);
		((CheckCodeDialog) mContext).getProgressBar().setProgress(
				lastMileStone + progressInterval);
		if (progress[0] == 100) {
			lastMileStone = progress[3];
			Log.i(TAG,
					"percent is 100 and lastMileStone is "
							+ String.valueOf(lastMileStone));
		}
	}

	protected void onPostExecute(String curriculumString) {
		Curriculum[] s = TAHelper.Instance().getCurriculums(curriculumString);
		if (s.length == 1) {
			Toast.makeText(
					mContext,
					"由以下原因可能造成无法查询课表\n" + "1.教务系统无法访问\n" + "2.该学期课程表为空\n"
							+ "3.没有进行教务评价", Toast.LENGTH_LONG).show();
			((Activity) mContext).finish();
			return;
		}

//		curriculumService.delete(accountId, semesterValue);
		for (int i = 1; i < s.length; i++) {
//			s[i].setMyid(accountId);
			s[i].setSemestername(semesterValue);
			curriculumService.save(s[i]);
		}

		((CheckCodeDialog) mContext).finish();
		((CheckCodeDialog) mContext).getProgressBar().dismiss();
	}

}
