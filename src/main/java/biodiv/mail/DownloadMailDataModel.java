package biodiv.mail;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;

import biodiv.user.User;

public class DownloadMailDataModel {

	private Map<String, Object> root;
	
	public DownloadMailDataModel(Long id,String name,Configuration config){
		root = new HashMap<>();
		User user = new User();
		user.setId(id);
		user.setName(name);
		root.put("downloadMail", user);
		root.put("serverUrl", config.getString("serverUrl"));
		root.put("siteName", config.getString("siteName"));
	}
	
	public Map<String, Object> getRoot(){
		return this.root;
	}
	 
}
