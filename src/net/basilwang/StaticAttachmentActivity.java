/*
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.basilwang;

import static net.basilwang.dao.Preferences.ABOUT_US;
import static net.basilwang.dao.Preferences.CLOSE_AD;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_PREFERENCES;
import static net.basilwang.dao.Preferences.LOGON_ADD_PREFERENCES;
import static net.basilwang.dao.Preferences.LOGON_PREFERENCES;
import static net.basilwang.dao.Preferences.RU_GUO_ZHAI;
import static net.basilwang.dao.Preferences.SHAREONWEIBO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import net.basilwang.PreferenceFragmentPlugin.OnPreferenceAttachedListener;
import net.basilwang.config.SAXParse;
import net.basilwang.dao.AccountService;
import net.basilwang.dao.Preferences;
import net.basilwang.dao.SemesterService;
import net.basilwang.entity.Account;
import net.basilwang.entity.Semester;
import net.basilwang.sever.Message;
import net.basilwang.sever.MessageService;
import net.basilwang.sever.RequestMessage;
import net.basilwang.utils.NetworkUtils;
import net.youmi.android.appoffers.CheckStatusNotifier;
import net.youmi.android.appoffers.YoumiOffersManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import cn.sharesdk.framework.AbstractWeibo;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.keep.AccessTokenKeeper;
import com.weibo.sdk.android.sso.SsoHandler;

public class StaticAttachmentActivity extends BaseActivity implements
		OnPreferenceAttachedListener, OnPreferenceChangeListener,
		OnPreferenceClickListener, CheckStatusNotifier {
	public StaticAttachmentActivity() {
		super(R.string.changing_fragments);
		// TODO Auto-generated constructor stub
	}

	// private String[] viewtypes;//is not used after add slidingMenu

	static final String TAG = "StaticAttachmentActivity";
	SubMenu subMenuForNetwork;
	// SubMenu subMenuWithoutNetwork;
	private Class dayorweekClass = CurriculumViewPagerFragment.class;
	private int accountId;
	private AccountService accountService;
	private Boolean isExiting=false;
	// 微博实例
	private Weibo mWeibo;
	private static final String CONSUMER_KEY = "3430810380";// 替换为开发者的appkey，例�1646212860";
	private static final String REDIRECT_URL = "http://www.baidu.com";// =
																		// "http://www.sina.com";
	public static Oauth2AccessToken accessToken;
	public static final String SINATAG = "sinasdk";
	/**
	 * SsoHandler 仅当sdk支持sso时有效，
	 */
	SsoHandler mSsoHandler;
	private Fragment mContent;
	private SlidingMenu menu;
	protected static int nuwMessages;
	private String newSemesterBegin;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// setTheme(SampleList.THEME); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		requestMessages();//获取服务器端最新消�
