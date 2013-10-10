package net.basilwang.sever;

import java.util.List;


public class Message {

	private List<String> content;
	public void setContent(List<String> message){
		content=message;
	}
	public List<String> getContent(){
		return content;
	}
}
