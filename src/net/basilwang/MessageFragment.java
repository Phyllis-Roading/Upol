package net.basilwang;

import java.util.ArrayList;
import java.util.List;

import net.basilwang.sever.MessageService;

import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageFragment extends Fragment {

	View messageView;
	UITableView messages;
	List<String> dbMessages=new ArrayList<String>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		SliderMenuFragment.messageNum=0;
		messageView=inflater.inflate(
				R.layout.messages_frament, container, false);
		messages=(UITableView)messageView.findViewById(R.id.message_fragment);
		requestMessages();
		createMessages();
		return messageView;
	}
	
	//获取本地数据库中的信息
	public void requestMessages(){
		MessageService messageService=new MessageService(this.getActivity());
		dbMessages=messageService.getMessages();
	}
	
	public void createMessages(){
		messages.clear();
		CustomClickListener listener = new CustomClickListener();//点击删除消息
    	messages.setClickListener(listener);
    	try{
    		if(dbMessages.size()==0){
    			messages.addBasicItem("没有新消息");
    		}else{
    			for(String m:dbMessages){
    				String title=m.substring(10, m.length());
    				String summary=m.substring(0, 10);
    				messages.addBasicItem(title, summary);
//    				messages.addBasicItem(m);
    			}
    		}
    	}catch(Exception e){
    		
    	}
		messages.commit();
	}

	private class CustomClickListener implements ClickListener {

		@Override
		public void onClick(int index) {
			try{
				String summery=dbMessages.get(index).substring(10, dbMessages.get(index).length());
				dialogMessage(summery,index);
			}catch(Exception e){
				
			}
        }
	}
	public void deleteMessage(int index){
		MessageService service=new MessageService(this.getActivity());
		service.delete(dbMessages.get(index));
		dbMessages.remove(index);
	}
	public void dialogMessage(String message,final int index){
		AlertDialog.Builder builder = new Builder(this.getActivity());
		builder.setMessage(message);
		builder.setTitle("消息内容");
		builder.setPositiveButton("返回", new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
			
		});
		builder.setNegativeButton("删除", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				deleteMessage(index);
				createMessages();
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