menu=new SlidingMenu(this);
		// YoumiToolkit.init(this, "2fc95b356bb979ae", "8b94f727980f7158");
		// //2012-10-09 basilwang pull version to compare
		// final TKMap versionMap = new TKMap("version");
		// versionMap.put("versionid",0);
		// versionMap.pullInBackground(new PullCallback() {
		//
		// @Override
		// public void done(TKMap map, TKException e) {
		// if (e==null) {
		// String versionid=versionMap.getString("versionid");
		// Toast.makeText(StaticAttachmentActivity.this,
		// "更新数据成功！versionid"+versionid, Toast.LENGTH_LONG).show();
		// }else {
		// Toast.makeText(StaticAttachmentActivity.this, "更新数据失败�,
		// Toast.LENGTH_LONG).show();
		// }
		// }
		// });
		setTitle(SAXParse.getTAConfiguration().getSelectedCollege().getName());
		getSherlock().setContentView(R.layout.main_container);

		/*
		 * Most interactions with what would otherwise be the system UI should
		 * now be done through this instance. Content, title, action bar, and
		 * menu inflation can all be done.
		 * 
		 * All of the base activities use this class to provide the normal
		 * action bar functionality so everything that they can do is possible
		 * using this static attacFragmentStatePagerSupporthment method.
		 * 
		 * Calling something like setContentView or getActionBar on this
		 * instance is required in order to properly set up the wrapped layout
		 * and dispatch menu events (if they are needed).
		 */

		// 微博实例的获�
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		

		// getSherlock().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

		// ((TextView)findViewById(R.id.text)).setText(R.string.static_attach_content);

		Context context = getSupportActionBar().getThemedContext();

		/*// is not used after adding slidingMenu
		 * viewtypes = getResources().getStringArray(R.array.viewtype);
		 * 
		 * ArrayAdapter<CharSequence> viewTypeList = ArrayAdapter
		 * .createFromResource(context, R.array.viewtype,
		 * R.layout.sherlock_spinner_item); viewTypeList
		 * .setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		 */

		// getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// getSupportActionBar().setListNavigationCallbacks(viewTypeList, this);
		// getSupportActionBar().setDisplayShowHomeEnabled(false);
		// getSupportActionBar().setDisplayShowTitleEnabled(false);
		accountService = new AccountService(this);
		refreshTabBarTitle();
		
		// add slidingMenu
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new CurriculumViewPagerFragment();

		// set the Above View
		setContentView(R.layout.main_container);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.mainContainer, mContent).commit();

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SliderMenuFragment(menu)).commit();

		// customize the SlidingMenu
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// add slidingmenu over

		
		/* // is not used after adding slidingMenu
		 * getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS
		 * ); int i = 0; for (String viewtype : viewtypes) { ActionBar.Tab tab =
		 * getSupportActionBar().newTab(); tab.setText(viewtype);
		 * tab.setTabListener(this); 2012-06-06 basilwang known bug. action bar
		 * must be have a callback on 2.3.3 switch (i) { case 0:
		 * tab.setTabListener(new TabListener(this, "day",
		 * CurriculumViewPagerFragment.class)); break; // case 1: //
		 * tab.setTabListener(new TabListener(this, "week", //
		 * WeekViewFragment.class)); // break; case 1: tab.setTabListener(new
		 * TabListener(this, "score", ScoreFragment.class)); break; case 2:
		 * tab.setTabListener(new TabListener(this, "download",
		 * DownloadCurriculumFragment.class)); break; case 3:
		 * tab.setTabListener(new TabListener(this, "settings",
		 * PreferenceFragmentPlugin.class)); break;
		 * 
		 * } i++; ActionBar.Tab tab = getSupportActionBar().newTab();
		 * tab.setTabListener(new TabListener(this, "day",
		 * CurriculumViewPagerFragment.class));
		 * getSupportActionBar().addTab(tab); }
		 */

		YoumiOffersManager
				.init(context, "2fc95b356bb979ae", "8b94f727980f7158");
		
		AbstractWeibo.initSDK(this);
		int flag = getIntent().getIntExtra("flag", 0);
		if (flag == SliderMenuFragment.EXIT_APPLICATION) {
			isExiting = true;
			finish();
		} else {
			isExiting = false;
		}
		
		newUserOrNot();

	}

	//联网登录时获取最新推送消息，插入数据�
	private void requestMessages() {
		// TODO Auto-generated method stub
		MessageService messageService=new MessageService(this);
		try{
			AccountService service=new AccountService(this);
			Account account=service.getAccounts().get(0);
			Log.v("1231321313213213text", account.getUserno());
			RequestMessage request=new RequestMessage();
			request.setResult(account.getUserno());
			Message message=new Message();
			message.setContent(request.getResult());
			nuwMessages=message.getContent().size();
			messageService.save(message);
			//判断本条消息是否修改开学日�
			for(String m:message.getContent()){
				if(m.substring(10, 26).equals("系统已自动将您的开学日期设置为：")){
					newSemesterBegin=m;
					updateSemesterBegin();
				}
			}
		}catch(Exception e){
			
		}
	}
	
	//根据服务器端的信息修改开学日�
	private void updateSemesterBegin() {
		// TODO Auto-generated method stub
		Calendar cal=Calendar.getInstance();
		String sYear=newSemesterBegin.substring(26, 30);
		String sMonth=newSemesterBegin.substring(31, 33);
		String sDay=newSemesterBegin.substring(34, 36);
		int iYear=Integer.parseInt(sYear);
		int iMonth=Integer.parseInt(sMonth)-1;
		int iDay=Integer.parseInt(sDay);
		cal.set(iYear, iMonth, iDay);
		Semester semester=new Semester();
		semester.setBeginDate(cal.getTime().getTime());
		SemesterService service=new SemesterService(this);
		service.updateBeginDataOfSemester(semester);
	}

		


	/**
	 * If this is a new user,please add account
	 */
	private void newUserOrNot() {
		if (accountService.getAccounts().size() == 0 && !isExiting) {
			Intent intent = new Intent(this, LogonPreferenceActivity.class);
			startActivity(intent);
		}
	}

	/*
	 * In order to receive these events you need to implement an interface from
	 * ActionBarSherlock so it knows to dispatch to this callback. There are
	 * three possible interface you can implement, one for each menu event.
	 * 
	 * Remember, there are no superclass implementations of these methods so you
	 * must return a value with meaning.
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		// boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

		// menu.add("设置")
		// // .setIcon(R.drawable.ic_compose)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		// menu.add("Search")
		// .setIcon( R.drawable.ic_search)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER |
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		// menu.add("Refresh")
		// .setIcon( R.drawable.ic_refresh)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		//
		// 2012-11-15 basil 去掉联网�
		// subMenuForNetwork = menu.addSubMenu(R.string.networkavailable);
		// subMenuForNetwork.add(0, R.style.Theme_Sherlock, 0,
		// R.string.checknetwork);
		// subMenuForNetwork.getItem().setShowAsAction(
		// MenuItem.SHOW_AS_ACTION_ALWAYS
		// | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		// subMenuForNetwork.getItem().setVisible(true);

		// subMenuWithoutNetwork = menu.addSubMenu(R.string.nonetwork);
		// subMenuWithoutNetwork.add(0, R.style.Theme_Sherlock, 0,
		// R.string.checknetwork);
		// subMenuWithoutNetwork.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
		// | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		// subMenuWithoutNetwork.getItem().setVisible(false);
		checkNetwork();

		return true;
	}

	@Override
	protected void onResume() {
		checkNetwork();
		super.onResume();
	}

	private void checkNetwork() {
		if (NetworkUtils.isConnect(this)) {
			if (subMenuForNetwork != null)
				subMenuForNetwork.getItem().setTitle(R.string.networkavailable);
			// subMenuWithoutNetwork.getItem().setVisible(false);
		} else {
			if (subMenuForNetwork != null)
				subMenuForNetwork.getItem().setTitle(R.string.nonetwork);
		}
	}

	// @曹洪�自己改写的，用于返回是否联网
	private boolean isNetAvailable() {
		return NetworkUtils.isConnect(this) ? true : false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// If this callback does not handle the item click,
		// onPerformDefaultAction
		// of the ActionProvider is invoked. Hence, the provider encapsulates
		// the
		// complete functionality of the menu item.
		// if (item.getTitle() == "设置") {
		// // if(NetworkUtils.isConnect(this))
		// // {
		// Intent intent = new Intent();
		// intent.setClass(this, MyPreferenceActivity.class);
		// startActivity(intent);
		// // }
		// // else
		// // {
		// // Toast.makeText(StaticAttachmentActivity.this, "请检查网�,
		// // Toast.LENGTH_SHORT).show();
		// // }
		//
		// }
		if (item.getTitle().equals("周/天")) {
			Toast.makeText(this, "周视图暂时关闭，下个版本炫丽呈现", Toast.LENGTH_SHORT).show();
			// 2012-12-5 basilwang there seems a bug if we change other
			// tablistenser in one tab
			// which will create a lot of item option , SCARED!!.
			// So we use another way. delete and add tab
//			if (dayorweekClass == CurriculumViewPagerFragment.class) {
//
//				// if(dayorweekClass==CurriculumViewPagerFragment.class)
//				// {
//				// getSupportActionBar().getSelectedTab().setTabListener(
//				// new TabListener(this, "curriculum",
//				// WeekViewFragment.class));
//				// getSupportActionBar().selectTab(
//				// getSupportActionBar().getSelectedTab());
//				// dayorweekClass = WeekViewFragment.class;
//				// }
//				// else if(dayorweekClass==WeekViewFragment.class)
//				// {
//				// getSupportActionBar().getSelectedTab().setTabListener(
//				// new TabListener(this, "curriculum",
//				// CurriculumViewPagerFragment.class));
//				// getSupportActionBar().selectTab(
//				// getSupportActionBar().getSelectedTab());
//				// dayorweekClass = CurriculumViewPagerFragment.class;
//				// }
//
//				/*
//				 * ActionBar.Tab tab = getSupportActionBar().newTab();
//				 * tab.setText("课表"); tab.setTabListener(new TabListener(this,
//				 * "week", WeekViewFragment.class));
//				 * getSupportActionBar().addTab(tab, 0, true);
//				 * getSupportActionBar().removeTabAt(1);
//				 */
//				switchContent(new WeekViewFragment());
//				dayorweekClass = WeekViewFragment.class;
//			} else if (dayorweekClass == WeekViewFragment.class) {
//
//				/*
//				 * ActionBar.Tab tab = getSupportActionBar().newTab();
//				 * tab.setText("课表"); tab.setTabListener(new TabListener(this,
//				 * "day", CurriculumViewPagerFragment.class));
//				 * getSupportActionBar().addTab(tab, 0, true);
//				 * getSupportActionBar().removeTabAt(1);
//				 */
//				switchContent(new CurriculumViewPagerFragment());
//				dayorweekClass = CurriculumViewPagerFragment.class;
//
//			}
		}
		if (item.getTitle() == getResources().getString(R.string.checknetwork)) {
			checkNetwork();

		}
		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	//
	//
	// }
	//
	//
	// @Override
	// public void onTabSelected(Tab tab, FragmentTransaction arg1) {
	//
	// // Intent intent=new Intent();
	// //
	// if(tab.getText()==viewtypes[0]&&this.getClass()!=CurriculumViewPagerActivity.class)
	// // {
	// // intent.setClass(this, CurriculumViewPagerActivity.class);
	// // startActivity(intent);
	// // }
	// //
	// if(tab.getText()==viewtypes[1]&&this.getClass()!=CurriculumActivity.class)
	// // {
	// // intent.setClass(this, CurriculumActivity.class);
	// // startActivity(intent);
	// // }
	// }
	//
	//
	// @Override
	// public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	//
	//
	// }
	private class TabListener implements ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class mClass;

		/**
		 * Constructor used each time a new tab is created.
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public TabListener(Activity activity, String tag, Class clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			FragmentTransaction ft = StaticAttachmentActivity.this
					.getSupportFragmentManager().beginTransaction();

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			mFragment = StaticAttachmentActivity.this
					.getSupportFragmentManager().findFragmentByTag(mTag);
			if (mFragment != null && !mFragment.isDetached()) {
				ft.detach(mFragment);
				ft.commit();
				mFragment = null;
			}
		}

		/* The following are each of the ActionBar.TabListener callbacks */

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.detach(mFragment);

			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// 2012-06-04 basilwang do sth when change the day or week view
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				ft.attach(mFragment);
			}
		}
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		return false;
	}

	@Override
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
		if (root == null)
			return; // for whatever reason in very rare cases this is null
		if (xmlId == R.xml.preferencesfragment) {
			PreferenceCategory logonPreference;
			PreferenceScreen logonAddPreference;
			PreferenceScreen aboutusPreference;
			PreferenceScreen shareonweiboPreference;
			PreferenceScreen ruguozhaiPreference;
			// The filelds we have deleted
			// CheckBoxPreference weekViewCheckboxPreference;
			// CheckBoxPreference scoreCheckboxPreference;
			CheckBoxPreference adCheckboxPreference;
			logonAddPreference = (PreferenceScreen) root
					.findPreference(LOGON_ADD_PREFERENCES);
			if (logonAddPreference != null) {
				logonAddPreference.setOnPreferenceClickListener(this);
			}
			logonPreference = (PreferenceCategory) root
					.findPreference(LOGON_PREFERENCES);

			aboutusPreference = (PreferenceScreen) root
					.findPreference(ABOUT_US);
			shareonweiboPreference = (PreferenceScreen) root
					.findPreference(SHAREONWEIBO);

			ruguozhaiPreference = (PreferenceScreen) root
					.findPreference(RU_GUO_ZHAI);

			adCheckboxPreference = (CheckBoxPreference) root
					.findPreference(CLOSE_AD);
			// weekViewCheckboxPreference = (CheckBoxPreference) root
			// .findPreference(WEEKVIEW_ENABLED);
			// 2012-09-26 basilwang if weekview already enabled, we set
			// WeekViewCheckboxPreference enable status is false
			// if (Preferences.isWeekViewUnlocked(this)) {
			// weekViewCheckboxPreference.setChecked(true);
			// weekViewCheckboxPreference.setEnabled(false);
			// } else {
			// weekViewCheckboxPreference.setChecked(false);
			// weekViewCheckboxPreference.setEnabled(true);
			// }
			if (Preferences.isAdClosed(this)) {
				adCheckboxPreference.setChecked(true);
				adCheckboxPreference.setEnabled(false);
			} else {
				adCheckboxPreference.setChecked(false);
				adCheckboxPreference.setEnabled(true);
			}
			aboutusPreference.setOnPreferenceClickListener(this);
			shareonweiboPreference.setOnPreferenceClickListener(this);
			ruguozhaiPreference.setOnPreferenceClickListener(this);
			// weekViewCheckboxPreference.setOnPreferenceClickListener(this);
			adCheckboxPreference.setOnPreferenceClickListener(this);
			YoumiOffersManager.init(this, "2fc95b356bb979ae",
					"8b94f727980f7158");
			YoumiOffersManager.checkStatus(StaticAttachmentActivity.this,
					StaticAttachmentActivity.this);
			reloadData(logonPreference, logonAddPreference,
					root.getPreferenceManager());
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(ABOUT_US)) {
			Intent intent = new Intent(StaticAttachmentActivity.this,
					About_us.class);
			startActivity(intent);
			return false;
		}

		if (preference.getKey().equals(SHAREONWEIBO)) {

			if (isNetAvailable()) {
				// 先读取accessToken,看是否还在期限内
				StaticAttachmentActivity.accessToken = AccessTokenKeeper
						.readAccessToken(this);

				// 如果还在期限内，给出提示，直接跳�
				if (StaticAttachmentActivity.accessToken.isSessionValid()) {
					String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
							.format(new java.util.Date(
									StaticAttachmentActivity.accessToken
											.getExpiresTime()));
					Toast.makeText(
							StaticAttachmentActivity.this,
							"access_token 仍在有效期内,无需再次登录: \naccess_token:"
									+ StaticAttachmentActivity.accessToken
											.getToken() + "\n有效期：" + date,
							Toast.LENGTH_SHORT).show();

					// 认证完毕，跳转页�
					Intent intent = new Intent(StaticAttachmentActivity.this,
							ShareOnWeibo.class);
					startActivity(intent);

				}

				// 如果accessToken为空或已过期，进行认证授�
				else {
					// mWeibo.authorize(StaticAttachmentActivity.this,
					// new AuthDialogListener());
					mSsoHandler = new SsoHandler(StaticAttachmentActivity.this,
							mWeibo);
					mSsoHandler.authorize(new AuthDialogListener());
				}

			}

			else {
				Toast.makeText(StaticAttachmentActivity.this, "请检查网络状态，好像没联网哦",
						Toast.LENGTH_LONG).show();

			}

			return false;

		}

		if (preference.getKey().equals(RU_GUO_ZHAI)) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse("http://m.ruguozhai.me");
			intent.setData(content_url);
			startActivity(intent);
			return false;
		}
		// if (preference.getKey().equals(WEEKVIEW_ENABLED)) {
		//
		// AlertDialogFactory
		// .getYoumiOfferDialog(this, (CheckBoxPreference) preference)
		// .create().show();
		// return false;
		// }
		if (preference.getKey().equals(CLOSE_AD)) {
			AlertDialogFactory
					.getYoumiOfferDialog(this, (CheckBoxPreference) preference)
					.create().show();
			return false;
		}
		if (preference.getKey().equals(LOGON_ADD_PREFERENCES)) {
			Intent intent = new Intent();
			intent.setClass(this, LogonPreferenceActivity.class);
			startActivity(intent);

		} else if (preference.getKey().equals(LOGON_ACCOUNT_PREFERENCES)) {
			String account = preference.getTitle().toString();
			Intent intent = new Intent();
			intent.putExtra("name", account);
			intent.setClass(this, EditLogonPreferenceActivity.class);
			startActivity(intent);
		}
		return true;
	}

	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			StaticAttachmentActivity.accessToken = new Oauth2AccessToken(token,
					expires_in);

			// 将认证信息保存起�
			AccessTokenKeeper.keepAccessToken(StaticAttachmentActivity.this,
					accessToken);

			Toast.makeText(StaticAttachmentActivity.this, "认证成功",
					Toast.LENGTH_LONG).show();

			// 认证完毕，跳转页�
			Intent intent = new Intent(StaticAttachmentActivity.this,
					ShareOnWeibo.class);
			startActivity(intent);
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

	// 使用sso认证方式，需要重写onActivityResult()方法，并调用authorizeCallBack()方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/**
		 * 下面两个注释掉的代码，仅当sdk支持sso时有效，
		 */
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	private void refreshTabBarTitle() {
		accountId = PreferenceManager.getDefaultSharedPreferences(this).getInt(
				LOGON_ACCOUNT_ID, 0);
		Account account = accountService.getAccountById(accountId);
		if (account.getName() != null) {
			getSupportActionBar()
					.setTitle(
							account.getName()
									+ this.getResources().getString(
											R.string.myaccount));
		} else {
			getSupportActionBar()
					.setTitle(
							this.getResources().getString(
									R.string.pleasecreateaccount));
		}
	}

	private void reloadData(PreferenceCategory logonPreference,
			PreferenceScreen logonAddPreference,
			PreferenceManager preferenceManager) {
		// 2012-11-23 basilwang refresh tabbar title
		refreshTabBarTitle();

		logonPreference.removeAll();
		logonPreference.addPreference(logonAddPreference);
		int accountId = PreferenceManager.getDefaultSharedPreferences(this)
				.getInt(LOGON_ACCOUNT_ID, 0);
		AccountService service = new AccountService(this);
		List<Account> list = service.getAccounts();
		if (list.size() == 0) {
			logonAddPreference.setEnabled(true);

		} else {
			logonAddPreference.setEnabled(false);
			for (Account account : list) {
				PreferenceScreen preferenceItem = preferenceManager
						.createPreferenceScreen(this);
				// CheckBoxPreference checkBoxPreference = new
				// CheckBoxPreference(this);
				// make sure each key is unique
				preferenceItem.setKey(LOGON_ACCOUNT_PREFERENCES);
				preferenceItem.setTitle(account.getName());
				if (accountId == account.getId())
					preferenceItem.setSummary(R.string.already_checked);
				// preferenceItem.setChecked(false);
				// checkBoxPreference.setDisableDependentsState(disableDependentsState)
				// checkBoxPreference.setSelectable(false);
				preferenceItem.setOrder(0);
				preferenceItem.setOnPreferenceClickListener(this);
				logonPreference.addPreference(preferenceItem);
			}

		}

	}

	@Override
	public void onCheckStatusResponse(Context context, boolean isAppInvalid,
			boolean isInTestMode, boolean isDeviceInvalid) {

		Log.v("youmi",
				new StringBuilder(256)
						.append("检查App及当前设备状态成功\n=>>App状态:")
						.append(isAppInvalid ? "[异常]" : "[正常]")
						.append("\n=>>是否为测试模式:")
						.append(isInTestMode ? "[测试模式]" : "[正常模式]")
						.append("\n=>>当前设备状态:")
						.append(isDeviceInvalid ? "[异常]" : "[正常]")
						.append("\n只有三个状态都为正常时，才可以获得收入。但无论状态是否异常，用户完成积分墙模式下的Offer后都可以获得积分。")
						.append("\n\n如果您使用的是积分墙模式并且希望所有设备都可以获得积分，可以不调用该检查接口或不处理检查结果。")
						.append("\n如果您使用的是积分墙模式并且希望在保证有收入的情况下用户才能够获得相应的积分，那么您应该在使用积分墙前，先调用此接口进行状态判断，如果状态都为正常时才启用积分墙。")
						.append("\n\n如果App状态不正常或为\"测试模式\"，请确认是否已经上传应用到有米主站并通过审核，上传应用前，请先忽略该状态检查接口，正常调用积分墙，以配合审核人员进行审核。")
						.append("\n\n在调用状态检查接口前，请务必先进行初始化。该接口成功调用一次即可，不需要多次调用。")
						.toString());
	}

	@Override
	public void onCheckStatusConnectionFailed(Context context) {

		Log.v("youmi", "请检查网络配置是否开�并重新运行该程序");
	}

	private Exit exit = new Exit();

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			pressAgainExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void pressAgainExit() {

		if (exit.isExit()) {
			this.finish();
		} else {
			Toast.makeText(getApplicationContext(), "再按一次退出",
					Toast.LENGTH_SHORT).show();
			exit.doExitInOneSecond();
		}
	}

	private class Exit {
		private boolean isExit = false;
		private Runnable task = new Runnable() {
			@Override
			public void run() {
				isExit = false;
			}
		};

		public void doExitInOneSecond() {
			isExit = true;
			HandlerThread thread = new HandlerThread("doTask");
			thread.start();
			new Handler(thread.getLooper()).postDelayed(task, 1000);
		}

		public boolean isExit() {
			return isExit;
		}
	}

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.mainContainer, fragment).commit();
		getSlidingMenu().showContent();
	}
	
	public static String actionToString(int action) {
		switch (action) {
			case AbstractWeibo.ACTION_AUTHORIZING: return "ACTION_AUTHORIZING";
			case AbstractWeibo.ACTION_GETTING_FRIEND_LIST: return "ACTION_GETTING_FRIEND_LIST";
			case AbstractWeibo.ACTION_FOLLOWING_USER: return "ACTION_FOLLOWING_USER";
			case AbstractWeibo.ACTION_SENDING_DIRECT_MESSAGE: return "ACTION_SENDING_DIRECT_MESSAGE";
			case AbstractWeibo.ACTION_TIMELINE: return "ACTION_TIMELINE";
			case AbstractWeibo.ACTION_USER_INFOR: return "ACTION_USER_INFOR";
			case AbstractWeibo.ACTION_SHARE: return "ACTION_SHARE";
			default: {
				return "UNKNOWN";
			}
		}
	}
	
	protected void onDestroy() {
		AbstractWeibo.stopSDK(this);
		super.onDestroy();
	}

	public void exit() {
		StaticAttachmentActivity.this.finish();
	}

}
