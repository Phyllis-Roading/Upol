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
import java.util.List;
import net.basilwang.PreferenceFragmentPlugin.OnPreferenceAttachedListener;
import net.basilwang.config.SAXParse;
import net.basilwang.dao.AccountService;
import net.basilwang.entity.Account;
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
import android.view.KeyEvent;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// setTheme(SampleList.THEME); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		menu = new SlidingMenu(this);
		setTitle(SAXParse.getTAConfiguration().getSelectedCollege().getName());
		getSherlock().setContentView(R.layout.main_container);

		accountService = new AccountService(this);
		refreshTabBarTitle();

		StringBuffer url = new StringBuffer(
				"http://xueli.upol.cn/M4/upol/platform/");
		url.append("zxgg/zxgg_01.jsp?pageInt=");
		// add slidingMenu
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new MessageFragment(url);

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
