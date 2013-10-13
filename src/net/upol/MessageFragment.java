package net.upol;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import net.basilwang.R;

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
	private Handler mHandler;
	SampleAdapter adapter;
	private String indexUrl;
	private static int moreCount = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		indexUrl = "http://xueli.upol.cn:9888/M4/upol/platform/zxgg/zxgg_01.jsp?pageInt=";
		messageView = inflater.inflate(R.layout.message_list, null);
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
		new GetNotificationTask().execute(indexUrl + getPageNum(),"true");
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
				adapter.clear();
				moreCount = 0;
				new GetNotificationTask().execute(indexUrl + getPageNum(),
						"true");
				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				moreCount++;
				new GetNotificationTask().execute(indexUrl + getPageNum(),
						"false");
				adapter.notifyDataSetChanged();
				onLoad();
			}
		}, 2000);
	}

	public int getPageNum() {
		return moreCount / 2 + 1;
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
			// convertView.setTag(0, getItem(position).url);

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(getActivity(), MessageActivity.class);
		i.putExtra("position", arg2);
		// i.putExtra("link", arg1.getTag(0).toString());
		startActivity(i);
	}

	private class GetNotificationTask extends
			AsyncTask<Object, Integer, String> {
		private String isRefresh;

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
			Log.v("isRefresh", (String)params[1]);
			isRefresh = (String) params[1];
			try {
				HttpResponse response = httpClient.execute(post);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					result = EntityUtils.toString(response.getEntity());
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
			for (int i = getStartIndex(); i < getStartIndex() + 8; i++) {
				Element a = trs.get(i).select("a").first();
				adapter.add(new SampleItem(a.text(),
						"http://xueli.upol.cn:9888/M4/upol/platform/zxgg/"
								+ a.attr("href"), getDate(trs.get(i).child(2)
								.text()), R.drawable.open));
			}
			if (isRefresh.equals("true"))
				mListView.setAdapter(adapter);

		}

		private String getDate(String str) {
			return str.replace("(", "").replace(")", "");
		}

		private int getStartIndex() {
			return moreCount % 2 * 8;
		}

	}

}
