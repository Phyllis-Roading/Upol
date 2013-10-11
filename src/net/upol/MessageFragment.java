package net.upol;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.basilwang.R;
import view.XListView;
import view.XListView.IXListViewListener;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageFragment extends Fragment implements IXListViewListener,
		OnItemClickListener {
	View messageView;
	private XListView mListView;
	private ArrayList<String> items = new ArrayList<String>();
	private Handler mHandler;
	private int start = 0;
	private static int refreshCnt = 0;
	SampleAdapter adapter;
	GetNotificationTask getNotifTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		messageView = inflater.inflate(R.layout.message_list, null);
		geneItems();
		mListView = (XListView) messageView.findViewById(R.id.xListView);
		mListView.setPullLoadEnable(true);
		mListView.setOnItemClickListener(this);
		initAdapter();
		mListView.setXListViewListener(this);
		mHandler = new Handler();
		return messageView;
	}

	private void initAdapter() {
		adapter = new SampleAdapter(this.getActivity());
		getNotifTask = new GetNotificationTask();
		getNotifTask
				.execute("http://xueli.upol.cn:9888/M4/upol/platform/zxgg/zxgg_01.jsp");
	}

	private void geneItems() {
		for (int i = 0; i != 5; ++i) {
			items.add("refresh cnt " + (++start));
		}
	}

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd   HH:mm");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		mListView.setRefreshTime(str);
	}

	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				start = ++refreshCnt;
				items.clear();
				geneItems();
				adapter.clear();
				for (int i = 0; i < 1; i++) {
					adapter.add(new SampleItem(
							"关于2013年9月网络统考考生提前身份证验证的通知",
							"http://xueli.upol.cn:9888/M4/upol/platform/zxgg/zxgg_01.jsp",
							"2013-08-29", R.drawable.open));
				}
				mListView.setAdapter(adapter);
				// mAdapter.notifyDataSetChanged();
				// mAdapter = new ArrayAdapter<String>(XListViewActivity.this,
				// R.layout.list_item, items);
				// mListView.setAdapter(mAdapter);
				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				geneItems();
				adapter.notifyDataSetChanged();
				for (int i = 0; i < 1; i++) {
					adapter.add(new SampleItem(
							"关于2013年9月网络统考考生提前身份证验证的通知",
							"http://xueli.upol.cn:9888/M4/upol/platform/zxgg/zxgg_01.jsp",
							"2013-08-29", R.drawable.open));
				}
				mListView.setAdapter(adapter);
				onLoad();
			}
		}, 2000);
	}

	private class SampleItem {
		public String message;
		public String date;
		public String url;
		int open;

		public SampleItem(String message, String url, String date, int open) {
			this.message = message;
			this.url = url;
			this.date = date;
			this.open = open;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.message_list_item, null);
			}
			TextView message = (TextView) convertView
					.findViewById(R.id.message);
			message.setText(getItem(position).message);
			TextView date = (TextView) convertView.findViewById(R.id.date);
			date.setText(getItem(position).date);
			ImageView open = (ImageView) convertView.findViewById(R.id.open);
			open.setBackgroundResource(getItem(position).open);

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(getActivity(), MessageActivity.class);
		i.putExtra("position", arg2);
		startActivity(i);
	}

	private class GetNotificationTask extends
			AsyncTask<Object, Integer, String> {

		@Override
		protected void onPostExecute(String result) {
			if (result.contains("登陆超时,请重新登陆！")) {
				Toast.makeText(getActivity(), "请检查学号、密码和验证码",
						Toast.LENGTH_SHORT).show();
			} else {
				parseWithJsoup(result);
			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Object... params) {
			String result = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost((String) params[0]);
			try {
				HttpResponse response = httpClient.execute(post);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					result = EntityUtils.toString(response.getEntity());
					// Log.v("tag", result);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.v("error1", e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				Log.v("error2", e.toString());
			}
			return result;

		}

		private void parseWithJsoup(String HTML) {
			Document doc = Jsoup.parse(HTML);
			Elements trs = doc.select("tr[valign=middle]");
			for (int i = 0; i < 6; i++) {
				Element a = trs.get(i).select("a").first();
				adapter.add(new SampleItem(a.text(),
						"http://xueli.upol.cn:9888/M4/upol/platform/zxgg/"
								+ a.attr("href"), getDate(trs.get(i).child(2)
								.text()), R.drawable.open));
			}
			mListView.setAdapter(adapter);
		}

		private String getDate(String str) {
			return str.replace("(", "").replace(")", "");
		}

	}

}
