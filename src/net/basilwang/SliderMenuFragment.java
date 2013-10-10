package net.basilwang;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SliderMenuFragment extends ListFragment{

	public static final int  EXIT_APPLICATION = 0x0001; 
	private SlidingMenu menu;
	private Handler handler;
	
	public SliderMenuFragment(SlidingMenu menu) {
		this.menu=menu;
	}
	protected static int messageNum=0;
	private int[] messages={0,R.drawable.message1,
			R.drawable.message2,R.drawable.message3,
			R.drawable.message4,R.drawable.message5,
			R.drawable.message6};
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(StaticAttachmentActivity.nuwMessages<6){
			messageNum=StaticAttachmentActivity.nuwMessages;
		}else{
			messageNum=6;
		}
		String[] menuNames = getResources().getStringArray(R.array.menu_name);
		SampleAdapter adapter = new SampleAdapter(getActivity());
		for (int i = 0; i < menuNames.length; i++) {
			adapter.add(new SampleItem(menuNames[i], getIconResc(i),getMessage_IconResc(i)));
			setListAdapter(adapter);
		}
	}

	private int getIconResc(int position) {
		int[] iconResc = {
				R.drawable.admin,
				R.drawable.curriculum, 
				R.drawable.mygrade,
				R.drawable.downloadc,
				R.drawable.set,
				R.drawable.message,
				R.drawable.shared,
				R.drawable.exit };
		return iconResc[position];

	}
	private int getMessage_IconResc(int position) {
		int[] iconResc = {
				0,
				0, 
				0,
				0,
				0,
				messages[messageNum],
				0,
				0};
		return iconResc[position];

	}

	private class SampleItem {
		public String tag;
		public int iconRes;
		public int messageRes;

		public SampleItem(String tag, int iconRes,int messageRes) {
			this.tag = tag;
			this.iconRes = iconRes;
			this.messageRes=messageRes;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView
					.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);
			ImageView message_icon = (ImageView) convertView
					.findViewById(R.id.new_message_icon);
			message_icon.setImageResource(getItem(position).messageRes);

			return convertView;
		}

	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		switch (position) {
	    case 1:
			newContent = new CurriculumViewPagerFragment();
			break;
		case 2:
			newContent = new ScoreFragment();
			break;
		case 3:
			newContent = new DownloadCurriculumFragment();
			break;
		case 4:newContent = new PreferenceFragmentPlugin();
		    break;
		case 5:newContent = new MessageFragment();
			break;
		case 6:
			break;
		case 7:exit();
			break;
		}
		if (newContent != null)
			switchFragment(newContent);
	}
	

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof StaticAttachmentActivity) {
			StaticAttachmentActivity fca = (StaticAttachmentActivity) getActivity();
			fca.switchContent(fragment);
		}
	}
	public void exit(){
		Intent mIntent = new Intent();  
        mIntent.setClass(this.getActivity(),StaticAttachmentActivity.class);  
        //这里设置flag还是比较重要�
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
        //发出退出程序指�
        mIntent.putExtra("flag", EXIT_APPLICATION);  
        startActivity(mIntent); 
	}



}
