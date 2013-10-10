package net.basilwang;

import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;
import static net.basilwang.dao.Preferences.SCORE_TIP_SHOW;
import static net.basilwang.dao.Preferences.CURRICULUM_TIP_SHOW;
import static net.basilwang.dao.Preferences.WEEK_VIEW_TIP_SHOW;

import static net.basilwang.dao.Preferences.SOCRE_DOWNLOAD_TIP;
import static net.basilwang.dao.Preferences.CURRICULUM_DOWNLOAD_TIP;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;

import net.basilwang.config.SAXParse;
import net.basilwang.core.TAHelper;
import net.basilwang.dao.AccountService;
import net.basilwang.dao.CurriculumService;
import net.basilwang.dao.IDAOService;
import net.basilwang.dao.Preferences;
import net.basilwang.dao.ScoreService;
import net.basilwang.dao.SemesterService;
import net.basilwang.entity.Account;
import net.basilwang.listener.ActionModeListener;
import net.basilwang.utils.NetworkUtils;
import net.basilwang.utils.PreferenceUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Window;

public class EditLogonPreferenceActivity extends SherlockActivity implements
		ActionModeListener {
	private EditText mAccountTextbox;
	private EditText mUrlTextbox;
	private EditText mUserTextbox;
	private EditText mPassTextbox;
	// private Spinner mInterval;
	private Account account;
	TAHelper taHelper;
	ActionMode mMode;

	private void initView() {
		setTitle(SAXParse.getTAConfiguration().getSelectedCollege().getName());
		mAccountTextbox = (EditText) findViewById(R.id.user_identifier);
		mUrlTextbox = (EditText) findViewById(R.id.url);
		mUrlTextbox.setVisibility(View.INVISIBLE);
		mUserTextbox = (EditText) findViewById(R.id.user);
		mPassTextbox = (EditText) findViewById(R.id.pass);
		/** 2012-12-6 mini star :entrance year will be invisible **/
		EditText mEntranceyearbox = (EditText) findViewById(R.id.entranceyear);
		mEntranceyearbox.setVisibility(View.INVISIBLE);
		TextView entracneYear = (TextView) findViewById(R.id.entranceyear_label);
		entracneYear.setVisibility(View.INVISIBLE);
		/*************************************/
		String[] modeTitles = { "保存", "注销" };
		mMode = startActionMode(new AddActionMode(modeTitles,
				EditLogonPreferenceActivity.this));
	}

	public void onActionItemClickedListener(String title) {
		if (title.equals("保存")) {
			String message = confirmTextBoxIsNotNull();
			if (message.equals("OK") == true) {
				savePrefs();
				SharedPreferences.Editor ed = Preferences
						.getEditor(EditLogonPreferenceActivity.this);
				ed.putInt(LOGON_ACCOUNT_ID, account.getId());
				ed.commit();
				mMode.finish();
			}
			Toast.makeText(EditLogonPreferenceActivity.this, message,
					Toast.LENGTH_SHORT).show();
		}
		if (title.equals("注销")) {
			showAlertDialogForLogOff();
		}
	}

	public void finishActionMode() {
		EditLogonPreferenceActivity.this.finish();
	}

	private void showAlertDialogForLogOff() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder.setTitle(R.string.logoff_title);
		alertDialogBuilder.setMessage(R.string.logoff_tips);
		alertDialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						new LogOff().deleteAccount();
						EditLogonPreferenceActivity.this.finish();
					}
				});
		alertDialogBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialogBuilder.create();
		alertDialogBuilder.show();
	}

	public IDAOService getInstance(Class object) {
		Constructor constructor = null;
		IDAOService instance = null;
		try {
			constructor = object.getConstructor(Context.class);
			instance = (IDAOService) constructor.newInstance(getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.synchronize_settings);
		initView();
		if (!NetworkUtils.isConnect(this)) {
			// 如果没有网络连接，则提示用户“没有网络连接”，并且不能进行跳转
			Toast.makeText(EditLogonPreferenceActivity.this,
					this.getResources().getString(R.string.nonetwork_toast),
					Toast.LENGTH_SHORT).show();
		}
		Bundle bundle = getIntent().getExtras();
		String accountName = bundle.getString("name");
		AccountService service = new AccountService(this);
		account = service.getAccountByName(accountName);

		mAccountTextbox.setText(accountName);

		String tracksUrl = account.getUrl();
		mUrlTextbox.setText(tracksUrl);
		// select server portion of URL
		int startIndex = 0;
		int index = tracksUrl.indexOf("://");
		if (index > 0) {
			startIndex = index + 3;
		}
		mUrlTextbox.setSelection(startIndex, tracksUrl.length());

		mUserTextbox.setText(account.getUserno());
		mPassTextbox.setText(account.getPassword());
	}

	public String confirmTextBoxIsNotNull() {
		if (mAccountTextbox.getText().toString().trim().equals(""))
			return "帐号标识 不能为空";
		if (mUserTextbox.getText().toString().trim().equals(""))
			return "学号不能为空";
		if (mPassTextbox.getText().toString().equals(""))
			return "密码不能为空";

		return "OK";
	}

	private boolean savePrefs() {

		URI uri = null;
		try {
			uri = new URI(mUrlTextbox.getText().toString());
		} catch (URISyntaxException ignored) {

		}
		account.setName(mAccountTextbox.getText().toString());
		account.setUrl(uri.toString());
		account.setUserno(mUserTextbox.getText().toString());
		account.setPassword(mPassTextbox.getText().toString());
		AccountService service = new AccountService(this);
		service.update(account);

		return true;
	}

	public Activity getActivity() {
		return EditLogonPreferenceActivity.this;
	}

	private class LogOff {

		public void deleteAccount() {
			Class[] daoServices = { AccountService.class,
					CurriculumService.class, SemesterService.class,
					ScoreService.class };
			for (int i = 0; i < daoServices.length; i++) {
				IDAOService daoService = getInstance(daoServices[i]);
				daoService.deleteAccount();
			}
			clearPreferAndSaveTipPrefer();
		}

		private void clearPreference() {
			SharedPreferences.Editor ed = Preferences.getEditor(getActivity());
			ed.clear();
			ed.commit();
		}

		public void clearPreferAndSaveTipPrefer() {
			String[] tips = { SCORE_TIP_SHOW, CURRICULUM_TIP_SHOW,
					WEEK_VIEW_TIP_SHOW, SOCRE_DOWNLOAD_TIP,
					CURRICULUM_DOWNLOAD_TIP };
			int[] tipValues = new int[tips.length];
			for (int i = 0; i < tips.length; i++) {
				tipValues[i] = PreferenceManager.getDefaultSharedPreferences(
						getActivity()).getInt(tips[i], 0);
			}
			clearPreference();
			// save tips value in prefer
			for (int i = 0; i < tips.length; i++) {
				PreferenceUtils.modifyIntValueInPreferences(getActivity(),
						tips[i], tipValues[i]);
			}
		}
	}
}
