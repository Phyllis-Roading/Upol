package net.basilwang;

//import org.androidpn.client.ServiceManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  //去掉窗口标题
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      //全屏显示
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
      
	  setContentView(R.layout.splash);
	  new Handler().postDelayed(new Runnable(){
	   @Override
	   public void run() {
	    Intent mainIntent = new Intent(SplashActivity.this,StaticAttachmentActivity.class);
	    SplashActivity.this.startActivity(mainIntent);
	    SplashActivity.this.finish();
	   }
	  }, 2000);//2000为间隔的时间-毫秒
      
//	  //启动推送服务
//      ServiceManager serviceManager = new ServiceManager(this);
//      serviceManager.setNotificationIcon(R.drawable.ic_launcher);
//      serviceManager.startService();
	  
	 }
	}