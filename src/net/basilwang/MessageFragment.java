package net.basilwang;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import view.XListView;
import view.XListView.IXListViewListener;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageFragment extends Fragment implements IXListViewListener {
	View messageView;
	private XListView mListView;
	private ArrayList<String> items = new ArrayList<String>();
	private Handler mHandler;
	private int start = 0;
	private static int refreshCnt = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		messageView = inflater.inflate(R.layout.message_list, null);
		geneItems();
		mListView = (XListView) messageView.findViewById(R.id.xListView);
		mListView.setPullLoadEnable(true);
		SampleAdapter adapter = new SampleAdapter(this.getActivity());
		for (int i = 0; i < 3; i++) {
			adapter.add(new SampleItem(
					"����2013��9������ͳ��������ǰ���֤��֤��֪ͨ", "2013-08-28",
					R.drawable.open));
		}
		mListView.setAdapter(adapter);
		mListView.setXListViewListener(this);
		mHandler = new Handler();
		return messageView;
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
				// mAdapter.notifyDataSetChanged();
				onLoad();
			}
		}, 2000);
	}

	private class SampleItem {
		public String message;
		public String date;
		int open;

		public SampleItem(String message, String date, int open) {
			this.message = message;
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

}
