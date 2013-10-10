
package net.basilwang.sever;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class RequestMessage{
	List<String> result=new ArrayList<String>();
	public void setResult(String userNO){
		try{
			String url="http://120.192.31.164:8001/weblogin/UnitAss/SmsConter.aspx?StudentNum="+userNO;
			HttpClient httpClient=new DefaultHttpClient();
			HttpPost post=new HttpPost(url);
			HttpResponse response=httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
        		Log.v("Tag", "httpClientSuccess");
                // 解析返回的内容  
                result = filterHtml(EntityUtils.toString(response.getEntity()));
            }
		}catch(Exception e){
			
		}
	}
	public List<String> getResult(){
		return result;
	}
	public List<String> filterHtml(String source){
		Pattern p = Pattern.compile("\\$(.*)\\$");
		Matcher match = p.matcher(source);
		List<String> list=new ArrayList<String>();
		while(match.find()){
			String domain="";
			domain=match.group(1);
			list.add(domain);
		}
		return list;
	}
}
