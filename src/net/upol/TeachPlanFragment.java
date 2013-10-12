package net.upol;

import net.basilwang.CheckCodeDialog;
import net.basilwang.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class TeachPlanFragment extends SherlockFragment {

	View planView;
	private SampleAdapter adapter;
	private ListView mlistView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		planView = inflater.inflate(R.layout.teach_plan_list, null);
		mlistView=(ListView)planView.findViewById(android.R.id.list);
		initAdapter();
		return planView;
	}

	private void initAdapter() {
		adapter = new SampleAdapter(this.getActivity());
		for (int i = 0; i < 4; i++) {
			adapter.add(new SampleItem("(030103)有机化学", "专业基础课", "学分:4",
					"学时:11", "学期:1", "未选", "通过"));
			
		}
		mlistView.setAdapter(adapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent(getActivity(), CheckCodeDialog.class);
		i.putExtra("task", "curriculum");
		startActivityForResult(i, 0x1);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		SubMenu sub = menu.addSubMenu("下载设置");
		sub.setIcon(R.drawable.btn_download_setting);
		sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		super.onCreateOptionsMenu(menu, inflater);
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
