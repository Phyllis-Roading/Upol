package net.upol;

import net.basilwang.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class MessageActivity extends SherlockActivity {

	private WebView webView;

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
		Intent i = getIntent();
		String url = i.getStringExtra("link");
		webView = (WebView) findViewById(R.id.webView1);
		webView.loadUrl(url);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			MessageActivity.this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
