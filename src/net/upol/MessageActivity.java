package net.upol;

import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import net.basilwang.R;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.annotation.SuppressLint;
import android.content.Intent;

public class MessageActivity extends SherlockActivity {

	WebView webView;
	ArrayList<String> list;
	int position;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		init();
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());
	}

	private void init() {
		getSupportActionBar().setTitle(R.string.message_content);
		getSupportActionBar().setIcon(R.drawable.cancel);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		list = new ArrayList<String>();
		Intent i = getIntent();
		position = i.getIntExtra("position", 0) - 1;
		System.out.println("succeed" + position);
		webView = (WebView) findViewById(R.id.webView1);
		list.add("http://www.sina.com.cn/");
		webView.loadUrl("http://www.sina.com.cn/");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			MessageActivity.this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
