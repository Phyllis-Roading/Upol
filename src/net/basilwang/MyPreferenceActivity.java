package net.basilwang;

import static net.basilwang.dao.Preferences.ABOUT_US;
import static net.basilwang.dao.Preferences.CEMESTER_END_PREFERENCES;
import static net.basilwang.dao.Preferences.CEMESTER_START_PREFERENCES;
import static net.basilwang.dao.Preferences.CEMESTER_WEEKS_NUM;
import static net.basilwang.dao.Preferences.CLOSE_AD;
import static net.basilwang.dao.Preferences.CLOSE_AD_STATUS;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_PREFERENCES;
import static net.basilwang.dao.Preferences.LOGON_ADD_PREFERENCES;
import static net.basilwang.dao.Preferences.LOGON_PREFERENCES;
import static net.basilwang.dao.Preferences.SHAREONWEIBO;
import static net.basilwang.dao.Preferences.WEEKVIEW_ENABLED;

import java.util.List;
import net.basilwang.config.SAXParse;
import net.basilwang.dao.AccountService;
import net.basilwang.dao.Preferences;
import net.basilwang.entity.Account;
import net.basilwang.utils.NetworkUtils;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class MyPreferenceActivity extends SherlockPreferenceActivity implements
		OnPreferenceClickListener {
	static final String TAG = "MyPreferenceActivity";
	private PreferenceCategory logonPreference;
	private PreferenceScreen logonAddPreference, cemesterStartPreference,
			cemesterEndPreference;
	// un used fields
	// private CheckBoxPreference weekViewCheckboxPreference;
	// private CheckBoxPreference scoreCheckboxPreference;
	private Time mShowFromTime;
	private Time mDueTime;
	SubMenu subMenuForNetwork;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(SAXParse.getTAConfiguration().getSelectedCollege().getName());
		addPreferencesFromResource(R.xml.preferences);
		getSupportActionBar().setHomeButtonEnabled(true);
		mShowFromTime = new Time();
		mDueTime = new Time();

		logonAddPreference = (PreferenceScreen) findPreference(LOGON_ADD_PREFERENCES);
		logonAddPreference.setOnPreferenceClickListener(this);
		logonPreference = (PreferenceCategory) findPreference(LOGON_PREFERENCES);

		cemesterStartPreference = (PreferenceScreen) findPreference(CEMESTER_START_PREFERENCES);
		cemesterEndPreference = (PreferenceScreen) findPreference(CEMESTER_END_PREFERENCES);

		long millis = PreferenceManager.getDefaultSharedPreferences(this)
				.getLong(CEMESTER_START_PREFERENCES, 0);
		if (millis != 0) {
			mShowFromTime.set(millis);
			setDate(cemesterStartPreference, millis);
		}
		millis = PreferenceManager.getDefaultSharedPreferences(this).getLong(
				CEMESTER_END_PREFERENCES, 0);
		if (millis != 0) {
			mDueTime.set(millis);
			setDate(cemesterEndPreference, millis);
		}
		cemesterStartPreference
				.setOnPreferenceClickListener(new DateClickListener(
						mShowFromTime, cemesterStartPreference));
		cemesterEndPreference
				.setOnPreferenceClickListener(new DateClickListener(mDueTime,
						cemesterEndPreference));

		checkNetwork();
		reloadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		subMenuForNetwork = menu.addSubMenu(R.string.networkavailable);
		subMenuForNetwork.add(0, R.style.Theme_Sherlock, 0,
				R.string.checknetwork);
		subMenuForNetwork.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		subMenuForNetwork.getItem().setVisible(true);

		checkNetwork();

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			Intent intent = new Intent(this, StaticAttachmentActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// 判断是哪个Preference改变了
		if (preference.getKey().equals(LOGON_ADD_PREFERENCES)) {
			Log.e(TAG, getString(R.string.logon_add_preferences));
			// if(NetworkUtils.isConnect(this))
			// {
			// 这里即使不联网，但是由于没有访问网络的动作，所以不会出问题
			Intent intent = new Intent();
			intent.setClass(this, LogonPreferenceActivity.class);
			startActivity(intent);
			// }
			// else
			// {
			// Toast.makeText(MyPreferenceActivity.this, "好像没有联网哦",
			// Toast.LENGTH_SHORT).show();
			// }

		} else if (preference.getKey().equals(LOGON_ACCOUNT_PREFERENCES)) {
			String account = preference.getTitle().toString();
			Log.e(TAG, account);
			Intent intent = new Intent();
			intent.putExtra("name", account);
			intent.setClass(this, EditLogonPreferenceActivity.class);
			startActivity(intent);

		} else if (preference.getKey().equals(CEMESTER_START_PREFERENCES)) {

		} else if (preference.getKey().equals(WEEKVIEW_ENABLED)) {

		}

		// 返回true表示允许改变
		return true;
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

	private void reloadData() {
		logonPreference.removeAll();
		logonPreference.addPreference(logonAddPreference);
		SharedPreferences myPrefs = this.getPreferences(MODE_PRIVATE);
		int accountId = PreferenceManager.getDefaultSharedPreferences(this)
				.getInt(LOGON_ACCOUNT_ID, 0);
		AccountService service = new AccountService(this);
		List<Account> list = service.getAccounts();
		for (Account account : list) {
			PreferenceScreen preferenceItem = getPreferenceManager()
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

	public void onResume() {
		super.onResume();
		checkNetwork();
		reloadData();
	}

	private void setDate(PreferenceScreen screen, long millis) {
		CharSequence value;
		int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
				| DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH
				| DateUtils.FORMAT_ABBREV_WEEKDAY;
		value = DateUtils.formatDateTime(MyPreferenceActivity.this, millis,
				flags);
		screen.setSummary(value);
	}

	private void updateToDefault(Time displayTime) {
		displayTime.setToNow();
		displayTime.second = 0;
		int minute = displayTime.minute;
		if (minute > 0 && minute <= 30) {
			displayTime.minute = 30;
		} else {
			displayTime.minute = 0;
			displayTime.hour += 1;
		}
	}

	private class DateListener implements DatePickerDialog.OnDateSetListener {
		// View mView;
		PreferenceScreen mScreen;

		public DateListener(PreferenceScreen screen) {
			// mView = view;
			mScreen = screen;
		}

		public void onDateSet(DatePicker view, int year, int month, int monthDay) {
			// Cache the member variables locally to avoid inner class overhead.
			Time showFromTime = mShowFromTime;
			Time dueTime = mDueTime;
			Time innerTime;
			if (mScreen == cemesterStartPreference)
				innerTime = mShowFromTime;
			else
				innerTime = mDueTime;
			long showFromMillis;
			long dueMillis;
			long innerMillis;

			if (Time.isEpoch(innerTime)) {
				updateToDefault(innerTime);
			}

			innerTime.year = year;
			innerTime.month = month;
			innerTime.monthDay = monthDay;
			innerMillis = innerTime.normalize(true);

			// mShowFromDateVisible = true;

			// if (mDueDateVisible) {
			// // Also update the end date to keep the duration constant.
			// dueTime.year = year + yearDuration;
			// dueTime.month = month + monthDuration;
			// dueTime.monthDay = monthDay + monthDayDuration;
			// }

			int dueWeekNumber;
			int showFromWeekNumber;
			SharedPreferences.Editor ed = Preferences
					.getEditor(MyPreferenceActivity.this);
			if (mScreen == cemesterStartPreference) {
				// showFromTime=innerTime;
				if (Time.compare(showFromTime, dueTime) > 0) {
					dueTime.set(showFromTime.monthDay, showFromTime.month,
							showFromTime.year);
				}

			} else {
				// dueTime=innerTime;
				if (Time.compare(showFromTime, dueTime) > 0) {
					showFromTime.set(dueTime.monthDay, dueTime.month,
							dueTime.year);
				}

			}
			showFromMillis = showFromTime.normalize(true);
			dueMillis = dueTime.normalize(true);
			ed.putLong(CEMESTER_START_PREFERENCES, showFromMillis);
			ed.putLong(CEMESTER_END_PREFERENCES, dueMillis);
			dueWeekNumber = dueTime.getWeekNumber();
			showFromWeekNumber = showFromTime.getWeekNumber();
			int span = net.basilwang.utils.DateUtils.getWeekSpan(showFromTime,
					dueTime);

			Log.v(TAG, String.valueOf(dueWeekNumber));
			Log.v(TAG, String.valueOf(showFromWeekNumber));
			Log.v(TAG, String.valueOf(dueWeekNumber - showFromWeekNumber + 1));
			Log.v(TAG, String.valueOf(span));
			Toast.makeText(MyPreferenceActivity.this, String.valueOf(span),
					1000).show();

			ed.putInt(CEMESTER_WEEKS_NUM, dueWeekNumber - showFromWeekNumber
					+ 1);
			ed.commit();
			setDate(cemesterStartPreference, showFromMillis);
			setDate(cemesterEndPreference, dueMillis);

		}
	}

	private class DateClickListener implements OnPreferenceClickListener {
		private Time mTime;
		private PreferenceScreen mScreen;

		public DateClickListener(Time time, PreferenceScreen screen) {
			mTime = time;
			mScreen = screen;
		}

		@Override
		public boolean onPreferenceClick(Preference preference) {
			Time displayTime = mTime;
			if (Time.isEpoch(displayTime)) {
				displayTime = new Time();
				updateToDefault(displayTime);
			}
			new DatePickerDialog(MyPreferenceActivity.this, new DateListener(
					mScreen), displayTime.year, displayTime.month,
					displayTime.monthDay).show();
			return false;
		}
	}

}
