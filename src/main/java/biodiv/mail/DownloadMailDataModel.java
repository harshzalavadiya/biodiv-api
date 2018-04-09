package biodiv.mail;

import java.util.HashMap;
import java.util.Map;

import biodiv.user.User;

public class DownloadMailDataModel {

	private Map<String, Object> root;
	
	public DownloadMailDataModel(Long id,String name){
		root = new HashMap<>();
		User user = new User();
		user.setId(id);
		user.setName(name);
		root.put("downloadMail", user);
	}
	
	public Map<String, Object> getRoot(){
		return this.root;
	}
	 
}
