package net.basilwang.sever;

import java.util.ArrayList;
import java.util.List;

import net.basilwang.dao.DAOHelper;
import net.basilwang.dao.IDAOService;
import android.content.Context;
import android.database.Cursor;

public class MessageService implements IDAOService{

	private DAOHelper daoHelper;

	public MessageService(Context context) {
		this.daoHelper = new DAOHelper(context);
	}

	public int save(Message message) {
		for(String n:message.getContent()){
			String sql = "INSERT INTO messages (content) VALUES (?)";
			Object[] bindArgs = {n};
			daoHelper.insert(sql, bindArgs);
		}
		return 0;
	}

	public List<String> getMessages() {
		String sql = "select * from messages";
		Cursor result = daoHelper.query(sql, null);
		List<String> list = new ArrayList<String>();
		while (result.moveToNext()) {
			String message="";
			message=result.getString(result.getColumnIndex("content"));
			list.add(message);
		}
		daoHelper.closeDB();
		return list;
	}
	
	public void delete(String content){
		String sql = "delete from messages where content = ?";
		Object[] bindArgs = {content};
		daoHelper.delete(sql, bindArgs);
	}

	@Override
	public void deleteAccount() {
		// TODO Auto-generated method stub
		String sql = "DELETE  FROM curriculum";
		daoHelper.delete(sql);
	}
	
}
