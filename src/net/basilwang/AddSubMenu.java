package net.basilwang;

import net.basilwang.listener.ShowTipListener;
import net.basilwang.utils.PreferenceUtils;
import net.basilwang.utils.TipUtils;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;

public class AddSubMenu implements ShowTipListener {
	private SubMenu sub;
	private Context context;
	private int tipPhotoId;
	private String preferKey;

	public void initSub(Menu menu) {
		sub = menu.addSubMenu("下载设置");
		sub.setIcon(R.drawable.btn_download_setting);
	}

	public void setSubMenuItemListener(final View.OnClickListener buttonListener) {
		SubMenuListener menuListener = new SubMenuListener(buttonListener);
	}

	public AddSubMenu(Menu menu, Context context) {
		this.context = context;
		initSub(menu);
	}

	public SubMenu getSubMenu() {
		return sub;
	}

	public void setTipPhotoAndPreferKey(int tipPhotoId, String key) {
		this.tipPhotoId = tipPhotoId;
		this.preferKey = key;
	}

	@Override
	public void showTipIfNecessary() {
		int tip = PreferenceManager.getDefaultSharedPreferences(context)
				.getInt(preferKey, 0);
		if (tip == 0) {
			TipUtils.showTipIfNecessary(context, tipPhotoId, this);
		}
	}

	@Override
	public void dismissTip() {
		PreferenceUtils.modifyIntValueInPreferences(context, preferKey, 1);
	}

	private class SubMenuListener implements OnMenuItemClickListener {

		private final View.OnClickListener buttonListener;

		public SubMenuListener(final View.OnClickListener buttonListener) {
			this.buttonListener = buttonListener;
		}

		public void initButton(MenuItem item) {
			Button download = (Button) item.getActionView().findViewById(
					R.id.download_semester_button);
			download.setOnClickListener(buttonListener);
		}

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			showTipIfNecessary();
			return false;
		}
	}

}
