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
import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.framework.WeiboActionListener;
import cn.sharesdk.onekeyshare.ShareAllGird;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SliderMenuFragment extends ListFragment implements Callback,
OnClickListener,WeiboActionListener {

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
			showGrid(false);
			break;
		case 7:exit();
			break;
		}
		if (newContent != null)
			switchFragment(newContent);
	}
	
	private void showGrid(boolean silent) {
		Intent i = new Intent(menu.getContext(), ShareAllGird.class);
		// 分享时Notification的图�
		i.putExtra("notif_icon", R.drawable.ic_launcher);
		// 分享时Notification的标�
		i.putExtra("notif_title", menu.getContext().getString(R.string.app_name));

		// address是接收人地址，仅在信息和邮件使用，否则可以不提供
		i.putExtra("address", "12345678901");
		// title标题，在印象笔记、邮箱、信息、微信（包括好友和朋友圈）、人人网和QQ空间使用，否则可以不提供
		i.putExtra("title", menu.getContext().getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用，否则可以不提供
		i.putExtra("titleUrl", "http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字�
		i.putExtra("text", menu.getContext().getString(R.string.share_content));
		// imagePath是本地的图片路径，除Linked-In外的所有平台都支持这个字段
		//i.putExtra("imagePath", MainActivity.TEST_IMAGE);
		// imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字�
		//i.putExtra("imageUrl", "http://img.appgo.cn/imgs/sharesdk/content/2013/06/13/1371120300254.jpg");
		// url仅在微信（包括好友和朋友圈）中使用，否则可以不提�
		i.putExtra("url", "http://sharesdk.cn");
		// thumbPath是缩略图的本地路径，仅在微信（包括好友和朋友圈）中使用，否则可以不提�
//		i.putExtra("thumbPath", MainActivity.TEST_IMAGE);
		// appPath是待分享应用程序的本地路劲，仅在微信（包括好友和朋友圈）中使用，否则可以不提�
		//i.putExtra("appPath", MainActivity.TEST_IMAGE);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
		i.putExtra("comment", menu.getContext().getString(R.string.share));
		// site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供
		i.putExtra("site", menu.getContext().getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供
		i.putExtra("siteUrl", "http://sharesdk.cn");

		// foursquare分享时的地方�
		i.putExtra("venueName", "Southeast in China");
		// foursquare分享时的地方描述
		i.putExtra("venueDescription", "This is a beautiful place!");
		// foursquare分享时的地方纬度
		i.putExtra("latitude", 36.644009419436394f);
		// foursquare分享时的地方经度
		i.putExtra("longitude", 117.0709615945816f);
		// 是否直接分享
		i.putExtra("silent", silent);
		// 设置自定义的外部回调
		i.putExtra("callback", OneKeyShareCallback.class.getName());
		menu.getContext().startActivity(i);
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
	
	public void onComplete(AbstractWeibo weibo, int action,
			HashMap<String, Object> res) {
		Message msg = new Message();
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	public void onCancel(AbstractWeibo weibo, int action) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	public void onError(AbstractWeibo weibo, int action, Throwable t) {
		t.printStackTrace();

		Message msg = new Message();
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	/** 处理操作结果 */
	public boolean handleMessage(Message msg) {
		AbstractWeibo weibo = (AbstractWeibo) msg.obj;
		String text = StaticAttachmentActivity.actionToString(msg.arg2);
		switch (msg.arg1) {
			case 1: { // 成功
				text = weibo.getName() + " completed at " + text;
			}
			break;
			case 2: { // 失败
				text = weibo.getName() + " caught error at " + text;
			}
			break;
			case 3: { // 取消
				text = weibo.getName() + " canceled at " + text;
			}
			break;
		}

		Toast.makeText(menu.getContext(), text, Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
