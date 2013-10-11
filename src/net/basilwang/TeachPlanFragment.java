package net.basilwang;

import view.XListView;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TeachPlanFragment extends Fragment {

	View planView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		planView = inflater.inflate(R.layout.message_list, null);
		XListView planList = (XListView) planView.findViewById(R.id.xListView);
		SampleAdapter adapter = new SampleAdapter(this.getActivity());
		for (int i = 0; i < 4; i++) {
			adapter.add(new SampleItem("(030103)有机化学", "专业基础课", "学分:4",
					"学时:11", "学期:1", "未选", "通过"));
		}
		planList.setAdapter(adapter);
		return planView;
	}

	private class SampleItem {
		public String tag1;
		public String tag2;
		public String tag3;
		public String tag4;
		public String tag5;
		public String tag6;
		public String tag7;

		public SampleItem(String tag1, String tag2, String tag3, String tag4,
				String tag5, String tag6, String tag7) {
			this.tag1 = tag1;
			this.tag2 = tag2;
			this.tag3 = tag3;
			this.tag4 = tag4;
			this.tag5 = tag5;
			this.tag6 = tag6;
			this.tag7 = tag7;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.teach_plan_list_item, null);
			}
			TextView title1 = (TextView) convertView
					.findViewById(R.id.list_item_textview1);
			title1.setText(getItem(position).tag1);
			TextView title2 = (TextView) convertView
					.findViewById(R.id.list_item_textview2);
			title2.setText(getItem(position).tag2);
			TextView title3 = (TextView) convertView
					.findViewById(R.id.list_item_textview3);
			title3.setText(getItem(position).tag3);
			TextView title4 = (TextView) convertView
					.findViewById(R.id.list_item_textview4);
			title4.setText(getItem(position).tag4);
			TextView title5 = (TextView) convertView
					.findViewById(R.id.list_item_textview5);
			title5.setText(getItem(position).tag5);
			TextView title6 = (TextView) convertView
					.findViewById(R.id.list_item_textview6);
			title6.setText(getItem(position).tag6);
			TextView title7 = (TextView) convertView
					.findViewById(R.id.list_item_textview7);
			title7.setText(getItem(position).tag7);

			return convertView;
		}

	}

}
