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

import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_PREFERENCES;
import static net.basilwang.dao.Preferences.LOGON_ADD_PREFERENCES;
import static net.basilwang.dao.Preferences.LOGON_PREFERENCES;

import java.util.Calendar;
import java.util.List;

import net.basilwang.PreferenceFragmentPlugin.OnPreferenceAttachedListener;
import net.basilwang.config.SAXParse;
import net.basilwang.dao.AccountService;
import net.basilwang.dao.SemesterService;
import net.basilwang.entity.Account;
import net.basilwang.entity.Semester;
import net.basilwang.sever.Message;
import net.basilwang.sever.MessageService;
import net.basilwang.sever.RequestMessage;
import net.basilwang.utils.NetworkUtils;
import net.upol.MessageFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class StaticAttachmentActivity extends BaseActivity implements
		OnPreferenceAttachedListener, OnPreferenceChangeListener,
		OnPreferenceClickListener {
	public StaticAttachmentActivity() {
		super(R.string.changing_fragments);
	}

	// private String[] viewtypes;//is not used after add slidingMenu

	static final String TAG = "StaticAttachmentActivity";
	SubMenu subMenuForNetwork;
	// SubMenu subMenuWithoutNetwork;
	private int accountId;
	private AccountService accountService;
	private Boolean isExiting = false;
	/**
	 * SsoHandler 仅当sdk支持sso时有效，
	 */
	private Fragment mContent;
	private SlidingMenu menu;
	protected static int nuwMessages;
	private String newSemesterBegin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// setTheme(SampleList.THEME); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		requestMessages();// 获取服务器端最新消�
		menu = new SlidingMenu(this);
		setTitle(SAXParse.getTAConfiguration().getSelectedCollege().getName());
		getSherlock().setContentView(R.layout.main_container);

		accountService = new AccountService(this);
		refreshTabBarTitle();

		// add slidingMenu
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new MessageFragment();

		// set the Above View
		setContentView(R.layout.main_container);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.mainContainer, mContent).commit();

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SliderMenuFragment(menu))
				.commit();

		// customize the SlidingMenu
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// add slidingmenu over

		int flag = getIntent().getIntExtra("flag", 0);
		if (flag == SliderMenuFragment.EXIT_APPLICATION) {
			isExiting = true;
			finish();
		} else {
			isExiting = false;
		}

		newUserOrNot();

	}

	// 联网登录时获取最新推送消息，插入数据�
	private void requestMessages() {
		MessageService messageService = new MessageService(this);
		try {
			AccountService service = new AccountService(this);
			Account account = service.getAccounts().get(0);
			Log.v("1231321313213213text", account.getUserno());
			RequestMessage request = new RequestMessage();
			request.setResult(account.getUserno());
			Message message = new Message();
			message.setContent(request.getResult());
			nuwMessages = message.getContent().size();
			messageService.save(message);
			// 判断本条消息是否修改开学日�
			for (String m : message.getContent()) {
				if (m.substring(10, 26).equals("系统已自动将您的开学日期设置为：")) {
					newSemesterBegin = m;
					updateSemesterBegin();
				}
			}
		} catch (Exception e) {

		}
	}

	// 根据服务器端的信息修改开学日�
	private void updateSemesterBegin() {
		Calendar cal = Calendar.getInstance();
		String sYear = newSemesterBegin.substring(26, 30);
		String sMonth = newSemesterBegin.substring(31, 33);
		String sDay = newSemesterBegin.substring(34, 36);
		int iYear = Integer.parseInt(sYear);
		int iMonth = Integer.parseInt(sMonth) - 1;
		int iDay = Integer.parseInt(sDay);
		cal.set(iYear, iMonth, iDay);
		Semester semester = new Semester();
		semester.setBeginDate(cal.getTime().getTime());
		SemesterService service = new SemesterService(this);
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
		if (item.getTitle().equals("周/天")) {
			Toast.makeText(this, "周视图暂时关闭，下个版本炫丽呈现", Toast.LENGTH_SHORT).show();
		}
		if (item.getTitle() == getResources().getString(R.string.checknetwork)) {
			checkNetwork();

		}
		return super.onOptionsItemSelected(item);
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
			// The filelds we have deleted
			// CheckBoxPreference weekViewCheckboxPreference;
			// CheckBoxPreference scoreCheckboxPreference;
			logonAddPreference = (PreferenceScreen) root
					.findPreference(LOGON_ADD_PREFERENCES);
			if (logonAddPreference != null) {
				logonAddPreference.setOnPreferenceClickListener(this);
			}
			logonPreference = (PreferenceCategory) root
					.findPreference(LOGON_PREFERENCES);

			reloadData(logonPreference, logonAddPreference,
					root.getPreferenceManager());
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {

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

	public void exit() {
		StaticAttachmentActivity.this.finish();
	}

}
