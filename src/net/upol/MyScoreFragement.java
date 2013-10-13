package net.upol;

import net.basilwang.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyScoreFragement extends ListFragment {

	View myScoreView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		myScoreView = inflater.inflate(R.layout.my_score_list, null);
		initAdapter();
		return myScoreView;
	}

	private void initAdapter() {
		SampleAdapter adapter = new SampleAdapter(this.getActivity());
		for (int i = 0; i < 4; i++) {
			adapter.add(new SampleItem("计算机应用基础", "学分:4", "10春季", "正考：", "补考：",
					"重修："));
			setListAdapter(adapter);
		}
	}

	private class SampleItem {
		public String tag1;
		public String tag2;
		public String tag3;
		public String tag4;
		public String tag5;
		public String tag6;

		public SampleItem(String tag1, String tag2, String tag3, String tag4,
				String tag5, String tag6) {
			this.tag1 = tag1;
			this.tag2 = tag2;
			this.tag3 = tag3;
			this.tag4 = tag4;
			this.tag5 = tag5;
			this.tag6 = tag6;

		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.my_score_list_item, null);
			}
			TextView item1 = (TextView) convertView
					.findViewById(R.id.list_item_textview1);
			item1.setText(getItem(position).tag1);
			TextView item2 = (TextView) convertView
					.findViewById(R.id.list_item_textview2);
			item2.setText(getItem(position).tag2);
			TextView item3 = (TextView) convertView
					.findViewById(R.id.list_item_textview3);
			item3.setText(getItem(position).tag3);
			TextView item4 = (TextView) convertView
					.findViewById(R.id.list_item_textview4);
			item4.setText(getItem(position).tag4);
			TextView item5 = (TextView) convertView
					.findViewById(R.id.list_item_textview5);
			item5.setText(getItem(position).tag5);
			TextView item6 = (TextView) convertView
					.findViewById(R.id.list_item_textview6);
			item6.setText(getItem(position).tag6);

			return convertView;
		}

	}
}
