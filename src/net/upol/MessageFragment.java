package net.upol;

import java.sql.Date;
import java.text.SimpleDateFormat;
import net.basilwang.R;
import view.XListView;
import view.XListView.IXListViewListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageFragment extends Fragment implements IXListViewListener,
		OnItemClickListener {
	View messageView;
	private XListView mListView;
	private Handler mHandler;
	SampleAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		for (int i = 0; i < 3; i++) {
			adapter.add(new SampleItem("关于2013年9月网络统考考生提前身份证验证的通知",
					"2013-08-28", R.drawable.open));
		}
		mListView.setAdapter(adapter);

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
				for (int i = 0; i < 1; i++) {
					adapter.add(new SampleItem("关于2013年9月网络统考考生提前身份证验证的通知",
							"2013-08-29", R.drawable.open));
				}
				mListView.setAdapter(adapter);
				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				for (int i = 0; i < 1; i++) {
					adapter.add(new SampleItem("关于2013年9月网络统考考生提前身份证验证的通知",
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(getActivity(), MessageActivity.class);
		i.putExtra("position", arg2);
		startActivity(i);
	}

}
